package de.tarent.octopus.config;

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
 * Klasse zur Kapselung der Informaionen zu einem ContentWorker.
 * 
 * @see de.tarent.octopus.content.TcContentWorker
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class ContentWorkerDeclaration {

    String workerName;
    String implementationSource;
    String factory;
    boolean singletonInstantiation = true;


    /**
     * Gibt an ob von dem Worker immer neue Instanzen erzeugt werden sollen,
     * oder immer die gleiche verwendet wird. Default ist singletonInstantiation = true
     */
    public boolean isSingletonInstantiation() {
        return singletonInstantiation;
    }

    public void setSingletonInstantiation(final boolean newSingletonInstantiation) {
        this.singletonInstantiation = newSingletonInstantiation;
    }


    /**
     * Liefert die Factory, von der dieser Worker erzeugt werden kann.
     * @return Voll qualifizierter Klassenname der Factory
     */
    public String getFactory() {
        return factory;
    }

    public void setFactory(final String newFactory) {
        this.factory = newFactory;
    }


    /** 
     * Liefert die Factoryspezifische Quelle des Workers.
     * Dies kann z.B. der Klassenname oder Scriptname des Workers sein.
     */
    public String getImplementationSource() {
        return implementationSource;
    }

    public void setImplementationSource(final String newImplementationSource) {
        this.implementationSource = newImplementationSource;
    }



    /**
     * Eindeutiger Bezeichner, unter dem den Worker in dem Modul verfügbar ist.
     */
    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(final String newWorkerName) {
        this.workerName = newWorkerName;
    }
    
}
