/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owlapisimplex.maven;

import de.jpdigital.owl.apigenerator.core.IriConstantsGenerationFailedExpection;
import de.jpdigital.owl.apigenerator.core.IriConstantsGenerator;
import de.jpdigital.owl.apigenerator.core.OntologyLoadingException;
import de.jpdigital.owl.apigenerator.core.OntologyOwlApi;
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
     * imports in the OWL files can be resolved.
     */
    @Parameter(required = true)
    private String[] owlFiles;

    /**
     * Location of the output directory
     */
    @Parameter(
        defaultValue = "${project.build.directory}/generated-sources",
        property = "outputDir",
        required = true
    )
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true)
    private transient MavenProject project;

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
        for(final String owlFile : owlFiles) {
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
        
        project.addCompileSourceRoot(outputDir.getAbsolutePath());
    }

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
