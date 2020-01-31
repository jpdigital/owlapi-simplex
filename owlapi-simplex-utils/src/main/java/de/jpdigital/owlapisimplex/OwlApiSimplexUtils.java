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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OwlApiSimplexUtils {

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

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

    public DataProperties buildDataProperties() {
        return DataProperties.buildDataProperties(ontologyManager, reasoner);
    }

    public Instances buildInstances() {
        return Instances.buildInstances(ontology, ontologyManager, reasoner);
    }

    public ObjectProperties buildObjectProperties() {
        return ObjectProperties.buildObjectProperties(
            ontologyManager, reasoner
        );
    }

}
