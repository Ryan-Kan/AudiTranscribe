/*
 * AUDTFileReader0x00050002.java
 * Description: Class that handles the reading of the AudiTranscribe (AUDT) file for file version
 *              0x00050002.
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

package app.auditranscribe.io.audt_file.v0x00050002;

import app.auditranscribe.io.audt_file.base.AUDTFileReader;
import app.auditranscribe.io.audt_file.base.data_encapsulators.*;
import app.auditranscribe.io.audt_file.v0x00050002.data_encapsulators.*;
import app.auditranscribe.io.exceptions.FailedToReadDataException;
import app.auditranscribe.io.exceptions.IncorrectFileFormatException;
import app.auditranscribe.io.exceptions.InvalidFileVersionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class AUDTFileReader0x00050002 extends AUDTFileReader {
    // Attributes
    private String originalFileName;  // To be used as input to the project info data object later

    /**
     * Initialization method to make an <code>AUDTFileReader0x00050002</code> object.
     *
     * @param filepath    Path to the AUDT file. The file name at the end of the file path should
     *                    <b>include</b> the extension of the AUDT file.
     * @param inputStream Input stream of the file.
     * @throws IOException                  If something went wrong when reading the AUDT file.
     * @throws IncorrectFileFormatException If the file was formatted incorrectly.
     * @throws InvalidFileVersionException  If the LZ4 version is outdated.
     */
    public AUDTFileReader0x00050002(
            String filepath, InputStream inputStream
    ) throws IOException, IncorrectFileFormatException, InvalidFileVersionException {
        super(0x00050002, filepath, inputStream);
    }

    // Public methods
    public UnchangingDataPropertiesObject readUnchangingDataProperties() throws FailedToReadDataException {
        // Ensure that the unchanging data properties section ID is correct
        int sectionID = readSectionID();
        if (sectionID != UnchangingDataPropertiesObject.SECTION_ID) {
            throw new FailedToReadDataException(
                    "Failed to read the unchanging data properties section; the unchanging data properties section " +
                            "has the incorrect section ID of " + sectionID + " (expected: " +
                            UnchangingDataPropertiesObject.SECTION_ID + ")"
            );
        }

        // Read in the rest of the data
        int numSkippableBytes = readInteger();

        // Check if there is an EOS
        if (!checkEOSDelimiter()) {
            throw new FailedToReadDataException(
                    "Failed to read unchanging data properties; end of section delimiter missing"
            );
        }

        // Create and return a `UnchangingDataPropertiesObject`
        return new UnchangingDataPropertiesObject0x00050002(numSkippableBytes);
    }

    public QTransformDataObject readQTransformData() throws FailedToReadDataException {
        // Ensure that the Q-Transform data section ID is correct
        int sectionID = readSectionID();
        if (sectionID != QTransformDataObject.SECTION_ID) {
            throw new FailedToReadDataException(
                    "Failed to read Q-Transform data; the Q-Transform data section has the incorrect " +
                            "section ID of " + sectionID + " (expected: " + QTransformDataObject.SECTION_ID + ")"
            );
        }

        // Read in the rest of the data
        double minMagnitude = readDouble();
        double maxMagnitude = readDouble();
        byte[] qTransformData = readByteArray();

        // Check if there is an EOS
        if (!checkEOSDelimiter()) {
            throw new FailedToReadDataException("Failed to read Q-Transform data; end of section delimiter missing");
        }

        // Create and return a `QTransformDataObject`
        return new QTransformDataObject0x00050002(qTransformData, minMagnitude, maxMagnitude);
    }

    public AudioDataObject readAudioData() throws FailedToReadDataException {
        // Ensure that the audio data section ID is correct
        int sectionID = readSectionID();
        if (sectionID != AudioDataObject.SECTION_ID) {
            throw new FailedToReadDataException(
                    "Failed to read audio data; the audio data section has the incorrect section ID of " + sectionID +
                            " (expected: " + AudioDataObject.SECTION_ID + ")"
            );
        }

        // Read in the rest of the data
        byte[] compressedMP3Bytes = readByteArray();
        double sampleRate = readDouble();
        int totalDurationInMS = readInteger();
        originalFileName = readString();

        // Check if there is an EOS
        if (!checkEOSDelimiter()) {
            throw new FailedToReadDataException("Failed to read audio data; end of section delimiter missing");
        }

        // Create and return an `AudioDataObject`
        return new AudioDataObject0x00050002(compressedMP3Bytes, sampleRate, totalDurationInMS, originalFileName);
    }

    public ProjectInfoDataObject readProjectInfoData() throws FailedToReadDataException {
        // Ensure that the GUI data section ID is correct
        int sectionID = readSectionID();
        if (sectionID != ProjectInfoDataObject.SECTION_ID) {
            throw new FailedToReadDataException(
                    "Failed to read project info data; the project info data section has the incorrect section ID of " +
                            sectionID + "(expected: " + ProjectInfoDataObject.SECTION_ID + ")"
            );
        }

        // Read in the rest of the data first
        int musicKeyIndex = readInteger();
        int timeSignatureIndex = readInteger();
        double bpm = readDouble();
        double offsetSeconds = readDouble();
        double playbackVolume = readDouble();
        int currTimeInMS = readInteger();

        // Check if there is an EOS
        if (!checkEOSDelimiter()) {
            throw new FailedToReadDataException("Failed to read project info data; end of section delimiter missing");
        }

        // Create and return a `ProjectInfoDataObject`
        ProjectInfoDataObject0x00050002 obj = new ProjectInfoDataObject0x00050002(
                musicKeyIndex, timeSignatureIndex, bpm, offsetSeconds, playbackVolume, currTimeInMS
        );
        obj.setProjectName(originalFileName);  // Set the superclass' attribute
        return obj;
    }

    public MusicNotesDataObject readMusicNotesData() throws FailedToReadDataException, IOException {
        // Ensure that the GUI data section ID is correct
        int sectionID = readSectionID();
        if (sectionID != MusicNotesDataObject.SECTION_ID) {
            throw new FailedToReadDataException(
                    "Failed to read music notes data; the music notes data section has the incorrect section ID of " +
                            sectionID + " (expected: " + MusicNotesDataObject.SECTION_ID + ")"
            );
        }

        // Read in the rest of the data first
        double[] timesToPlaceRectangles = read1DDoubleArray();
        double[] noteDurations = read1DDoubleArray();
        int[] noteNums = read1DIntegerArray();

        // Check if there is an EOS
        if (!checkEOSDelimiter()) {
            throw new FailedToReadDataException("Failed to read music notes data; end of section delimiter missing");
        }

        // Create and return a `MusicNotesDataObject`
        return new MusicNotesDataObject0x00050002(timesToPlaceRectangles, noteDurations, noteNums);
    }
}
