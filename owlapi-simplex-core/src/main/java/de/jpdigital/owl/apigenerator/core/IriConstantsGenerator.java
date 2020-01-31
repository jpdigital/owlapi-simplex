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

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Generates constants for the IRIs of OWL entities in an ontology.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class IriConstantsGenerator {

    private static final String CONSTANT_NAME = "constantName";

    /**
     * The ontology to use.
     */
    private final OntologyOwlApi ontologyOwlApi;

    /**
     * The directory in which the generated classes are stored.
     */
    private final Path outputDir;

    /**
     * Creates a new {@code IriConstantsGenerator}
     *
     * @param ontologyOwlApi The ontology to use.
     * @param outputDir      The output directory.
     */
    private IriConstantsGenerator(
        final OntologyOwlApi ontologyOwlApi, final Path outputDir
    ) {
        this.ontologyOwlApi = ontologyOwlApi;
        this.outputDir = outputDir;
    }

    /**
     * Factory method creating a new {@code IriConstantsGenerator} instance.
     *
     * The provided parameters are checked. If an parameter is invalid an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param ontologyOwlApi The ontology to use.
     * @param outputDir      The output directory.
     *
     * @return An {@code IriConstantsGenerator}.
     */
    public static IriConstantsGenerator buildIriConstantsGenerator(
        final OntologyOwlApi ontologyOwlApi,
        final Path outputDir
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

        return new IriConstantsGenerator(ontologyOwlApi, outputDir);
    }

    /**
     * Generates constants for the {@link IRI}s of a OWL class entities in the
     * ontology.
     *
     * @throws IriConstantsGenerationFailedExpection
     */
    public void generateClassIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
                .getOntology()
                .classesInSignature(Imports.INCLUDED)
                .map(owlClass -> (HasIRI) owlClass),
            OwlEntityType.CLASS
        );

    }

    /**
     * Generates constants for the {@link IRI}s of a OWL object property
     * entities in the ontology.
     *
     * @throws IriConstantsGenerationFailedExpection
     */
    public void generateObjectPropertyIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
                .getOntology()
                .objectPropertiesInSignature(Imports.INCLUDED)
                .map(objProp -> (HasIRI) objProp),
            OwlEntityType.OBJECT_PROPERTY
        );
    }

    /**
     * Generates constants for the {@link IRI}s of a OWL data property entities
     * in the ontology.
     *
     * @throws IriConstantsGenerationFailedExpection
     */
    public void generateDataPropertyIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
                .getOntology()
                .dataPropertiesInSignature(Imports.INCLUDED)
                .map(dataProp -> (HasIRI) dataProp),
            OwlEntityType.DATA_PROPERTY
        );
    }

    /**
     * Generates constants for the {@link IRI}s of a OWL individual entities in
     * the ontology.
     *
     * @throws IriConstantsGenerationFailedExpection
     */
    public void generateIndividualPropertyIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
                .getOntology()
                .individualsInSignature(Imports.INCLUDED)
                .map(individual -> (HasIRI) individual),
            OwlEntityType.INDIVIDUAL
        );
    }

    public void generateAnnotationIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
                .getOntology()
                .annotationPropertiesInSignature(Imports.INCLUDED)
                .map(annotation -> (HasIRI) annotation),
            OwlEntityType.ANNOTIATION_PROPERTY
        );
    }

    /**
     * Generates the IRI constants for the provided OWL entities.
     *
     * @param hasIris    The OWL entities.
     * @param entityType The type of the OWL entities.
     *
     * @throws IriConstantsGenerationFailedExpection If an error occurs.
     */
    private void generateIriConstants(
        final Stream<HasIRI> hasIris, final OwlEntityType entityType
    )
        throws IriConstantsGenerationFailedExpection {

        final Set<IRI> iriSet = hasIris
            .map(hasIri -> hasIri.getIRI())
            .filter(
                iri -> !iri.toString().startsWith(
                    "http://www.w3.org/"
                )
            )
            .collect(Collectors.toSet());

        final Map<String, IriBundle> iriBundles = new HashMap<>();
        for (final IRI iri : iriSet) {
            final String namespace = iri.getNamespace();
            final IriBundle iriBundle;
            if (iriBundles.containsKey(namespace)) {
                iriBundle = iriBundles.get(namespace);
            } else {
                final IriBundleBuilder builder = new IriBundleBuilder(
                    iri, entityType
                );
                iriBundle = builder.build();
                iriBundles.put(namespace, iriBundle);
            }

            iriBundle.addIri(iri);
        }

        for (final IriBundle iriBundle : iriBundles.values()) {
            writeConstantsFile(iriBundle);
        }
    }

    /**
     * Helper method for writing the contants files.
     */
    private void writeConstantsFile(final IriBundle iriBundle)
        throws IriConstantsGenerationFailedExpection {
        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("license", "");
        dataModel.put("package", iriBundle.getPackageName());
        dataModel.put("baseIri", iriBundle.getNamespace());
        dataModel.put("className", iriBundle.getClassName());
        final List<Map<String, String>> constants = iriBundle
            .getIris()
            .stream()
            .map(iri -> generateIriConstant(iri))
            .collect(Collectors.toList());
        preventDuplicateConstantNames(constants);
        dataModel.put("iris", constants);

        final TemplateService templateService = TemplateService
            .getTemplateService();
        final String result = templateService.processTemplate(
            "Iris.java.ftl", dataModel
        );

        final Path packageDir = outputDir.resolve(iriBundle.getPackagePath());

        final Path classFile = packageDir.resolve(
            String.format("%s.java", iriBundle.getClassName())
        );

        try {
            Files.createDirectories(packageDir);
            Files.write(classFile, result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

    }

    /**
     * Helper method for generating an valid Java name for an IRI.
     *
     * @param iri The iri.
     *
     * @return A valid Java name for the constant.
     */
    private Map<String, String> generateIriConstant(final IRI iri) {
        final Map<String, String> constant = new HashMap<>();

        final String iriString = iri.getIRIString();

        final String constantName = ensureCamelCase(
            iri.getFragment().replace("-", ""))
            .replaceAll("(.)([\\p{Lu}])", "$1_$2")
            .toUpperCase(Locale.ROOT);

        constant.put(CONSTANT_NAME, constantName);
        constant.put("value", iriString);

        return constant;
    }

    /**
     * Helper method for ensuring that a name is camel case.
     *
     * @param name The name to check.
     *
     * @return The name in camel case (without two uppercase letters following
     *         each other).
     */
    private String ensureCamelCase(final String name) {

        final Matcher matcher = Pattern
            .compile("([A-Z])([A-Z]*)([A-Z])")
            .matcher(name);

        int last = 0;
        final StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(name.substring(last, matcher.start()));
            builder.append(matcher.group(1));
            builder.append(matcher.group(2).toLowerCase(Locale.ROOT));
            builder.append(matcher.group(3));
            last = matcher.end();
        }
        builder.append(name.substring(last));
        return builder.toString();
    }
    
    private void preventDuplicateConstantNames(
        final List<Map<String, String>> constants
    ) {
        final Map<String, Integer> nameCount = new HashMap<>();
        for (final Map<String, String> constant : constants) {
            if (nameCount.containsKey(constant.get(CONSTANT_NAME))) {
                final int count = nameCount.get(constant.get(CONSTANT_NAME)) + 1;
                nameCount.put(constant.get(CONSTANT_NAME), count);
                final String constantName = constant.get(CONSTANT_NAME);
                constant.put(
                    CONSTANT_NAME, String.format("%s%d", constantName, count)
                );
            } else {
                nameCount.put(constant.get(CONSTANT_NAME), 1);
            }
        }
    }

}
