package pl.edu.misztal.OptimalShift.image.ui;

import javax.swing.*;

/**
 * @author Krzysztof
 */
public class LookAndFeel {

    /**
     * ustawia look and feel systemu dla aplikacji okienkowej
     */
    public static void doIt() {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }
    }
}
