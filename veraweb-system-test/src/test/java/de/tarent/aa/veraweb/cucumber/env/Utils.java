package de.tarent.aa.veraweb.cucumber.env;

import gherkin.formatter.model.Feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.thoughtworks.selenium.Selenium;

/**
 * Utility class.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public class Utils {
    
    public static DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
    public static SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat DEFAULT_DATE_WITH_TIME_FORMATTER = new SimpleDateFormat("dd.MM.yyyy H:mm");
    

    /**
     * Generate output path.
     * 
     * @param rootDir
     *            path of root directory
     * @param curFeature
     *            current feature
     * @param currentCaseCount
     *            current case count
     * @param currentStepCount
     *            current step count
     * @return generated path
     */
    public static String generateOutputPath(String rootDir, Feature curFeature, int currentCaseCount,
        int currentStepCount) {
        // <rootDir>/<feature-name>/case_<caseNr.>_step_<stepNr.>
        String path = rootDir;

        path += curFeature.getName() + "/case_" + currentCaseCount + "_step_" + currentStepCount;

        return path;
    }

    /**
     * Delete directory.
     * 
     * @param path
     *            path
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
                }
            }
        }
        dir.delete();
    }

    /**
     * Create directory.
     * 
     * @param path
     *            path
     */
    public static void createDirectoryIfDoesntExist(String path) {
        String pathToCreate = path;
        if (!path.endsWith("/")) {
            pathToCreate = path.substring(0, path.lastIndexOf('/'));
        }
        new File(pathToCreate).mkdirs();
    }

    /**
     * Zip directory.
     * 
     * @param dirPath
     *            path of directory
     * @param filePath
     *            path of file
     * @throws IOException
     *             exception
     */
    public static void zipDirectory(String dirPath, String filePath) throws IOException {
        new File(filePath).delete();

        zipDirectory(new File(dirPath), new File(filePath));
    }

    /**
     * Zip directory.
     * 
     * @param directory
     *            directory
     * @param zip
     *            zip file
     * @throws IOException
     *             exception
     */
    public static final void zipDirectory(File directory, File zip) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        zip(directory, directory, zos);
        zos.close();
    }

    /**
     * Zip files.
     * 
     * @param directory
     *            directory
     * @param base
     *            base directory
     * @param zos
     *            zip outputstream
     * @throws IOException
     *             exception
     */
    private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read = 0;
        for (int i = 0, j = files.length; i < j; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zos);
            } else {
                FileInputStream in = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
                zos.putNextEntry(entry);
                while (-1 != (read = in.read(buffer))) {
                    zos.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }

    /**
     * Write base64 encoded string to file.
     * 
     * @param b64
     *            base64 encoded string
     * @param filePath
     *            path to file
     * @throws IOException
     *             exception
     */
    public static void writeB64ToFile(String b64, String filePath) throws IOException {
        byte[] decoded = Base64.decodeBase64(b64);

        writeByteArrayToFile(decoded, filePath);
    }

    /**
     * Write byte array to file.
     * 
     * @param content
     *            content as byte array
     * @param filePath
     *            path to file
     * @throws IOException
     *             exception
     */
    public static void writeByteArrayToFile(byte[] content, String filePath) throws IOException {
        FileOutputStream fw = new FileOutputStream(new File(filePath));
        fw.write(content);
        fw.close();
    }

    /**
     * Write HTML source to file.
     * 
     * @param htmlSource
     *            HTML source
     * @param filePath
     *            path oto file
     * @throws IOException
     *             exception
     */
    public static void writeHtmlFile(String htmlSource, String filePath) throws IOException {
        FileOutputStream fw = new FileOutputStream(new File(filePath));
        fw.write(htmlSource.getBytes());
        fw.close();
    }

    /**
     * Create tmp file.
     * 
     * @param selenium
     *            {@link Selenium}
     * @param directory
     *            path of directory
     * @param content
     *            content to write in tmp file
     * @param fileNamePrefix
     *            prefix of file name
     * @return path of created tmp file
     * @throws IOException
     *             exception
     */
    public static String createTempFile(Selenium selenium, String directory, String content, String fileNamePrefix)
        throws IOException {
        if (directory == null || "".equals(directory)) {
            File tmp = File.createTempFile(fileNamePrefix, ".pdf");
            tmp.deleteOnExit();

            FileOutputStream os = new FileOutputStream(tmp);
            os.write(content.getBytes());
            os.flush();
            os.close();

            return tmp.getAbsolutePath();
        } else {
            String doc = directory + "/" + Long.toHexString(System.currentTimeMillis()) + ".pdf";
            // create a testfile on the remote server. this file can be used for uploading!
            selenium.captureScreenshot(doc);

            new File(doc).deleteOnExit();
            return doc;
        }
    }

    /**
     * Format given milliseconds to readable time string.
     * 
     * @param timeInMillis
     *            time in milliseconds
     * @return formatted time string
     */
    public static String formatTime(long timeInMillis) {
        long minutes = timeInMillis / 60000;
        long seconds = (timeInMillis / 1000) % 60;
        long millis = timeInMillis % 1000;

        return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "." + millis + "s";
    }
    
    public static String formatVerawebDate(Timestamp timestamp) {
        if (isTimeInDate(timestamp)) {
            return DEFAULT_DATE_WITH_TIME_FORMATTER.format(timestamp);
        } else {
            return DEFAULT_DATE_FORMATTER.format(timestamp);
        }
    }
    
    private static boolean isTimeInDate(Date date) {
        return date != null && ((date.getTime() / 1000) % 60) == 0;
    }
}
