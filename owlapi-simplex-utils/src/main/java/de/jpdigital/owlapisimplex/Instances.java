/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owlapisimplex;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Instances {

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

    private final OWLReasoner reasoner;

    private Instances(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

    public static Instances buildInstances(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(ontologyManager);
        Objects.requireNonNull(reasoner);

        return new Instances(ontology, ontologyManager, reasoner);
    }

    /**
     * Retrieves the instances of an OWL class.
     *
     * @param query The query describing the OWL class.
     *
     * @return All instances matching the DL query.
     */
    public Stream<OWLNamedIndividual> instances(final String query) {

        final ShortFormProvider shortFormProvider
                                    = new SimpleShortFormProvider();
        final Set<OWLOntology> importsClosure = ontology
            .importsClosure()
            .collect(Collectors.toSet());

        final BidirectionalShortFormProvider bidiShortformProvider
                                                 = new BidirectionalShortFormProviderAdapter(
                ontologyManager,
                importsClosure,
                shortFormProvider);

        final ManchesterOWLSyntaxParser parser = OWLManager
            .createManchesterParser();
        parser.setDefaultOntology(ontology);
        parser.setOWLEntityChecker(
            new ShortFormEntityChecker(bidiShortformProvider));

        final OWLClassExpression queryExpression = parser
            .parseClassExpression(query);

        return reasoner
            .getInstances(queryExpression, false)
            .entities()
            .sorted(this::sortResults);
    }
    
    public List<OWLNamedIndividual> getInstances(final String query) {
        return instances(query).collect(Collectors.toList());
    }
    
    public Stream<OWLNamedIndividual> instances(final IRI iri) {
        return instances(iri.getShortForm());
    }
    
    public List<OWLNamedIndividual> getInstances(final IRI iri) {
        return instances(iri).collect(Collectors.toList());
    }

    /**
     * Helper method for sorting {@link OWLNamedIndividual}s by their IRI.
     *
     * @param result1
     * @param result2
     *
     * @return
     */
    private int sortResults(final OWLNamedIndividual result1,
                            final OWLNamedIndividual result2) {

        return result1
            .getIRI()
            .toString()
            .compareTo(result2.getIRI().toString());
    }

}
