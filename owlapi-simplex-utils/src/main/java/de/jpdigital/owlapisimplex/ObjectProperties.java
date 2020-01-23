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

import org.semanticweb.owlapi.model.IRI;
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
 * An Utility class for working with OWL object properties.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ObjectProperties {

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
     * @param ontology The ontology to use.
     * @param ontologyManager The ontology manager.
     * @param reasoner The reasoner.
     */
    private ObjectProperties(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

    /**
     * Factory method for generating new instances of {@code ObjectProperties}.
     * 
     @param ontology The ontology to use.
     * @param ontologyManager The ontology manager.
     * @param reasoner The reasoner.
     
     * @return A {@code DataProperties} instance.
     */
    public static ObjectProperties buildObjectProperties(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(ontologyManager);
        Objects.requireNonNull(reasoner);

        return new ObjectProperties(ontology, ontologyManager, reasoner);
    }

    /**
     * Get the values of an object property for an individual as {@link Stream}.
     *
     * @param fromIndividual The individual from which the values are read.
     * @param propertyIri    The IRI of the property which values are read.
     *
     * @return A {@link Stream} of the values of the object property.
     */
    public Stream<OWLNamedIndividual> objectPropertyValues(
        final OWLNamedIndividual fromIndividual,
        final IRI propertyIri
    ) {

        final OWLObjectProperty property = ontologyManager
            .getOWLDataFactory()
            .getOWLObjectProperty(propertyIri);

        return reasoner.objectPropertyValues(fromIndividual, property);
    }

    /**
     * Get the values of an object property for an individual as {@link List}.
     *
     * @param fromIndividual The individual from which the values are read.
     * @param propertyIri    The IRI of the property which values are read.
     *
     * @return A {@link List} of the values of the object property.
     */
    public List<OWLNamedIndividual> getObjectPropertyValues(
        final OWLNamedIndividual fromIndividual,
        final IRI propertyIri
    ) {
        return objectPropertyValues(fromIndividual, propertyIri)
            .collect(Collectors.toList());
    }

}
