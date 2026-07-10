package civgame.controller.menu;

import civgame.audio.MusicPlayer;
import civgame.controller.game.GameController;
import civgame.controller.game.TurnController;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameInitializer;
import civgame.model.game.GameSession;
import civgame.model.game.TurnEngine;
import civgame.util.Constants;
import civgame.view.game.GameFrame;
import civgame.view.menu.ExitConfirmationDialog;
import civgame.view.menu.MainMenuFrame;
import civgame.view.menu.MainMenuPanel;
import civgame.view.menu.SettingsPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MainMenuController {

    private MainMenuFrame mainMenuFrame;
    private MusicPlayer musicPlayer;

    public MainMenuController(MainMenuFrame mainMenuFrame, MusicPlayer musicPlayer) {
        this.mainMenuFrame = mainMenuFrame;
        this.musicPlayer = musicPlayer;
        registerListeners();
    }

    private void registerListeners() {
        MainMenuPanel menuPanel = mainMenuFrame.getMainMenuPanel();
        SettingsPanel settingsPanel = mainMenuFrame.getSettingsPanel();

        menuPanel.getStartButton().addActionListener(new StartButtonListener());
        menuPanel.getSettingsButton().addActionListener(new SettingsButtonListener());
        menuPanel.getExitButton().addActionListener(new ExitButtonListener());

        settingsPanel.getBackButton().addActionListener(new BackButtonListener());
        settingsPanel.getVolumeScrollBar().addAdjustmentListener(new VolumeChangeListener(settingsPanel));
    }

    public void startBackgroundMusic() {
        musicPlayer.loadAndPlay(Constants.BACKGROUND_MUSIC_PATH);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            GameInitializer gameInitializer = new GameInitializer();
            GameSession gameSession = gameInitializer.createNewGame();

            FogOfWarCalculator fogOfWarCalculator = new FogOfWarCalculator();
            fogOfWarCalculator.updateVisibility(gameSession);

            GameFrame gameFrame = new GameFrame(gameSession);
            GameController gameController = new GameController(gameFrame, gameSession);

            gameFrame.setVisible(true);
            mainMenuFrame.dispose();

            TurnEngine turnEngine = new TurnEngine();
            TurnController turnController = new TurnController(gameFrame, gameSession, turnEngine, fogOfWarCalculator);
        }
    }
    private class SettingsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            mainMenuFrame.showSettingsCard();
        }
    }

    private class ExitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            boolean confirmed = ExitConfirmationDialog.showConfirmDialog(mainMenuFrame);
            if (confirmed) {
                System.exit(0);
            }
        }
    }

    private class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            mainMenuFrame.showMenuCard();
        }
    }

    private class VolumeChangeListener implements AdjustmentListener {

        private SettingsPanel settingsPanel;

        public VolumeChangeListener(SettingsPanel settingsPanel) {
            this.settingsPanel = settingsPanel;
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
            int newVolume = adjustmentEvent.getValue();
            musicPlayer.setVolumePercent(newVolume);
            settingsPanel.updateVolumeLabel(newVolume);
        }
    }
}