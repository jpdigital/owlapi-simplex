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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OntologyOwlApi {

    private static final Logger LOGGER = LogManager.getLogger(
        OntologyOwlApi.class
    );

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

    private final OWLReasoner reasoner;

    public OntologyOwlApi(final OWLOntology ontology,
                          final OWLOntologyManager ontologyManager,
                          final OWLReasoner reasoner) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

    public static OntologyOwlApi loadOntologies(final String[] ontologyFiles)
        throws OntologyLoadingException {

        final List<Path> ontologyFilePaths = Arrays
            .asList(ontologyFiles)
            .stream()
            .map(path -> Paths.get(path))
            .collect(Collectors.toList());

        return loadOntologies(ontologyFilePaths);
    }

    public static OntologyOwlApi loadOntologies(
        final List<Path> ontologyFiles
    ) throws OntologyLoadingException {
        LOGGER.error(
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

    public List<OWLClass> getAllClasses() {

        return ontology
            .classesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    public List<OWLObjectProperty> getAllObjectProperties() {
        return ontology
            .objectPropertiesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    public List<OWLDataProperty> getAllDataProperties() {
        return ontology
            .dataPropertiesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    public List<OWLNamedIndividual> getAllIndividuals() {
        return ontology
            .individualsInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

    public List<OWLAnnotationProperty> getAllAnnotationProperties() {
        return ontology
            .annotationPropertiesInSignature(Imports.INCLUDED)
            .collect(Collectors.toList());
    }

}
