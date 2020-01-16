/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owl.apigenerator.core;

import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class IriBundleBuilder {
    
    private static final Logger LOGGER = LogManager.getLogger(
        IriBundleBuilder.class
    );

    private final IRI iri;
    private final OwlEntityType owlEntityType;

    IriBundleBuilder(final IRI iri, final OwlEntityType owlEntityType) {
        this.iri = iri;
        this.owlEntityType = owlEntityType;
    }
    
    IriBundle build() throws IriConstantsGenerationFailedExpection {
        
        final String namespace = iri.getNamespace();
        final String packageName = generatePackageName().toLowerCase(
            Locale.ROOT
        );
        final Path packagePath = generatePackagePath(packageName);
        final String className = generateClassName(generatePackageName());
        
        return new IriBundle(namespace, packageName, packagePath, className);
    }
    
    private String generatePackageName() {
        LOGGER.warn("Generating package name from IRI {}...", iri.toString());
        
        final String scheme = iri.getScheme();
        final String namespace;
        if (scheme == null || scheme.isEmpty()) {
            namespace = iri.getNamespace();
        } else {
            namespace = iri.getNamespace().substring(scheme.length() + 3);
        }
        
        final int domainIndexEnd = namespace.indexOf('/');
        final String domain = namespace.substring(0, domainIndexEnd);
        final String path;
        if (namespace.endsWith("#")) {
            path = namespace.substring(domainIndexEnd + 1,
                                       namespace.length() - 1);
        } else {
            path = namespace.substring(domainIndexEnd + 1);
        }
        
        final List<String> tokens = new ArrayList<>(
            Arrays.asList(domain.split("\\."))
        );
        Collections.reverse(tokens);
        tokens.addAll(Arrays.asList(path.split("/")));
        
        final String packageName = tokens
            .stream()
            .map(token -> WordUtils.capitalize(token, '-'))
            .map(token -> WordUtils.uncapitalize(token))
            .map(token -> token.replace("-", ""))
            .map(token -> avoidNumericBegin(token))
//            .map(token -> token.toLowerCase(Locale.ROOT))
            .collect(Collectors.joining("."));
        LOGGER.warn(
            "Generated package name '{}' from IRI '{}'.", 
            packageName,
            iri.toString()
        );
        return packageName;
    }
    
    private Path generatePackagePath(final String packageName) {
        return Paths.get(packageName.replace('.', '/'));
    }
    
    private String generateClassName(final String packageName)
        throws IriConstantsGenerationFailedExpection {
        final String suffix;
        switch (owlEntityType) {
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
                    String.format("Unknown entityType \"%s\".", owlEntityType)
                );
        }
        
        final int lastDotIndex = packageName.lastIndexOf('.');
        return WordUtils.capitalize(
            String.format(
                "%s%s", packageName.substring(lastDotIndex + 1), suffix
            ), 
            '-', '.')
            .replace("-", "");
    }
    
     private String avoidNumericBegin(final String value) {
        if (value.matches("^[0-9].*")) {
            return String.format("_%s",  value);
        } else {
            return value;
        }
    }
}
