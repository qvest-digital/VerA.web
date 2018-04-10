/**
 *
 */
package de.tarent.commons.fileformats.office;

import de.tarent.commons.fileformats.FileFormatAdapter;

/**
 * @author Fabian Köster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class OpenDocumentTextFileFormat extends FileFormatAdapter
{
	public OpenDocumentTextFileFormat()
	{
		super("FILETYPE_ODT", "ODT", "OpenDocumentText", "Das OpenDocument-Textdokument-Format", "odt.png", "odt");
	}
}
