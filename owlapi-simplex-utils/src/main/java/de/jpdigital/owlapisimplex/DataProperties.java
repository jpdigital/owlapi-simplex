/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owlapisimplex;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DataProperties {

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

    private final OWLReasoner reasoner;

    private DataProperties(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

    public static DataProperties buildDataProperties(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(ontologyManager);
        Objects.requireNonNull(reasoner);

        return new DataProperties(ontology, ontologyManager, reasoner);
    }

   
    /**
     * Get the values of an data property for an individual as {@link Stream}.
     *
     * @param fromIndividual The individual from which the values are read.
     * @param propertyIri    The IRI of the property which values are read.
     *
     * @return A {@link Stream} of the values of the data property.
     */
    public Stream<OWLLiteral> dataPropertyValues(
        final OWLNamedIndividual fromIndividual,
        final IRI propertyIri
    ) {
        final OWLDataProperty property = ontologyManager
            .getOWLDataFactory()
            .getOWLDataProperty(propertyIri);

        return reasoner.dataPropertyValues(fromIndividual, property);
    }

    /**
     * Get the values of an data property for an individual as {@link List}.
     *
     * @param fromIndividual The individual from which the values are read.
     * @param propertyIri    The IRI of the property which values are read.
     *
     * @return A {@link List} of the values of the data property.
     */
    public List<OWLLiteral> getDataPropertyValues(
        final OWLNamedIndividual fromIndividual,
        final IRI propertyIri) {

        return dataPropertyValues(fromIndividual, propertyIri)
            .collect(Collectors.toList());
    }

}
