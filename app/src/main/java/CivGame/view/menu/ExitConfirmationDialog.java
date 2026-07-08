package civgame.view.menu;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExitConfirmationDialog {

    private ExitConfirmationDialog() {
    }

    public static boolean showConfirmDialog(JFrame parentFrame) {
        int userChoice = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to exit the game?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return userChoice == JOptionPane.YES_OPTION;
    }
}