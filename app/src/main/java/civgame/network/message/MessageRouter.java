package civgame.network.message;

import civgame.network.JsonSerializer;
import civgame.network.NetworkException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class MessageRouter {

    private final JsonSerializer jsonSerializer;
    private final Map<String, Class<? extends Message>> messageTypes;

    public MessageRouter() {
        this.jsonSerializer = new JsonSerializer();
        this.messageTypes = new HashMap<>();
        registerDefaultMessageTypes();
    }

    private void registerDefaultMessageTypes() {
        registerMessageType(MessageType.CONNECT_REQUEST, ConnectRequest.class);
        registerMessageType(MessageType.CONNECT_RESPONSE, ConnectResponse.class);
        registerMessageType(MessageType.DISCONNECT_BROADCAST, DisconnectBroadcast.class);
        registerMessageType(MessageType.ERROR_RESPONSE, ErrorResponse.class);
        registerMessageType(MessageType.PING_REQUEST, PingRequest.class);
        registerMessageType(MessageType.PONG_RESPONSE, PongResponse.class);
        registerMessageType(MessageType.JOIN_LOBBY_REQUEST, civgame.network.message.lobby.JoinLobbyRequest.class);
        registerMessageType(MessageType.JOIN_LOBBY_RESPONSE, civgame.network.message.lobby.JoinLobbyResponse.class);
        registerMessageType(MessageType.LEAVE_LOBBY_REQUEST, civgame.network.message.lobby.LeaveLobbyRequest.class);
        registerMessageType(MessageType.PLAYER_JOINED_BROADCAST, civgame.network.message.lobby.PlayerJoinedBroadcast.class);
        registerMessageType(MessageType.PLAYER_LEFT_BROADCAST, civgame.network.message.lobby.PlayerLeftBroadcast.class);
        registerMessageType(MessageType.SET_READY_REQUEST, civgame.network.message.lobby.SetReadyRequest.class);
        registerMessageType(MessageType.PLAYER_READY_BROADCAST, civgame.network.message.lobby.PlayerReadyBroadcast.class);
        registerMessageType(MessageType.SELECT_MAP_REQUEST, civgame.network.message.lobby.SelectMapRequest.class);
        registerMessageType(MessageType.MAP_SELECTED_BROADCAST, civgame.network.message.lobby.MapSelectedBroadcast.class);
        registerMessageType(MessageType.START_GAME_REQUEST, civgame.network.message.lobby.StartGameRequest.class);
        registerMessageType(MessageType.START_GAME_RESPONSE, civgame.network.message.lobby.StartGameResponse.class);
        registerMessageType(MessageType.CHAT_MESSAGE_REQUEST, civgame.network.message.chat.ChatMessageRequest.class);
        registerMessageType(MessageType.CHAT_MESSAGE_BROADCAST, civgame.network.message.chat.ChatMessageBroadcast.class);
        registerMessageType(MessageType.GAME_START_BROADCAST, civgame.network.message.game.GameStartBroadcast.class);
        registerMessageType(MessageType.PLAYER_ELIMINATED_BROADCAST, civgame.network.message.game.PlayerEliminatedBroadcast.class);
        registerMessageType(MessageType.GAME_OVER_BROADCAST, civgame.network.message.game.GameOverBroadcast.class);
        registerMessageType(MessageType.END_TURN_REQUEST, civgame.network.message.game.EndTurnRequest.class);
        registerMessageType(MessageType.END_TURN_RESPONSE, civgame.network.message.game.EndTurnResponse.class);
        registerMessageType(MessageType.TURN_UPDATE_BROADCAST, civgame.network.message.game.TurnUpdateBroadcast.class);
        registerMessageType(MessageType.DECLARE_WAR_REQUEST, civgame.network.message.diplomacy.DeclareWarRequest.class);
        registerMessageType(MessageType.DECLARE_WAR_BROADCAST, civgame.network.message.diplomacy.DeclareWarBroadcast.class);
        registerMessageType(MessageType.OFFER_PEACE_REQUEST, civgame.network.message.diplomacy.OfferPeaceRequest.class);
        registerMessageType(MessageType.PEACE_OFFER_BROADCAST, civgame.network.message.diplomacy.PeaceOfferBroadcast.class);
        registerMessageType(MessageType.PEACE_RESPONSE_REQUEST, civgame.network.message.diplomacy.PeaceResponseRequest.class);
        registerMessageType(MessageType.PEACE_ESTABLISHED_BROADCAST, civgame.network.message.diplomacy.PeaceEstablishedBroadcast.class);
        registerMessageType(MessageType.PROPOSE_ALLIANCE_REQUEST, civgame.network.message.diplomacy.ProposeAllianceRequest.class);
        registerMessageType(MessageType.ALLIANCE_PROPOSAL_BROADCAST, civgame.network.message.diplomacy.AllianceProposalBroadcast.class);
        registerMessageType(MessageType.ALLIANCE_RESPONSE_REQUEST, civgame.network.message.diplomacy.AllianceResponseRequest.class);
        registerMessageType(MessageType.ALLIANCE_ESTABLISHED_BROADCAST, civgame.network.message.diplomacy.AllianceEstablishedBroadcast.class);
        registerMessageType(MessageType.RELATION_UPDATE_BROADCAST, civgame.network.message.diplomacy.RelationUpdateBroadcast.class);
        registerMessageType(MessageType.DIPLOMACY_RESPONSE, civgame.network.message.diplomacy.DiplomacyResponse.class);
        registerMessageType(MessageType.ATTACK_REQUEST, civgame.network.message.combat.AttackRequest.class);
        registerMessageType(MessageType.ATTACK_RESPONSE, civgame.network.message.combat.AttackResponse.class);
        registerMessageType(MessageType.COMBAT_RESULT_BROADCAST, civgame.network.message.combat.CombatResultBroadcast.class);
        registerMessageType(MessageType.FOG_REVEAL_BROADCAST, civgame.network.message.game.FogRevealBroadcast.class);
        registerMessageType(MessageType.TRADE_OFFER_REQUEST, civgame.network.message.trade.TradeOfferRequest.class);
        registerMessageType(MessageType.TRADE_ACCEPT_REQUEST, civgame.network.message.trade.TradeAcceptRequest.class);
        registerMessageType(MessageType.TRADE_REJECT_REQUEST, civgame.network.message.trade.TradeRejectRequest.class);
        registerMessageType(MessageType.TRADE_CANCEL_REQUEST, civgame.network.message.trade.TradeCancelRequest.class);
        registerMessageType(MessageType.TRADE_OFFER_BROADCAST, civgame.network.message.trade.TradeOfferBroadcast.class);
        registerMessageType(MessageType.TRADE_INBOX_UPDATE_BROADCAST, civgame.network.message.trade.TradeInboxUpdateBroadcast.class);
        registerMessageType(MessageType.ITEM_PRODUCE_REQUEST, civgame.network.message.item.ItemProduceRequest.class);
        registerMessageType(MessageType.ITEM_USE_REQUEST, civgame.network.message.item.ItemUseRequest.class);
        registerMessageType(MessageType.ITEM_ACTION_RESPONSE, civgame.network.message.item.ItemActionResponse.class);
        registerMessageType(MessageType.ITEM_ACTION_BROADCAST, civgame.network.message.item.ItemActionBroadcast.class);
        registerMessageType(MessageType.TOGGLE_CHEATS_REQUEST, civgame.network.message.lobby.ToggleCheatsRequest.class);
        registerMessageType(MessageType.TOGGLE_CHEATS_BROADCAST, civgame.network.message.lobby.ToggleCheatsBroadcast.class);
    }

    public void registerMessageType(String type, Class<? extends Message> messageClass) {
        messageTypes.put(type, messageClass);
    }

    public String serializeMessage(Message message) throws NetworkException {
        return jsonSerializer.serialize(message);
    }

    public Message deserializeMessage(String json) throws NetworkException {
        try {
            JsonElement element = JsonParser.parseString(json);
            if (!element.isJsonObject()) {
                throw new NetworkException("Invalid JSON: not an object");
            }

            JsonObject jsonObject = element.getAsJsonObject();
            if (!jsonObject.has("type")) {
                throw new NetworkException("Invalid message format: missing 'type' field");
            }

            String messageType = jsonObject.get("type").getAsString();
            Class<? extends Message> targetClass = messageTypes.get(messageType);

            if (targetClass == null) {
                throw new NetworkException("Unknown message type: " + messageType);
            }

            return jsonSerializer.deserialize(json, targetClass);
        } catch (NetworkException e) {
            throw e;
        } catch (Exception e) {
            throw new NetworkException("Failed to deserialize message: " + e.getMessage());
        }
    }

    public void routeMessage(Message message, MessageHandler handler) throws NetworkException {
        if (message == null) {
            throw new NetworkException("Cannot route null message");
        }

        if (handler == null) {
            throw new NetworkException("Cannot route message to null handler");
        }

        if (message instanceof Request) {
            handler.handleRequest((Request) message);
        } else if (message instanceof Response) {
            handler.handleResponse((Response) message);
        } else if (message instanceof Broadcast) {
            handler.handleBroadcast((Broadcast) message);
        } else {
            throw new NetworkException("Unknown message category: " + message.getClass().getSimpleName());
        }
    }

    public void routeJsonMessage(String json, MessageHandler handler) throws NetworkException {
        Message message = deserializeMessage(json);
        routeMessage(message, handler);
    }

    public boolean isKnownMessageType(String type) {
        return messageTypes.containsKey(type);
    }
}