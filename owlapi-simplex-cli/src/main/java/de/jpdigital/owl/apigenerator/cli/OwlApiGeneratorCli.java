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
package de.jpdigital.owl.apigenerator.cli;

import de.jpdigital.owl.apigenerator.core.IriConstantsGenerationFailedExpection;
import de.jpdigital.owl.apigenerator.core.IriConstantsGenerator;
import de.jpdigital.owl.apigenerator.core.OntologyLoaderGenerationFailedException;
import de.jpdigital.owl.apigenerator.core.OntologyLoaderGenerator;
import de.jpdigital.owl.apigenerator.core.OntologyOwlApi;
import de.jpdigital.owl.apigenerator.core.RepositoryGenerationFailedException;
import de.jpdigital.owl.apigenerator.core.RepositoryGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * CLI interface for the OWL API Generator
 *
 * Call java -jar OwlApiGeneratorCli outputdir inputfile1 inputfile2 ...
 *
 */
@CommandLine.Command(
    name = "OwlApiGeneratorCli",
    mixinStandardHelpOptions = true,
    version = "1.0.0-SNAPSHOT",
    description = "Generates an ontology specific API for an OWL ontology"
)
public class OwlApiGeneratorCli implements Callable<Integer> {

    private static final Logger LOGGER = LogManager.getFormatterLogger(
        OwlApiGeneratorCli.class
    );

    @Option(
        names = "--no-class-iris",
        negatable = true,
        defaultValue = "true",
        description = "Generate IRI constants for classses?"
    )
    private boolean generateIriConstantsForClasses;

    @Option(
        names = "--no-obj-props-iris",
        negatable = true,
        defaultValue = "true",
        description = "Generate IRI constants for object properties?"
    )
    private boolean generateIriConstantsForObjectProperties;

    @Option(
        names = "--no-data-props-iris",
        negatable = true,
        defaultValue = "true",
        description = "Generate IRI constants for data properties?"
    )
    private boolean generateIriConstantsForDataProperties;

    @Option(
        names = "--no-individual-iris",
        negatable = true,
        defaultValue = "true",
        description = "Generate IRI constants for individuals?"
    )
    private boolean generateIriConstantsForIndividuals;

    @Option(
        names = "--no-annotation-props-iris",
        negatable = true,
        defaultValue = "true",
        description = "Generate IRI constants for annotation properties?"
    )
    private boolean generateIriConstantsForAnnotationProperties;

    @Option(
        names = "--no-repositories",
        negatable = true,
        defaultValue = "true",
        description = "Generate repostories?"
    )
    private boolean generateRepositories;

    @Option(
        names = "--no-loader",
        negatable = true,
        defaultValue = "true",
        description = "Generate OntologyLoader?"
    )
    private boolean generateOntologyLoader;

    /**
     * Path for storing the generated sources.
     */
    @Parameters(
        index = "0",
        description = "The output directory. The generated sources will be placed here."
    )
    private Path outputDirPath;

    /**
     * The paths of the OWL files to load.
     */
    @Parameters(
        index = "1..*",
        description = "The ontology(ies) for which the API is generated. The files are loaded in the order they are provided. The last ontology is used as source and must import all other ontologies."
    )
    private List<Path> ontologyPaths;

    public static void main(final String[] args) {
        int exitCode = new CommandLine(new OwlApiGeneratorCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {

        LOGGER.info("Loading ontology files...");
        LOGGER.debug("Output directory path: %s", outputDirPath);
        LOGGER.debug(
            "Ontology paths: %s",
            ontologyPaths
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList())
        );
        final OntologyOwlApi ontologyOwlApi = OntologyOwlApi.loadOntologies(
            ontologyPaths
        );

        LOGGER.info("Generating IRI constants...");
        final IriConstantsGenerator iriConstantsGenerator
                                        = IriConstantsGenerator
                .buildIriConstantsGenerator(ontologyOwlApi, outputDirPath);
        try {
            if (generateIriConstantsForClasses) {
                iriConstantsGenerator.generateClassIriConstants();
            }
            if (generateIriConstantsForObjectProperties) {
                iriConstantsGenerator.generateObjectPropertyIriConstants();
            }
            if (generateIriConstantsForDataProperties) {
                iriConstantsGenerator.generateDataPropertyIriConstants();
            }
            if (generateIriConstantsForIndividuals) {
                iriConstantsGenerator.generateIndividualPropertyIriConstants();
            }
            if (generateIriConstantsForAnnotationProperties) {
                iriConstantsGenerator.generateAnnotationIriConstants();
            }
        } catch (IriConstantsGenerationFailedExpection ex) {
            LOGGER.error("Error while generating IRI constants: ", ex);
            return -1;
        }

        if (generateRepositories) {
            LOGGER.info("Generating repositories...");
            final RepositoryGenerator repositoryGenerator = RepositoryGenerator
                .buildRepositoryGenerator(ontologyOwlApi, outputDirPath);
            try {
                repositoryGenerator.generateRepositoryClasses();
            } catch (RepositoryGenerationFailedException ex) {
                LOGGER.error("Failed to generate repositories: ", ex);
            }
        }

        if (generateOntologyLoader) {
            LOGGER.info("Generating ontology loader...");
            final OntologyLoaderGenerator ontologyLoaderGenerator
                                              = OntologyLoaderGenerator
                    .buildDirectoryOntologyLoaderGenerator(
                        ontologyOwlApi, outputDirPath
                    );
            try {
                ontologyLoaderGenerator.generateOntologyLoader();
            } catch (OntologyLoaderGenerationFailedException ex) {
                LOGGER.error("Failed to generate OntologyLoaders: ", ex);
            }
        }

        return 0;
    }

}
