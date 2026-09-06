package civgame;

import civgame.audio.MusicPlayer;
import civgame.controller.menu.MainMenuController;
import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.server.GameServer;
import civgame.view.menu.MainMenuFrame;

import javax.swing.SwingUtilities;

public class Main {

    private static GameServer gameServer;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--server")) {
            startServer();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    launchApplication();
                }
            });
        }
    }

    private static void startServer() {
        System.out.println("Starting in server mode...");
        NetworkConfig config = new NetworkConfig();
        gameServer = new GameServer(config);
        
        try {
            gameServer.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    if (gameServer != null) {
                        gameServer.stop();
                    }
                }
            }));
            
            System.out.println("Server is running. Press Ctrl+C to stop.");
            
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (NetworkException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void launchApplication() {
        MainMenuFrame mainMenuFrame = new MainMenuFrame();
        MusicPlayer musicPlayer = new MusicPlayer();
        MainMenuController mainMenuController = new MainMenuController(mainMenuFrame, musicPlayer);
        mainMenuController.startBackgroundMusic();
        mainMenuFrame.setVisible(true);
    }
}