package app.auditranscribe.utils;

import app.auditranscribe.generic.exceptions.FormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitConversionUtilsTest {
    // Note unit conversion
    @Test
    void noteToFreq() {
        assertEquals(440, UnitConversionUtils.noteToFreq("A4"), 0.001);
        assertEquals(16.352, UnitConversionUtils.noteToFreq("C0"), 0.001);
        assertEquals(15804.266, UnitConversionUtils.noteToFreq("B9"), 0.001);
    }

    @Test
    void noteToNoteNumber() {
        // Basic tests
        assertEquals(0, UnitConversionUtils.noteToNoteNumber("c0"));
        assertEquals(57, UnitConversionUtils.noteToNoteNumber("a4"));
        assertEquals(60, UnitConversionUtils.noteToNoteNumber("c5"));
        assertEquals(108, UnitConversionUtils.noteToNoteNumber("c9"));

        // Full key tests
        assertEquals(48, UnitConversionUtils.noteToNoteNumber("C4"));
        assertEquals(49, UnitConversionUtils.noteToNoteNumber("C#4"));
        assertEquals(50, UnitConversionUtils.noteToNoteNumber("D4"));
        assertEquals(51, UnitConversionUtils.noteToNoteNumber("D#4"));
        assertEquals(52, UnitConversionUtils.noteToNoteNumber("E4"));
        assertEquals(53, UnitConversionUtils.noteToNoteNumber("F4"));
        assertEquals(54, UnitConversionUtils.noteToNoteNumber("F#4"));
        assertEquals(55, UnitConversionUtils.noteToNoteNumber("G4"));
        assertEquals(56, UnitConversionUtils.noteToNoteNumber("G#4"));
        assertEquals(57, UnitConversionUtils.noteToNoteNumber("A4"));
        assertEquals(58, UnitConversionUtils.noteToNoteNumber("A#4"));
        assertEquals(59, UnitConversionUtils.noteToNoteNumber("B4"));
        assertEquals(60, UnitConversionUtils.noteToNoteNumber("C5"));

        // Accidental tests
        assertEquals(60, UnitConversionUtils.noteToNoteNumber("C5"));
        assertEquals(61, UnitConversionUtils.noteToNoteNumber("C#5"));
        assertEquals(61, UnitConversionUtils.noteToNoteNumber("C♯5"));
        assertEquals(62, UnitConversionUtils.noteToNoteNumber("C##5"));
        assertEquals(62, UnitConversionUtils.noteToNoteNumber("C♯♯5"));
        assertEquals(59, UnitConversionUtils.noteToNoteNumber("Cb5"));
        assertEquals(59, UnitConversionUtils.noteToNoteNumber("C!5"));
        assertEquals(59, UnitConversionUtils.noteToNoteNumber("C♭5"));
        assertEquals(58, UnitConversionUtils.noteToNoteNumber("Cbb5"));
        assertEquals(58, UnitConversionUtils.noteToNoteNumber("C!!5"));
        assertEquals(58, UnitConversionUtils.noteToNoteNumber("C♭♭5"));

        // Weird 'no octave provided' tests
        assertEquals(-2, UnitConversionUtils.noteToNoteNumber("C♭♭"));
        assertEquals(2, UnitConversionUtils.noteToNoteNumber("C##"));

        // Improper format tests
        assertThrowsExactly(FormatException.class, () -> UnitConversionUtils.noteToNoteNumber("haha1"));
        assertThrowsExactly(FormatException.class, () -> UnitConversionUtils.noteToNoteNumber("1C#"));
        assertThrowsExactly(FormatException.class, () -> UnitConversionUtils.noteToNoteNumber("#C1"));
    }

    @Test
    void noteNumberToFreq() {
        assertEquals(440, UnitConversionUtils.noteNumberToFreq(57), 0.001);  // A4
        assertEquals(16.352, UnitConversionUtils.noteNumberToFreq(0), 0.001);  // C0
        assertEquals(15804.266, UnitConversionUtils.noteNumberToFreq(119), 0.001);  // B9
    }

    // Audio unit conversion
    @Test
    void hzToOctaves() {
        assertEquals(4, UnitConversionUtils.hzToOctaves(440), 1e-10);
        assertEquals(6.2186402865, UnitConversionUtils.hzToOctaves(2048), 1e-10);
        assertEquals(8.8102795025, UnitConversionUtils.hzToOctaves(12345), 1e-10);
    }
}