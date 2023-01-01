/*
 * IOMethods.java
 * Description: Input/Output handling methods.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public Licence as published by the Free Software Foundation, either version 3 of the
 * Licence, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public Licence for more details.
 *
 * You should have received a copy of the GNU General Public Licence along with this program. If
 * not, see <https://www.gnu.org/licenses/>
 *
 * Copyright © AudiTranscribe Team
 */

package app.auditranscribe.io;

import app.auditranscribe.MainApplication;
import app.auditranscribe.system.OSMethods;
import app.auditranscribe.system.OSType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Input/Output handling methods.
 */
public final class IOMethods {
    private IOMethods() {
        // Private constructor to signal this is a utility class
    }

    // File path methods

    /**
     * Method that gets the application data directory path.
     *
     * @return The <b>absolute</b> path to the application data directory.
     */
    public static String getApplicationDataDirectory() {
        // Get the operating system
        OSType osType = OSMethods.getOS();

        // Get the user data directory based on the operating system name
        return switch (osType) {
            case WINDOWS -> IOMethods.joinPaths(
                    true,
                    System.getenv("AppData"), "AudiTranscribe"
            );
            case MAC -> IOMethods.joinPaths(
                    true,
                    IOConstants.USER_HOME_PATH, "/Library/Application Support", "AudiTranscribe"
            );
            default -> IOMethods.joinPaths(
                    true,
                    OSMethods.getOrDefault(
                            "XDG_DATA_HOME",
                            IOMethods.joinPaths(
                                    true,
                                    IOConstants.USER_HOME_PATH, "/.local/share"
                            )
                    ),
                    "AudiTranscribe"
            );
        };
    }

    // CRUD operations

    /**
     * Method that creates a file at the specified <code>path</code>.
     *
     * @param path The <b>absolute path</b> to the file.
     * @return Returns:
     * <ul>
     *     <li><code>0</code> if the file was successfully created;</li>
     *     <li><code>1</code> if the file already exists; or</li>
     *     <li><code>-1</code> if the file failed to be created (<em>and does not already
     *     exist</em>).</li>
     * </ul>
     */
    public static int createFile(String path) {
        return createFile(new File(path));
    }

    /**
     * Method that creates a file specified by a <code>File</code> object.
     *
     * @param file A <code>File</code> object that contains the absolute path to the file to create.
     * @return Returns:
     * <ul>
     *     <li><code>0</code> if the file was successfully created;</li>
     *     <li><code>1</code> if the file already exists; or</li>
     *     <li><code>-1</code> if the file failed to be created (<em>and does not already
     *     exist</em>).</li>
     * </ul>
     */
    public static int createFile(File file) {
        try {
            return file.createNewFile() ? 0 : 1;
        } catch (IOException e) {
            return -1;
        }
    }


    /**
     * Method that creates a folder, if it does not already exist.<br>
     * This method will also create any parent directories that does not already exist.
     *
     * @param folder File object representing the folder to create.
     * @return A boolean. Returns <code>true</code> if folder was created successfully, and
     * <code>false</code> otherwise.
     */
    public static boolean createFolder(File folder) {
        return createFolder(folder.getAbsolutePath());
    }

    /**
     * Method that creates a folder, if it does not already exist, at the specified
     * <code>absolutePath</code>.<br>
     * This method will also create any parent directories that does not already exist.
     *
     * @param absolutePath <b>Absolute path</b> to the folder.
     * @return A boolean. Returns <code>true</code> if folder was created successfully, and
     * <code>false</code> otherwise.
     */
    public static boolean createFolder(String absolutePath) {
        try {
            Files.createDirectory(Paths.get(absolutePath));
            return true;
        } catch (IOException e) {
            return new File(absolutePath).mkdirs();
        }
    }

    /**
     * Given a file's path (with respect to the <b>resource path</b>), will return an
     * <code>InputStream</code> for reading.
     *
     * @param filePath Path to the file, with respect to the <b>resource path</b>.
     * @return An <code>InputStream</code> object for reading.
     */
    // Todo: rename to `readAsInputStream`
    public static InputStream getInputStream(String filePath) {
        // If the file path contains backslashes, replace with forward slashes
        filePath = filePath.replaceAll("\\\\", "/");
        return MainApplication.class.getResourceAsStream(filePath);
    }

    /**
     * Given a file's path (with respect to the <b>resource path</b>), will return a string,
     * representing the contents of the file.
     *
     * @param filePath Path to the file, with respect to the <b>resource path</b>.
     * @param encoding Encoding to use (e.g. <code>UTF-8</code>).
     * @return Contents of the file as a string.
     * @throws IOException If the encoding format is not recognized, or if something went wrong when
     *                     reading the input stream.
     */
    public static String readAsString(String filePath, String encoding) throws IOException {
        return inputStreamToString(getInputStream(filePath), encoding);
    }

    /**
     * Method that deletes a file or <b>empty</b> folder given a <code>File</code> object.
     *
     * @param file The <code>File</code> object, pointing to the location to delete.
     * @return A boolean; <code>true</code> is the file or folder was deleted and
     * <code>false</code> otherwise.
     */
    public static boolean delete(File file) {
        return delete(file.getAbsolutePath());
    }

    /**
     * Method that deletes a file or <b>empty</b> folder at the specified <code>absolutePath</code>.
     *
     * @param absolutePath <b>Absolute path</b> to the file or <b>empty</b> folder.
     * @return A boolean; is <code>true</code> is the file or folder was deleted and
     * <code>false</code> otherwise.
     */
    public static boolean delete(String absolutePath) {
        try {
            return Files.deleteIfExists(Paths.get(absolutePath));
        } catch (IOException e) {
            new File(absolutePath).deleteOnExit();
            return false;
        }
    }

    // Path handling

    /**
     * Method that joins several paths together.
     *
     * @param useOSSeparator Whether to use the native OS separator to join the paths, or to use the
     *                       UNIX separator of `/` to join paths.
     * @param paths          The paths to join.
     * @return The joined path.
     */
    public static String joinPaths(boolean useOSSeparator, String... paths) {
//        StringBuilder buffer = new StringBuilder();
//        for (String path : paths) {
//            if (path == null || path.equals("")) {
//                continue;  // Empty paths can be skipped
//            }
//
//            if (buffer.length() > 0) {  // If there is something in the buffer,
//                buffer.append("/");     // append the separator first...
//            }
//            buffer.append(path);        // ...before appending the path
//        }
//
//        return buffer.toString();

        // Determine the separator character to use
        String separator = "/";
        if (useOSSeparator) separator = IOConstants.SEPARATOR;

        // Build the path
        StringBuilder buffer = new StringBuilder();
        String prevElem = null;
        for (String path : paths) {
            if (path == null || path.equals("")) {
                continue;  // Empty paths can be skipped
            }

            if (prevElem == null) {  // Handle the first element
                buffer.append(path);
            } else if (prevElem.endsWith(separator)) {  // Check if the previous path ended with the separator
                buffer.append(path.startsWith(separator) ? path.substring(separator.length()) : path);
            } else {
                if (!path.startsWith(separator)) {  // If it does not start with a separator,
                    buffer.append(separator);       // append the separator first...
                }
                buffer.append(path);                // ...before appending the element
            }

            // Update the previous path
            prevElem = path;
        }

        return buffer.toString();
    }

    /**
     * Method that joins several paths together.
     *
     * @param paths The paths to join.
     * @return The joined path.
     */
    public static String joinPaths(String... paths) {
        return joinPaths(false, paths);
    }

    // Miscellaneous methods

    /**
     * Method that converts the bytes passed by an input stream <code>in</code> to a string with the
     * specified encoding.
     *
     * @param in       Input stream.
     * @param encoding Encoding to use (e.g. <code>UTF-8</code>).
     * @return String representation of the input stream.
     * @throws IOException If the encoding format is not recognized, or if something went wrong when
     *                     reading the input stream.
     */
    public static String inputStreamToString(InputStream in, String encoding) throws IOException {
        // Define a buffer for writing the output to
        byte[] buff = new byte[8192];

        // Write input stream to the output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }

        // Convert the output stream bytes into a string by using the encoding
        return out.toString(encoding);
    }
}
