package civgame.server.handler;

import civgame.network.message.Broadcast;
import civgame.server.game.MultiplayerGameState;

public interface MessageBroadcaster {

    void sendToPlayer(String clientId, Broadcast broadcast);

    void sendError(String clientId, String requestId, String message);

    void sendSuccess(String clientId, String requestId);

    void broadcastToGame(MultiplayerGameState game, Broadcast broadcast);

    void broadcastGameStateUpdate(MultiplayerGameState game);
}