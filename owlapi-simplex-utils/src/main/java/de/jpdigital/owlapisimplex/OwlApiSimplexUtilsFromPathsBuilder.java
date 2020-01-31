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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the ontology document of an ontology from the file system.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OwlApiSimplexUtilsFromPathsBuilder
    extends OwlApiSimplexUtilsBuilder {

    /**
     * The paths to the ontology documents to load. These files must be in the
     * correct order so that the OWL API can resolve all imports correctly.
     */
    private final List<Path> paths;

    /**
     * Creates new instance using the provided list of paths.
     *
     * @param paths
     */
    public OwlApiSimplexUtilsFromPathsBuilder(final List<Path> paths) {
        this.paths = paths;
    }

    /**
     * Load the ontology documents from the file system.
     *
     * @return A list of the loaded ontologies.
     *
     * @throws OwlApiSimplexException If an error ocurrs, for example if one of
     *                                the provided paths is not readable or not
     *                                a file.
     */
    @Override
    protected List<OWLOntology> loadOntologies() throws OwlApiSimplexException {

        final List<OWLOntology> ontologies = new ArrayList<>();
        for (final Path path : paths) {
            if (!Files.exists(path)) {
                throw new OwlApiSimplexException(
                    String.format("File %s does not exist.", path.toString())
                );
            }
            if (!Files.isReadable(path)) {
                throw new OwlApiSimplexException(
                    String.format("File %s is not readable.", path.toString())
                );
            }

            try ( InputStream inputStream = Files.newInputStream(path)) {
                final OWLOntology ontology = loadOntology(inputStream);
                ontologies.add(ontology);
            } catch (OWLOntologyCreationException | IOException ex) {
                throw new OwlApiSimplexException(
                    String.format(
                        "Failed to load ontology from OWL file %s",
                        path.toString()
                    ),
                    ex
                );
            }
        }

        return ontologies;
    }

}
