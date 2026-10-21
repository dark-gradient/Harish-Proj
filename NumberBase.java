/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * NumberBase.java
 * Represents the four supported positional number systems:
 * Binary (Base 2), Octal (Base 8), Decimal (Base 10), and Hexadecimal (Base 16).
 */
public enum NumberBase {
    BINARY(2, "Binary", "Binary (Base 2)", "₂", "0, 1"),
    OCTAL(8, "Octal", "Octal (Base 8)", "₈", "0–7"),
    DECIMAL(10, "Decimal", "Decimal (Base 10)", "₁₀", "0–9"),
    HEXADECIMAL(16, "Hexadecimal", "Hexadecimal (Base 16)", "₁₆", "0–9, A–F (or a–f)");

    private final int radix;
    private final String shortName;
    private final String displayName;
    private final String subscript;
    private final String allowedDigitsDescription;

    NumberBase(int radix, String shortName, String displayName, String subscript, String allowedDigitsDescription) {
        this.radix = radix;
        this.shortName = shortName;
        this.displayName = displayName;
        this.subscript = subscript;
        this.allowedDigitsDescription = allowedDigitsDescription;
    }

    public int getRadix() {
        return radix;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSubscript() {
        return subscript;
    }

    public String getAllowedDigitsDescription() {
        return allowedDigitsDescription;
    }

    /**
     * Checks whether a single character is a valid digit for this number base.
     *
     * @param ch the character to test
     * @return true if valid for this base, false otherwise
     */
    public boolean isValidDigit(char ch) {
        char upper = Character.toUpperCase(ch);
        return switch (this) {
            case BINARY -> upper == '0' || upper == '1';
            case OCTAL -> upper >= '0' && upper <= '7';
            case DECIMAL -> upper >= '0' && upper <= '9';
            case HEXADECIMAL -> (upper >= '0' && upper <= '9') || (upper >= 'A' && upper <= 'F');
        };
    }

    /**
     * Converts a valid character in this base to its integer numeric value (0 to 15).
     *
     * @param ch the digit character
     * @return numeric value of the digit
     */
    public int digitToValue(char ch) {
        char upper = Character.toUpperCase(ch);
        if (upper >= '0' && upper <= '9') {
            return upper - '0';
        }
        if (upper >= 'A' && upper <= 'F') {
            return 10 + (upper - 'A');
        }
        throw new IllegalArgumentException("Invalid digit '" + ch + "' for " + displayName);
    }

    /**
     * Converts a numeric digit value (0 to 15) to its canonical uppercase character.
     *
     * @param value numeric value between 0 and 15
     * @return uppercase character ('0'-'9' or 'A'-'F')
     */
    public static char valueToDigit(int value) {
        if (value >= 0 && value <= 9) {
            return (char) ('0' + value);
        }
        if (value >= 10 && value <= 15) {
            return (char) ('A' + (value - 10));
        }
        throw new IllegalArgumentException("Digit value out of range (0-15): " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
