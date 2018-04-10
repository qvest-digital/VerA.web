package de.tarent.octopus.content;

import de.tarent.octopus.config.*;
import java.util.HashMap;

public class AnnotationWorkerFactoryTest
    extends junit.framework.TestCase {

    TcModuleConfig config;
    AnnotationWorkerFactory factory;
    ContentWorkerDeclaration workerDeclaration;

    /**
     *  Void Constructor for instantiation as worker
     */
    public AnnotationWorkerFactoryTest() {
    }

    public AnnotationWorkerFactoryTest(String init) {
        super(init);
    }

    public void setUp() {
        config = TcModuleConfig.createMockupModuleConfig("/tmp", new HashMap());
        factory = new AnnotationWorkerFactory();
        workerDeclaration = new ContentWorkerDeclaration();

    }

    public void testCreation()
        throws Exception {
        workerDeclaration.setImplementationSource(getClass().getName());

        TcContentWorker worker = factory.createInstance(getClass().getClassLoader(), workerDeclaration);
        Object workerDelegate = ((DelegatingWorker)worker).getWorkerDelegate();
        assertEquals("Worker is instance of the class.", workerDelegate.getClass(), getClass());
    }

    public void testErrorHandling_noSource()
        throws Exception {

        try {
            factory.createInstance(getClass().getClassLoader(), workerDeclaration);
        } catch (Exception e) {
            // Success
            return;
        }

        fail("No exception on worker creation with missing source");
    }

    public void testErrorHandling_wrongSource()
        throws Exception {

        try {
            workerDeclaration.setImplementationSource("xxx.yyy.zzzz");
            factory.createInstance(getClass().getClassLoader(), workerDeclaration);
        } catch (Exception e) {
            // Success
            return;
        }

        fail("No exception on worker creation with wrong source");
    }
}
