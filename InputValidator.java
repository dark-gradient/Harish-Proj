/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * InputValidator.java
 * Validates raw user strings against the selected source NumberBase.
 * Supports non-negative whole numbers, zero ("0"), and leading zeros.
 * Rejects blank inputs, negative numbers, explicit plus signs, fractions/radix points,
 * embedded spaces, and out-of-range digits with clear, specific error messages.
 */
public final class InputValidator {

    private InputValidator() {
        // Utility class; prevent instantiation
    }

    /**
     * Validates a raw input string against the specified source base.
     *
     * @param rawInput   the raw string from the input JTextField
     * @param sourceBase the selected source NumberBase
     * @return ValidationResult containing normalized digits or an understandable error message
     */
    public static ValidationResult validate(String rawInput, NumberBase sourceBase) {
        if (sourceBase == null) {
            return ValidationResult.failure(
                    "",
                    "Please select a valid source number base.",
                    "Select Binary, Octal, Decimal, or Hexadecimal from the 'From Base' dropdown.");
        }

        if (rawInput == null || rawInput.trim().isEmpty()) {
            return ValidationResult.failure(
                    "",
                    "Input cannot be empty. Please enter a non-negative whole number.",
                    buildGuidanceBlock(
                            sourceBase,
                            "Blank or whitespace-only input was provided.",
                            "Enter at least one valid digit for " + sourceBase.getDisplayName() + "."));
        }

        String trimmed = rawInput.trim();

        // 1. Check for negative numbers
        if (trimmed.startsWith("-")) {
            return ValidationResult.failure(
                    trimmed,
                    "Negative numbers are not supported. Please enter a non-negative whole number (0 or greater).",
                    buildGuidanceBlock(
                            sourceBase,
                            "Input '" + trimmed + "' begins with a negative sign ('-').",
                            "Remove the '-' sign and enter a non-negative whole number."));
        }

        // 2. Check for explicit plus sign
        if (trimmed.startsWith("+")) {
            return ValidationResult.failure(
                    trimmed,
                    "Plus signs ('+') are not allowed. Please enter digits only without a sign prefix.",
                    buildGuidanceBlock(
                            sourceBase,
                            "Input '" + trimmed + "' begins with a '+' sign.",
                            "Remove the '+' sign and enter only digits valid for " + sourceBase.getDisplayName() + "."));
        }

        // 3. Check for fractional / radix points
        if (trimmed.contains(".") || trimmed.contains(",")) {
            char separator = trimmed.contains(".") ? '.' : ',';
            return ValidationResult.failure(
                    trimmed,
                    "Fractional numbers ('" + separator + "') are not supported. Please enter a whole number only.",
                    buildGuidanceBlock(
                            sourceBase,
                            "Input '" + trimmed + "' contains a fractional or grouping separator ('" + separator + "').",
                            "This converter operates strictly on non-negative whole integers. Remove the fractional part or separator."));
        }

        // 4. Check for internal whitespace
        for (int i = 0; i < trimmed.length(); i++) {
            if (Character.isWhitespace(trimmed.charAt(i))) {
                return ValidationResult.failure(
                        trimmed,
                        "Spaces are not allowed inside the number (found space at position " + (i + 1) + ").",
                        buildGuidanceBlock(
                                sourceBase,
                                "Input '" + trimmed + "' contains embedded whitespace at character position " + (i + 1) + ".",
                                "Enter a continuous string of digits without spaces."));
            }
        }

        // 5. Check for common prefix mistakes (e.g., 0x for Hex or 0b for Binary)
        String upper = trimmed.toUpperCase();
        if (sourceBase == NumberBase.HEXADECIMAL && upper.startsWith("0X")) {
            return ValidationResult.failure(
                    trimmed,
                    "Do not include the '0x' prefix for Hexadecimal input. Enter only hexadecimal digits (0–9, A–F).",
                    buildGuidanceBlock(
                            sourceBase,
                            "Input '" + trimmed + "' includes the programming prefix '0x'.",
                            "Remove '0x' and enter only the hexadecimal digits (for example, enter '"
                                    + (upper.length() > 2 ? upper.substring(2) : "1A") + "')."));
        }
        if (sourceBase == NumberBase.BINARY && upper.startsWith("0B")) {
            return ValidationResult.failure(
                    trimmed,
                    "Do not include the '0b' prefix for Binary input. Enter only binary digits (0 and 1).",
                    buildGuidanceBlock(
                            sourceBase,
                            "Input '" + trimmed + "' includes the programming prefix '0b'.",
                            "Remove '0b' and enter only 0s and 1s."));
        }

        // 6. Validate every character against the selected source base
        for (int i = 0; i < upper.length(); i++) {
            char ch = trimmed.charAt(i);
            if (!sourceBase.isValidDigit(ch)) {
                return ValidationResult.failure(
                        trimmed,
                        "Invalid character '" + ch + "' at position " + (i + 1) + " for "
                                + sourceBase.getDisplayName() + ". Allowed digits: "
                                + sourceBase.getAllowedDigitsDescription() + ".",
                        buildGuidanceBlock(
                                sourceBase,
                                "Character '" + ch + "' (position " + (i + 1) + " of \"" + trimmed
                                        + "\") is not a valid digit in Base " + sourceBase.getRadix() + ".",
                                "Replace or remove '" + ch + "'. Valid digits for "
                                        + sourceBase.getDisplayName() + " are: "
                                        + sourceBase.getAllowedDigitsDescription() + "."));
            }
        }

        // 7. Normalize uppercase and strip redundant leading zeros (preserving single "0" for zero)
        int firstNonZero = 0;
        while (firstNonZero < upper.length() - 1 && upper.charAt(firstNonZero) == '0') {
            firstNonZero++;
        }
        String normalized = upper.substring(firstNonZero);
        boolean hadLeadingZeros = firstNonZero > 0;

        return ValidationResult.success(upper, normalized, hadLeadingZeros);
    }

    private static String buildGuidanceBlock(NumberBase base, String problem, String solution) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("  INPUT VALIDATION ERROR\n");
        sb.append("========================================================================\n\n");
        sb.append("  Selected Source Base : ").append(base.getDisplayName()).append("\n");
        sb.append("  Allowed Characters   : ").append(base.getAllowedDigitsDescription()).append("\n");
        sb.append("  Supported Domain     : Non-negative whole numbers (0, 1, 2, ...)\n\n");
        sb.append("  Issue Detected:\n");
        sb.append("    • ").append(problem).append("\n\n");
        sb.append("  How to Fix:\n");
        sb.append("    • ").append(solution).append("\n");
        sb.append("========================================================================\n");
        return sb.toString();
    }
}
