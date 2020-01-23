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

/**
 * Thrown if an unexpected error occurs.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UnexpectedErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>UnexpectedErrorException</code> without
     * detail message.
     */
    public UnexpectedErrorException() {
        super();
    }

    /**
     * Constructs an instance of <code>UnexpectedErrorException</code> with the
     * specified detail message.
     *
     * @param msg The detail message.
     */
    public UnexpectedErrorException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>UnexpectedErrorException</code> which
     * wraps the specified exception.
     *
     * @param exception The exception to wrap.
     */
    public UnexpectedErrorException(final Exception exception) {
        super(exception);
    }

    /**
     * Constructs an instance of <code>UnexpectedErrorException</code> with the
     * specified message which also wraps the specified exception.
     *
     * @param msg       The detail message.
     * @param exception The exception to wrap.
     */
    public UnexpectedErrorException(final String msg, final Exception exception) {
        super(msg, exception);
    }

}
