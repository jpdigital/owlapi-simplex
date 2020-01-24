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
package de.jpdigital.owlapisimplex.maven;

import de.jpdigital.owl.apigenerator.core.IriConstantsGenerationFailedExpection;
import de.jpdigital.owl.apigenerator.core.IriConstantsGenerator;
import de.jpdigital.owl.apigenerator.core.OntologyLoadingException;
import de.jpdigital.owl.apigenerator.core.OntologyOwlApi;
import de.jpdigital.owl.apigenerator.core.RepositoryGenerator;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mojo invoking the code generators provided by owlapi-simplex-core with the
 * parameters set in the {@code pom.xml}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Mojo(
    name = "gen-api",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    threadSafe = true
)
public class OwlApiSimplexMojo extends AbstractMojo {

    /**
     * The OWL files to use. They must be provided in the correct order so that
     * imports in the OWL files can be resolved. The path are relative to the
     * resource directory.
     */
    @Parameter(required = true)
    private String[] owlFiles;

    /**
     * Location of the output directory, relative to the project build
     * directory.
     *
     * Default value is {@code ${project.build.directory}/generated-sources}.
     */
    @Parameter(
        defaultValue = "${project.build.directory}/generated-sources",
        property = "outputDir",
        required = true
    )
    private File outputDirectory;

    /**
     * Provides access to the Maven project itself.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private transient MavenProject project;

    /**
     * Processes the parameters set in the {@code pom.xml} and calls the code
     * generators.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException   If an errors occurs.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File outputDir = this.outputDirectory;

        getLog().info(String.format("Generating API for ontology files:"));
        Arrays.stream(owlFiles).forEach(
            file -> getLog().info(String.format("\t%s", file))
        );

        //Check if the output directory exists
        if (!outputDir.exists()) {
            final boolean result = outputDir.mkdirs();
            if (!result) {
                throw new MojoFailureException(
                    "Failed to create output directory."
                );
            }
        }

        final Build build = project.getBuild();

        final List<Resource> resources = build.getResources();
        final List<Path> owlFilePaths = new ArrayList<>();
        for (final String owlFile : owlFiles) {
            owlFilePaths.add(findOntologyPath(owlFile, resources));
        }

        final OntologyOwlApi ontologyOwlApi;
        try {
            ontologyOwlApi = OntologyOwlApi.loadOntologies(owlFilePaths);
        } catch (OntologyLoadingException ex) {
            throw new MojoFailureException(
                "Failed to load ontology files.", ex
            );
        }

        final IriConstantsGenerator iriConstantsGenerator
                                        = IriConstantsGenerator
                .buildIriConstantsGenerator(ontologyOwlApi, outputDir.toPath());

        try {
            iriConstantsGenerator.generateClassIriConstants();
            iriConstantsGenerator.generateObjectPropertyIriConstants();
            iriConstantsGenerator.generateDataPropertyIriConstants();
            iriConstantsGenerator.generateIndividualPropertyIriConstants();
            iriConstantsGenerator.generateAnnotationIriConstants();
        } catch (IriConstantsGenerationFailedExpection ex) {
            throw new MojoFailureException(
                "Error while generating IRI constants.", ex
            );
        }
        
        final RepositoryGenerator repositoryGenerator = RepositoryGenerator
            .buildRepositoryGenerator(ontologyOwlApi, outputDir.toPath());
        repositoryGenerator.generateRepositoryClasses();

        project.addCompileSourceRoot(outputDir.getAbsolutePath());
    }

    /**
     * Helper method for finding the OWL files in the resource directories. The
     * method checks all resource directory for the presence of the provided OWL
     * file. If the file is not found in any of these directories a
     * {@link MojoFailureException} is thrown.
     *
     * @param owlFilePath The path of the OWL to look for.
     * @param resources   The resource directories of the project.
     *
     * @return The absolute path to the OWL file.
     *
     * @throws MojoFailureException If the OWL file is not found in one of the
     *                              resource directories.
     */
    private Path findOntologyPath(
        final String owlFilePath, final List<Resource> resources
    ) throws MojoFailureException {
        final Path ontologyPath = resources
            .stream()
            .map(resource -> Paths.get(resource.getDirectory()))
            .map(path -> path.resolve(owlFilePath))
            .filter(path -> Files.isRegularFile(path))
            .findAny()
            .orElseThrow(
                () -> new MojoFailureException(
                    String.format(
                        "OWL file %s not found in any of the resource "
                            + "directories.",
                        owlFilePath
                    )
                )
            );

        if (!Files.isReadable(ontologyPath)) {
            throw new MojoFailureException(
                String.format(
                    "OWL file %s (absolute path: %s) is not readable.",
                    owlFilePath,
                    ontologyPath.toString()
                )
            );
        }

        return ontologyPath;
    }

}
