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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link OwlApiSimplexUtilsBuilder} which loads the 
 * ontology document from the class path.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OwlApiSimplexUtilsFromResourcesBuilder
    extends OwlApiSimplexUtilsBuilder {

    /**
     * The paths of the resources to load.
     */
    private final List<String> resourcePaths;

    public OwlApiSimplexUtilsFromResourcesBuilder(
        final List<String> resourcePaths
    ) {
        this.resourcePaths = resourcePaths;
    }

    /**
     * Load the ontology documents from the class path.
     *
     * @return A list of the loaded ontologies.
     *
     * @throws OwlApiSimplexException If an error ocurrs, for example if one of
     *                                the provided resources is not readable.
     */
    @Override
    public List<OWLOntology> loadOntologies() throws OwlApiSimplexException {
        final List<OWLOntology> ontologies = new ArrayList<>();
        for (final String resourcePath : resourcePaths) {
            try ( InputStream inputStream = getClass().getResourceAsStream(
                resourcePath
            )) {
                final OWLOntology ontology = loadOntology(inputStream);
                ontologies.add(ontology);
            } catch (IOException | OWLOntologyCreationException ex) {
                throw new OwlApiSimplexException(
                    String.format(
                        "Failed to load ontology from OWL file resource %s",
                        resourcePath
                    ),
                    ex
                );
            }
        }
        
        return ontologies;
    }

}
