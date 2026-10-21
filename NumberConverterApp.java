import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/**
 * EGB1201 Java Programming Project Review
 * Number System Converter — Standard JDK 17+ Swing Desktop Application
 *
 * NumberConverterApp.java
 * Application entry point. Configures system anti-aliased text rendering,
 * registers bundled IBM Plex Sans and IBM Plex Mono UI defaults, overrides
 * default Swing Look-and-Feel colors to enforce the restrained stone,
 * near-white, dark ink, and oxblood palette, and launches ConverterFrame
 * on the Event Dispatch Thread.
 *
 * Compilation & Execution (Java 17+):
 *   javac *.java
 *   java NumberConverterApp
 */
public final class NumberConverterApp {

    private NumberConverterApp() {
        // Application entry class
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ignored) {
                    // Fall back to default Swing Look and Feel if unavailable
                }

                configureRestrainedUiDefaults();

                ConverterFrame frame = new ConverterFrame();
                frame.setVisible(true);
            }
        });
    }

    /**
     * Overrides default Swing/Metal blue highlights, focus rings, selection backgrounds,
     * and default widget fonts with the bundled IBM Plex font family and restrained palette.
     */
    private static void configureRestrainedUiDefaults() {
        ColorUIResource stoneBg = new ColorUIResource(new Color(0xF2, 0xF0, 0xEC));      // #F2F0EC
        ColorUIResource surfaceBg = new ColorUIResource(new Color(0xFC, 0xFB, 0xF8));    // #FCFBF8
        ColorUIResource primaryInk = new ColorUIResource(new Color(0x28, 0x25, 0x22));   // #282522
        ColorUIResource secondaryInk = new ColorUIResource(new Color(0x6D, 0x66, 0x5E)); // #6D665E
        ColorUIResource borderStone = new ColorUIResource(new Color(0xD6, 0xD0, 0xC8));  // #D6D0C8
        ColorUIResource oxblood = new ColorUIResource(new Color(0x76, 0x28, 0x3A));      // #76283A
        ColorUIResource paleRose = new ColorUIResource(new Color(0xF3, 0xE8, 0xEB));     // #F3E8EB

        FontUIResource labelFont = new FontUIResource(ConverterFrame.getLabelFont());
        FontUIResource controlFont = new FontUIResource(ConverterFrame.getControlFont());
        FontUIResource buttonFont = new FontUIResource(ConverterFrame.getButtonFont());
        FontUIResource bodyFont = new FontUIResource(ConverterFrame.getBodyFont());
        FontUIResource stepsMonoFont = new FontUIResource(ConverterFrame.getStepsMonoFont());

        UIManager.put("Panel.background", stoneBg);
        UIManager.put("Panel.foreground", primaryInk);
        UIManager.put("Panel.font", bodyFont);
        UIManager.put("Label.foreground", primaryInk);
        UIManager.put("Label.font", labelFont);

        // Text selection & focus overrides (prevent default Swing blue)
        UIManager.put("TextField.background", surfaceBg);
        UIManager.put("TextField.foreground", primaryInk);
        UIManager.put("TextField.caretForeground", primaryInk);
        UIManager.put("TextField.selectionBackground", paleRose);
        UIManager.put("TextField.selectionForeground", primaryInk);
        UIManager.put("TextField.inactiveForeground", secondaryInk);
        UIManager.put("TextField.font", bodyFont);

        UIManager.put("TextArea.background", surfaceBg);
        UIManager.put("TextArea.foreground", primaryInk);
        UIManager.put("TextArea.caretForeground", primaryInk);
        UIManager.put("TextArea.selectionBackground", paleRose);
        UIManager.put("TextArea.selectionForeground", primaryInk);
        UIManager.put("TextArea.font", stepsMonoFont);

        // ComboBox overrides (prevent default Metal blue selection bar)
        UIManager.put("ComboBox.background", surfaceBg);
        UIManager.put("ComboBox.foreground", primaryInk);
        UIManager.put("ComboBox.selectionBackground", paleRose);
        UIManager.put("ComboBox.selectionForeground", primaryInk);
        UIManager.put("ComboBox.buttonBackground", stoneBg);
        UIManager.put("ComboBox.buttonShadow", borderStone);
        UIManager.put("ComboBox.buttonDarkShadow", primaryInk);
        UIManager.put("ComboBox.buttonHighlight", surfaceBg);
        UIManager.put("ComboBox.font", controlFont);

        // Button overrides
        UIManager.put("Button.focus", oxblood);
        UIManager.put("Button.select", new ColorUIResource(new Color(0x5E, 0x1F, 0x2E)));
        UIManager.put("Button.disabledText", secondaryInk);
        UIManager.put("Button.font", buttonFont);

        // ScrollBar & ScrollPane overrides
        UIManager.put("ScrollPane.background", surfaceBg);
        UIManager.put("ScrollPane.font", bodyFont);
        UIManager.put("ScrollBar.background", stoneBg);
        UIManager.put("ScrollBar.thumb", borderStone);
        UIManager.put("ScrollBar.thumbDarkShadow", borderStone);
        UIManager.put("ScrollBar.thumbHighlight", borderStone);
        UIManager.put("ScrollBar.thumbShadow", borderStone);
        UIManager.put("ScrollBar.track", surfaceBg);
    }
}
