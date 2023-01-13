/*
 * AudioDataObject.java
 * Description: Data object that stores the audio data.
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

package app.auditranscribe.io.audt_file.base.data_encapsulators;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data object that stores the audio data.
 */
public abstract class AudioDataObject extends AbstractAUDTDataObject {
    // Constants
    public static final int SECTION_ID = 3;

    // Attributes
    public byte[] compressedOriginalMP3Bytes;
    public byte[] compressedSlowedMP3Bytes;
    public double sampleRate;
    public int totalDurationInMS;

    // Overridden methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioDataObject that = (AudioDataObject) o;
        return Arrays.equals(compressedOriginalMP3Bytes, that.compressedOriginalMP3Bytes) &&
                Arrays.equals(compressedSlowedMP3Bytes, that.compressedSlowedMP3Bytes) &&
                Double.compare(that.sampleRate, sampleRate) == 0 &&
                totalDurationInMS == that.totalDurationInMS;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sampleRate, totalDurationInMS);
        result = 31 * result + Arrays.hashCode(compressedOriginalMP3Bytes);
        result = 31 * result + Arrays.hashCode(compressedSlowedMP3Bytes);
        return result;
    }
}
