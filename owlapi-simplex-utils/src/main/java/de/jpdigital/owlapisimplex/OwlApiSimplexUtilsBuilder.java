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

import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class OwlApiSimplexUtilsBuilder {

    private final OWLOntologyManager ontologyManager;

    OwlApiSimplexUtilsBuilder() {
        ontologyManager = OWLManager.createConcurrentOWLOntologyManager();
    }

    protected OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    protected OWLOntology loadOntology(final InputStream inputStream)
        throws OWLOntologyCreationException {
        return ontologyManager.loadOntologyFromOntologyDocument(
            Objects.requireNonNull(
                inputStream,
                "Can't load an ontology form an null InputStream"
            )
        );
    }

    protected abstract List<OWLOntology> loadOntologies() throws
        OwlApiSimplexException;

    /**
     * Validate if all required ontologies are available.Intented to be
     * overwritten by subclasses the the owlapi-simplex-generator. 
     * 
     * The default implemetation does nothing.
     *
     * @param ontologies The ontologies to validate.
     *
     * @throws OwlApiSimplexException If no all required ontologies are
     *                                available.
     */
    protected void validate(final List<OWLOntology> ontologies)
        throws OwlApiSimplexException {
        // Nothing
    }

    public OwlApiSimplexUtils build() throws OwlApiSimplexException {
        final List<OWLOntology> ontologies = loadOntologies();
        validate(ontologies);
        final OWLOntology ontology = ontologies.get(ontologies.size() - 1);
        final OWLReasonerFactory reasonerFactory = new OpenlletReasonerFactory();
        final OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        return new OwlApiSimplexUtils(ontology, getOntologyManager(), reasoner);
    }

}
