/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owl.apigenerator.core;

import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class IriConstantsGenerator {

    private static final Logger LOGGER = LogManager.getLogger(IriConstantsGenerator.class);
    
    private final OntologyOwlApi ontologyOwlApi;

    private final Path outputDir;

    private IriConstantsGenerator(
        final OntologyOwlApi ontologyOwlApi, final Path outputDir
    ) {
        this.ontologyOwlApi = ontologyOwlApi;
        this.outputDir = outputDir;
    }

    public static IriConstantsGenerator buildIriConstantsGenerator(
        final OntologyOwlApi ontologyOwlApi,
        final Path outputDir
    ) {
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

        Objects.requireNonNull(ontologyOwlApi, "ontologyOwlApi can't be null");

        return new IriConstantsGenerator(ontologyOwlApi, outputDir);
    }

    public void generateClassIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
                .getOntology()
                .classesInSignature(Imports.INCLUDED)
                .map(owlClass -> (HasIRI) owlClass), 
            OwlEntityType.CLASS
        );
        
//        final Set<IRI> iriSet = ontologyOwlApi
//            .getOntology()
//            .classesInSignature(Imports.INCLUDED)
//            .map(owlClass -> owlClass.getIRI())
//            .filter(
//                iri -> !iri.toString().startsWith(
//                    "http://www.w3.org/2002/07/owl"
//                )
//            )
//            .collect(Collectors.toSet());
//        
//        final Map<String, IriBundle> classIris = new HashMap<>();
//        
//        for (final IRI iri : iriSet) {
//            final String namespace = iri.getNamespace();
//            final IriBundle iriBundle;
//            if (classIris.containsKey(namespace)) {
//                iriBundle = classIris.get(namespace);
//            } else {
//                final IriBundleBuilder builder = new IriBundleBuilder(
//                    iri, OwlEntityType.CLASS
//                );
//                iriBundle = builder.build();
//                classIris.put(namespace, iriBundle);
//            }
//            
//            iriBundle.addIri(iri);
//        }
//        
//        for (final IriBundle iriBundle : classIris.values()) {
//            writeConstantsFile(iriBundle);
//        }
        
//        final Map<String, Set<IRI>> classIris = new HashMap<>();
//        for (final IRI iri : iriSet) {
//            final String namespace = generatePackageName(iri);
//            final Set<IRI> irisInNamespace;
//            if (classIris.containsKey(namespace)) {
//                irisInNamespace = classIris.get(namespace);
//            } else {
//                irisInNamespace = new HashSet<>();
//                classIris.put(namespace, irisInNamespace);
//            }
//
//            irisInNamespace.add(iri);
//        }
//
//        for (final Map.Entry<String, Set<IRI>> entry : classIris.entrySet()) {
//            writeConstantsFile(OwlEntityType.CLASS, entry);
//        }
    }
    
    public void generateObjectPropertyIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
            .getOntology()
            .objectPropertiesInSignature(Imports.INCLUDED)
            .map(objProp -> (HasIRI) objProp), 
            OwlEntityType.OBJECT_PROPERTY
        );
    }
    
     public void generateDataPropertyIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
            .getOntology()
            .dataPropertiesInSignature(Imports.INCLUDED)
            .map(dataProp -> (HasIRI) dataProp), 
            OwlEntityType.DATA_PROPERTY
        );
    }
     
      public void generateIndividualPropertyIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
            .getOntology()
            .individualsInSignature(Imports.INCLUDED)
            .map(individual -> (HasIRI) individual), 
            OwlEntityType.INDIVIDUAL
        );
    }
      
       public void generateAnnotationIriConstants()
        throws IriConstantsGenerationFailedExpection {
        generateIriConstants(
            ontologyOwlApi
            .getOntology()
            .annotationPropertiesInSignature(Imports.INCLUDED)
            .map(annotation -> (HasIRI) annotation), 
            OwlEntityType.ANNOTIATION_PROPERTY
        );
    }
       
       
    
    private void generateIriConstants(
        final Stream<HasIRI> hasIris, final OwlEntityType entityType
    ) 
        throws IriConstantsGenerationFailedExpection {
        
        final Set<IRI> iriSet = hasIris
            .map(hasIri -> hasIri.getIRI())
            .filter(
            iri -> !iri.toString().startsWith(
                "http://www.w3.org/"
            )
        )
        .collect(Collectors.toSet());
        
        final Map<String, IriBundle> iriBundles = new HashMap<>();
        for (final IRI iri : iriSet)  {
            final String namespace = iri.getNamespace();
            final IriBundle iriBundle;
            if (iriBundles.containsKey(namespace)) {
                iriBundle = iriBundles.get(namespace);
            } else {
                final IriBundleBuilder builder = new IriBundleBuilder(
                    iri, entityType
                );
                iriBundle = builder.build();
                iriBundles.put(namespace, iriBundle);
            }
            
            iriBundle.addIri(iri);
        }
        
        for (final IriBundle iriBundle : iriBundles.values()) {
            writeConstantsFile(iriBundle);
        }
    }

    private void writeConstantsFile(final IriBundle iriBundle) 
    throws IriConstantsGenerationFailedExpection {
        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("license", "");
        dataModel.put("package", iriBundle.getPackageName());
        dataModel.put("baseIri", iriBundle.getNamespace());
        dataModel.put("className", iriBundle.getClassName());
        dataModel.put(
            "iris",
            iriBundle.getIris()
                .stream()
                .map(iri -> generateIriConstant(iri))
                .collect(Collectors.toList())
        );
        
        final TemplateService templateService = TemplateService
            .getTemplateService();
        final String result = templateService.processTemplate(
            "Iris.java.ftl", dataModel
        );
        
        final Path packageDir = outputDir.resolve(iriBundle.getPackagePath());
        
        final Path classFile = packageDir.resolve(
            String.format("%s.java", iriBundle.getClassName())
        );

        try {
            Files.createDirectories(packageDir);
            Files.write(classFile, result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

    }
    
  
//    private String generateNamespace(final IRI fromIri) {
//        final String iriNamespace = fromIri.getNamespace();
//        final String iriScheme = fromIri.getScheme();
//        
//        if (iriNamespace == null) {
//            return "";
//        }
//        
//        if (iriScheme == null || iriScheme.isEmpty()) {
//            return iriNamespace;
//        }
//        
//        if (iriNamespace.length() < iriScheme.length() + 3) {
//            return iriNamespace;
//        }
//        
//        return iriNamespace
//            .substring(iriScheme.length() + 3)
//            .replace('/', '.')
//            .replace("#", "");
//    }
    
  
    
   
    
    private String generateClassName(
        final String packageName, final OwlEntityType entityType
    )
        throws IriConstantsGenerationFailedExpection {
        final String suffix;
        switch (entityType) {
            case ANNOTATION_VALUE: 
                suffix =  "AnnotationValues";
                break;
            case ANNOTIATION_PROPERTY:
                suffix = "AnnotationProperties";
                break;
            case CLASS:
                suffix = "OwlClasses";
                break;
            case DATA_PROPERTY:
                suffix = "DataProperties";
                break;
            case INDIVIDUAL:
                suffix = "Individuals";
                break;
            case OBJECT_PROPERTY:
                suffix = "ObjectProperties";
                break;
            default:
                throw new IriConstantsGenerationFailedExpection(
                    String.format("Unknown entityType \"%s\".", entityType)
                );
        }
        
        final int lastDotIndex = packageName.lastIndexOf('.');
        return WordUtils.capitalize(
            String.format(
                "%s%s", 
                packageName.substring(lastDotIndex + 1), suffix
            ), 
            '-', '.')
            .replace("-", "");
    }

    private Map<String, String> generateIriConstant(final IRI iri) {
        final Map<String, String> constant = new HashMap<>();

        final String iriString = iri.getIRIString();

//        final String constantName = iriString
//            .replace("://", "_")
//            .replace('/', '_')
//            .replace('.', '_')
//            .replace('-', '_')
//            .replace('#', '_')
//            .toUpperCase(Locale.ROOT);
        final String constantName = ensureCamelCase(
            iri.getFragment().replace("-", ""))
            //.replace("([A-Z])([A-Z]*)([A-Z])", "$1$2$3")
            .replaceAll("(.)([\\p{Lu}])", "$1_$2")
            .toUpperCase(Locale.ROOT)
        ;

        constant.put("constantName", constantName);
        constant.put("value", iriString);

        return constant;
    }
    
    private String ensureCamelCase(final String name) {
        
        final Matcher matcher = Pattern
            .compile("([A-Z])([A-Z]*)([A-Z])")
            .matcher(name);
        
            int last  =0 ;
            final StringBuilder builder = new StringBuilder();
            while(matcher.find()) {
                builder.append(name.substring(last, matcher.start()));
                builder.append(matcher.group(1));
                builder.append(matcher.group(2).toLowerCase(Locale.ROOT));
                builder.append(matcher.group(3));
                last = matcher.end();
            }
            builder.append(name.substring(last));
            return builder.toString();
        
    }

    
}
