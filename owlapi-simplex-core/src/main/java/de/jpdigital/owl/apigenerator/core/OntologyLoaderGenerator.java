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
package de.jpdigital.owl.apigenerator.core;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Generatates an implementation of the {@link OwlApiSimplexUtilsBuilder} for
 * the ontology files provided to the owlapi-simplex-generator.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OntologyLoaderGenerator {

    /**
     * The ontology to use
     */
    private final OntologyOwlApi ontologyOwlApi;

    /**
     * The directory in which the gnerated packages and classes are stored.
     */
    private final Path outputDir;

    /**
     * Where should the generated classes load the OWL files from?
     */
    private final OwlFileSource fileSource;
    
    /**
     * Resource paths for the OWL files to load. Only required if 
     * {@link #fileSource} is {@link OwlFileSource#CLASS_PATH}.
     */
    private List<String> resourcePaths;

    private OntologyLoaderGenerator(
        final OntologyOwlApi ontologyOwlApi,
        final Path outputDir,
        final OwlFileSource fileSource
    ) {
        this.ontologyOwlApi = ontologyOwlApi;
        this.outputDir = outputDir;
        this.fileSource = fileSource;
    }
    
    private OntologyLoaderGenerator(
        final OntologyOwlApi ontologyOwlApi,
        final Path outputDir,
        final OwlFileSource fileSource,
        final List<String> resourcePaths
    ) {
        this.ontologyOwlApi = ontologyOwlApi;
        this.outputDir = outputDir;
        this.fileSource = fileSource;
        this.resourcePaths = resourcePaths;
    }

    public static OntologyLoaderGenerator buildDirectoryOntologyLoaderGenerator(
        final OntologyOwlApi ontologyOwlApi, final Path outputDir
    ) {
        validateOutputDir(outputDir);
        Objects.requireNonNull(ontologyOwlApi, "ontologyOwlApi can't be null");

        return new OntologyLoaderGenerator(
            ontologyOwlApi, outputDir, OwlFileSource.DIRECTORY
        );
    }
    
    public static OntologyLoaderGenerator buildClassPathOntologyLoaderGenerator(
        final OntologyOwlApi ontologyOwlApi, 
        final Path outputDir, 
        final List<String> resourcePaths
    ) {
        validateOutputDir(outputDir);
        Objects.requireNonNull(ontologyOwlApi, "ontologyOwlApi can't be null");
        Objects.requireNonNull(resourcePaths, "resourcePaths can't be null");
        if (resourcePaths.isEmpty()) {
            throw new IllegalArgumentException("resourcePath can't be empty.");
        }
        
        return new OntologyLoaderGenerator(
            ontologyOwlApi, outputDir, OwlFileSource.CLASS_PATH, resourcePaths
        );
    }

    private static  void validateOutputDir(final Path outputDir) {
         if (!Files.isDirectory(outputDir)) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided path \"%s\" is not a directory.",
                    outputDir.toString()
                )
            );
        }

        if (!Files.isWritable(outputDir)) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided output directory \"%s\" is not writable.",
                    outputDir.toString()
                )
            );
        }
    }
    
    public void generateOntologyLoader() 
        throws OntologyLoaderGenerationFailedException {
        final String license = "";

        final OWLOntology ontology = ontologyOwlApi.getOntology();
        final IRI ontologyIri = getOntologyIri(ontology);
        final String packageName = generatePackageName(ontologyIri);

        final String className = "OntologyLoader";

        final String baseClass;
        switch(fileSource) {
            case CLASS_PATH:
                baseClass = "de.jpdigital.owlapisimplex.OwlApiSimplexUtilsFromResourcesBuilder";
                break;
            case DIRECTORY:
                baseClass = "de.jpdigital.owlapisimplex.OwlApiSimplexUtilsFromPathsBuilder";
                break;
            default:
                throw new UnexpectedErrorException(
                    String.format(
                        "Unexpected value \"%s\" for fileSource.", 
                        fileSource.toString()
                    )
                );
        }

        final List<String> ontologyIris = ontologyOwlApi
            .getLoadedOntologies()
            .stream()
            .map(this::getOntologyIri)
            .map(IRI::toString)
            .collect(Collectors.toList());
        
        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("license", license);
        dataModel.put("package", packageName);
        dataModel.put("className", className);
        dataModel.put("baseClass", baseClass);
        dataModel.put("ontologyIris", ontologyIris);
        dataModel.put("ontologySources", resourcePaths);
        
        final Path packagePath = Utils.generatePackagePath(packageName);
        
        final TemplateService templateService = TemplateService
            .getTemplateService();
        final String result = templateService.processTemplate(
            "OntologyLoader.java.ftl", dataModel
        );
        
        final Path packageDir = outputDir.resolve(packagePath);
        final Path classFile = packageDir.resolve(
            String.format("%s.java", className)
        );
        
        try {
            Files.createDirectories(packageDir);
            Files.write(classFile, result.getBytes(StandardCharsets.UTF_8));
        } catch(IOException ex) {
            throw new OntologyLoaderGenerationFailedException(ex);
        }
    }

    private IRI getOntologyIri(final OWLOntology ontology) {
        final OWLOntologyID ontologyId = ontology.getOntologyID();
        final Optional<IRI> ontologyIri = ontologyId.getOntologyIRI();
        if (ontologyIri.isPresent()) {
            return ontologyIri.get();
        } else {
            final Optional<IRI> versionIri = ontologyId.getVersionIRI();
            if (versionIri.isPresent()) {
                return versionIri.get();
            } else {
                throw new UnexpectedErrorException(
                    "The provided ontology neither has an IRI or an version "
                        + "IRI. Unable to generate an OntologyLoader for this "
                        + "ontology."
                );
            }
        }
    }
    
    private String generatePackageName(final IRI ontologyIri) {
        final String packageName = Utils.generatePackageName(ontologyIri);
        
        if (packageName.endsWith(".")) {
            return packageName.substring(0, packageName.length() - 1);
        } else {
            return packageName;
        }
    }

}
