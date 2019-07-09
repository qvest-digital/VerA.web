package de.tarent.commons.fileformats;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

/**
 * @author niko
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileFormatAdapter implements FileFormat {
    private String m_sKey;
    private String m_sShortName;
    private String m_sLongName;
    private String m_sDescription;
    private boolean m_bCanLoad = true;
    private boolean m_bCanSave = true;
    private boolean m_bTemplateFormat = false;
    private boolean m_bXMLFormat = false;

    private ImageIcon m_oIcon = null;
    private List m_oSuffixes;

    // -----------------------------------

    /**
     * Erzeugt ein OfficeFileFormat
     *
     * @param key         - der Schlüssel über den auf das FileFormat zugegriffen werden kann
     * @param shortname   - eine Kurzbezeichnung des Dateiformats
     * @param longname    - die vollständige Bezeichnung des Dateiformats
     * @param description - eine genaue Erklärung des Dateiformats
     * @param icon        - ein Icon für das Dateiformat
     * @param suffixes    - ein String oder ein String[] mit de(r/n) Dateiendung(en)
     */
    public FileFormatAdapter(String key, String shortname, String longname, String description, Object icon, Object suffixes) {
        this(key, shortname, longname, description, icon, suffixes, true, true);
    }

    /**
     * Erzeugt ein OfficeFileFormat
     *
     * @param key         - der Schlüssel über den auf das FileFormat zugegriffen werden kann
     * @param shortname   - eine Kurzbezeichnung des Dateiformats
     * @param longname    - die vollständige Bezeichnung des Dateiformats
     * @param description - eine genaue Erklärung des Dateiformats
     * @param icon        - ein Icon für das Dateiformat
     * @param suffixes    - ein String oder ein String[] mit de(r/n) Dateiendung(en)
     * @param canLoad     - gibt an ob die API dieses Format lesen kann
     * @param canSave     - gibt an ob die API dieses Format schreiben kann
     */
    public FileFormatAdapter(String key, String shortname, String longname, String description, Object icon, Object suffixes,
      boolean canLoad, boolean canSave) {
        m_sKey = key;
        m_sShortName = shortname;
        m_sLongName = longname;
        m_sDescription = description;
        m_bCanLoad = canLoad;
        m_bCanSave = canSave;

        //    if (icon instanceof ImageIcon)
        //    {
        //      m_oIcon = (ImageIcon)icon;
        //    }
        //    else if (icon instanceof String)
        //    {
        //      m_oIcon = loadIcon((String)icon);
        //    }
        //    else m_oIcon = null;

        m_oSuffixes = new ArrayList();
        if (suffixes != null) {
            if (suffixes instanceof String[]) {
                for (int i = 0; i < (((String[]) suffixes).length); i++) {
                    addSuffix(((String[]) suffixes)[i]);
                }
            } else if (suffixes instanceof String) {
                addSuffix((String) suffixes);
            }
        }
    }

    // -----------------------------------

    public String getKey() {
        return m_sKey;
    }

    public void setKey(String key) {
        m_sKey = key;
    }

    public String getDescription() {
        return m_sDescription;
    }

    public void setDescription(String description) {
        m_sDescription = description;
    }

    public ImageIcon getIcon() {
        return m_oIcon;
    }

    public void setIcon(ImageIcon icon) {
        m_oIcon = icon;
    }

    public String getLongName() {
        return m_sLongName;
    }

    public void setLongName(String longName) {
        m_sLongName = longName;
    }

    public String getShortName() {
        return m_sShortName;
    }

    public void setShortName(String shortName) {
        m_sShortName = shortName;
    }

    public String getSuffix(int index) {
        return (String) (m_oSuffixes.get(index));
    }

    public void addSuffix(String suffix) {
        m_oSuffixes.add(suffix);
    }

    public int getNumberOfSuffixes() {
        return m_oSuffixes.size();
    }

    public boolean endsWithSuffix(String filename) {
        Iterator it = m_oSuffixes.iterator();
        while (it.hasNext()) {
            String suffix = (String) (it.next());
            if (filename.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private class OfficeFileFilter extends FileFilter {
        public boolean accept(File f) {
            for (int i = 0; i < (getNumberOfSuffixes()); i++) {
                String suffix = getSuffix(i).toUpperCase();
                if (f.getName().toUpperCase().endsWith(suffix)) {
                    return true;
                }
            }

            return false;
        }

        public String getDescription() {
            return FileFormatAdapter.this.getDescription();
        }
    }

    public FileFilter getFileFilter() {
        return new OfficeFileFilter();
    }

    public boolean canLoad() {
        return m_bCanLoad;
    }

    public boolean canSave() {
        return m_bCanSave;
    }

    public void setCanLoad(boolean canLoad) {
        m_bCanLoad = canLoad;
    }

    public void setCanSave(boolean canSave) {
        m_bCanSave = canSave;
    }

    public void setTemplateFormat(boolean pTemplate) {
        m_bTemplateFormat = pTemplate;
    }

    public boolean isTemplateFormat() {
        return m_bTemplateFormat;
    }

    public void setXMLFormat(boolean pXMLFormat) {
        m_bXMLFormat = pXMLFormat;
    }

    public boolean isXMLFormat() {
        return m_bXMLFormat;
    }
}
