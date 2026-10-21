import java.math.BigInteger;

/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * ConversionResult.java
 * Immutable value object holding the final converted value, intermediate
 * BigInteger decimal representation, and full step-by-step mathematical explanation.
 */
public final class ConversionResult {
    private final NumberBase sourceBase;
    private final NumberBase targetBase;
    private final String rawInput;
    private final String normalizedInput;
    private final BigInteger decimalValue;
    private final String convertedOutput;
    private final String stepByStepExplanation;

    public ConversionResult(
            NumberBase sourceBase,
            NumberBase targetBase,
            String rawInput,
            String normalizedInput,
            BigInteger decimalValue,
            String convertedOutput,
            String stepByStepExplanation) {
        this.sourceBase = sourceBase;
        this.targetBase = targetBase;
        this.rawInput = rawInput;
        this.normalizedInput = normalizedInput;
        this.decimalValue = decimalValue;
        this.convertedOutput = convertedOutput;
        this.stepByStepExplanation = stepByStepExplanation;
    }

    public NumberBase getSourceBase() {
        return sourceBase;
    }

    public NumberBase getTargetBase() {
        return targetBase;
    }

    public String getRawInput() {
        return rawInput;
    }

    public String getNormalizedInput() {
        return normalizedInput;
    }

    public BigInteger getDecimalValue() {
        return decimalValue;
    }

    public String getConvertedOutput() {
        return convertedOutput;
    }

    public String getStepByStepExplanation() {
        return stepByStepExplanation;
    }
}
