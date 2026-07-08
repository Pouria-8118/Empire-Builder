package civgame;

import civgame.audio.MusicPlayer;
import civgame.controller.menu.MainMenuController;
import civgame.view.menu.MainMenuFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                launchApplication();
            }
        });
    }

    private static void launchApplication() {
        MainMenuFrame mainMenuFrame = new MainMenuFrame();
        MusicPlayer musicPlayer = new MusicPlayer();
        MainMenuController mainMenuController = new MainMenuController(mainMenuFrame, musicPlayer);

        mainMenuController.startBackgroundMusic();
        mainMenuFrame.setVisible(true);
    }
}