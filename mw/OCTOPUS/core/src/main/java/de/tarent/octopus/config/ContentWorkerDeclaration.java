package de.tarent.octopus.config;
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
