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

import java.nio.file.Path;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A helper class which stores the informations about the IRIs in a specific
 * namespace. 
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class IriBundle {

    /**
     * The namespace of the bundle
     */
    private final String namespace;

    /**
     * Name of the package
     */
    private final String packageName;

    /*
     * Relative path of the package directory
     */
    private final Path packagePath;

    /*
     * Class Name (Last token of the
     */
    private final String className;

    private final SortedSet<IRI> iris;

    IriBundle(
        final String namespace,
        final String packageName,
        final Path packagePath,
        final String className
    ) {
        this.namespace = namespace;
        this.packageName = packageName;
        this.packagePath = packagePath;
        this.className = className;
        iris = new TreeSet<>(
            (iri1, iri2) -> iri1.toString().compareTo(iri2.toString())
        );
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPackageName() {
        return packageName;
    }

    public Path getPackagePath() {
        return packagePath;
    }

    public String getClassName() {
        return className;
    }

    public SortedSet<IRI> getIris() {
        return Collections.unmodifiableSortedSet(iris);
    }

    public void addIri(final IRI iri) {
        iris.add(iri);
    }

}
