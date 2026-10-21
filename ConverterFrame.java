import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * ConverterFrame.java
 * Restrained, functional desktop mathematical utility built strictly with
 * javax.swing and java.awt. Uses flat surfaces, thin 1px structural dividers,
 * locally bundled IBM Plex Sans and IBM Plex Mono typography, and explicit
 * default/hover/focus/pressed/error states.
 */
public class ConverterFrame extends JFrame {

    // Target desktop dimensions (fits 1366x768 and 1440x900 displays without outer scrolling)
    private static final int DEFAULT_WIDTH = 1180;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int MIN_WIDTH = 760;
    private static final int MIN_HEIGHT = 520;

    // Strict restrained palette (no blue, green, cyan, violet, or gradients)
    private static final Color COLOR_STONE_BG = new Color(0xF2, 0xF0, 0xEC);       // #F2F0EC
    private static final Color COLOR_SURFACE = new Color(0xFC, 0xFB, 0xF8);        // #FCFBF8
    private static final Color COLOR_PRIMARY_INK = new Color(0x28, 0x25, 0x22);    // #282522
    private static final Color COLOR_SECONDARY_TEXT = new Color(0x6D, 0x66, 0x5E); // #6D665E
    private static final Color COLOR_BORDER = new Color(0xD6, 0xD0, 0xC8);         // #D6D0C8
    private static final Color COLOR_OXBLOOD = new Color(0x76, 0x28, 0x3A);        // #76283A
    private static final Color COLOR_OXBLOOD_HOVER = new Color(0x63, 0x20, 0x30);  // Darker solid oxblood for hover
    private static final Color COLOR_OXBLOOD_PRESS = new Color(0x52, 0x1A, 0x27);  // Solid pressed state
    private static final Color COLOR_PALE_ROSE = new Color(0xF3, 0xE8, 0xEB);      // #F3E8EB
    private static final Color COLOR_ERROR_TEXT = new Color(0x8C, 0x2F, 0x39);     // #8C2F39

    // Bundled IBM Plex font asset paths (relative to resources/fonts)
    private static final String PLEX_SANS_REGULAR_FILE = "IBMPlexSans-Regular.ttf";
    private static final String PLEX_SANS_MEDIUM_FILE = "IBMPlexSans-Medium.ttf";
    private static final String PLEX_SANS_SEMIBOLD_FILE = "IBMPlexSans-SemiBold.ttf";
    private static final String PLEX_MONO_REGULAR_FILE = "IBMPlexMono-Regular.ttf";
    private static final String PLEX_MONO_MEDIUM_FILE = "IBMPlexMono-Medium.ttf";

    // Base font objects loaded from local resources/fonts directory
    private static final Font BASE_SANS_REGULAR =
            loadBundledBaseFont(PLEX_SANS_REGULAR_FILE, Font.SANS_SERIF, Font.PLAIN);
    private static final Font BASE_SANS_MEDIUM =
            loadBundledBaseFont(PLEX_SANS_MEDIUM_FILE, Font.SANS_SERIF, Font.PLAIN);
    private static final Font BASE_SANS_SEMIBOLD =
            loadBundledBaseFont(PLEX_SANS_SEMIBOLD_FILE, Font.SANS_SERIF, Font.BOLD);
    private static final Font BASE_MONO_REGULAR =
            loadBundledBaseFont(PLEX_MONO_REGULAR_FILE, Font.MONOSPACED, Font.PLAIN);
    private static final Font BASE_MONO_MEDIUM =
            loadBundledBaseFont(PLEX_MONO_MEDIUM_FILE, Font.MONOSPACED, Font.BOLD);

    // IBM Plex typographic hierarchy
    // 1. IBM Plex Sans SemiBold for application title and important section headings
    private static final Font FONT_TITLE = BASE_SANS_SEMIBOLD.deriveFont(18f);
    private static final Font FONT_HEADING = BASE_SANS_SEMIBOLD.deriveFont(13f);

    // 2. IBM Plex Sans Medium for field labels, primary controls, and validation messages
    private static final Font FONT_LABEL = BASE_SANS_MEDIUM.deriveFont(13f);
    private static final Font FONT_CONTROL = BASE_SANS_MEDIUM.deriveFont(14f);
    private static final Font FONT_BUTTON = BASE_SANS_MEDIUM.deriveFont(13f);
    private static final Font FONT_VALIDATION_ERROR = BASE_SANS_MEDIUM.deriveFont(13f);

    // 3. IBM Plex Sans Regular for text input and general body/hint interface text
    private static final Font FONT_INPUT_TEXT = BASE_SANS_REGULAR.deriveFont(14f);
    private static final Font FONT_BODY = BASE_SANS_REGULAR.deriveFont(13f);
    private static final Font FONT_HINT = BASE_SANS_REGULAR.deriveFont(12f);

    // 4. IBM Plex Mono Medium for final converted result and Regular for step-by-step calculations
    private static final Font FONT_RESULT_MONO = BASE_MONO_MEDIUM.deriveFont(18f);
    private static final Font FONT_STEPS_MONO = BASE_MONO_REGULAR.deriveFont(13f);

    // Component borders for default, focus, and error states
    private static final Border INPUT_BORDER_DEFAULT = new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1),
            new EmptyBorder(5, 8, 5, 8));
    private static final Border INPUT_BORDER_FOCUS = new CompoundBorder(
            new LineBorder(COLOR_OXBLOOD, 2),
            new EmptyBorder(4, 7, 4, 7));
    private static final Border INPUT_BORDER_ERROR = new CompoundBorder(
            new LineBorder(COLOR_ERROR_TEXT, 2),
            new EmptyBorder(4, 7, 4, 7));

    private static final Border COMBO_BORDER_DEFAULT = new LineBorder(COLOR_BORDER, 1);
    private static final Border COMBO_BORDER_FOCUS = new LineBorder(COLOR_OXBLOOD, 2);

    // Swing UI components
    private final JTextField inputField;
    private final JComboBox<NumberBase> sourceBaseCombo;
    private final JComboBox<NumberBase> targetBaseCombo;
    private final JLabel validationHintLabel;
    private final JButton convertButton;

    private final JPanel resultRegionPanel;
    private final JLabel resultTitleLabel;
    private final JTextField resultOutputField;
    private final JLabel resultBaseLabel;

    private final JTextArea stepsTextArea;

    private boolean hasValidationError = false;

    public ConverterFrame() {
        super("Number System Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension initialSize = computeAdaptiveWindowSize();
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setPreferredSize(initialSize);

        getContentPane().setBackground(COLOR_STONE_BG);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // Initialize Swing components
        inputField = new JTextField();
        sourceBaseCombo = new JComboBox<>(NumberBase.values());
        targetBaseCombo = new JComboBox<>(NumberBase.values());
        validationHintLabel = new JLabel();
        convertButton = new JButton("Convert");

        resultRegionPanel = new JPanel(new BorderLayout(12, 2));
        resultTitleLabel = new JLabel("Result");
        resultOutputField = new JTextField();
        resultBaseLabel = new JLabel();

        stepsTextArea = new JTextArea();

        // Default base selection: Decimal (Base 10) -> Binary (Base 2)
        sourceBaseCombo.setSelectedItem(NumberBase.DECIMAL);
        targetBaseCombo.setSelectedItem(NumberBase.BINARY);

        // Assemble top section (Header + Input Strip + Result Band) and center Calculation Region
        JPanel topContainer = new JPanel(new BorderLayout(0, 0));
        topContainer.setBackground(COLOR_STONE_BG);
        topContainer.add(createHeaderBar(), BorderLayout.NORTH);
        topContainer.add(createInputSection(), BorderLayout.CENTER);
        topContainer.add(createResultSection(), BorderLayout.SOUTH);

        getContentPane().add(topContainer, BorderLayout.NORTH);
        getContentPane().add(createCalculationSection(), BorderLayout.CENTER);

        // Register AWT event listeners and interaction states
        registerEventHandlers();
        configureInteractionStates();

        // Initialize allowed-digits hint and empty result state
        updateAllowedDigitsHint();
        initializeEmptyResultState();

        pack();
        setSize(initialSize);
        setLocationRelativeTo(null);
    }

    /**
     * Loads a bundled TrueType font from classpath resources (/resources/fonts/ or /fonts/)
     * or the local resources/fonts/ directory, registers it with the local GraphicsEnvironment,
     * and falls back cleanly to standard logical fonts if unavailable.
     */
    private static Font loadBundledBaseFont(String fileName, String fallbackFamily, int fallbackStyle) {
        try (InputStream stream = openFontResourceStream(fileName)) {
            if (stream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
                try {
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
                } catch (Exception ignored) {
                    // Registration is optional when deriving direct Font instances
                }
                return font;
            }
        } catch (Exception ignored) {
            // Fall through to standard logical font fallback
        }
        return new Font(fallbackFamily, fallbackStyle, 13);
    }

    private static InputStream openFontResourceStream(String fileName) {
        try {
            InputStream classpathStream = ConverterFrame.class.getResourceAsStream("/resources/fonts/" + fileName);
            if (classpathStream != null) {
                return classpathStream;
            }
            classpathStream = ConverterFrame.class.getResourceAsStream("/fonts/" + fileName);
            if (classpathStream != null) {
                return classpathStream;
            }
            File localFile = new File("resources/fonts", fileName);
            if (localFile.isFile() && localFile.canRead()) {
                return new FileInputStream(localFile);
            }
        } catch (Exception ignored) {
            // Return null so caller uses clean logical font fallback
        }
        return null;
    }

    // Package-private font accessors for UIManager defaults and typography verification
    static Font getTitleFont() {
        return FONT_TITLE;
    }

    static Font getHeadingFont() {
        return FONT_HEADING;
    }

    static Font getLabelFont() {
        return FONT_LABEL;
    }

    static Font getControlFont() {
        return FONT_CONTROL;
    }

    static Font getButtonFont() {
        return FONT_BUTTON;
    }

    static Font getBodyFont() {
        return FONT_BODY;
    }

    static Font getResultMonoFont() {
        return FONT_RESULT_MONO;
    }

    static Font getStepsMonoFont() {
        return FONT_STEPS_MONO;
    }

    private Dimension computeAdaptiveWindowSize() {
        try {
            Rectangle usableBounds = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getMaximumWindowBounds();
            int width = Math.min(DEFAULT_WIDTH, Math.max(MIN_WIDTH, usableBounds.width - 40));
            int height = Math.min(DEFAULT_HEIGHT, Math.max(MIN_HEIGHT, usableBounds.height - 24));
            return new Dimension(width, height);
        } catch (Exception ignored) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int width = Math.min(DEFAULT_WIDTH, Math.max(MIN_WIDTH, screen.width - 60));
            int height = Math.min(DEFAULT_HEIGHT, Math.max(MIN_HEIGHT, screen.height - 48));
            return new Dimension(width, height);
        }
    }

    /**
     * Compact, left-aligned title bar separated by a 1px solid stone border.
     */
    private JPanel createHeaderBar() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_STONE_BG);
        headerPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel titleLabel = new JLabel("Number System Converter");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_PRIMARY_INK);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }

    /**
     * Flat input control panel using GridBagLayout.
     * Row 0: Number label and input field.
     * Row 1: Allowed-digits hint for the selected source base.
     * Row 2: From Base dropdown, To Base dropdown, and Convert button aligned on one row.
     */
    private JPanel createInputSection() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(COLOR_SURFACE);
        inputPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(12, 16, 12, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Number Label + Input Field
        JLabel inputLabel = new JLabel("Number:");
        inputLabel.setFont(FONT_LABEL);
        inputLabel.setForeground(COLOR_PRIMARY_INK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 2, 10);
        inputPanel.add(inputLabel, gbc);

        inputField.setFont(FONT_INPUT_TEXT);
        inputField.setBackground(COLOR_SURFACE);
        inputField.setForeground(COLOR_PRIMARY_INK);
        inputField.setCaretColor(COLOR_PRIMARY_INK);
        inputField.setSelectionColor(COLOR_PALE_ROSE);
        inputField.setSelectedTextColor(COLOR_PRIMARY_INK);
        inputField.setBorder(INPUT_BORDER_DEFAULT);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 2, 0);
        inputPanel.add(inputField, gbc);

        // Row 1: Concise allowed-digits hint
        validationHintLabel.setFont(FONT_HINT);
        validationHintLabel.setForeground(COLOR_SECONDARY_TEXT);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);
        inputPanel.add(validationHintLabel, gbc);

        // Row 2: From Base, To Base, and Convert button aligned on the same row
        JLabel fromLabel = new JLabel("From Base:");
        fromLabel.setFont(FONT_LABEL);
        fromLabel.setForeground(COLOR_PRIMARY_INK);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        inputPanel.add(fromLabel, gbc);

        configureComboBox(sourceBaseCombo);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 16);
        inputPanel.add(sourceBaseCombo, gbc);

        JLabel toLabel = new JLabel("To Base:");
        toLabel.setFont(FONT_LABEL);
        toLabel.setForeground(COLOR_PRIMARY_INK);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        inputPanel.add(toLabel, gbc);

        configureComboBox(targetBaseCombo);
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 12);
        inputPanel.add(targetBaseCombo, gbc);

        configurePrimaryButton(convertButton);
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.10;
        gbc.insets = new Insets(0, 0, 0, 0);
        inputPanel.add(convertButton, gbc);

        return inputPanel;
    }

    /**
     * Flat result band separated by a 1px bottom border.
     * Displays the final converted value or a clear error message.
     */
    private JPanel createResultSection() {
        resultRegionPanel.setBackground(COLOR_STONE_BG);
        resultRegionPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(10, 16, 10, 16)));

        JPanel topRow = new JPanel(new BorderLayout(8, 0));
        topRow.setOpaque(false);

        resultTitleLabel.setFont(FONT_HEADING);
        resultTitleLabel.setForeground(COLOR_PRIMARY_INK);

        resultBaseLabel.setFont(FONT_HINT);
        resultBaseLabel.setForeground(COLOR_SECONDARY_TEXT);
        resultBaseLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topRow.add(resultTitleLabel, BorderLayout.WEST);
        topRow.add(resultBaseLabel, BorderLayout.EAST);

        resultOutputField.setEditable(false);
        resultOutputField.setFont(FONT_RESULT_MONO);
        resultOutputField.setForeground(COLOR_PRIMARY_INK);
        resultOutputField.setCaretColor(COLOR_PRIMARY_INK);
        resultOutputField.setSelectionColor(COLOR_PALE_ROSE);
        resultOutputField.setSelectedTextColor(COLOR_PRIMARY_INK);
        resultOutputField.setOpaque(false);
        resultOutputField.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        resultOutputField.setPreferredSize(new Dimension(200, 26));

        resultRegionPanel.add(topRow, BorderLayout.NORTH);
        resultRegionPanel.add(resultOutputField, BorderLayout.CENTER);

        return resultRegionPanel;
    }

    /**
     * Calculation section occupying all remaining vertical height.
     */
    private JPanel createCalculationSection() {
        JPanel calcPanel = new JPanel(new BorderLayout(0, 6));
        calcPanel.setBackground(COLOR_STONE_BG);
        calcPanel.setBorder(new EmptyBorder(10, 16, 14, 16));

        JLabel calcHeaderLabel = new JLabel("Step-by-Step Mathematical Calculations");
        calcHeaderLabel.setFont(FONT_HEADING);
        calcHeaderLabel.setForeground(COLOR_PRIMARY_INK);

        stepsTextArea.setEditable(false);
        stepsTextArea.setFont(FONT_STEPS_MONO);
        stepsTextArea.setForeground(COLOR_PRIMARY_INK);
        stepsTextArea.setBackground(COLOR_SURFACE);
        stepsTextArea.setCaretColor(COLOR_PRIMARY_INK);
        stepsTextArea.setSelectionColor(COLOR_PALE_ROSE);
        stepsTextArea.setSelectedTextColor(COLOR_PRIMARY_INK);
        stepsTextArea.setLineWrap(false);
        stepsTextArea.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane scrollPane = new JScrollPane(
                stepsTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(COLOR_SURFACE);
        scrollPane.setMinimumSize(new Dimension(300, 180));

        calcPanel.add(calcHeaderLabel, BorderLayout.NORTH);
        calcPanel.add(scrollPane, BorderLayout.CENTER);

        return calcPanel;
    }

    private void configureComboBox(final JComboBox<NumberBase> comboBox) {
        comboBox.setFont(FONT_CONTROL);
        comboBox.setBackground(COLOR_SURFACE);
        comboBox.setForeground(COLOR_PRIMARY_INK);
        comboBox.setBorder(COMBO_BORDER_DEFAULT);
        comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        comboBox.setPreferredSize(new Dimension(200, 34));

        // Custom list cell renderer with IBM Plex Sans and pale neutral-rose selection (#F3E8EB)
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setFont(index == -1 ? FONT_CONTROL : FONT_BODY);
                label.setBorder(new EmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    label.setBackground(COLOR_PALE_ROSE);
                    label.setForeground(COLOR_PRIMARY_INK);
                } else {
                    label.setBackground(COLOR_SURFACE);
                    label.setForeground(COLOR_PRIMARY_INK);
                }
                return label;
            }
        });
    }

    private void configurePrimaryButton(final JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_OXBLOOD);
        button.setForeground(COLOR_SURFACE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(COLOR_OXBLOOD, 1),
                new EmptyBorder(6, 16, 6, 16)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 34));
        button.setMinimumSize(new Dimension(110, 34));
    }

    /**
     * Wires explicit default, hover, keyboard focus, and pressed states for all interactive controls.
     */
    private void configureInteractionStates() {
        // Input field focus indicator
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!hasValidationError) {
                    inputField.setBorder(INPUT_BORDER_FOCUS);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!hasValidationError) {
                    inputField.setBorder(INPUT_BORDER_DEFAULT);
                }
            }
        });

        // ComboBox focus indicators
        addComboFocusBehavior(sourceBaseCombo);
        addComboFocusBehavior(targetBaseCombo);

        // Convert button hover, press, and keyboard focus states
        convertButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                convertButton.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_PRIMARY_INK, 2),
                        new EmptyBorder(5, 15, 5, 15)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                convertButton.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_OXBLOOD, 1),
                        new EmptyBorder(6, 16, 6, 16)));
            }
        });

        convertButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (convertButton.isEnabled()) {
                    convertButton.setBackground(COLOR_OXBLOOD_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (convertButton.isEnabled()) {
                    convertButton.setBackground(COLOR_OXBLOOD);
                }
            }
        });

        convertButton.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!convertButton.isEnabled()) {
                    convertButton.setBackground(COLOR_BORDER);
                    convertButton.setForeground(COLOR_SECONDARY_TEXT);
                } else if (convertButton.getModel().isPressed()) {
                    convertButton.setBackground(COLOR_OXBLOOD_PRESS);
                } else if (convertButton.getModel().isRollover()) {
                    convertButton.setBackground(COLOR_OXBLOOD_HOVER);
                } else {
                    convertButton.setBackground(COLOR_OXBLOOD);
                    convertButton.setForeground(COLOR_SURFACE);
                }
            }
        });
    }

    private void addComboFocusBehavior(final JComponent combo) {
        combo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                combo.setBorder(COMBO_BORDER_FOCUS);
            }

            @Override
            public void focusLost(FocusEvent e) {
                combo.setBorder(COMBO_BORDER_DEFAULT);
            }
        });
    }

    private void registerEventHandlers() {
        ActionListener convertAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        };

        convertButton.addActionListener(convertAction);
        inputField.addActionListener(convertAction);

        ItemListener baseChangeListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateAllowedDigitsHint();
                }
            }
        };

        sourceBaseCombo.addItemListener(baseChangeListener);
    }

    private void updateAllowedDigitsHint() {
        NumberBase source = (NumberBase) sourceBaseCombo.getSelectedItem();
        if (source != null) {
            validationHintLabel.setText(
                    "Allowed digits for " + source.getDisplayName() + ": "
                            + source.getAllowedDigitsDescription());
        }
    }

    private void initializeEmptyResultState() {
        hasValidationError = false;
        inputField.setBorder(INPUT_BORDER_DEFAULT);

        resultRegionPanel.setBackground(COLOR_STONE_BG);
        resultTitleLabel.setText("Result");
        resultTitleLabel.setForeground(COLOR_PRIMARY_INK);

        resultOutputField.setForeground(COLOR_PRIMARY_INK);
        resultOutputField.setFont(FONT_RESULT_MONO);
        resultOutputField.setText("");
        resultBaseLabel.setText("");

        stepsTextArea.setText("");
        stepsTextArea.setCaretPosition(0);
    }

    private void performConversion() {
        NumberBase sourceBase = (NumberBase) sourceBaseCombo.getSelectedItem();
        NumberBase targetBase = (NumberBase) targetBaseCombo.getSelectedItem();
        String rawInput = inputField.getText();

        ValidationResult validation = InputValidator.validate(rawInput, sourceBase);

        if (!validation.isValid()) {
            hasValidationError = true;
            inputField.setBorder(INPUT_BORDER_ERROR);

            resultRegionPanel.setBackground(COLOR_PALE_ROSE);
            resultTitleLabel.setText("Validation Error");
            resultTitleLabel.setForeground(COLOR_ERROR_TEXT);

            resultOutputField.setForeground(COLOR_ERROR_TEXT);
            resultOutputField.setFont(FONT_VALIDATION_ERROR);
            resultOutputField.setText("Error: " + validation.getErrorMessage());
            resultBaseLabel.setText("");

            stepsTextArea.setText(validation.getDetailedGuidance());
            stepsTextArea.setCaretPosition(0);
            return;
        }

        hasValidationError = false;
        inputField.setBorder(inputField.hasFocus() ? INPUT_BORDER_FOCUS : INPUT_BORDER_DEFAULT);

        ConversionResult result = NumberSystemConverter.convert(validation, sourceBase, targetBase);

        resultRegionPanel.setBackground(COLOR_STONE_BG);
        resultTitleLabel.setText(
                "Result  —  (" + result.getNormalizedInput() + ")" + sourceBase.getSubscript()
                        + " to " + targetBase.getDisplayName());
        resultTitleLabel.setForeground(COLOR_PRIMARY_INK);

        resultOutputField.setForeground(COLOR_PRIMARY_INK);
        resultOutputField.setFont(FONT_RESULT_MONO);
        resultOutputField.setText(result.getConvertedOutput());

        resultBaseLabel.setForeground(COLOR_SECONDARY_TEXT);
        resultBaseLabel.setText("Base " + targetBase.getRadix());

        stepsTextArea.setText(result.getStepByStepExplanation());
        stepsTextArea.setCaretPosition(0);
    }
}
