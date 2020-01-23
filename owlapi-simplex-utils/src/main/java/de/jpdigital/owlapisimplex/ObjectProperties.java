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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ObjectProperties {

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

    private final OWLReasoner reasoner;

    private ObjectProperties(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
    }

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
