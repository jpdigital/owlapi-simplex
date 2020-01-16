package de.jpdigital.owl.apigenerator.cli;

import de.jpdigital.owl.apigenerator.core.IriConstantsGenerationFailedExpection;
import de.jpdigital.owl.apigenerator.core.IriConstantsGenerator;
import de.jpdigital.owl.apigenerator.core.OntologyOwlApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
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

    @Parameters(
        index = "0",
        description = "The output directory. The generated sources will be "
                          + "placed here."
    )
    private Path outputDirPath;

    @Parameters(
        index = "1..*",
        description = "The ontology(ies) for which the API is generated. The "
                          + "files are loaded in the order they are provided. "
                          + "The last ontology is used as source and must "
                          + "import all other ontologies."
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

        final IriConstantsGenerator iriConstantsGenerator
                                        = IriConstantsGenerator
                .buildIriConstantsGenerator(ontologyOwlApi, outputDirPath);

        try {
            iriConstantsGenerator.generateClassIriConstants();
            iriConstantsGenerator.generateObjectPropertyIriConstants();
            iriConstantsGenerator.generateDataPropertyIriConstants();
            iriConstantsGenerator.generateIndividualPropertyIriConstants();
            iriConstantsGenerator.generateAnnotationIriConstants();
        } catch (IriConstantsGenerationFailedExpection ex) {
            LOGGER.error("Error while generating IRI constants.", ex);
            return -1;
        }

        return 0;
    }

}
