/*
 * FrequencyBinsTest.java
 * Description: Test `FrequencyBins.java`.
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

package site.overwrite.auditranscribe.spectrogram.spectral_representations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyBinsTest {
    @Test
    void getFFTFreqBins() {
        // Get the generated frequency bins
        double[] freqBins1 = FrequencyBins.getFFTFreqBins(16, 22050);
        double[] freqBins2 = FrequencyBins.getFFTFreqBins(25, 44100);
        double[] freqBins3 = FrequencyBins.getFFTFreqBins(20, 11025);

        // Define correct frequency bins (using Librosa)
        double[] correctFreqBins1 = {
                0., 1378.125, 2756.25, 4134.375, 5512.5, 6890.625, 8268.75, 9646.875, 11025.
        };
        double[] correctFreqBins2 = {
                0., 1764., 3528., 5292., 7056., 8820., 10584., 12348., 14112., 15876., 17640., 19404., 21168.
        };
        double[] correctFreqBins3 = {
                0., 551.25, 1102.5, 1653.75, 2205., 2756.25, 3307.5, 3858.75, 4410., 4961.25, 5512.5
        };

        // Assertions
        assertArrayEquals(correctFreqBins1, freqBins1, 1e-5);
        assertArrayEquals(correctFreqBins2, freqBins2, 1e-5);
        assertArrayEquals(correctFreqBins3, freqBins3, 1e-5);
    }

    @Test
    void getQTFreqBins() {
        // Get the generated frequency bins
        double[] freqBins1 = FrequencyBins.getQTFreqBins(36, 12, 10);
        double[] freqBins2 = FrequencyBins.getQTFreqBins(48, 16, 123.45);
        double[] freqBins3 = FrequencyBins.getQTFreqBins(50, 20, 256.256);

        // Define correct frequency bins (using Librosa)
        double[] correctFreqBins1 = {
                10.0, 10.594630943592954, 11.22462048309373, 11.89207115002721, 12.599210498948732, 13.348398541700345,
                14.142135623730951, 14.983070768766815, 15.874010519681994, 16.81792830507429, 17.817974362806787,
                18.87748625363387, 20.0, 21.189261887185907, 22.44924096618746, 23.78414230005442, 25.198420997897465,
                26.69679708340069, 28.284271247461902, 29.96614153753363, 31.74802103936399, 33.63585661014858,
                35.635948725613574, 37.75497250726774, 40.0, 42.378523774371814, 44.898481932374914, 47.56828460010884,
                50.39684199579493, 53.393594166801364, 56.568542494923804, 59.93228307506727, 63.496042078727974,
                67.27171322029716, 71.27189745122715, 75.50994501453548
        };
        double[] correctFreqBins2 = {
                123.45, 128.91559844066424, 134.62317959752608, 140.5834569607136, 146.8076183470859, 153.3073469004716,
                160.09484302166715, 167.1828472693594, 174.5846642749586, 182.3141877162312, 190.3859263966103,
                198.81503147913546, 207.61732492614212, 216.80932919808382, 226.40829826723333, 236.43225000447785,
                246.9, 257.8311968813285, 269.24635919505215, 281.1669139214272, 293.6152366941718, 306.6146938009432,
                320.1896860433343, 334.3656945387188, 349.1693285499172, 364.6283754324624, 380.7718527932206,
                397.6300629582709, 415.23464985228424, 433.61865839616763, 452.81659653446667, 472.8645000089557,
                493.8, 515.662393762657, 538.4927183901043, 562.3338278428544, 587.2304733883436, 613.2293876018864,
                640.3793720866686, 668.7313890774376, 698.3386570998344, 729.2567508649248, 761.5437055864412,
                795.2601259165418, 830.4692997045685, 867.2373167923353, 905.6331930689333, 945.7290000179114
        };
        double[] correctFreqBins3 = {
                256.256, 265.292848323896, 274.64838041570033, 284.3338346342177, 294.3608456581202, 304.74145846213725,
                315.48814278611485, 326.61380811432485, 338.13181918301876, 350.05601203485435, 362.4007106394798,
                375.1807441002421, 388.4114644676885, 402.10876518125946, 416.2891001613269, 430.9695035745117,
                446.1676102960231, 461.90167709360173, 478.1906045585122, 495.05395980993035, 512.512, 530.585696647792,
                549.2967608314007, 568.6676692684352, 588.7216913162403, 609.4829169242745, 630.9762855722297,
                653.2276162286497, 676.2636383660375, 700.1120240697087, 724.8014212789597, 750.3614882004842,
                776.8229289353771, 804.2175303625188, 832.5782003226539, 861.9390071490234, 892.3352205920462,
                923.8033541872036, 956.3812091170244, 990.1079196198607, 1025.024, 1061.1713932955838,
                1098.5935216628013, 1137.3353385368705, 1177.4433826324807, 1218.965833848549, 1261.9525711444592,
                1306.4552324572994, 1352.527276732075, 1400.2240481394176
        };

        // Assertions
        assertArrayEquals(correctFreqBins1, freqBins1, 1e-5);
        assertArrayEquals(correctFreqBins2, freqBins2, 1e-5);
        assertArrayEquals(correctFreqBins3, freqBins3, 1e-5);
    }

    @Test
    void getMelFreqBins() {
        // Get the generated frequency bins
        double[] freqBins1 = FrequencyBins.getMelFreqBins(40, 0, 11025);
        double[] freqBins2 = FrequencyBins.getMelFreqBins(10, 512, 4096);

        // Define correct frequency bins (using Librosa)
        double[] correctFreqBins1 = {
                0., 85.317, 170.635, 255.952,
                341.269, 426.586, 511.904, 597.221,
                682.538, 767.855, 853.173, 938.49,
                1024.856, 1119.114, 1222.042, 1334.436,
                1457.167, 1591.187, 1737.532, 1897.337,
                2071.84, 2262.393, 2470.47, 2697.686,
                2945.799, 3216.731, 3512.582, 3835.643,
                4188.417, 4573.636, 4994.285, 5453.621,
                5955.205, 6502.92, 7101.009, 7754.107,
                8467.272, 9246.028, 10096.408, 11025.
        };
        double[] correctFreqBins2 = {
                512., 718.13865968, 924.27731935, 1143.95888821,
                1414.93125114, 1750.08950592, 2164.63752303, 2677.38055126,
                3311.57828504, 4096.
        };

        // Assertions
        assertArrayEquals(correctFreqBins1, freqBins1, 1e-3);
        assertArrayEquals(correctFreqBins2, freqBins2, 1e-3);
    }
}