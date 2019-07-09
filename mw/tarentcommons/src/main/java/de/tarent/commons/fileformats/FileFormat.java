package de.tarent.commons.fileformats;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

/**
 * @author Fabian Köster (f.koester@tarent.de), tarent GmbH Bonn
 */
public interface FileFormat {
    public String getKey();

    public void setKey(String pKey);

    public String getDescription();

    public void setDescription(String description);

    public ImageIcon getIcon();

    public void setIcon(ImageIcon icon);

    public String getLongName();

    public void setLongName(String longName);

    public String getShortName();

    public void setShortName(String shortName);

    public String getSuffix(int index);

    public void addSuffix(String suffix);

    public boolean endsWithSuffix(String filename);

    public int getNumberOfSuffixes();

    public FileFilter getFileFilter();

    public boolean canLoad();

    public boolean canSave();

    public void setCanLoad(boolean canLoad);

    public void setCanSave(boolean canSave);

    public boolean isTemplateFormat();

    public boolean isXMLFormat();

    public void setTemplateFormat(boolean pTemplate);

    public void setXMLFormat(boolean pXML);
}
