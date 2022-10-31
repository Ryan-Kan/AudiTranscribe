/*
 * MathUtils.java
 * Description: Mathematical utility methods.
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

package site.overwrite.auditranscribe.utils;

import site.overwrite.auditranscribe.generic.exceptions.ValueException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Mathematical utility methods.
 */
public final class MathUtils {
    private MathUtils() {
        // Private constructor to signal this is a utility class
    }

    // General mathematical methods

    /**
     * Method to calculate the log base 2 of the number <code>x</code>.
     *
     * @param x Number to take the log base 2 of.
     * @return Log base 2 of <code>x</code>.
     */
    public static double log2(double x) {
        return Math.log(x) * 1.4426950408889634;  // ln x * (1/ln 2), to 16 dp
    }

    /**
     * Method to calculate the log base <code>n</code> of the number <code>x</code>.
     *
     * @param x Number to take the log base <code>n</code> of.
     * @param n The base of the logarithm.
     * @return Log base <code>n</code> of <code>x</code>.
     * @throws ValueException If the base of the logarithm is not positive, or is 1.
     */
    public static double logN(double x, double n) {
        // Validate `n`
        if (n <= 0 || n == 1) {
            throw new ValueException("Invalid value for the base of logarithm: " + n);
        }

        // Special case of `n = 2` as we already handle it
        if (n == 2) return log2(x);

        // Return the logarithm
        return Math.log(x) / Math.log(n);
    }

    /**
     * Method that returns the integer ceiling of <code>p / q</code>.<br>
     * Assumes that both <code>p</code> and <code>q</code> are <b>positive integers</b>.
     *
     * @return Ceiling division of <code>p</code> by <code>q</code>.
     * @implNote See <a href="https://stackoverflow.com/a/2745086">this StackOverflow answer</a> by
     * Sparky.
     */
    public static int ceilDiv(int p, int q) {
        return 1 + ((p - 1) / q);
    }

    // Data-related methods

    /**
     * Linearly interpolate the two values <code>a</code> and <code>b</code> by the scaling factor
     * <code>x</code>.
     *
     * @param a First value.
     * @param b Second value.
     * @param x Scaling factor.
     * @return An <b>integer</b> representing the linearly interpolated value.<br>
     * If <code>x = 0</code> then this will return <code>a</code>.
     * If <code>x = 1</code> then this will return <code>b</code>.
     */
    public static int intLerp(int a, int b, double x) {
        return (int) lerp(a, b, x);
    }

    /**
     * Linearly interpolate the two values <code>a</code> and <code>b</code> by the scaling factor
     * <code>x</code>.
     *
     * @param a First value.
     * @param b Second value.
     * @param x Scaling factor.
     * @return A <b>double</b> representing the linearly interpolated value.<br>
     * If <code>x = 0</code> then this will return <code>a</code>.
     * If <code>x = 1</code> then this will return <code>b</code>.
     */
    public static double lerp(double a, double b, double x) {
        return (a + (b - a) * x);
    }

    /**
     * Normalizes the value <code>x</code> to the range <code>zMin</code> to <code>zMax</code>
     * inclusive.
     *
     * @param x    Value to normalize.
     * @param xMin Minimum possible value of <code>x</code>.
     * @param xMax Maximum possible value of <code>x</code>.
     * @param zMin Minimum normalized value.
     * @param zMax Maximum normalized value.
     * @return Normalized value of <code>x</code>.
     */
    public static double normalize(double x, double xMin, double xMax, double zMin, double zMax) {
        return ((x - xMin) / (xMax - xMin)) * (zMax - zMin) + zMin;
    }

    /**
     * Normalizes the value <code>x</code> to the range 0 to 1 inclusive.
     *
     * @param x   Value to normalise.
     * @param min Minimum possible value of <code>x</code>.
     * @param max Maximum possible value of <code>x</code>.
     * @return Normalized value of <code>x</code>.
     */
    public static double normalize(double x, double min, double max) {
        return normalize(x, min, max, 0, 1);
    }

    /**
     * Rounds the double <code>x</code> to <code>dp</code> decimal places.
     *
     * @param x  The double.
     * @param dp Number of decimal places to round to.
     * @return Rounded double.
     * @throws ValueException If the number of decimal places (<code>dp</code>) is less than 0.
     * @implNote If <code>x</code> is <code>NaN</code> then the rounding will also return
     * <code>NaN</code>.
     */
    public static double round(double x, int dp) {
        if (Double.isNaN(x)) return Double.NaN;
        if (dp < 0) throw new ValueException("Invalid number of decimal places: " + dp);

        BigDecimal bd = new BigDecimal(Double.toString(x));
        bd = bd.setScale(dp, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Combinatorial methods

    /**
     * Method that computes the <code>k</code>th <em>self-product</em> of <code>n</code>
     * elements and returns them in lexicographical order.<br>
     * The <code>k</code>th <em>self-product</em> is the permutation of <code>k</code> copies of the
     * <code>n</code> element array. Note for this method the elements of the <code>n</code> element
     * array will be 0, 1, 2,...,<code>n-1</code>, like array indices.
     *
     * @param n Number of elements.
     * @param k Number of repeats.
     * @return 2D array of integers. Each subarray contains one possible <em>self-product</em>, and
     * there will be <code>n</code><sup><code>k</code></sup> subarrays in total, unless
     * <code>k = 0</code> in which case there will be 0 subarrays.
     */
    public static int[][] selfProduct(int n, int k) {
        // Handle base case
        if (k == 0) {
            // If no repeats is specified, just return empty array
            return new int[0][0];
        }

        // Otherwise, compute number of elements in the final array
        int numElem = (int) Math.pow(n, k);

        // Define final output array
        int[][] output = new int[numElem][k];

        // Get all possible elements for the 2nd to nth elements
        int[][] tempSelfProduct = selfProduct(n, k - 1);

        // Check how many elements are to be appended
        if (tempSelfProduct.length == 0) {
            // No elements after this one; just linearly add to the output list
            for (int i = 0; i < n; i++) {
                output[i][0] = i;
            }
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < tempSelfProduct.length; j++) {
                    // Fill in temp array
                    int[] temp = new int[k];
                    temp[0] = i;
                    System.arraycopy(tempSelfProduct[j], 0, temp, 1, k - 1);

                    // Set array in the output
                    output[i * tempSelfProduct.length + j] = temp;
                }
            }
        }

        // Return the output array
        return output;
    }

    // Checking-related methods

    /**
     * Method to check if the integer <code>x</code> is a power of 2.<br>
     * This assumes that <code>x</code> is positive.
     *
     * @param x Integer to check.
     * @return A boolean, <code>true</code> if the integer is a power of 2 and <code>false</code>
     * otherwise.
     */
    public static boolean isPowerOf2(int x) {
        if (x <= 0) throw new ValueException("The provided integer must be positive");
        return (x & (x - 1)) == 0;
    }

    // Misc methods

    /**
     * Return how many times the integer <code>x</code> can be evenly divided by 2.
     * Returns 0 for non-positive integers.
     *
     * @param x The integer in question.
     * @return Number of times <code>x</code> can be divided by 2 if <code>x</code> is positive;
     * zero otherwise.
     */
    public static int numTwoFactors(int x) {
        // If `x` is not positive, then return 0 by definition
        if (x <= 0) return 0;

        // Compute the number of factors of two
        int numTwos = 0;
        while (x % 2 == 0) {
            numTwos++;
            x /= 2;
        }

        // Return the number of factors of two
        return numTwos;
    }

    /**
     * Method that wraps a value to the appropriate value within the range <code>min</code> to
     * <code>max</code>, where <code>min</code> is inclusive and <code>max</code> is exclusive.
     *
     * @param value Value to wrap.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @return Wrapped value.
     * @throws ValueException If:<ul>
     *                        <li>
     *                        The maximum value is not a positive integer.
     *                        </li>
     *                        <li>
     *                        The minimum value is larger than or equal to the maximum value.
     *                        </li>
     *                        </ul>
     */
    public static int wrapValue(int value, int min, int max) {
        // Check if the values are valid
        if (max <= 0) {
            throw new ValueException("The maximum value must be a positive integer.");
        }

        if (min >= max) {
            throw new ValueException("The minimum value must be smaller than the maximum value.");
        }

        // Perform actual computation
        int r = value % max;
        if (r < min) {
            r += max - min;
        }
        return r;
    }

    /**
     * Method that wraps a value to the maximum value if the value is smaller than the maximum value
     * and wraps to the minimum if the value is larger than the minimum value.
     *
     * @param value Value to wrap.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @return Wrapped value.
     * @throws ValueException If:<ul>
     *                        <li>
     *                        The maximum value is not a positive number.
     *                        </li>
     *                        <li>
     *                        The minimum value is larger than the maximum value.
     *                        </li>
     *                        </ul>
     */
    public static BigDecimal wrapValue(BigDecimal value, BigDecimal min, BigDecimal max) {
        // Check if the values are valid
        if (max.doubleValue() <= 0) {
            throw new ValueException("The maximum value must be a positive number.");
        }

        if (min.compareTo(max) >= 0) {
            throw new ValueException("The minimum value must be smaller than to the maximum value.");
        }

        // Perform actual computation
        if (value.compareTo(min) < 0) {
            return max;
        } else if (value.compareTo(max) > 0) {
            return min;
        }
        return value;
    }
}
