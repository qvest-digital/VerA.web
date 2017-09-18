package de.tarent.octopus.server;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * A Closeable is a Resource, which may be closed if not needed any longer.
 * This interace ist semanticaly the same as {@link java.io.Closeable} introduced with JDK 1.5.
 * <br><br>
 * Within the octopus, this interface may be used to safely close resources after the processing of a request.
 * This is done by a List of Closeable-Object within the OctopusContext. After the generation of the 
 * octopus response, the close method is called for each object in this list. This will be done even if the request processing will throw an exception.
 * Use the {@link OctopusContext#addCleanupCode(Closeable)} to add an Object to the list of closeable objects.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public interface Closeable {
    
    /**                                         
     * Closes the resource.
     * @throws Exception This method may throw any exception, but should not rely on an proper handling by the calling code.
     */
    public void close();
}