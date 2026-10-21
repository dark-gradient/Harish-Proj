import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * NumberSystemConverter.java
 * Core mathematical conversion engine using java.math.BigInteger.
 * Supports all 4 bases (Binary, Octal, Decimal, Hexadecimal), including all
 * 6 bidirectional pairs (12 cross-base conversions) and 4 identity conversions.
 * Generates detailed, textbook-quality step-by-step mathematical explanations:
 *   - Positional Power Expansion (Source Base -> Decimal)
 *   - Repeated Division-Remainder Method (Decimal -> Target Base)
 *   - Direct Bit-Grouping Method (3-bit Octal / 4-bit Hexadecimal) alongside
 *     the Decimal Bridge for Binary <-> Octal, Binary <-> Hex, and Octal <-> Hex.
 */
public final class NumberSystemConverter {

    private NumberSystemConverter() {
        // Utility class; prevent instantiation
    }

    /**
     * Performs the conversion and builds the complete step-by-step calculation breakdown.
     *
     * @param validationResult a valid ValidationResult from InputValidator
     * @param sourceBase       the source NumberBase
     * @param targetBase       the target NumberBase
     * @return ConversionResult containing the final output and full step-by-step derivation
     */
    public static ConversionResult convert(
            ValidationResult validationResult,
            NumberBase sourceBase,
            NumberBase targetBase) {
        if (validationResult == null || !validationResult.isValid()) {
            throw new IllegalArgumentException("Cannot convert an invalid input.");
        }

        String rawInput = validationResult.getRawTrimmedInput();
        String normalizedInput = validationResult.getNormalizedInput();
        boolean hadLeadingZeros = validationResult.hadLeadingZeros();

        // Parse to exact BigInteger using Positional Expansion
        BigInteger decimalValue = parseToBigInteger(normalizedInput, sourceBase);
        String convertedOutput = formatFromBigInteger(decimalValue, targetBase);

        String steps = buildCompleteStepByStep(
                rawInput,
                normalizedInput,
                hadLeadingZeros,
                sourceBase,
                targetBase,
                decimalValue,
                convertedOutput);

        return new ConversionResult(
                sourceBase,
                targetBase,
                rawInput,
                normalizedInput,
                decimalValue,
                convertedOutput,
                steps);
    }

    /**
     * Computes the exact BigInteger value of a normalized string in the given base
     * using explicit positional arithmetic.
     */
    public static BigInteger parseToBigInteger(String normalizedInput, NumberBase base) {
        BigInteger radix = BigInteger.valueOf(base.getRadix());
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < normalizedInput.length(); i++) {
            int digitVal = base.digitToValue(normalizedInput.charAt(i));
            result = result.multiply(radix).add(BigInteger.valueOf(digitVal));
        }
        return result;
    }

    /**
     * Converts a non-negative BigInteger into its uppercase string representation
     * in the specified target base using repeated Euclidean division.
     */
    public static String formatFromBigInteger(BigInteger value, NumberBase targetBase) {
        if (value.compareTo(BigInteger.ZERO) == 0) {
            return "0";
        }
        BigInteger radix = BigInteger.valueOf(targetBase.getRadix());
        StringBuilder sb = new StringBuilder();
        BigInteger current = value;
        while (current.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] qr = current.divideAndRemainder(radix);
            int remainder = qr[1].intValue();
            sb.append(NumberBase.valueToDigit(remainder));
            current = qr[0];
        }
        return sb.reverse().toString();
    }

    private static String buildCompleteStepByStep(
            String rawInput,
            String normalizedInput,
            boolean hadLeadingZeros,
            NumberBase sourceBase,
            NumberBase targetBase,
            BigInteger decimalValue,
            String convertedOutput) {

        StringBuilder sb = new StringBuilder();

        // Header summary block
        sb.append("========================================================================\n");
        sb.append("  CONVERSION SUMMARY\n");
        sb.append("========================================================================\n");
        sb.append("  Source Base      : ").append(sourceBase.getDisplayName()).append("\n");
        sb.append("  Target Base      : ").append(targetBase.getDisplayName()).append("\n");
        if (hadLeadingZeros) {
            sb.append("  Original Input   : (").append(rawInput).append(")").append(sourceBase.getSubscript()).append("\n");
            sb.append("  Normalized Input : (").append(normalizedInput).append(")").append(sourceBase.getSubscript())
                    .append("  [Leading zeros stripped; value unchanged]\n");
        } else {
            sb.append("  Input Value      : (").append(normalizedInput).append(")").append(sourceBase.getSubscript()).append("\n");
        }
        sb.append("  Final Result     : (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");
        sb.append("========================================================================\n\n");

        // Case 1: Same base (Identity Conversion)
        if (sourceBase == targetBase) {
            sb.append("STEP 1: IDENTITY BASE CHECK\n");
            sb.append("------------------------------------------------------------------------\n");
            sb.append("  The source base and target base are both ").append(sourceBase.getDisplayName()).append(".\n");
            if (hadLeadingZeros) {
                sb.append("  Removing redundant leading zeros from '").append(rawInput)
                        .append("' yields '").append(normalizedInput).append("'.\n\n");
            } else {
                sb.append("  No base conversion is required; the digits remain identical.\n\n");
            }
            sb.append("STEP 2: POSITIONAL VALUE VERIFICATION\n");
            sb.append("------------------------------------------------------------------------\n");
            sb.append(buildPositionalExpansionSteps(normalizedInput, sourceBase, decimalValue));
            sb.append("\nFINAL ANSWER:\n");
            sb.append("  (").append(rawInput).append(")").append(sourceBase.getSubscript())
                    .append(" = (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");
            return sb.toString();
        }

        // Case 2: Source is non-Decimal and Target is Decimal (Positional Power Expansion)
        if (sourceBase != NumberBase.DECIMAL && targetBase == NumberBase.DECIMAL) {
            sb.append("STEP 1: POSITIONAL POWER EXPANSION (")
                    .append(sourceBase.getShortName().toUpperCase())
                    .append(" TO DECIMAL)\n");
            sb.append("------------------------------------------------------------------------\n");
            sb.append(buildPositionalExpansionSteps(normalizedInput, sourceBase, decimalValue));
            sb.append("\nFINAL ANSWER:\n");
            sb.append("  (").append(normalizedInput).append(")").append(sourceBase.getSubscript())
                    .append(" = (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");
            return sb.toString();
        }

        // Case 3: Source is Decimal and Target is non-Decimal (Repeated Euclidean Division)
        if (sourceBase == NumberBase.DECIMAL && targetBase != NumberBase.DECIMAL) {
            sb.append("STEP 1: REPEATED DIVISION BY TARGET BASE (DECIMAL TO ")
                    .append(targetBase.getShortName().toUpperCase())
                    .append(")\n");
            sb.append("------------------------------------------------------------------------\n");
            sb.append(buildRepeatedDivisionSteps(decimalValue, targetBase, convertedOutput));
            sb.append("\nFINAL ANSWER:\n");
            sb.append("  (").append(normalizedInput).append(")").append(sourceBase.getSubscript())
                    .append(" = (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");
            return sb.toString();
        }

        // Case 4: Non-Decimal to Non-Decimal (Binary <-> Octal, Binary <-> Hex, Octal <-> Hex)
        // Show BOTH Method A (Direct Bit-Grouping) and Method B (Intermediate Decimal Bridge)
        sb.append("METHOD A: DIRECT BIT-GROUPING METHOD (")
                .append(sourceBase.getShortName().toUpperCase())
                .append(" -> ")
                .append(targetBase.getShortName().toUpperCase())
                .append(")\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(buildBitGroupingSteps(normalizedInput, sourceBase, targetBase, decimalValue, convertedOutput));
        sb.append("\n");

        sb.append("METHOD B: INTERMEDIATE DECIMAL BRIDGE VERIFICATION\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append("  Part B1 — Convert ").append(sourceBase.getDisplayName()).append(" to Decimal (Base 10):\n\n");
        sb.append(indentBlock(buildPositionalExpansionSteps(normalizedInput, sourceBase, decimalValue), "  "));
        sb.append("\n");
        sb.append("  Part B2 — Convert Decimal (").append(decimalValue).append(")₁₀ to ")
                .append(targetBase.getDisplayName()).append(":\n\n");
        sb.append(indentBlock(buildRepeatedDivisionSteps(decimalValue, targetBase, convertedOutput), "  "));

        sb.append("\n========================================================================\n");
        sb.append("  FINAL VERIFIED RESULT: (").append(normalizedInput).append(")").append(sourceBase.getSubscript())
                .append(" = (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");
        sb.append("========================================================================\n");

        return sb.toString();
    }

    private static String buildPositionalExpansionSteps(
            String normalizedInput,
            NumberBase sourceBase,
            BigInteger decimalValue) {

        StringBuilder sb = new StringBuilder();
        int radix = sourceBase.getRadix();
        int length = normalizedInput.length();
        BigInteger bigRadix = BigInteger.valueOf(radix);

        sb.append("Formula: Decimal Value = Σ (digit × ").append(radix).append("^position), ")
                .append("where rightmost position = 0\n\n");

        if (normalizedInput.equals("0")) {
            sb.append("  Position 0 : 0 × ").append(radix).append("^0 = 0 × 1 = 0\n");
            sb.append("  Total Sum  : (0)₁₀\n");
            return sb.toString();
        }

        StringBuilder symbolicLine = new StringBuilder();
        symbolicLine.append("(").append(normalizedInput).append(")").append(sourceBase.getSubscript()).append(" = ");
        List<String> symbolicTerms = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            char ch = normalizedInput.charAt(i);
            int digitVal = sourceBase.digitToValue(ch);
            int exponent = length - 1 - i;
            if (sourceBase == NumberBase.HEXADECIMAL && ch >= 'A' && ch <= 'F') {
                symbolicTerms.add("(" + ch + "[" + digitVal + "] × " + radix + "^" + exponent + ")");
            } else {
                symbolicTerms.add("(" + digitVal + " × " + radix + "^" + exponent + ")");
            }
        }
        symbolicLine.append(String.join(" + ", symbolicTerms));
        sb.append("  ").append(symbolicLine).append("\n\n");

        sb.append("  Positional Breakdown (Left to Right):\n");
        List<String> evaluatedTerms = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            char ch = normalizedInput.charAt(i);
            int digitVal = sourceBase.digitToValue(ch);
            int exponent = length - 1 - i;
            BigInteger power = bigRadix.pow(exponent);
            BigInteger product = power.multiply(BigInteger.valueOf(digitVal));
            evaluatedTerms.add(product.toString());

            String digitLabel = (sourceBase == NumberBase.HEXADECIMAL && ch >= 'A' && ch <= 'F')
                    ? String.format("'%c' (%2d)", ch, digitVal)
                    : String.format("'%c'     ", ch);

            sb.append(String.format(
                    "    • Position %2d : Digit %s × %d^%-2d = %2d × %-12s = %s%n",
                    exponent,
                    digitLabel,
                    radix,
                    exponent,
                    digitVal,
                    power.toString(),
                    product.toString()));
        }

        sb.append("\n  Summing Evaluated Terms:\n");
        sb.append("    = ").append(String.join(" + ", evaluatedTerms)).append("\n");
        sb.append("    = (").append(decimalValue.toString()).append(")₁₀\n");

        return sb.toString();
    }

    private static String buildRepeatedDivisionSteps(
            BigInteger decimalValue,
            NumberBase targetBase,
            String convertedOutput) {

        StringBuilder sb = new StringBuilder();
        int radix = targetBase.getRadix();
        BigInteger bigRadix = BigInteger.valueOf(radix);

        sb.append("Method: Repeatedly divide the decimal number by ").append(radix)
                .append(" and record each remainder.\n");
        sb.append("        Read the remainders from bottom (last step) to top (first step).\n\n");

        if (decimalValue.compareTo(BigInteger.ZERO) == 0) {
            sb.append("  Step 1 : 0 ÷ ").append(radix).append(" = 0, Remainder = 0\n");
            sb.append("  Result : (0)").append(targetBase.getSubscript()).append("\n");
            return sb.toString();
        }

        sb.append(String.format(
                "  %-6s | %-24s | %-18s | %-12s | %-10s%n",
                "Step",
                "Division (N ÷ " + radix + ")",
                "Quotient",
                "Remainder",
                targetBase.getShortName() + " Digit"));
        sb.append("  -------+--------------------------+--------------------+--------------+------------\n");

        BigInteger current = decimalValue;
        int step = 1;
        List<Character> digitsBottomUp = new ArrayList<>();

        while (current.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] qr = current.divideAndRemainder(bigRadix);
            BigInteger quotient = qr[0];
            int remainder = qr[1].intValue();
            char targetDigit = NumberBase.valueToDigit(remainder);
            digitsBottomUp.add(targetDigit);

            String divisionExpr = current.toString() + " ÷ " + radix;
            String remainderNote = (targetBase == NumberBase.HEXADECIMAL && remainder >= 10)
                    ? remainder + " -> " + targetDigit
                    : String.valueOf(targetDigit);

            sb.append(String.format(
                    "  %-6d | %-24s | %-18s | %-12d | %-10s%n",
                    step,
                    divisionExpr,
                    quotient.toString(),
                    remainder,
                    remainderNote));

            current = quotient;
            step++;
        }

        List<String> reversedDigits = new ArrayList<>();
        for (int i = digitsBottomUp.size() - 1; i >= 0; i--) {
            reversedDigits.add(String.valueOf(digitsBottomUp.get(i)));
        }

        sb.append("\n  Reading Remainders from Bottom to Top (Most Significant to Least Significant):\n");
        sb.append("    ").append(String.join(" -> ", reversedDigits)).append("\n");
        sb.append("    = (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");

        return sb.toString();
    }

    private static String buildBitGroupingSteps(
            String normalizedInput,
            NumberBase sourceBase,
            NumberBase targetBase,
            BigInteger decimalValue,
            String convertedOutput) {

        StringBuilder sb = new StringBuilder();

        if (sourceBase == NumberBase.BINARY && (targetBase == NumberBase.OCTAL || targetBase == NumberBase.HEXADECIMAL)) {
            int groupSize = (targetBase == NumberBase.OCTAL) ? 3 : 4;
            sb.append("Principle: Since ").append(targetBase.getRadix()).append(" = 2^").append(groupSize)
                    .append(", each ").append(targetBase.getShortName())
                    .append(" digit corresponds to exactly ").append(groupSize).append(" binary bits.\n\n");

            String padded = padBinaryToGroupSize(normalizedInput, groupSize);
            int addedZeros = padded.length() - normalizedInput.length();

            sb.append("  1. Pad binary number on the left with ").append(addedZeros)
                    .append(" leading zero(s) to make length a multiple of ").append(groupSize).append(":\n");
            sb.append("     Raw Binary    : ").append(normalizedInput).append(" (").append(normalizedInput.length()).append(" bits)\n");
            sb.append("     Padded Binary : ").append(formatGroups(padded, groupSize)).append(" (").append(padded.length()).append(" bits)\n\n");

            sb.append("  2. Convert each ").append(groupSize).append("-bit group into its ")
                    .append(targetBase.getShortName()).append(" digit:\n");

            List<String> groups = splitIntoGroups(padded, groupSize);
            List<String> mappedDigits = new ArrayList<>();
            for (int i = 0; i < groups.size(); i++) {
                String group = groups.get(i);
                int val = Integer.parseInt(group, 2);
                char digit = NumberBase.valueToDigit(val);
                mappedDigits.add(String.valueOf(digit));
                sb.append(String.format(
                        "     • Group %2d : (%s)₂ = %s = %2d  -->  '%c'%n",
                        (i + 1),
                        group,
                        describeBitGroupWeights(group),
                        val,
                        digit));
            }

            String joined = String.join("", mappedDigits);
            sb.append("\n  3. Combine Resulting Digits:\n");
            sb.append("     ").append(String.join("  ", groups)).append("  -->  ").append(joined).append("\n");
            sb.append("     Normalized Output: (").append(convertedOutput).append(")").append(targetBase.getSubscript()).append("\n");
            return sb.toString();
        }

        if ((sourceBase == NumberBase.OCTAL || sourceBase == NumberBase.HEXADECIMAL) && targetBase == NumberBase.BINARY) {
            int groupSize = (sourceBase == NumberBase.OCTAL) ? 3 : 4;
            sb.append("Principle: Since ").append(sourceBase.getRadix()).append(" = 2^").append(groupSize)
                    .append(", replace each ").append(sourceBase.getShortName())
                    .append(" digit with its ").append(groupSize).append("-bit binary equivalent.\n\n");

            sb.append("  1. Expand each ").append(sourceBase.getShortName()).append(" digit to ")
                    .append(groupSize).append(" binary bits:\n");

            List<String> bitGroups = new ArrayList<>();
            for (int i = 0; i < normalizedInput.length(); i++) {
                char ch = normalizedInput.charAt(i);
                int val = sourceBase.digitToValue(ch);
                String binGroup = toFixedWidthBinary(val, groupSize);
                bitGroups.add(binGroup);
                sb.append(String.format(
                        "     • Digit %2d : '%c' (Decimal %2d)  -->  (%s)₂%n",
                        (i + 1),
                        ch,
                        val,
                        binGroup));
            }

            String concatenated = String.join("", bitGroups);
            sb.append("\n  2. Concatenate all ").append(groupSize).append("-bit binary groups:\n");
            sb.append("     ").append(String.join(" ", bitGroups)).append(" = ").append(concatenated).append("\n");
            if (!concatenated.equals(convertedOutput)) {
                sb.append("  3. Strip leading zeros to obtain canonical Binary form:\n");
                sb.append("     (").append(convertedOutput).append(")₂\n");
            } else {
                sb.append("  3. Result in Binary: (").append(convertedOutput).append(")₂\n");
            }
            return sb.toString();
        }

        int sourceBits = (sourceBase == NumberBase.OCTAL) ? 3 : 4;
        int targetBits = (targetBase == NumberBase.OCTAL) ? 3 : 4;

        sb.append("Principle: Convert ").append(sourceBase.getShortName())
                .append(" to Binary (").append(sourceBits).append("-bit groups), then regroup into ")
                .append(targetBits).append("-bit blocks for ").append(targetBase.getShortName()).append(".\n\n");

        sb.append("  Step A1 — Expand each ").append(sourceBase.getShortName())
                .append(" digit into ").append(sourceBits).append(" binary bits:\n");
        List<String> sourceGroups = new ArrayList<>();
        for (int i = 0; i < normalizedInput.length(); i++) {
            char ch = normalizedInput.charAt(i);
            int val = sourceBase.digitToValue(ch);
            String binGroup = toFixedWidthBinary(val, sourceBits);
            sourceGroups.add(binGroup);
            sb.append(String.format("     • '%c' (%2d)  -->  %s%n", ch, val, binGroup));
        }

        String binaryBridge = formatFromBigInteger(decimalValue, NumberBase.BINARY);
        sb.append("     Combined Binary : ").append(String.join(" ", sourceGroups))
                .append("  -->  (").append(binaryBridge).append(")₂\n\n");

        String paddedForTarget = padBinaryToGroupSize(binaryBridge, targetBits);
        List<String> targetGroupList = splitIntoGroups(paddedForTarget, targetBits);

        sb.append("  Step A2 — Regroup (").append(binaryBridge).append(")₂ into ")
                .append(targetBits).append("-bit blocks (right-to-left) for ").append(targetBase.getShortName()).append(":\n");
        sb.append("     Padded ").append(targetBits).append("-Bit Groups : ")
                .append(String.join(" ", targetGroupList)).append("\n");

        for (int i = 0; i < targetGroupList.size(); i++) {
            String grp = targetGroupList.get(i);
            int val = Integer.parseInt(grp, 2);
            char digit = NumberBase.valueToDigit(val);
            sb.append(String.format("     • Group %2d : (%s)₂ = %2d  -->  '%c'%n", (i + 1), grp, val, digit));
        }

        sb.append("\n  Result via Bit-Regrouping: (").append(convertedOutput).append(")")
                .append(targetBase.getSubscript()).append("\n");

        return sb.toString();
    }

    private static String padBinaryToGroupSize(String binary, int groupSize) {
        int remainder = binary.length() % groupSize;
        if (remainder == 0) {
            return binary;
        }
        int padCount = groupSize - remainder;
        return "0".repeat(padCount) + binary;
    }

    private static List<String> splitIntoGroups(String str, int groupSize) {
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < str.length(); i += groupSize) {
            groups.add(str.substring(i, Math.min(str.length(), i + groupSize)));
        }
        return groups;
    }

    private static String formatGroups(String padded, int groupSize) {
        return String.join(" ", splitIntoGroups(padded, groupSize));
    }

    private static String toFixedWidthBinary(int value, int width) {
        String bin = Integer.toBinaryString(value);
        if (bin.length() >= width) {
            return bin;
        }
        return "0".repeat(width - bin.length()) + bin;
    }

    private static String describeBitGroupWeights(String bitGroup) {
        List<String> terms = new ArrayList<>();
        int len = bitGroup.length();
        for (int i = 0; i < len; i++) {
            int bit = bitGroup.charAt(i) - '0';
            int weight = 1 << (len - 1 - i);
            terms.add("(" + bit + "×" + weight + ")");
        }
        return String.join(" + ", terms);
    }

    private static String indentBlock(String text, String indent) {
        String[] lines = text.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].isEmpty()) {
                sb.append(indent).append(lines[i]);
            }
            if (i < lines.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
