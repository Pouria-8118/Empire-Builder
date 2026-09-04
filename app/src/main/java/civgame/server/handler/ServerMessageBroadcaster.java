package civgame.server.handler;

import civgame.network.JsonSerializer;
import civgame.network.NetworkException;
import civgame.network.TCPServer;
import civgame.network.message.Broadcast;
import civgame.network.message.ErrorResponse;
import civgame.network.message.Message;
import civgame.network.message.item.ItemActionResponse;
import civgame.server.ConnectionManager;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;

public class ServerMessageBroadcaster implements MessageBroadcaster {

    private final ConnectionManager connectionManager;
    private final JsonSerializer jsonSerializer;

    public ServerMessageBroadcaster(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jsonSerializer = new JsonSerializer();
    }

    private void sendToClient(String clientId, Message message) {
        TCPServer.ClientConnection connection = connectionManager.getConnection(clientId);
        if (connection == null) {
            return;
        }
        try {
            String json = jsonSerializer.serialize(message);
            connection.sendMessage(json);
        } catch (NetworkException e) {
            System.err.println("Failed to send message to " + clientId + ": " + e.getMessage());
        }
    }

    @Override
    public void sendToPlayer(String clientId, Broadcast broadcast) {
        sendToClient(clientId, broadcast);
    }

    @Override
    public void sendError(String clientId, String requestId, String message) {
        ErrorResponse error = new ErrorResponse(requestId, "GENERAL_ERROR", message);
        sendToClient(clientId, error);
    }

    @Override
    public void sendSuccess(String clientId, String requestId) {
        ItemActionResponse response = new ItemActionResponse(requestId, true, "SUCCESS");
        sendToClient(clientId, response);
    }

    @Override
    public void broadcastToGame(MultiplayerGameState game, Broadcast broadcast) {
        for (PlayerState player : game.getAllPlayers()) {
            if (player.isActive()) {
                sendToClient(player.getClientId(), broadcast);
            }
        }
    }

    @Override
    public void broadcastGameStateUpdate(MultiplayerGameState game) {
        // TODO: Implement full game state broadcast with Fog of War
    }
}