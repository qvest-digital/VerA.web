package de.tarent.octopus.config;
import java.io.File;

/**
 * This interface return the absolute path for the given octopus
 * module, dependent of the surrounded container.
 */
public interface TcModuleLookup {
	public final static String PREF_NAME_REAL_PATH = "realPath";

	public File getModulePath(String module);
}
