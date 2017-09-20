package de.tarent.octopus.extensions;

/*-
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
 * This implements a simple extension framework for
 * the Octopus application server. An extension has to be
 * implement this interface and can then be initialized 
 * and called without relying on reflections. 
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public interface OctopusExtension
{
    /**
     * Initializes the extension. Runtime parameters are
     * encoded in the parameter.
     * 
     * @param param Runtime parameters for this extension.
     */
    public void initialize(Object param);
    
    /**
     * Starts the extension. This should be called on
     * startup of regular server operation.
     */
    public void start();
    
    /**
     * Stops the extension. This should be called before
     * the server shuts down.
     */
    public void stop();
}
