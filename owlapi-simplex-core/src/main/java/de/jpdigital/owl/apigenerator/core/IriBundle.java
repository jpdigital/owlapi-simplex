/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owl.apigenerator.core;

import org.semanticweb.owlapi.model.IRI;

import java.nio.file.Path;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
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
