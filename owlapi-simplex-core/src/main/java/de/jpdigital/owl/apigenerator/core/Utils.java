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
public class Utils {

    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    private Utils() {
        // Nothing
    }

    /**
     * Helper method for generating a valid Java package name from the namespace
     * IRI.
     *
     * @param fromIri The IRI to convert to a Java package name.
     *
     * @return A valid package name for the namespace IRI.
     */
    public static String generatePackageName(final IRI fromIri) {
        return generatePackageName(fromIri, true);
    }

    public static String generatePackageName(
        final IRI fromIri, final boolean lowerCase
    ) {
        LOGGER.info(
            "Generating package name from IRI {}...", fromIri.toString()
        );

        final String scheme = fromIri.getScheme();
        final String namespace;
        if (scheme == null || scheme.isEmpty()) {
            namespace = fromIri.getNamespace();
        } else {
            namespace = fromIri.getNamespace().substring(scheme.length() + 3);
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
        tokens.addAll(
            Arrays
                .stream(path.split("/"))
                .map(token -> token.replace('.', '-'))
                .collect(Collectors.toList())
        );

        final String packageName = tokens
            .stream()
            .map(token -> WordUtils.capitalize(token, '-'))
            .map(token -> WordUtils.uncapitalize(token))
            .map(token -> token.replace("-", ""))
            .map(token -> token.replace('.', '_'))
            .map(token -> avoidNumericBegin(token))
            .collect(Collectors.joining("."));
        LOGGER.info(
            "Generated package name '{}' from IRI '{}'.",
            packageName,
            fromIri.toString()
        );
        if (lowerCase) {
            return packageName.toLowerCase(Locale.ROOT);
        } else {
            return packageName;
        }
    }

    /**
     * Returns the path for the package.
     *
     * @param packageName The name of the package.
     *
     * @return The path of the package.
     */
    public static Path generatePackagePath(final String packageName) {
        return Paths.get(packageName.replace('.', '/'));
    }

    /**
     * Helper method for avoiding a numeric first character in package or class
     * names.
     *
     * @param value The value to validate.
     *
     * @return If the provided value does not start with a number the value is
     *         return unchanged. If it starts with a number a underscore is
     *         added as first character.
     */
    private static String avoidNumericBegin(final String value) {
        if (value.matches("^[0-9].*")) {
            return String.format("_%s", value);
        } else {
            return value;
        }
    }

}
