/*
 * AbstractFilter.java
 *
 * Created on 2022-03-07
 * Updated on 2022-05-10
 *
 * Description: `AbstractFilter` class for resampling filters.
 */

package site.overwrite.auditranscribe.audio.filters;

import com.google.gson.Gson;
import site.overwrite.auditranscribe.io.IOMethods;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Abstract resampling filter class.
 */
public abstract class AbstractFilter {
    // Attributes
    private double[] halfWin;
    private int precision;
    private double rolloff;

    // Public methods

    /**
     * Gets the right wing of the interpolation filter.
     *
     * @return The right wing of the interpolation filter.
     */
    public double[] getHalfWin() {
        // Create a copy of the original
        double[] halfWinCopy = new double[halfWin.length];
        System.arraycopy(halfWin, 0, halfWinCopy, 0, halfWin.length);

        // Return the copy
        return halfWinCopy;
    }

    /**
     * Gets the precision of the filter.
     *
     * @return The number of samples between zero-crossings of the filter.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Gets the rolloff factor of the filter.
     *
     * @return The roll-off frequency of the filter as a fraction of the Nyquist frequency.
     */
    public double getRolloff() {
        return rolloff;
    }


    // Private methods

    /**
     * Define all the attributes of this filter.
     *
     * @param dataFilePath Path (with reference to the resources directory) to the JSON data file
     *                     that contains this data.
     */
    public void defineAttributes(String dataFilePath) {
        // Assert the file path entered is not empty
        assert dataFilePath != null;

        // Create the GSON loader object
        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(IOMethods.getInputStream(dataFilePath))) {
            // Try loading the filter data
            FilterData filterData = gson.fromJson(reader, FilterData.class);

            // Set attributes
            halfWin = filterData.halfWin;
            precision = filterData.precision;
            rolloff = filterData.rolloff;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
