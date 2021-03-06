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

import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import java.nio.file.Path;

/**
 * Builder for {@link IriBundle}s.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class IriBundleBuilder {
    
    private static final Logger LOGGER = LogManager.getLogger(
        IriBundleBuilder.class
    );

    /**
     * The IRI from which the bundle is build.
     */
    private final IRI iri;
    /**
     * The OWL entity type of the entity identified by the {@code IRI}.
     */
    private final OwlEntityType owlEntityType;

    /**
     * Creates a new {@code IRIBundleBuilder} for the provided IRI and the 
     * provided entity type.
     * 
     * @param iri The IRI to use.
     * @param owlEntityType The OWL entity type of the entity identified by the 
     * {@code IRI}.
     */
    IriBundleBuilder(final IRI iri, final OwlEntityType owlEntityType) {
        this.iri = iri;
        this.owlEntityType = owlEntityType;
    }
    
    /**
     * Creates the {@link IriBundle}.
     * 
     * @return 
     * @throws IriConstantsGenerationFailedExpection If an error occurs.
     */
    IriBundle build() throws IriConstantsGenerationFailedExpection {
        LOGGER.debug(
            "Building IriBundle for IRI \"{}\" and entity type \"{}\":",
            iri.toString(),
            owlEntityType.toString()
        );
        
        final String namespace = iri.getNamespace();
        final String packageName = Utils.generatePackageName(iri);
        final Path packagePath = Utils.generatePackagePath(packageName);
        final String className = generateClassName(
            Utils.generatePackageName(iri, false)
        );
        
        LOGGER.debug("\t namespace: {}", namespace);
        LOGGER.debug("\t packageName: {}", packageName);
        LOGGER.debug("\t packagePath: {}", packagePath.toString());
        LOGGER.debug("\t className: {}", className);
        
        return new IriBundle(namespace, packageName, packagePath, className);
    }
    
    
    /**
     * Generates the class name for the IRI bundle.
     * 
     * @param packageName The package name of the Bundle.
     * @return The class name of the bundle.
     * @throws IriConstantsGenerationFailedExpection 
     */
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
        final String className = WordUtils.capitalize(
            String.format(
                "%s%s", packageName.substring(lastDotIndex + 1), suffix
            ), 
            '-', '.')
            .replace("-", "");
        return avoidNumericBegin(className);
    }
    
    /**
     * Helper method for avoiding a numeric first character in package or class 
     * names.
     * @param value The value to validate.
     
     * @return If the provided value does not start with a number the value is
     * return unchanged. If it starts with a number a underscore is added as
     * first character.
     */
     private String avoidNumericBegin(final String value) {
        if (value.matches("^[0-9].*")) {
            return String.format("_%s",  value);
        } else {
            return value;
        }
    }
}
