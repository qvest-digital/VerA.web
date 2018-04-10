package de.tarent.commons.fileformats;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Fabian Köster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class FileFormatList
{
	List fileFormats;

	public FileFormatList(List pFileFormats)
	{
		fileFormats = pFileFormats;
	}

	public FileFormatList(FileFormat pFileFormat)
	{
		List single = new ArrayList();
		single.add(pFileFormat);
		new FileFormatList(single);
	}

	public List getFileFormats()
	{
		return fileFormats;
	}
}
