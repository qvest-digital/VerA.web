package de.tarent.commons.plugin;
import java.util.List;

public abstract class PluginAdapter implements Plugin {

    public PluginAdapter() {

    }

    public abstract String getID();

    public abstract void init();

    public abstract Object getImplementationFor(Class type);

    public abstract List getSupportedTypes();

    public abstract boolean isTypeSupported(Class type);
}
