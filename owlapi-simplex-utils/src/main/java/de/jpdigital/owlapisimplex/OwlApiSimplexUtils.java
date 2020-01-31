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

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * A helper class for owlapi-simplex-utils. 
 * 
 * Used to pass the necessary instances of classes from the OWL API to 
 * other classes. Also provides factory methods for some classes provided
 * by owlapi-simplex-utils.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OwlApiSimplexUtils {

    /**
     * The ontology to use.
     */
    private final OWLOntology ontology;

    /**
     * Ontology manager to use.
     */
    private final OWLOntologyManager ontologyManager;

    /**
     * A reasoner for the ontology.
     */
    private final OWLReasoner reasoner;

    OwlApiSimplexUtils(
        final OWLOntology ontology,
        final OWLOntologyManager ontologyManager,
        final OWLReasoner reasoner
    ) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        this.reasoner = reasoner;
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
     * Builds a new instance of {@link DataProperties} using the OWL API objects
     * passed to the {@code OwlApiSimplexUtils} instance.
     * 
     * @return A new instance of {@link DataProperties}.
     */
    public DataProperties buildDataProperties() {
        return DataProperties.buildDataProperties(ontologyManager, reasoner);
    }

    /**
     * Builds a new instance of {@link Instances} using the OWL API objects
     * passed to the {@code OwlApiSimplexUtils} instance.
     * 
     * @return A new instance of {@link Instances}.
     */
    public Instances buildInstances() {
        return Instances.buildInstances(ontology, ontologyManager, reasoner);
    }

    /**
     * Builds a new instance of {@link ObjectProperties} using the OWL API objects
     * passed to the {@code OwlApiSimplexUtils} instance.
     * 
     * @return A new instance of {@link ObjectProperties}.
     */
    public ObjectProperties buildObjectProperties() {
        return ObjectProperties.buildObjectProperties(
            ontologyManager, reasoner
        );
    }

}
