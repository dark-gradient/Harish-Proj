import java.awt.Font;
import java.math.BigInteger;

/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * NumberConverterTest.java
 * Self-contained command-line verification suite testing:
 *   1. All 6 bidirectional base pairs (12 directional conversions)
 *   2. All 4 same-base identity conversions
 *   3. Zero ("0") and inputs with leading zeros ("0001011", "000")
 *   4. Arbitrarily large >64-bit BigInteger values
 *   5. Rejection of blank, negative, plus-prefixed, fractional, and invalid-digit inputs
 *   6. Bundled IBM Plex Sans & IBM Plex Mono font loading and hierarchy
 *
 * Run with:
 *   javac *.java
 *   java NumberConverterTest
 */
public final class NumberConverterTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("  EGB1201 NUMBER SYSTEM CONVERTER — AUTOMATED VERIFICATION SUITE");
        System.out.println("========================================================================");

        // 1. Verify all 6 Bidirectional Pairs (12 directional conversions) using value 255
        assertConversion("11111111", NumberBase.BINARY, NumberBase.OCTAL, "377");
        assertConversion("377", NumberBase.OCTAL, NumberBase.BINARY, "11111111");

        assertConversion("11111111", NumberBase.BINARY, NumberBase.DECIMAL, "255");
        assertConversion("255", NumberBase.DECIMAL, NumberBase.BINARY, "11111111");

        assertConversion("11111111", NumberBase.BINARY, NumberBase.HEXADECIMAL, "FF");
        assertConversion("FF", NumberBase.HEXADECIMAL, NumberBase.BINARY, "11111111");

        assertConversion("377", NumberBase.OCTAL, NumberBase.DECIMAL, "255");
        assertConversion("255", NumberBase.DECIMAL, NumberBase.OCTAL, "377");

        assertConversion("377", NumberBase.OCTAL, NumberBase.HEXADECIMAL, "FF");
        assertConversion("FF", NumberBase.HEXADECIMAL, NumberBase.OCTAL, "377");

        assertConversion("255", NumberBase.DECIMAL, NumberBase.HEXADECIMAL, "FF");
        assertConversion("ff", NumberBase.HEXADECIMAL, NumberBase.DECIMAL, "255");

        // 2. Verify all 4 Identity conversions
        assertConversion("101101", NumberBase.BINARY, NumberBase.BINARY, "101101");
        assertConversion("752", NumberBase.OCTAL, NumberBase.OCTAL, "752");
        assertConversion("4096", NumberBase.DECIMAL, NumberBase.DECIMAL, "4096");
        assertConversion("1a2b", NumberBase.HEXADECIMAL, NumberBase.HEXADECIMAL, "1A2B");

        // 3. Verify Zero and Leading Zeros
        assertConversion("0", NumberBase.DECIMAL, NumberBase.BINARY, "0");
        assertConversion("0000", NumberBase.BINARY, NumberBase.HEXADECIMAL, "0");
        assertConversion("0001011", NumberBase.BINARY, NumberBase.DECIMAL, "11");
        assertConversion("007F", NumberBase.HEXADECIMAL, NumberBase.OCTAL, "177");

        // 4. Verify Large BigInteger (> 64-bit unsigned max: 18446744073709551615)
        String hugeDec = "340282366920938463463374607431768211455"; // 2^128 - 1
        String hugeHex = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        assertConversion(hugeDec, NumberBase.DECIMAL, NumberBase.HEXADECIMAL, hugeHex);
        assertConversion(hugeHex, NumberBase.HEXADECIMAL, NumberBase.DECIMAL, hugeDec);

        // 5. Verify Input Validation Rejections
        assertInvalid("", NumberBase.DECIMAL, "Blank input");
        assertInvalid("   ", NumberBase.BINARY, "Whitespace-only input");
        assertInvalid("-101", NumberBase.BINARY, "Negative binary input");
        assertInvalid("-42", NumberBase.DECIMAL, "Negative decimal input");
        assertInvalid("+42", NumberBase.DECIMAL, "Plus sign input");
        assertInvalid("10.5", NumberBase.DECIMAL, "Fractional decimal input");
        assertInvalid("101.01", NumberBase.BINARY, "Fractional binary input");
        assertInvalid("10201", NumberBase.BINARY, "Digit '2' in Binary");
        assertInvalid("789", NumberBase.OCTAL, "Digit '8' in Octal");
        assertInvalid("12A4", NumberBase.DECIMAL, "Hex digit 'A' in Decimal");
        assertInvalid("19G", NumberBase.HEXADECIMAL, "Out-of-range letter 'G' in Hexadecimal");

        // 6. Verify Bundled IBM Plex Font Loading & Typographic Hierarchy
        assertBundledFont(ConverterFrame.getTitleFont(), "IBM Plex Sans", 18, "Application Title (IBM Plex Sans SemiBold)");
        assertBundledFont(ConverterFrame.getHeadingFont(), "IBM Plex Sans", 13, "Section Heading (IBM Plex Sans SemiBold)");
        assertBundledFont(ConverterFrame.getLabelFont(), "IBM Plex Sans", 13, "Field Label (IBM Plex Sans Medium)");
        assertBundledFont(ConverterFrame.getControlFont(), "IBM Plex Sans", 14, "Primary Control (IBM Plex Sans Medium)");
        assertBundledFont(ConverterFrame.getButtonFont(), "IBM Plex Sans", 13, "Primary Button (IBM Plex Sans Medium)");
        assertBundledFont(ConverterFrame.getBodyFont(), "IBM Plex Sans", 13, "Body Text (IBM Plex Sans Regular)");
        assertBundledFont(ConverterFrame.getResultMonoFont(), "IBM Plex Mono", 18, "Final Result (IBM Plex Mono Medium)");
        assertBundledFont(ConverterFrame.getStepsMonoFont(), "IBM Plex Mono", 13, "Calculation Steps (IBM Plex Mono Regular)");

        System.out.println("------------------------------------------------------------------------");
        System.out.println("  Total Passed: " + passed + "  |  Total Failed: " + failed);
        System.out.println("========================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertConversion(
            String rawInput,
            NumberBase source,
            NumberBase target,
            String expectedOutput) {
        ValidationResult val = InputValidator.validate(rawInput, source);
        if (!val.isValid()) {
            failed++;
            System.out.println("  [FAIL] Expected valid input '" + rawInput + "' for " + source
                    + ", but got error: " + val.getErrorMessage());
            return;
        }
        ConversionResult result = NumberSystemConverter.convert(val, source, target);
        boolean outputMatches = expectedOutput.equals(result.getConvertedOutput());
        boolean stepsNonEmpty = result.getStepByStepExplanation() != null
                && !result.getStepByStepExplanation().isEmpty();
        BigInteger expectedBigInt = new BigInteger(val.getNormalizedInput(), source.getRadix());
        boolean bigIntMatches = expectedBigInt.equals(result.getDecimalValue());

        if (outputMatches && stepsNonEmpty && bigIntMatches) {
            passed++;
            System.out.println("  [PASS] (" + rawInput + ")" + source.getSubscript()
                    + " -> (" + result.getConvertedOutput() + ")" + target.getSubscript());
        } else {
            failed++;
            System.out.println("  [FAIL] (" + rawInput + ")" + source.getSubscript()
                    + " -> Expected '" + expectedOutput + "', got '" + result.getConvertedOutput() + "'");
        }
    }

    private static void assertInvalid(String rawInput, NumberBase source, String description) {
        ValidationResult val = InputValidator.validate(rawInput, source);
        if (!val.isValid() && val.getErrorMessage() != null && !val.getErrorMessage().isEmpty()) {
            passed++;
            System.out.println("  [PASS] Rejected invalid input (" + description + "): " + val.getErrorMessage());
        } else {
            failed++;
            System.out.println("  [FAIL] Failed to reject invalid input (" + description + "): '" + rawInput + "'");
        }
    }

    private static void assertBundledFont(Font font, String expectedFamilyPrefix, int expectedSize, String role) {
        if (font != null
                && font.getFontName().startsWith(expectedFamilyPrefix)
                && font.getSize() == expectedSize) {
            passed++;
            System.out.println("  [PASS] Loaded bundled font for " + role + ": "
                    + font.getFontName() + " (" + font.getSize() + "pt)");
        } else {
            failed++;
            System.out.println("  [FAIL] Bundled font check failed for " + role + ": got "
                    + (font == null ? "null" : font.getFontName() + " (" + font.getSize() + "pt)"));
        }
    }
}
