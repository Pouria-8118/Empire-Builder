package civgame.controller.game;

import civgame.model.game.GameSession;

public interface GameLoadListener {

    void onGameLoaded(GameSession loadedGameSession);
}