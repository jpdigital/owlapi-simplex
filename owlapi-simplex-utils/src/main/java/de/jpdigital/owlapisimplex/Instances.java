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
 * An Utility class for working with OWL individuals.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Instances {

    /**
     * The ontology to use.
     */
    private final OWLOntology ontology;

    /**
     * The ontology manager for interacting the ontology.
     */
    private final OWLOntologyManager ontologyManager;

    /**
     * Reasoner for the ontology.
     */
    private final OWLReasoner reasoner;

    /**
     * Creates a new instance.
     *
     * @param ontology        The ontology to use.
     * @param ontologyManager The ontology manager.
     * @param reasoner        The reasoner.
     */
    private Instances(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

    /**
     * Factory method for generating new intances of {
     *
     * @ocde DataProperties}.
     *
     * @param ontology        The ontology to use.
     * @param ontologyManager The ontology manager.
     * @param reasoner        The reasoner.
     *
     * @return A {@code DataProperties} instance.
     */
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

    /**
     * Find all instances matching the provided DL query.
     * 
     * @param query The query to execute.
     * @return All indviduals matching the query.
     */
    public List<OWLNamedIndividual> getInstances(final String query) {
        return instances(query).collect(Collectors.toList());
    }

    /**
     * Get all instances with the provided IRI.
     * 
     * @param iri The IRI.
     * @return A stream with all matching individuals.
     */
    public Stream<OWLNamedIndividual> instances(final IRI iri) {
        return instances(iri.getShortForm());
    }

    /**
     * Get all instances with the provided IRI.
     * 
     * @param iri The IRI.
     * @return A list with all matching individuals.
     */
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
