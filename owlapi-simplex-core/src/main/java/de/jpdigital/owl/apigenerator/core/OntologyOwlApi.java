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

import openllet.owlapi.OpenlletReasonerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides several methods for working with OWL ontologies required
 * by the code generators.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OntologyOwlApi {

    private static final Logger LOGGER = LogManager.getLogger(
        OntologyOwlApi.class
    );

    /**
     * The ontology
     */
    private final OWLOntology ontology;

    /**
     * The {@link OWLOntologyManager} used to access the ontology.
     */
    private final OWLOntologyManager ontologyManager;

    /**
     * A reasoner for the ontology.
     */
    private final OWLReasoner reasoner;

    /**
     * Creates a new {@code OntologyOwlApi} instance.
     *
     * @param ontology        The ontology to use.
     * @param ontologyManager The ontology manager for accessing the ontology.
     * @param reasoner        A reasoner for the ontology.
     */
    public OntologyOwlApi(final OWLOntology ontology,
                          final OWLOntologyManager ontologyManager,
                          final OWLReasoner reasoner) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

    /**
     * Load the provided ontologies.
     *
     * @param ontologyFiles The OWL files to load. The files must be in the
     *                      correct order so that imports in the OWL files can
     *                      be resolved.
     *
     * @return A new {@link OntologyOwlApi} instance.
     *
     * @throws OntologyLoadingException
     */
    public static OntologyOwlApi loadOntologies(final String[] ontologyFiles)
        throws OntologyLoadingException {

        final List<Path> ontologyFilePaths = Arrays
            .asList(ontologyFiles)
            .stream()
            .map(path -> Paths.get(path))
            .collect(Collectors.toList());

        return loadOntologies(ontologyFilePaths);
    }

    /**
     *Load the provided ontologies.
     *
     * @param ontologyFiles The OWL files to load. The files must be in the
     *                      correct order so that imports in the OWL files can
     *                      be resolved.
     *
     * @return A new {@link OntologyOwlApi} instance.
     *
     * @throws OntologyLoadingException
     */
    public static OntologyOwlApi loadOntologies(
        final List<Path> ontologyFiles
    ) throws OntologyLoadingException {
        LOGGER.info(
            "Trying to load ontologies from these paths: {}...",
            ontologyFiles
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList())
        );
        for (final Path path : ontologyFiles) {
            LOGGER.info(
                "Trying to load ontology from path {}...",
                path.toAbsolutePath().toString()
            );
            if (!Files.exists(path)) {
                throw new OntologyLoadingException(
                    String.format(
                        "Ontology file %s does not exist.",
                        path.toAbsolutePath().toString()
                    )
                );
            }

            if (!Files.isReadable(path)) {
                throw new OntologyLoadingException(
                    String.format(
                        "Ontology file %s is not readable.",
                        path.toAbsolutePath().toString()
                    )
                );
            }
        }

        final OWLOntologyManager ontologyManager = OWLManager
            .createConcurrentOWLOntologyManager();

        final List<OWLOntology> ontologies = new ArrayList<>();
        for (final Path file : ontologyFiles) {
            final OWLOntology ontology;
            try ( InputStream inputStream = Files.newInputStream(file)) {

                ontology = ontologyManager.loadOntologyFromOntologyDocument(
                    Objects.requireNonNull(
                        inputStream,
                        String.format(
                            "Failed to load ontology file %s.",
                            file.toAbsolutePath().toString()
                        )
                    )
                );

            } catch (IOException | OWLOntologyCreationException ex) {
                throw new OntologyLoadingException(ex);
            }

            ontologies.add(ontology);
        }

        final OWLOntology ontology = ontologies.get(ontologies.size() - 1);
        final OWLReasonerFactory reasonerFactory = new OpenlletReasonerFactory();
        final OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        return new OntologyOwlApi(ontology, ontologyManager, reasoner);
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    /**
     * Get all classes from the ontology.
     * @return A list of the OWL classes.
     */
    public List<OWLClass> getAllClasses() {

        return ontology
            .classesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    /**
     * Get all object properties from the ontology
     * 
     * @return A list of all object properties.
     */
    public List<OWLObjectProperty> getAllObjectProperties() {
        return ontology
            .objectPropertiesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    /**
     * Get all data properties from the ontology
     * 
     * @return A list of all data properties.
     */
    public List<OWLDataProperty> getAllDataProperties() {
        return ontology
            .dataPropertiesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    /**
     * Get all individuals from the ontology
     * 
     * @return A list of all individuals.
     */
    public List<OWLNamedIndividual> getAllIndividuals() {
        return ontology
            .individualsInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    /**
     * Get all annotation properties from the ontology
     * 
     * @return A list of all anootation properties.
     */
    public List<OWLAnnotationProperty> getAllAnnotationProperties() {
        return ontology
            .annotationPropertiesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

}
