/*
 * Copyright (C) 2020 Jens Pelzetter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jpdigital.owl.apigenerator.core;

import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates a repository class for each OWL class from the ontology.
 *
 * Each repository provides methods for
 * <ul>
 * <li>Retrieving all instances of a class</li>
 * <li>For each data property a method is generated thtat allows it to filter
 * the instances for a specific value</li>
 * </ul>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RepositoryGenerator {

    private static final Logger LOGGER = LogManager.getLogger(
        RepositoryGenerator.class
    );

    /**
     * The ontology to use.
     */
    private final OntologyOwlApi ontologyOwlApi;

    /**
     * The directory in which the generated packages and classes are stored.
     */
    private final Path outputDir;

    private RepositoryGenerator(
        final OntologyOwlApi ontologyOwlApi, final Path outputDir
    ) {
        this.ontologyOwlApi = ontologyOwlApi;
        this.outputDir = outputDir;
    }

    /**
     * Factory method creating a new {@code IriConstantsGenerator} instance.The
     * provided parameters are checked.
     *
     * If an parameter is invalid an {@link IllegalArgumentException} is thrown.
     *
     * @param ontologyOwlApi The ontology to use.
     * @param outputDir      The output directory.
     *
     * @return An {@code IriConstantsGenerator}.
     */
    public static RepositoryGenerator buildRepositoryGenerator(
        final OntologyOwlApi ontologyOwlApi, final Path outputDir
    ) {
        if (!Files.isDirectory(outputDir)) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided path \"%s\" is not a directory.",
                    outputDir.toString()
                )
            );
        }

        if (!Files.isWritable(outputDir)) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided output directory \"%s\" is not writable.",
                    outputDir.toString()
                )
            );
        }

        Objects.requireNonNull(ontologyOwlApi, "ontologyOwlApi can't be null");

        return new RepositoryGenerator(ontologyOwlApi, outputDir);
    }

    public void generateRepositoryClasses()
        throws RepositoryGenerationFailedException {

        final Set<OWLClass> owlClasses = ontologyOwlApi
            .getOntology()
            .classesInSignature(Imports.INCLUDED)
            .filter(
                owlClass -> !owlClass.getIRI().toString().startsWith(
                    "http://www.w3.org/"
                )
            )
            .collect(Collectors.toSet());

        for (final OWLClass owlClass : owlClasses) {
            generateRepositoryClass(owlClass);
        }
    }

    /**
     * Generates a repository class for the provided OWL class.
     *
     * @param owlClass The OWL class
     *
     * @return The fully qualified name of the generated Java class.
     */
    private void generateRepositoryClass(final OWLClass owlClass)
        throws RepositoryGenerationFailedException {
        LOGGER.info(
            "OWL class {} is in the domain of the following data properties:",
            owlClass.getIRI().toString()
        );

        final List<IRI> dataPropertesIris = ontologyOwlApi
            .getOntology()
            .dataPropertiesInSignature(Imports.INCLUDED)
            .filter(dataProp -> classInDomainOfDataProperty(owlClass, dataProp))
            .map(dataProp -> dataProp.getIRI())
            .collect(Collectors.toList());

        dataPropertesIris.forEach(
            dataProp -> LOGGER.info("* {}", dataProp.toString())
        );

        final List<String> dataProperties = dataPropertesIris
            .stream()
            .map(dataProperty -> dataProperty.getShortForm())
            .collect(Collectors.toList());

        final String packageName = Utils.generatePackageName(owlClass.getIRI());
        final String className = generateClassName(owlClass.getIRI());

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("license", "");
        dataModel.put("package", packageName);
        dataModel.put("classIri", owlClass.getIRI().toString());
        dataModel.put("className", className);
        dataModel.put("owlClassName", owlClass.getIRI().getShortForm());
        dataModel.put("dataProperties", dataProperties);

        final Path packagePath = Utils.generatePackagePath(packageName);

        final TemplateService templateService = TemplateService
            .getTemplateService();
        final String result = templateService.processTemplate(
            "Repository.java.ftl", dataModel
        );

        final Path packageDir = outputDir.resolve(packagePath);
        final Path classFile = packageDir.resolve(
            String.format("%s.java", className)
        );

        try {
            Files.createDirectories(packageDir);
            Files.write(classFile, result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RepositoryGenerationFailedException(ex);
        }
    }

//    private String dataPropertyRangeTypes(final OWLDataProperty dataProperty) {
//        return ontologyOwlApi
//            .getOntology()
//            .dataPropertyRangeAxioms(dataProperty)
//            .map(axiom -> axiom.getRange().asOWLDatatype())
//            .filter(dataType -> dataType.isBuiltIn())
//            .map(dataType -> dataType.getBuiltInDatatype())
//            .map(dataType -> dataType.getShortForm())
//            .collect(Collectors.joining(", "));
//
//    }
    private boolean classInDomainOfDataProperty(
        final OWLClass owlClass, final OWLDataProperty dataProperty
    ) {
        return ontologyOwlApi
            .getReasoner()
            .dataPropertyDomains(dataProperty)
            .anyMatch(classInDomain -> classInDomain.equals(owlClass));
    }

    private String generateClassName(final IRI iri) {
        final String iriShortForm = iri.getShortForm();

        final String capitalized = WordUtils.capitalize(iriShortForm, '-', '_');
        final String replaced = capitalized
        .replace("-", "")
        .replace('.', '_');
        
        if (replaced.matches("^[0-9].*")) {
            return String.format("_%s", replaced);
        } else {
            return replaced;
        }
    }

}
