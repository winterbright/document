/*
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eredlab.g4.ccl.net.ftp.parser;

/**
 * This class encapsulates all errors that may be thrown by
 * the process of an FTPFileEntryParserFactory creating and
 * instantiating an FTPFileEntryParser.
 */
public class ParserInitializationException extends RuntimeException {

    /**
     * Root exception that caused this to be thrown
     */
    private final Throwable rootCause;

    /**
     * Constucts a ParserInitializationException with just a message
     *
     * @param message Exception message
     */
    public ParserInitializationException(String message) {
        super(message);
        this.rootCause = null;
    }

    /**
     * Constucts a ParserInitializationException with a message
     * and a root cause.
     *
     * @param message   Exception message
     * @param rootCause root cause throwable that caused
     * this to be thrown
     */
    public ParserInitializationException(String message, Throwable rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    /**
     * returns the root cause of this exception or null
     * if no root cause was specified.
     *
     * @return the root cause of this exception being thrown
     */
    public Throwable getRootCause() {
        return this.rootCause;
    }

}
