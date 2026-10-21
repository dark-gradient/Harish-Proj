/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * ValidationResult.java
 * Immutable value object representing the outcome of validating user input
 * against the selected source NumberBase.
 */
public final class ValidationResult {
    private final boolean valid;
    private final String rawTrimmedInput;
    private final String normalizedInput;
    private final boolean hadLeadingZeros;
    private final String errorMessage;
    private final String detailedGuidance;

    private ValidationResult(
            boolean valid,
            String rawTrimmedInput,
            String normalizedInput,
            boolean hadLeadingZeros,
            String errorMessage,
            String detailedGuidance) {
        this.valid = valid;
        this.rawTrimmedInput = rawTrimmedInput;
        this.normalizedInput = normalizedInput;
        this.hadLeadingZeros = hadLeadingZeros;
        this.errorMessage = errorMessage;
        this.detailedGuidance = detailedGuidance;
    }

    public static ValidationResult success(String rawTrimmedInput, String normalizedInput, boolean hadLeadingZeros) {
        return new ValidationResult(true, rawTrimmedInput, normalizedInput, hadLeadingZeros, "", "");
    }

    public static ValidationResult failure(String rawTrimmedInput, String errorMessage, String detailedGuidance) {
        return new ValidationResult(false, rawTrimmedInput, "", false, errorMessage, detailedGuidance);
    }

    public boolean isValid() {
        return valid;
    }

    public String getRawTrimmedInput() {
        return rawTrimmedInput;
    }

    public String getNormalizedInput() {
        return normalizedInput;
    }

    public boolean hadLeadingZeros() {
        return hadLeadingZeros;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDetailedGuidance() {
        return detailedGuidance;
    }
}
