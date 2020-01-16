/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.jpdigital.owl.apigenerator.core;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class IriConstantsGenerationFailedExpection extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>IriConstantsGenerationFailedExpection</code> without detail message.
     */
    public IriConstantsGenerationFailedExpection() {
        super();
    }


    /**
     * Constructs an instance of <code>IriConstantsGenerationFailedExpection</code> with the specified detail message.
     *
     * @param msg The detail message.
     */
    public IriConstantsGenerationFailedExpection(final String msg) {
        super(msg);
    }

    /**
      * Constructs an instance of <code>IriConstantsGenerationFailedExpection</code> which wraps the 
      * specified exception.
      *
      * @param exception The exception to wrap.
      */
    public IriConstantsGenerationFailedExpection(final Exception exception) {
        super(exception);
    }

    /**
      * Constructs an instance of <code>IriConstantsGenerationFailedExpection</code> with the specified message which also wraps the 
      * specified exception.
      *
      * @param msg The detail message.
      * @param exception The exception to wrap.
      */
    public IriConstantsGenerationFailedExpection(final String msg, final Exception exception) {
        super(msg, exception);
    }
}
