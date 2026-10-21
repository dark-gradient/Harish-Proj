# EGB1201 Number System Converter — Java Swing Desktop Application Plan

A standalone, light-themed Java 17+ Swing desktop application (`javax.swing` & `java.awt`) engineered for the **EGB1201 Java Programming Project Review**. It converts arbitrary-precision non-negative whole numbers across **Binary (Base 2)**, **Octal (Base 8)**, **Decimal (Base 10)**, and **Hexadecimal (Base 16)** with strict input validation and comprehensive, textbook-clear mathematical calculation steps.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> All core architectural and pedagogical preferences have been confirmed and incorporated into this plan.

- **Confirmed Decision 1 — Dual Pedagogical Step-by-Step Explanations**: For conversions between Binary, Octal, and Hexadecimal, the step-by-step calculation output will display **both** the **Direct Bit-Grouping Method** (3-bit Octal groups / 4-bit Hexadecimal nibbles) and the **Intermediate Decimal Bridge Method** (positional power expansion $\sum d_i \times b^i$ followed by repeated division-remainder steps).
- **Confirmed Decision 2 — Default-Package Modular OOP Architecture**: The Java source code will be structured into clean, single-responsibility classes in the default package so it can be compiled and executed directly with `javac *.java && java NumberConverterApp` during a college lab demonstration without deep package directory boilerplate.
- **Confirmed Decision 3 — Streamlined Action Controls**: The Swing control bar will include **Convert** (also triggered via `Enter` in the input field), **Swap Bases** (`⇄`), and **Clear / Reset** buttons.

---

## 1. Overview & Core Concept

- **What It Does**: Accepts a non-negative integer string in a chosen source base, validates every character against that base's legal digit set, computes the exact converted representation in the chosen target base using `java.math.BigInteger`, and generates a structured, human-readable mathematical derivation showing every step of the conversion.
- **Target Audience / Persona**: EGB1201 Java Programming instructors, reviewers, and students evaluating object-oriented design, Swing layout craftsmanship, event-driven programming, input validation, and mathematical correctness.
- **Key Value**: Combines arbitrary-precision reliability (`BigInteger`) with clear academic formatting (aligned division tables, explicit exponent terms, and bit-grouping tables) in a clean, distraction-free desktop GUI.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Launch & Initial State**:
   - The application opens centered on screen (`JFrame`, minimum size `760 × 640`, default `860 × 700`) with a clean header banner, an input/configuration card at the top, a prominent final result panel in the middle, and a scrollable monospace step-by-step calculation area below.
   - Default bases are pre-selected as **Decimal (Base 10) → Binary (Base 2)** with a clear hint label indicating valid characters for the active source base.
2. **Base Selection & Dynamic Hinting**:
   - Selecting a source base in the `From Base` `JComboBox` immediately updates the helper hint label below the input field (e.g., *"Allowed digits for Hexadecimal (Base 16): 0–9, A–F"*).
   - Clicking **Swap Bases** exchanges the selected source and target bases; if a valid converted result is already displayed, it populates the input field with that result and immediately re-runs the conversion in the reverse direction.
3. **Input Validation & Conversion Execution**:
   - Clicking **Convert** (or pressing `Enter` inside the input `JTextField`) trims outer whitespace and runs strict validation:
     - **Blank input**: Displays an inline validation alert and explanatory message in the steps area (*"Input cannot be empty. Please enter a non-negative whole number."*).
     - **Negative input (`-`) or plus sign (`+`)**: Rejects with a specific message explaining that only non-negative whole numbers are supported.
     - **Fractional input (`.`, `,`)**: Rejects with a clear message explaining that fractional/radix-point values are outside scope.
     - **Illegal base characters**: Pinpoints the first invalid character and its position (e.g., *"Invalid character '8' at position 3 for Octal (Base 8). Allowed digits: 0–7."*).
   - Valid inputs (including `"0"` and inputs with leading zeros such as `"0001011"`) proceed to conversion. If leading zeros were present, the step-by-step output notes both the raw input and its normalized canonical form.
4. **Result & Step-by-Step Inspection**:
   - The **Final Result Panel** displays the canonical converted value in a large, selectable read-only `JTextField` with a subscript/badge label showing the target base (e.g., `Base 16`).
   - The **Step-by-Step Calculations Area** (`JTextArea` inside `JScrollPane`) renders formatted sections with aligned columns using a monospace font (`Consolas` / `Font.MONOSPACED`), automatically scrolling to the top of the derivation.
5. **Clear / Reset**:
   - Clicking **Clear** resets the input field, clears any error state, restores the default status message, and sets keyboard focus back to the input `JTextField`.

### Visual Identity & Theme (Swing Light Theme)
- **Aesthetic Direction**: Clean Academic Utility — crisp borders, generous internal padding (`EmptyBorder`), clear section titles (`TitledBorder` / custom compound borders), and high-contrast typography suited for classroom projectors and lab monitors.
- **Color Palette (AWT `Color` Constants)**:
  - **Window Background**: Soft Alabaster (`#F8FAFC` / `new Color(248, 250, 252)`)
  - **Card / Panel Surface**: Pure White (`#FFFFFF` / `new Color(255, 255, 255)`)
  - **Card Border / Divider**: Cool Slate Hairline (`#CBD5E1` / `new Color(203, 213, 225)`)
  - **Primary Accent (Header & Convert Button)**: Academic Navy/Royal Blue (`#1D4ED8` / `new Color(29, 78, 216)`) with white label text
  - **Secondary Controls (Swap / Clear)**: Neutral Slate Surface (`#F1F5F9` / `new Color(241, 245, 249)`) with dark slate text (`#1E293B`)
  - **Result Highlight Surface**: Subtle Emerald/Blue Tint (`#EFF6FF` / `new Color(239, 246, 255)`) for valid conversions; Soft Crimson Tint (`#FEF2F2` / `new Color(254, 242, 242)`) with `#B91C1C` text when displaying validation errors
- **Typography (`java.awt.Font`)**:
  - **Application Title**: `SansSerif`, `Font.BOLD`, `20pt`
  - **Section Headers & Labels**: `SansSerif`, `Font.BOLD`, `13pt`
  - **Controls & ComboBoxes**: `SansSerif`, `Font.PLAIN`, `14pt`
  - **Final Result Display**: `Font.MONOSPACED`, `Font.BOLD`, `20pt`
  - **Step-by-Step Calculation Area**: `Font.MONOSPACED`, `Font.PLAIN`, `13pt` (ensures vertical alignment of division tables, exponents, and bit groups)
- **Layout Managers Used**:
  - **`BorderLayout`**: Root frame layout separating the top header/input zone from the central result + scrollable step-by-step display and bottom status bar.
  - **`GridBagLayout`**: Precision alignment of labels, input `JTextField`, `From Base` and `To Base` `JComboBox` selectors, and the inline validation hint inside the configuration panel.
  - **`GridLayout` / `FlowLayout`**: Uniform spacing and sizing for the action buttons (`Convert`, `Swap Bases`, `Clear`).

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: `java.math.BigInteger` Everywhere (Zero Primitive Overflow)**
  - *Chosen Approach*: Parse, validate, and compute every positional expansion and division-remainder step using `BigInteger`.
  - *Why*: Standard 32-bit `int` overflows at 31 binary bits and 64-bit `long` overflows at 63 binary bits. Using `BigInteger` allows students or examiners to test 128-bit or 256-bit binary/hexadecimal numbers with complete mathematical accuracy in both the final output and every intermediate calculation step.
  - *Alternatives Considered*: `Long.parseLong(input, radix)` — rejected because long binary strings immediately throw `NumberFormatException` on overflow.

- **Decision 2: Dual Pedagogical Explanation for Binary ↔ Octal ↔ Hexadecimal**
  - *Chosen Approach*: When converting between any two non-decimal bases (Binary ↔ Octal, Binary ↔ Hexadecimal, Octal ↔ Hexadecimal), generate **Method A (Direct Bit-Grouping)** first (padding binary strings to multiples of 3 or 4 bits and mapping each group in a clean lookup table) followed by **Method B (Decimal Bridge Verification)** showing the full Base-to-Decimal summation and Decimal-to-Target repeated division.
  - *Why*: EGB1201 reviews frequently ask how shortcut bit-grouping relates to general positional radix conversion. Showing both proves the student's code understands both computer architecture shortcuts and general radix arithmetic, and proves both paths yield the identical answer.

- **Decision 3: All 16 Base Combinations (6 Bidirectional Pairs + 4 Identity Cases)**
  - *Chosen Approach*: Support every permutation of `{Binary, Octal, Decimal, Hexadecimal}` × `{Binary, Octal, Decimal, Hexadecimal}`:
    1. **Decimal → Binary / Octal / Hex**: Repeated Euclidean division by target radix $R$ ($N = Q \times R + r$), recording quotients and remainders (with Hex remainders $10\text{–}15$ mapped to $\text{A–F}$) and reading remainders bottom-to-top.
    2. **Binary / Octal / Hex → Decimal**: Positional weight expansion $\sum_{i=0}^{k-1} d_i \times B^{\text{pos}}$, listing each digit's positional power, evaluated product, and final sum.
    3. **Binary ↔ Octal & Binary ↔ Hex**: Method A (3-bit or 4-bit grouping table) + Method B (Source → Decimal expansion + Decimal → Target division).
    4. **Octal ↔ Hexadecimal**: Method A (Octal → 3-bit Binary → 4-bit Regrouping → Hex, or vice versa) + Method B (Octal ↔ Decimal ↔ Hex bridge).
    5. **Same Base → Same Base (Identity)**: Explains why the number is already in the target base, shows its positional value breakdown, and normalizes any leading zeros.

---

## 4. Technical Architecture & Data Strategy

### Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────────┐
│                   NumberConverterApp (Entry Point / main)                  │
│     • Sets system/cross-platform Swing UI defaults on EventQueue           │
│     • Instantiates ConverterFrame on SwingUtilities.invokeLater(...)       │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                        ConverterFrame (javax.swing.JFrame)                 │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ HeaderPanel (BorderLayout)                                           │  │
│  │  • Title JLabel ("Number System Converter — EGB1201") & Subtitle     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ InputControlPanel (GridBagLayout + GridLayout Button Row)            │  │
│  │  • inputField (JTextField) + validationHintLabel (JLabel)            │  │
│  │  • sourceBaseCombo (JComboBox<NumberBase>)                           │  │
│  │  • targetBaseCombo (JComboBox<NumberBase>)                           │  │
│  │  • Action Buttons: [Convert]  [Swap Bases ⇄]  [Clear]                │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ ResultAndStepsPanel (BorderLayout)                                   │  │
│  │  • ResultBanner (JPanel): resultField (JTextField) + baseBadgeLabel  │  │
│  │  • StepsScrollPane (JScrollPane) -> stepsTextArea (JTextArea)        │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└───────────────┬─────────────────────────────────────────┬──────────────────┘
                │ Calls validate(...)                     │ Calls convert(...)
                ▼                                         ▼
┌───────────────────────────────────────┐ ┌──────────────────────────────────┐
│        InputValidator                 │ │      NumberSystemConverter       │
│  • Rejects null/blank input           │ │  • Converts across all 6 pairs   │
│  • Rejects '-' sign & '+' sign        │ │  • Uses BigInteger arithmetic    │
│  • Rejects '.' / ',' fractions        │ │  • Builds positional expansion   │
│  • Checks char legality for NumberBase│ │  • Builds division-remainder log │
│  • Returns ValidationResult           │ │  • Builds 3-bit/4-bit group logs │
└───────────────────┬───────────────────┘ └───────────────┬──────────────────┘
                    │                                     │
                    ▼                                     ▼
┌───────────────────────────────────────┐ ┌──────────────────────────────────┐
│     NumberBase (Standard Java Enum)   │ │    ConversionResult (Value Obj)  │
│  • BINARY (2, "Binary (Base 2)")      │ │  • rawInput & normalizedInput    │
│  • OCTAL (8, "Octal (Base 8)")        │ │  • sourceBase & targetBase       │
│  • DECIMAL (10, "Decimal (Base 10)")  │ │  • decimalValue (BigInteger)     │
│  • HEXADECIMAL (16, "Hex (Base 16)")  │ │  • formattedOutput (String)      │
│  • Allowed chars & regex rules        │ │  • stepByStepExplanation (String)│
└───────────────────────────────────────┘ └──────────────────────────────────┘
```

### Data Model & Core Classes (Default Package)
1. **`NumberBase` (Enum)**:
   - Encapsulates radix (`2`, `8`, `10`, `16`), display name, subscript notation (`₂`, `₈`, `₁₀`, `₁₆`), allowed character description, and character-level validation logic.
2. **`ValidationResult` (Immutable Record / Class)**:
   - Holds `boolean valid`, `String normalizedInput`, `String rawTrimmedInput`, `boolean hadLeadingZeros`, and `String errorMessage`.
3. **`ConversionResult` (Immutable Record / Class)**:
   - Holds the source and target `NumberBase`, original and normalized input strings, intermediate `BigInteger` decimal value, uppercase target base result string, and the complete multi-line `stepByStepExplanation`.
4. **`InputValidator` (Utility Class)**:
   - Performs ordered validation checks so error messages are maximally helpful:
     1. Empty / whitespace check
     2. Negative number check (starts with `-`)
     3. Explicit sign check (starts with `+`)
     4. Fractional / decimal-point check (contains `.` or `,`)
     5. Embedded whitespace check
     6. Per-character base validity check (reports the exact offending character and its 1-based index)
5. **`NumberSystemConverter` (Core Mathematical Engine)**:
   - Pure, stateless conversion and step-formatting methods using `BigInteger`:
     - `explainToDecimalExpansion(String normalizedInput, NumberBase sourceBase)`
     - `explainFromDecimalDivision(BigInteger decimalValue, NumberBase targetBase)`
     - `explainBitGrouping(String normalizedInput, NumberBase sourceBase, NumberBase targetBase)`
6. **`ConverterFrame` & `NumberConverterApp` (Swing GUI Layer)**:
   - Constructs all Swing components, wires `ActionListener` and `ItemListener` handlers via `java.awt.event`, updates visual states on valid vs. invalid input, and launches cleanly on the Event Dispatch Thread (`SwingUtilities.invokeLater`).

### Interactive Component & State Mapping
- **`Convert` Button (`ActionListener`) & Input Field (`ActionListener` on Enter)**:
  - Reads `inputField.getText()`, `sourceBaseCombo.getSelectedItem()`, and `targetBaseCombo.getSelectedItem()`.
  - Invokes `InputValidator.validate(rawText, sourceBase)`.
  - On **invalid input**: Highlights the status/result banner in error styling, displays the specific error message, and prints troubleshooting guidance in `stepsTextArea`.
  - On **valid input**: Invokes `NumberSystemConverter.convert(...)`, updates `resultField` with the converted string, updates the base badge, populates `stepsTextArea` with the formatted derivation, and resets scroll position to `0`.
- **`Swap Bases` Button (`ActionListener`)**:
  - Swaps the selected items of `sourceBaseCombo` and `targetBaseCombo`.
  - If a valid conversion result is currently present in `resultField`, copies that result into `inputField` and automatically triggers `performConversion()` so the user immediately sees the reverse calculation.
- **`Clear` Button (`ActionListener`)**:
  - Clears `inputField` and `resultField`, resets error styling to neutral light theme, displays a welcoming quick-guide in `stepsTextArea`, and calls `inputField.requestFocusInWindow()`.
- **`sourceBaseCombo` & `targetBaseCombo` (`ItemListener`)**:
  - Updates the `validationHintLabel` dynamically to show valid digits for the newly selected source base.
