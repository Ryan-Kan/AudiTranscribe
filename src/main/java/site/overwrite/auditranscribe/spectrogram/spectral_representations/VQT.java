/*
 * VQT.java
 *
 * Created on 2022-03-11
 * Updated on 2022-04-10
 *
 * Description: Class that implements the Variable Q-Transform (VQT) algorithm.
 */

package site.overwrite.auditranscribe.spectrogram.spectral_representations;

import javafx.util.Pair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import site.overwrite.auditranscribe.audio.Audio;
import site.overwrite.auditranscribe.audio.Filter;
import site.overwrite.auditranscribe.audio.Window;
import site.overwrite.auditranscribe.spectrogram.Wavelet;
import site.overwrite.auditranscribe.utils.ArrayUtils;
import site.overwrite.auditranscribe.utils.Complex;
import site.overwrite.auditranscribe.utils.MathUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * VQT class that contains Variable-Q Transform methods.
 *
 * @implNote Adapted largely from
 * <a href="https://librosa.org/doc/main/_modules/librosa/core/constantq.html#vqt">Librosa's
 * Implementation</a> of the VQT.
 * @see <a href="https://core.ac.uk/download/pdf/144846462.pdf">This paper</a> by Schoerkhuber,
 * Christian, and Anssi Klapuri. "Constant-Q transform toolbox for music processing." 7th Sound and
 * Music Computing Conference, Barcelona, Spain. 2010.
 */
public class VQT {
    // Constants
    static final double BW_FASTEST = 0.85;

    // Public methods

    /**
     * Variable-Q Transform function.
     *
     * @param y             Audio time series.
     * @param sr            Sample rate of the audio.
     * @param hopLength     Number of samples between successive VQT columns.
     * @param fmin          Minimum frequency.
     * @param numBins       Number of frequency bins, starting at <code>fmin</code>.
     * @param binsPerOctave Number of bins per octave.
     * @param gamma         Bandwidth offset for determining filter lengths. <code>gamma = 0</code>
     *                      means that the gamma value will be derived automatically.
     * @param window        Window specification for the basis filters.
     * @return Variable-Q value each frequency at each time.
     * @throws InvalidParameterException If number of frequency bins is negative or zero.
     * @throws InvalidParameterException If number of bins per octave is negative or zero.
     * @throws InvalidParameterException If number of bins is not a multiple of the number of bins
     *                                   per octave.
     * @throws InvalidParameterException If the current number of frequency bins results in the
     *                                   highest frequency exceeding the Nyquist frequency.
     */
    public static Complex[][] vqt(
            double[] y, double sr, int hopLength, double fmin, int numBins, int binsPerOctave, double gamma,
            Window window
    ) {
        return vqt(y, sr, hopLength, fmin, numBins, binsPerOctave, false, gamma, window);
    }

    /**
     * Variable-Q Transform function.
     *
     * @param y             Audio time series.
     * @param sr            Sample rate of the audio.
     * @param hopLength     Number of samples between successive VQT columns.
     * @param fmin          Minimum frequency.
     * @param numBins       Number of frequency bins, starting at <code>fmin</code>.
     * @param binsPerOctave Number of bins per octave.
     * @param isCQT         Whether this is a CQT or not.
     * @param gamma         Bandwidth offset for determining filter lengths. If <code>isCQT</code>is
     *                      true and <code>gamma = 0</code>, produces the Constant-Q Transform
     *                      (CQT). Otherwise, <code>gamma = 0</code> means that the gamma value will
     *                      be derived automatically.
     * @param window        Window specification for the basis filters.
     * @return Variable-Q value each frequency at each time.
     * @throws InvalidParameterException If number of frequency bins is negative or zero.
     * @throws InvalidParameterException If number of bins per octave is negative or zero.
     * @throws InvalidParameterException If number of bins is not a multiple of the number of bins
     *                                   per octave.
     * @throws InvalidParameterException If the current number of frequency bins results in the
     *                                   highest frequency exceeding the Nyquist frequency.
     */
    public static Complex[][] vqt(
            double[] y, double sr, int hopLength, double fmin, int numBins, int binsPerOctave, boolean isCQT,
            double gamma, Window window
    ) {
        // Validate parameters
        if (numBins <= 0) {
            throw new InvalidParameterException("Number of frequency bins cannot be negative or zero.");
        }

        if (binsPerOctave <= 0) {
            throw new InvalidParameterException("Number of bins per octave cannot be negative or zero");
        }

        if (numBins % binsPerOctave != 0) {
            throw new InvalidParameterException("Number of bins is not a multiple of the number of bins per octave.");
        }

        // Compute number of octaves that we are processing
        int numOctaves = getNumOctaves(numBins, binsPerOctave);

        // Get the VQT frequency bins
        double[] freqs = getFreqBins(numBins, binsPerOctave, fmin);

        // Get the frequencies for the top octave
        double[] freqsTop = new double[binsPerOctave];
        System.arraycopy(freqs, numBins - binsPerOctave, freqsTop, 0, binsPerOctave);

        // Get the highest frequency
        double highestFrequency = freqsTop[binsPerOctave - 1];

        // Calculate the relative difference in frequency between any two consecutive bands, alpha
        double alpha = SpectralHelpers.computeAlpha(binsPerOctave);

        // Compute filter cutoff
        Pair<double[], Double> waveletLengthsResponse = Wavelet.computeWaveletLengths(
                freqs, sr, window, isCQT, gamma, alpha, 1.
        );
        double filterCutoff = waveletLengthsResponse.getValue();

        // Determine required resampling quality
        double nyquistFrequency = sr / 2;

        if (filterCutoff > nyquistFrequency) {
            throw new InvalidParameterException(
                    "Wavelet basis with max frequency " + highestFrequency +
                            " would exceed the Nyquist frequency " + nyquistFrequency +
                            ". Try reducing the number of frequency bins."
            );
        }

        // Resample audio
        Filter filter;
        if (filterCutoff < BW_FASTEST * nyquistFrequency) {
            filter = Filter.KAISER_FAST;
        } else {
            filter = Filter.KAISER_BEST;
        }

        Triple<double[], Double, Integer> earlyDownsampleResponse = earlyDownsample(
                y, sr, hopLength, filter, numOctaves, nyquistFrequency, filterCutoff
        );
        double[] yNew = earlyDownsampleResponse.getLeft();
        double srNew = earlyDownsampleResponse.getMiddle();
        int hopLengthNew = earlyDownsampleResponse.getRight();

        // Define VQT response array
        List<Complex[][]> vqtResponses = new ArrayList<>();  // Todo: can we get a fixed size array?

        // Handle first octave specially if the filter type is NOT `KAISER_FAST`
        int startingOctave = 0;

        if (filter != Filter.KAISER_FAST) {  // Todo: we haven't exactly debugged this code block
            // Get the frequencies of the top octave
            double[] freqsOct = new double[binsPerOctave];
            System.arraycopy(freqs, numBins - binsPerOctave, freqsOct, 0, binsPerOctave);

            // Do the top octave before resampling to allow for fast resampling
            Triple<Complex[][], Integer, double[]> fftFilterResponse = vqtFilterFFT(
                    srNew, freqsOct, window, isCQT, gamma, alpha
            );
            Complex[][] fftBasis = fftFilterResponse.getLeft();
            int numFFT = fftFilterResponse.getMiddle();

            // Compute the VQT filter response and append it to the list
            vqtResponses.add(vqtResponse(yNew, numFFT, hopLengthNew, fftBasis));

            // Update values
            startingOctave = 1;
            filter = Filter.KAISER_FAST;
        }

        // Iterate down the octaves
        double[] myY = yNew;
        double mySR = srNew;
        int myHopLength = hopLengthNew;

        for (int octave = startingOctave; octave < numOctaves; octave++) {  // Starts from the HIGHEST frequencies
            // Get the frequencies of the current octave
            // Fixme: This may be incorrect with early downsampling
            double[] freqsOct = new double[binsPerOctave];
            System.arraycopy(freqs, numBins - binsPerOctave * (octave + 1), freqsOct, 0, binsPerOctave);

            // Get the FFT basis and the `numFFT` for this octave
            Triple<Complex[][], Integer, double[]> fftFilterResponse = vqtFilterFFT(
                    mySR, freqsOct, window, isCQT, gamma, alpha
            );
            Complex[][] fftBasis = fftFilterResponse.getLeft();
            int numFFT = fftFilterResponse.getMiddle();

            // Re-scale the filters to compensate for downsampling
            for (int i = 0; i < fftBasis.length; i++) {
                for (int j = 0; j < fftBasis[0].length; j++) {
                    fftBasis[i][j] = fftBasis[i][j].scale(Math.sqrt(srNew / mySR));
                }
            }

            // Compute the VQT filter response and append to the list
            vqtResponses.add(vqtResponse(myY, numFFT, myHopLength, fftBasis));

            // Update variables
            if (myHopLength % 2 == 0) {
                myHopLength /= 2;
                mySR /= 2.;
                myY = Audio.resample(myY, 2, 1, filter, true);
            }
        }

        // Convert the VQT responses list to an array
        // Todo: try and do this at the start
        int[] shape = new int[]{numOctaves, vqtResponses.get(0).length, vqtResponses.get(0)[0].length};
        Complex[][][] vqtResponsesArray = new Complex[shape[0]][shape[1]][shape[2]];
        vqtResponses.toArray(vqtResponsesArray);

        // Trim and stack the array
        Complex[][] V = trimAndStack(vqtResponsesArray, numBins);

        // Recompute lengths here because early downsampling may have changed our sampling rate
        waveletLengthsResponse = Wavelet.computeWaveletLengths(freqs, srNew, window, isCQT, gamma, alpha, 1.);
        double[] lengths = waveletLengthsResponse.getKey();

        // Scale `V` back to normal
        for (int i = 0; i < numBins; i++) {
            double scaleFactor = Math.sqrt(lengths[i]);

            for (int j = 0; j < shape[2]; j++) {
                V[i][j] = V[i][j].divides(scaleFactor);
            }
        }

        // Return VQT matrix
        return V;
    }

    /**
     * Compute the center frequencies of Variable-Q bins.
     *
     * @param numBins       Number of Variable-Q bins.
     * @param binsPerOctave Number of bins per octave.
     * @param fmin          Minimum frequency.
     * @return Array of center frequencies for each VQT bin.
     */
    public static double[] getFreqBins(int numBins, int binsPerOctave, double fmin) {
        double[] frequencies = new double[numBins];

        for (int i = 0; i < numBins; i++) {
            // Calculate the frequency of the current frequency bin
            double freq = fmin * Math.pow(2, (double) i / binsPerOctave);

            // Append it to the list of frequencies
            frequencies[i] = freq;
        }

        return frequencies;
    }

    // Private Methods

    /**
     * Gets the number of octaves required to store <code>numBins</code>.
     *
     * @param numBins       Number of Variable-Q bins.
     * @param binsPerOctave Number of bins per octave.
     * @return Number of octaves required to store <code>numBins</code>.
     */
    private static int getNumOctaves(int numBins, int binsPerOctave) {
        return (int) Math.ceil((double) numBins / binsPerOctave);
    }

    /**
     * Perform early downsampling on the audio samples.
     *
     * @param y            Audio time series.
     * @param sr           Sample rate of the audio.
     * @param hopLength    Number of samples between successive VQT columns.
     * @param filter       Filter window.
     * @param numOctaves   Number of octaves to consider
     * @param nyquist      Nyquist frequency value.
     * @param filterCutoff Highest frequency value before cutoff begins.
     * @return Three values. First is the downsampled audio time series. Second is the new sample
     * rate. Third is the new hop length.
     * @throws InvalidParameterException If The input signal length is too short for a
     *                                   <code>numOctaves</code>-octave VQT.
     */

    private static Triple<double[], Double, Integer> earlyDownsample(
            double[] y, double sr, int hopLength, Filter filter, int numOctaves, double nyquist, double filterCutoff
    ) {
        // Compute the number of early downsampling operations
        int downsampleCount1 = Math.max(0, (int) Math.ceil(MathUtils.log2(BW_FASTEST * nyquist / filterCutoff)) - 2);
        int downsampleCount2 = Math.max(0, MathUtils.numTwoFactors(hopLength) - numOctaves + 1);

        int downsampleCount = Math.min(downsampleCount1, downsampleCount2);

        // Actually perform the downsampling
        double[] yNew;

        if (downsampleCount > 0 && filter == Filter.KAISER_FAST) {
            // Compute how much to downsample by
            int downsampleFactor = (int) Math.pow(2, downsampleCount);

            // Check if the signal can actually be downsampled
            if (y.length < downsampleFactor) {
                throw new InvalidParameterException(
                        "Input signal length of " + y.length + " is too short for " + numOctaves +
                                "-octave VQT"
                );
            }

            // Downsample hop length and the sample rate
            hopLength /= downsampleFactor;
            double newSr = sr / downsampleFactor;

            // Downsample audio sample
            yNew = Audio.resample(y, sr, newSr, filter, true);
            sr = newSr;
        } else {
            yNew = y;
        }

        // Return needed values
        return new ImmutableTriple<>(yNew, sr, hopLength);
    }

    /**
     * Generate the frequency domain variable-Q filter basis.
     *
     * @param sr     Sample rate.
     * @param freqs  Centre frequencies of the frequency bins.
     * @param window Window function to use.
     * @param isCQT  Whether this is a CQT or not.
     * @param gamma  Gamma value.
     * @param alpha  Alpha value.
     * @return Triplet of values. First value is a 2D array of complex coefficients, representing
     * the FFT basis. Second value is an integer, representing the number of FFT frequency bins.
     * Third value is a double array, representing the filters' lengths.
     */
    private static Triple<Complex[][], Integer, double[]> vqtFilterFFT(
            double sr, double[] freqs, Window window, boolean isCQT, double gamma, double alpha
    ) {
        // Get the frequency and lengths of the wavelet basis
        Pair<Complex[][], double[]> waveletBasisResponse = Wavelet.computeWaveletBasis(
                freqs, sr, window, 1, true, 1, isCQT, gamma, alpha
        );
        Complex[][] basis = waveletBasisResponse.getKey();
        double[] lengths = waveletBasisResponse.getValue();

        // Number of FFT bins is the second element of the shape of the basis
        int fftWindowLength = basis.length;
        int numFFT = basis[0].length;

        // Re-normalize bases with respect to the FFT window length
        for (int i = 0; i < fftWindowLength; i++) {
            double normalisationFactor = lengths[i] / (double) numFFT;

            for (int j = 0; j < numFFT; j++) {
                basis[i][j] = basis[i][j].scale(normalisationFactor);
            }
        }

        // FFT and retain only the non-negative frequencies
        Complex[][] fftBasis = new Complex[fftWindowLength][numFFT];
        for (int i = 0; i < fftWindowLength; i++) {
            fftBasis[i] = FFT.fft(basis[i]);
        }

        // Return required data
        return new ImmutableTriple<>(fftBasis, numFFT, lengths);
    }

    /**
     * Compute the VQT filter response with a target STFT hop.
     *
     * @param y         Array of samples.
     * @param numFFT    Number of bins to use in the FFT.
     * @param hopLength Step size between each hop.
     * @param fftBasis  Basis to use for the FFT.
     * @return VQT filter response.
     */
    private static Complex[][] vqtResponse(double[] y, int numFFT, int hopLength, Complex[][] fftBasis) {
        // Get the STFT matrix
        Complex[][] D = STFT.stft(y, numFFT, hopLength, Window.ONES_WINDOW);

        // Matrix multiply `fftBasis` with `D` and return the result
        return ArrayUtils.matmul(fftBasis, D);
    }

    /**
     * Helper function to trim and stack a collection of VQT responses.
     *
     * @param vqtResponses Collection of VQT responses.
     * @param numBins      Number of bins for the VQT.
     * @return Trimmed and stacked array of VQT responses.
     */
    private static Complex[][] trimAndStack(Complex[][][] vqtResponses, int numBins) {
        // Get the number of columns in the output matrix
        int numCol = vqtResponses[0][0].length;

        // Generate output VQT matrix
        Complex[][] vqtOut = new Complex[numBins][numCol];

        // Copy per-octave data into output array
        int end = numBins;
        for (Complex[][] response : vqtResponses) {  // Note: FIRST element represents the HIGHEST FREQUENCY bin
            // By default, take the whole octave
            int numOctaves = response.length;

            // If the whole octave is more than we can fit, take the highest bins from `response`
            if (end < numOctaves) {
                System.arraycopy(response, numOctaves - end, vqtOut, 0, end);
            } else {
                System.arraycopy(response, 0, vqtOut, end - numOctaves, numOctaves);
            }

            end -= numOctaves;
        }

        // Return the stacked VQT matrix
        return vqtOut;
    }
}
