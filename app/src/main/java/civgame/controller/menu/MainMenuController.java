package civgame.controller.menu;

import civgame.audio.MusicPlayer;
import civgame.controller.game.GameController;
import civgame.controller.game.GameLoadListener;
import civgame.controller.game.TurnController;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameInitializer;
import civgame.model.game.GameSaveManager;
import civgame.model.game.GameSession;
import civgame.model.game.TurnEngine;
import civgame.model.map.MapBlueprint;
import civgame.util.Constants;
import civgame.view.editor.MapEditorFrame;
import civgame.view.game.GameFrame;
import civgame.view.game.SaveLoadDialog;
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
    private final GameSaveManager saveManager;

    public MainMenuController(MainMenuFrame mainMenuFrame, MusicPlayer musicPlayer) {
        this.mainMenuFrame = mainMenuFrame;
        this.musicPlayer = musicPlayer;
        this.saveManager = new GameSaveManager();
        registerListeners();
    }

    private void registerListeners() {
        MainMenuPanel menuPanel = mainMenuFrame.getMainMenuPanel();
        SettingsPanel settingsPanel = mainMenuFrame.getSettingsPanel();

        menuPanel.getStartButton().addActionListener(new StartButtonListener());
        menuPanel.getLoadGameButton().addActionListener(new LoadGameButtonListener());
        menuPanel.getMapEditorButton().addActionListener(new MapEditorButtonListener());
        menuPanel.getSettingsButton().addActionListener(new SettingsButtonListener());
        menuPanel.getExitButton().addActionListener(new ExitButtonListener());

        settingsPanel.getBackButton().addActionListener(new BackButtonListener());
        settingsPanel.getVolumeScrollBar().addAdjustmentListener(new VolumeChangeListener(settingsPanel));
    }

    public void startBackgroundMusic() {
        musicPlayer.loadAndPlay(Constants.BACKGROUND_MUSIC_PATH);
    }

    private void launchGame(GameSession gameSession) {
        FogOfWarCalculator fogOfWarCalculator = new FogOfWarCalculator();
        fogOfWarCalculator.updateVisibility(gameSession);

        GameFrame gameFrame = new GameFrame(gameSession);

        GameLoadListener loadListener = new GameLoadListener() {
            @Override
            public void onGameLoaded(GameSession loadedGameSession) {
                gameFrame.dispose();
                launchGame(loadedGameSession);
            }
        };

        GameController gameController = new GameController(gameFrame, gameSession, fogOfWarCalculator, loadListener);

        gameFrame.setVisible(true);
        mainMenuFrame.dispose();

        TurnEngine turnEngine = new TurnEngine();
        TurnController turnController = new TurnController(gameFrame, gameSession, turnEngine, fogOfWarCalculator);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            GameInitializer gameInitializer = new GameInitializer();
            GameSession gameSession = gameInitializer.createNewGame();
            launchGame(gameSession);
        }
    }

    private class LoadGameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SaveLoadDialog dialog = new SaveLoadDialog(saveManager, null, new SaveLoadDialog.LoadCallback() {
                @Override
                public void onGameLoaded(GameSession loadedGameSession) {
                    launchGame(loadedGameSession);
                }
            });
            dialog.setVisible(true);
        }
    }

    private class MapEditorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            MapEditorFrame mapEditorFrame = new MapEditorFrame(new MapEditorFrame.PlayCallback() {
                @Override
                public void onPlayRequested(MapBlueprint blueprint) {
                    GameInitializer gameInitializer = new GameInitializer();
                    GameSession gameSession = gameInitializer.createNewGameFromBlueprint(blueprint);
                    launchGame(gameSession);
                }
            });
            mapEditorFrame.setVisible(true);
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