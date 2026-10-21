/**
 * Exact 1:1 TypeScript mirror of the Java 17+ BigInteger conversion and validation engine
 * (NumberBase.java, ValidationResult.java, ConversionResult.java, InputValidator.java, NumberSystemConverter.java)
 * for interactive simulation of the Swing ConverterFrame in the preview viewport.
 */

export enum NumberBase {
  BINARY = 'BINARY',
  OCTAL = 'OCTAL',
  DECIMAL = 'DECIMAL',
  HEXADECIMAL = 'HEXADECIMAL',
}

export interface NumberBaseInfo {
  id: NumberBase;
  radix: number;
  shortName: string;
  displayName: string;
  subscript: string;
  allowedDigitsDescription: string;
}

export const NUMBER_BASES: Record<NumberBase, NumberBaseInfo> = {
  [NumberBase.BINARY]: {
    id: NumberBase.BINARY,
    radix: 2,
    shortName: 'Binary',
    displayName: 'Binary (Base 2)',
    subscript: '₂',
    allowedDigitsDescription: '0, 1',
  },
  [NumberBase.OCTAL]: {
    id: NumberBase.OCTAL,
    radix: 8,
    shortName: 'Octal',
    displayName: 'Octal (Base 8)',
    subscript: '₈',
    allowedDigitsDescription: '0–7',
  },
  [NumberBase.DECIMAL]: {
    id: NumberBase.DECIMAL,
    radix: 10,
    shortName: 'Decimal',
    displayName: 'Decimal (Base 10)',
    subscript: '₁₀',
    allowedDigitsDescription: '0–9',
  },
  [NumberBase.HEXADECIMAL]: {
    id: NumberBase.HEXADECIMAL,
    radix: 16,
    shortName: 'Hexadecimal',
    displayName: 'Hexadecimal (Base 16)',
    subscript: '₁₆',
    allowedDigitsDescription: '0–9, A–F (or a–f)',
  },
};

export const BASE_LIST: NumberBaseInfo[] = [
  NUMBER_BASES[NumberBase.BINARY],
  NUMBER_BASES[NumberBase.OCTAL],
  NUMBER_BASES[NumberBase.DECIMAL],
  NUMBER_BASES[NumberBase.HEXADECIMAL],
];

export function isValidDigit(base: NumberBase, ch: string): boolean {
  const upper = ch.toUpperCase();
  switch (base) {
    case NumberBase.BINARY:
      return upper === '0' || upper === '1';
    case NumberBase.OCTAL:
      return upper >= '0' && upper <= '7';
    case NumberBase.DECIMAL:
      return upper >= '0' && upper <= '9';
    case NumberBase.HEXADECIMAL:
      return (upper >= '0' && upper <= '9') || (upper >= 'A' && upper <= 'F');
  }
}

export function digitToValue(base: NumberBase, ch: string): number {
  const upper = ch.toUpperCase();
  const code = upper.charCodeAt(0);
  if (code >= 48 && code <= 57) {
    return code - 48;
  }
  if (code >= 65 && code <= 70) {
    return 10 + (code - 65);
  }
  throw new Error(`Invalid digit '${ch}' for ${NUMBER_BASES[base].displayName}`);
}

export function valueToDigit(value: number): string {
  if (value >= 0 && value <= 9) {
    return String(value);
  }
  if (value >= 10 && value <= 15) {
    return String.fromCharCode(65 + (value - 10));
  }
  throw new Error(`Digit value out of range (0-15): ${value}`);
}

export interface ValidationResult {
  valid: boolean;
  rawTrimmedInput: string;
  normalizedInput: string;
  hadLeadingZeros: boolean;
  errorMessage: string;
  detailedGuidance: string;
}

function buildGuidanceBlock(base: NumberBaseInfo, problem: string, solution: string): string {
  return [
    '========================================================================',
    '  INPUT VALIDATION ERROR',
    '========================================================================',
    '',
    `  Selected Source Base : ${base.displayName}`,
    `  Allowed Characters   : ${base.allowedDigitsDescription}`,
    '  Supported Domain     : Non-negative whole numbers (0, 1, 2, ...)',
    '',
    '  Issue Detected:',
    `    • ${problem}`,
    '',
    '  How to Fix:',
    `    • ${solution}`,
    '========================================================================',
    '',
  ].join('\n');
}

export function validateInput(rawInput: string, sourceBase: NumberBase): ValidationResult {
  const baseInfo = NUMBER_BASES[sourceBase];

  if (!rawInput || rawInput.trim().length === 0) {
    return {
      valid: false,
      rawTrimmedInput: '',
      normalizedInput: '',
      hadLeadingZeros: false,
      errorMessage: 'Input cannot be empty. Please enter a non-negative whole number.',
      detailedGuidance: buildGuidanceBlock(
        baseInfo,
        'Blank or whitespace-only input was provided.',
        `Enter at least one valid digit for ${baseInfo.displayName}.`
      ),
    };
  }

  const trimmed = rawInput.trim();

  if (trimmed.startsWith('-')) {
    return {
      valid: false,
      rawTrimmedInput: trimmed,
      normalizedInput: '',
      hadLeadingZeros: false,
      errorMessage: 'Negative numbers are not supported. Please enter a non-negative whole number (0 or greater).',
      detailedGuidance: buildGuidanceBlock(
        baseInfo,
        `Input '${trimmed}' begins with a negative sign ('-').`,
        "Remove the '-' sign and enter a non-negative whole number."
      ),
    };
  }

  if (trimmed.startsWith('+')) {
    return {
      valid: false,
      rawTrimmedInput: trimmed,
      normalizedInput: '',
      hadLeadingZeros: false,
      errorMessage: "Plus signs ('+') are not allowed. Please enter digits only without a sign prefix.",
      detailedGuidance: buildGuidanceBlock(
        baseInfo,
        `Input '${trimmed}' begins with a '+' sign.`,
        `Remove the '+' sign and enter only digits valid for ${baseInfo.displayName}.`
      ),
    };
  }

  if (trimmed.includes('.') || trimmed.includes(',')) {
    const separator = trimmed.includes('.') ? '.' : ',';
    return {
      valid: false,
      rawTrimmedInput: trimmed,
      normalizedInput: '',
      hadLeadingZeros: false,
      errorMessage: `Fractional numbers ('${separator}') are not supported. Please enter a whole number only.`,
      detailedGuidance: buildGuidanceBlock(
        baseInfo,
        `Input '${trimmed}' contains a fractional or grouping separator ('${separator}').`,
        'This converter operates strictly on non-negative whole integers. Remove the fractional part or separator.'
      ),
    };
  }

  for (let i = 0; i < trimmed.length; i++) {
    if (/\s/.test(trimmed[i])) {
      return {
        valid: false,
        rawTrimmedInput: trimmed,
        normalizedInput: '',
        hadLeadingZeros: false,
        errorMessage: `Spaces are not allowed inside the number (found space at position ${i + 1}).`,
        detailedGuidance: buildGuidanceBlock(
          baseInfo,
          `Input '${trimmed}' contains embedded whitespace at character position ${i + 1}.`,
          'Enter a continuous string of digits without spaces.'
        ),
      };
    }
  }

  const upper = trimmed.toUpperCase();
  if (sourceBase === NumberBase.HEXADECIMAL && upper.startsWith('0X')) {
    return {
      valid: false,
      rawTrimmedInput: trimmed,
      normalizedInput: '',
      hadLeadingZeros: false,
      errorMessage: "Do not include the '0x' prefix for Hexadecimal input. Enter only hexadecimal digits (0–9, A–F).",
      detailedGuidance: buildGuidanceBlock(
        baseInfo,
        `Input '${trimmed}' includes the programming prefix '0x'.`,
        `Remove '0x' and enter only the hexadecimal digits (for example, enter '${
          upper.length > 2 ? upper.slice(2) : '1A'
        }').`
      ),
    };
  }

  if (sourceBase === NumberBase.BINARY && upper.startsWith('0B')) {
    return {
      valid: false,
      rawTrimmedInput: trimmed,
      normalizedInput: '',
      hadLeadingZeros: false,
      errorMessage: "Do not include the '0b' prefix for Binary input. Enter only binary digits (0 and 1).",
      detailedGuidance: buildGuidanceBlock(
        baseInfo,
        `Input '${trimmed}' includes the programming prefix '0b'.`,
        "Remove '0b' and enter only 0s and 1s."
      ),
    };
  }

  for (let i = 0; i < upper.length; i++) {
    const ch = trimmed[i];
    if (!isValidDigit(sourceBase, ch)) {
      return {
        valid: false,
        rawTrimmedInput: trimmed,
        normalizedInput: '',
        hadLeadingZeros: false,
        errorMessage: `Invalid character '${ch}' at position ${i + 1} for ${baseInfo.displayName}. Allowed digits: ${baseInfo.allowedDigitsDescription}.`,
        detailedGuidance: buildGuidanceBlock(
          baseInfo,
          `Character '${ch}' (position ${i + 1} of "${trimmed}") is not a valid digit in Base ${baseInfo.radix}.`,
          `Replace or remove '${ch}'. Valid digits for ${baseInfo.displayName} are: ${baseInfo.allowedDigitsDescription}.`
        ),
      };
    }
  }

  let firstNonZero = 0;
  while (firstNonZero < upper.length - 1 && upper[firstNonZero] === '0') {
    firstNonZero++;
  }
  const normalized = upper.slice(firstNonZero);
  const hadLeadingZeros = firstNonZero > 0;

  return {
    valid: true,
    rawTrimmedInput: upper,
    normalizedInput: normalized,
    hadLeadingZeros,
    errorMessage: '',
    detailedGuidance: '',
  };
}

export interface ConversionResult {
  sourceBase: NumberBase;
  targetBase: NumberBase;
  rawInput: string;
  normalizedInput: string;
  decimalValue: bigint;
  convertedOutput: string;
  stepByStepExplanation: string;
}

export function parseToBigInt(normalizedInput: string, base: NumberBase): bigint {
  const radix = BigInt(NUMBER_BASES[base].radix);
  let result = 0n;
  for (let i = 0; i < normalizedInput.length; i++) {
    const digitVal = BigInt(digitToValue(base, normalizedInput[i]));
    result = result * radix + digitVal;
  }
  return result;
}

export function formatFromBigInt(value: bigint, targetBase: NumberBase): string {
  if (value === 0n) {
    return '0';
  }
  const radix = BigInt(NUMBER_BASES[targetBase].radix);
  const chars: string[] = [];
  let current = value;
  while (current > 0n) {
    const remainder = Number(current % radix);
    chars.push(valueToDigit(remainder));
    current = current / radix;
  }
  return chars.reverse().join('');
}

export function convertNumber(
  validationResult: ValidationResult,
  sourceBase: NumberBase,
  targetBase: NumberBase
): ConversionResult {
  const rawInput = validationResult.rawTrimmedInput;
  const normalizedInput = validationResult.normalizedInput;
  const hadLeadingZeros = validationResult.hadLeadingZeros;

  const decimalValue = parseToBigInt(normalizedInput, sourceBase);
  const convertedOutput = formatFromBigInt(decimalValue, targetBase);

  const stepByStepExplanation = buildCompleteStepByStep(
    rawInput,
    normalizedInput,
    hadLeadingZeros,
    sourceBase,
    targetBase,
    decimalValue,
    convertedOutput
  );

  return {
    sourceBase,
    targetBase,
    rawInput,
    normalizedInput,
    decimalValue,
    convertedOutput,
    stepByStepExplanation,
  };
}

function buildCompleteStepByStep(
  rawInput: string,
  normalizedInput: string,
  hadLeadingZeros: boolean,
  sourceBase: NumberBase,
  targetBase: NumberBase,
  decimalValue: bigint,
  convertedOutput: string
): string {
  const srcInfo = NUMBER_BASES[sourceBase];
  const tgtInfo = NUMBER_BASES[targetBase];
  const lines: string[] = [];

  lines.push('========================================================================');
  lines.push('  CONVERSION SUMMARY');
  lines.push('========================================================================');
  lines.push(`  Source Base      : ${srcInfo.displayName}`);
  lines.push(`  Target Base      : ${tgtInfo.displayName}`);
  if (hadLeadingZeros) {
    lines.push(`  Original Input   : (${rawInput})${srcInfo.subscript}`);
    lines.push(
      `  Normalized Input : (${normalizedInput})${srcInfo.subscript}  [Leading zeros stripped; value unchanged]`
    );
  } else {
    lines.push(`  Input Value      : (${normalizedInput})${srcInfo.subscript}`);
  }
  lines.push(`  Final Result     : (${convertedOutput})${tgtInfo.subscript}`);
  lines.push('========================================================================');
  lines.push('');

  // Case 1: Identity
  if (sourceBase === targetBase) {
    lines.push('STEP 1: IDENTITY BASE CHECK');
    lines.push('------------------------------------------------------------------------');
    lines.push(`  The source base and target base are both ${srcInfo.displayName}.`);
    if (hadLeadingZeros) {
      lines.push(`  Removing redundant leading zeros from '${rawInput}' yields '${normalizedInput}'.`);
    } else {
      lines.push('  No base conversion is required; the digits remain identical.');
    }
    lines.push('');
    lines.push('STEP 2: POSITIONAL VALUE VERIFICATION');
    lines.push('------------------------------------------------------------------------');
    lines.push(buildPositionalExpansionSteps(normalizedInput, sourceBase, decimalValue));
    lines.push('FINAL ANSWER:');
    lines.push(`  (${rawInput})${srcInfo.subscript} = (${convertedOutput})${tgtInfo.subscript}`);
    return lines.join('\n');
  }

  // Case 2: Non-Decimal -> Decimal
  if (sourceBase !== NumberBase.DECIMAL && targetBase === NumberBase.DECIMAL) {
    lines.push(`STEP 1: POSITIONAL POWER EXPANSION (${srcInfo.shortName.toUpperCase()} TO DECIMAL)`);
    lines.push('------------------------------------------------------------------------');
    lines.push(buildPositionalExpansionSteps(normalizedInput, sourceBase, decimalValue));
    lines.push('FINAL ANSWER:');
    lines.push(`  (${normalizedInput})${srcInfo.subscript} = (${convertedOutput})${tgtInfo.subscript}`);
    return lines.join('\n');
  }

  // Case 3: Decimal -> Non-Decimal
  if (sourceBase === NumberBase.DECIMAL && targetBase !== NumberBase.DECIMAL) {
    lines.push(`STEP 1: REPEATED DIVISION BY TARGET BASE (DECIMAL TO ${tgtInfo.shortName.toUpperCase()})`);
    lines.push('------------------------------------------------------------------------');
    lines.push(buildRepeatedDivisionSteps(decimalValue, targetBase, convertedOutput));
    lines.push('FINAL ANSWER:');
    lines.push(`  (${normalizedInput})${srcInfo.subscript} = (${convertedOutput})${tgtInfo.subscript}`);
    return lines.join('\n');
  }

  // Case 4: Non-Decimal -> Non-Decimal (Both Direct Bit-Grouping and Decimal Bridge)
  lines.push(
    `METHOD A: DIRECT BIT-GROUPING METHOD (${srcInfo.shortName.toUpperCase()} -> ${tgtInfo.shortName.toUpperCase()})`
  );
  lines.push('------------------------------------------------------------------------');
  lines.push(buildBitGroupingSteps(normalizedInput, sourceBase, targetBase, decimalValue, convertedOutput));

  lines.push('METHOD B: INTERMEDIATE DECIMAL BRIDGE VERIFICATION');
  lines.push('------------------------------------------------------------------------');
  lines.push(`  Part B1 — Convert ${srcInfo.displayName} to Decimal (Base 10):`);
  lines.push('');
  lines.push(indentBlock(buildPositionalExpansionSteps(normalizedInput, sourceBase, decimalValue), '  '));
  lines.push(`  Part B2 — Convert Decimal (${decimalValue.toString()})₁₀ to ${tgtInfo.displayName}:`);
  lines.push('');
  lines.push(indentBlock(buildRepeatedDivisionSteps(decimalValue, targetBase, convertedOutput), '  '));
  lines.push('========================================================================');
  lines.push(
    `  FINAL VERIFIED RESULT: (${normalizedInput})${srcInfo.subscript} = (${convertedOutput})${tgtInfo.subscript}`
  );
  lines.push('========================================================================');

  return lines.join('\n');
}

function buildPositionalExpansionSteps(
  normalizedInput: string,
  sourceBase: NumberBase,
  decimalValue: bigint
): string {
  const srcInfo = NUMBER_BASES[sourceBase];
  const radix = srcInfo.radix;
  const bigRadix = BigInt(radix);
  const length = normalizedInput.length;
  const lines: string[] = [];

  lines.push(`Formula: Decimal Value = Σ (digit × ${radix}^position), where rightmost position = 0`);
  lines.push('');

  if (normalizedInput === '0') {
    lines.push(`  Position 0 : 0 × ${radix}^0 = 0 × 1 = 0`);
    lines.push('  Total Sum  : (0)₁₀');
    lines.push('');
    return lines.join('\n');
  }

  const symbolicTerms: string[] = [];
  for (let i = 0; i < length; i++) {
    const ch = normalizedInput[i];
    const digitVal = digitToValue(sourceBase, ch);
    const exponent = length - 1 - i;
    if (sourceBase === NumberBase.HEXADECIMAL && ch >= 'A' && ch <= 'F') {
      symbolicTerms.push(`(${ch}[${digitVal}] × ${radix}^${exponent})`);
    } else {
      symbolicTerms.push(`(${digitVal} × ${radix}^${exponent})`);
    }
  }
  lines.push(`  (${normalizedInput})${srcInfo.subscript} = ${symbolicTerms.join(' + ')}`);
  lines.push('');
  lines.push('  Positional Breakdown (Left to Right):');

  const evaluatedTerms: string[] = [];
  for (let i = 0; i < length; i++) {
    const ch = normalizedInput[i];
    const digitVal = digitToValue(sourceBase, ch);
    const exponent = length - 1 - i;
    const power = bigRadix ** BigInt(exponent);
    const product = power * BigInt(digitVal);
    evaluatedTerms.push(product.toString());

    const digitLabel =
      sourceBase === NumberBase.HEXADECIMAL && ch >= 'A' && ch <= 'F'
        ? `'${ch}' (${String(digitVal).padStart(2, ' ')})`
        : `'${ch}'     `;

    const posStr = String(exponent).padStart(2, ' ');
    const expRight = String(exponent).padEnd(2, ' ');
    const digStr = String(digitVal).padStart(2, ' ');
    const powStr = power.toString().padEnd(12, ' ');

    lines.push(
      `    • Position ${posStr} : Digit ${digitLabel} × ${radix}^${expRight} = ${digStr} × ${powStr} = ${product.toString()}`
    );
  }

  lines.push('');
  lines.push('  Summing Evaluated Terms:');
  lines.push(`    = ${evaluatedTerms.join(' + ')}`);
  lines.push(`    = (${decimalValue.toString()})₁₀`);
  lines.push('');

  return lines.join('\n');
}

function buildRepeatedDivisionSteps(
  decimalValue: bigint,
  targetBase: NumberBase,
  convertedOutput: string
): string {
  const tgtInfo = NUMBER_BASES[targetBase];
  const radix = tgtInfo.radix;
  const bigRadix = BigInt(radix);
  const lines: string[] = [];

  lines.push(`Method: Repeatedly divide the decimal number by ${radix} and record each remainder.`);
  lines.push('        Read the remainders from bottom (last step) to top (first step).');
  lines.push('');

  if (decimalValue === 0n) {
    lines.push(`  Step 1 : 0 ÷ ${radix} = 0, Remainder = 0`);
    lines.push(`  Result : (0)${tgtInfo.subscript}`);
    lines.push('');
    return lines.join('\n');
  }

  const col1 = 'Step'.padEnd(6, ' ');
  const col2 = `Division (N ÷ ${radix})`.padEnd(24, ' ');
  const col3 = 'Quotient'.padEnd(18, ' ');
  const col4 = 'Remainder'.padEnd(12, ' ');
  const col5 = `${tgtInfo.shortName} Digit`.padEnd(10, ' ');

  lines.push(`  ${col1} | ${col2} | ${col3} | ${col4} | ${col5}`);
  lines.push('  -------+--------------------------+--------------------+--------------+------------');

  let current = decimalValue;
  let step = 1;
  const digitsBottomUp: string[] = [];

  while (current > 0n) {
    const quotient = current / bigRadix;
    const remainder = Number(current % bigRadix);
    const targetDigit = valueToDigit(remainder);
    digitsBottomUp.push(targetDigit);

    const divisionExpr = `${current.toString()} ÷ ${radix}`;
    const remainderNote =
      targetBase === NumberBase.HEXADECIMAL && remainder >= 10
        ? `${remainder} -> ${targetDigit}`
        : targetDigit;

    lines.push(
      `  ${String(step).padEnd(6, ' ')} | ${divisionExpr.padEnd(24, ' ')} | ${quotient
        .toString()
        .padEnd(18, ' ')} | ${String(remainder).padEnd(12, ' ')} | ${remainderNote.padEnd(10, ' ')}`
    );

    current = quotient;
    step++;
  }

  const reversedDigits = [...digitsBottomUp].reverse();
  lines.push('');
  lines.push('  Reading Remainders from Bottom to Top (Most Significant to Least Significant):');
  lines.push(`    ${reversedDigits.join(' -> ')}`);
  lines.push(`    = (${convertedOutput})${tgtInfo.subscript}`);
  lines.push('');

  return lines.join('\n');
}

function buildBitGroupingSteps(
  normalizedInput: string,
  sourceBase: NumberBase,
  targetBase: NumberBase,
  decimalValue: bigint,
  convertedOutput: string
): string {
  const srcInfo = NUMBER_BASES[sourceBase];
  const tgtInfo = NUMBER_BASES[targetBase];
  const lines: string[] = [];

  if (sourceBase === NumberBase.BINARY && (targetBase === NumberBase.OCTAL || targetBase === NumberBase.HEXADECIMAL)) {
    const groupSize = targetBase === NumberBase.OCTAL ? 3 : 4;
    lines.push(
      `Principle: Since ${tgtInfo.radix} = 2^${groupSize}, each ${tgtInfo.shortName} digit corresponds to exactly ${groupSize} binary bits.`
    );
    lines.push('');

    const padded = padBinaryToGroupSize(normalizedInput, groupSize);
    const addedZeros = padded.length - normalizedInput.length;
    const groups = splitIntoGroups(padded, groupSize);

    lines.push(
      `  1. Pad binary number on the left with ${addedZeros} leading zero(s) to make length a multiple of ${groupSize}:`
    );
    lines.push(`     Raw Binary    : ${normalizedInput} (${normalizedInput.length} bits)`);
    lines.push(`     Padded Binary : ${groups.join(' ')} (${padded.length} bits)`);
    lines.push('');
    lines.push(`  2. Convert each ${groupSize}-bit group into its ${tgtInfo.shortName} digit:`);

    const mappedDigits: string[] = [];
    for (let i = 0; i < groups.length; i++) {
      const grp = groups[i];
      const val = parseInt(grp, 2);
      const digit = valueToDigit(val);
      mappedDigits.push(digit);
      lines.push(
        `     • Group ${String(i + 1).padStart(2, ' ')} : (${grp})₂ = ${describeBitGroupWeights(grp)} = ${String(
          val
        ).padStart(2, ' ')}  -->  '${digit}'`
      );
    }

    lines.push('');
    lines.push('  3. Combine Resulting Digits:');
    lines.push(`     ${groups.join('  ')}  -->  ${mappedDigits.join('')}`);
    lines.push(`     Normalized Output: (${convertedOutput})${tgtInfo.subscript}`);
    lines.push('');
    return lines.join('\n');
  }

  if ((sourceBase === NumberBase.OCTAL || sourceBase === NumberBase.HEXADECIMAL) && targetBase === NumberBase.BINARY) {
    const groupSize = sourceBase === NumberBase.OCTAL ? 3 : 4;
    lines.push(
      `Principle: Since ${srcInfo.radix} = 2^${groupSize}, replace each ${srcInfo.shortName} digit with its ${groupSize}-bit binary equivalent.`
    );
    lines.push('');
    lines.push(`  1. Expand each ${srcInfo.shortName} digit to ${groupSize} binary bits:`);

    const bitGroups: string[] = [];
    for (let i = 0; i < normalizedInput.length; i++) {
      const ch = normalizedInput[i];
      const val = digitToValue(sourceBase, ch);
      const binGroup = val.toString(2).padStart(groupSize, '0');
      bitGroups.push(binGroup);
      lines.push(
        `     • Digit ${String(i + 1).padStart(2, ' ')} : '${ch}' (Decimal ${String(val).padStart(
          2,
          ' '
        )})  -->  (${binGroup})₂`
      );
    }

    const concatenated = bitGroups.join('');
    lines.push('');
    lines.push(`  2. Concatenate all ${groupSize}-bit binary groups:`);
    lines.push(`     ${bitGroups.join(' ')} = ${concatenated}`);
    if (concatenated !== convertedOutput) {
      lines.push('  3. Strip leading zeros to obtain canonical Binary form:');
      lines.push(`     (${convertedOutput})₂`);
    } else {
      lines.push(`  3. Result in Binary: (${convertedOutput})₂`);
    }
    lines.push('');
    return lines.join('\n');
  }

  const sourceBits = sourceBase === NumberBase.OCTAL ? 3 : 4;
  const targetBits = targetBase === NumberBase.OCTAL ? 3 : 4;

  lines.push(
    `Principle: Convert ${srcInfo.shortName} to Binary (${sourceBits}-bit groups), then regroup into ${targetBits}-bit blocks for ${tgtInfo.shortName}.`
  );
  lines.push('');
  lines.push(`  Step A1 — Expand each ${srcInfo.shortName} digit into ${sourceBits} binary bits:`);

  const sourceGroups: string[] = [];
  for (let i = 0; i < normalizedInput.length; i++) {
    const ch = normalizedInput[i];
    const val = digitToValue(sourceBase, ch);
    const binGroup = val.toString(2).padStart(sourceBits, '0');
    sourceGroups.push(binGroup);
    lines.push(`     • '${ch}' (${String(val).padStart(2, ' ')})  -->  ${binGroup}`);
  }

  const binaryBridge = formatFromBigInt(decimalValue, NumberBase.BINARY);
  lines.push(`     Combined Binary : ${sourceGroups.join(' ')}  -->  (${binaryBridge})₂`);
  lines.push('');

  const paddedForTarget = padBinaryToGroupSize(binaryBridge, targetBits);
  const targetGroupList = splitIntoGroups(paddedForTarget, targetBits);

  lines.push(
    `  Step A2 — Regroup (${binaryBridge})₂ into ${targetBits}-bit blocks (right-to-left) for ${tgtInfo.shortName}:`
  );
  lines.push(`     Padded ${targetBits}-Bit Groups : ${targetGroupList.join(' ')}`);

  for (let i = 0; i < targetGroupList.length; i++) {
    const grp = targetGroupList[i];
    const val = parseInt(grp, 2);
    const digit = valueToDigit(val);
    lines.push(
      `     • Group ${String(i + 1).padStart(2, ' ')} : (${grp})₂ = ${String(val).padStart(2, ' ')}  -->  '${digit}'`
    );
  }

  lines.push('');
  lines.push(`  Result via Bit-Regrouping: (${convertedOutput})${tgtInfo.subscript}`);
  lines.push('');

  return lines.join('\n');
}

function padBinaryToGroupSize(binary: string, groupSize: number): string {
  const remainder = binary.length % groupSize;
  if (remainder === 0) return binary;
  return '0'.repeat(groupSize - remainder) + binary;
}

function splitIntoGroups(str: string, groupSize: number): string[] {
  const groups: string[] = [];
  for (let i = 0; i < str.length; i += groupSize) {
    groups.push(str.slice(i, i + groupSize));
  }
  return groups;
}

function describeBitGroupWeights(bitGroup: string): string {
  const terms: string[] = [];
  const len = bitGroup.length;
  for (let i = 0; i < len; i++) {
    const bit = bitGroup.charCodeAt(i) - 48;
    const weight = 1 << (len - 1 - i);
    terms.push(`(${bit}×${weight})`);
  }
  return terms.join(' + ');
}

function indentBlock(text: string, indent: string): string {
  return text
    .split('\n')
    .map((line) => (line.length > 0 ? `${indent}${line}` : ''))
    .join('\n');
}
