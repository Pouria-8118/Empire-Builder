package civgame.server.diplomacy;

import civgame.model.diplomacy.PlayerRelation;
import civgame.model.diplomacy.PlayerRelationMap;
import civgame.network.NetworkException;
import civgame.network.message.Broadcast;
import civgame.network.message.diplomacy.AllianceEstablishedBroadcast;
import civgame.network.message.diplomacy.AllianceProposalBroadcast;
import civgame.network.message.diplomacy.DeclareWarBroadcast;
import civgame.network.message.diplomacy.PeaceEstablishedBroadcast;
import civgame.network.message.diplomacy.PeaceOfferBroadcast;
import civgame.network.message.diplomacy.RelationUpdateBroadcast;
import civgame.server.ClientHandler;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DiplomacyManager {
    
    private final Map<String, PendingDiplomaticOffer> pendingPeaceOffers;
    private final Map<String, PendingDiplomaticOffer> pendingAllianceOffers;
    
    public DiplomacyManager() {
        this.pendingPeaceOffers = new HashMap<>();
        this.pendingAllianceOffers = new HashMap<>();
    }
    
    private String makeOfferKey(String proposer, String target) {
        return proposer + "->" + target;
    }
    
    public synchronized void cleanupExpiredOffers() {
        removeExpiredOffers(pendingPeaceOffers);
        removeExpiredOffers(pendingAllianceOffers);
    }
    
    private void removeExpiredOffers(Map<String, PendingDiplomaticOffer> map) {
        Iterator<Map.Entry<String, PendingDiplomaticOffer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PendingDiplomaticOffer> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }
    
    public synchronized DeclareWarResult handleDeclareWar(MultiplayerGameState game,
                                                           String aggressorClientId,
                                                           String targetClientId) {
        PlayerRelationMap relationMap = game.getRelationMap();
        if (relationMap == null) {
            return new DeclareWarResult(false, "Game does not support diplomacy");
        }
        
        PlayerState aggressor = game.getPlayer(aggressorClientId);
        PlayerState target = game.getPlayer(targetClientId);
        if (aggressor == null || target == null) {
            return new DeclareWarResult(false, "Invalid player");
        }
        
        PlayerRelation current = relationMap.getRelation(aggressorClientId, targetClientId);
        if (current == PlayerRelation.WAR) {
            return new DeclareWarResult(false, "Already at war");
        }
        
        relationMap.setRelation(aggressorClientId, targetClientId, PlayerRelation.WAR);
        
        removePendingOffersBetween(aggressorClientId, targetClientId);
        
        DeclareWarBroadcast warBroadcast = new DeclareWarBroadcast(
                "server",
                aggressorClientId,
                aggressor.getUsername(),
                targetClientId,
                target.getUsername()
        );
        
        RelationUpdateBroadcast updateBroadcast = new RelationUpdateBroadcast(
                "server",
                aggressorClientId,
                aggressor.getUsername(),
                targetClientId,
                target.getUsername(),
                PlayerRelation.WAR
        );
        
        return new DeclareWarResult(true, "War declared", warBroadcast, updateBroadcast);
    }
    
    public synchronized OfferResult handleOfferPeace(MultiplayerGameState game,
                                                      String proposerClientId,
                                                      String targetClientId) {
        PlayerRelationMap relationMap = game.getRelationMap();
        if (relationMap == null) {
            return new OfferResult(false, "Game does not support diplomacy");
        }
        
        PlayerState proposer = game.getPlayer(proposerClientId);
        PlayerState target = game.getPlayer(targetClientId);
        if (proposer == null || target == null) {
            return new OfferResult(false, "Invalid player");
        }
        
        PlayerRelation current = relationMap.getRelation(proposerClientId, targetClientId);
        if (current != PlayerRelation.WAR) {
            return new OfferResult(false, "You are not at war");
        }
        
        String key = makeOfferKey(proposerClientId, targetClientId);
        if (pendingPeaceOffers.containsKey(key)) {
            return new OfferResult(false, "Peace offer already pending");
        }
        
        PendingDiplomaticOffer offer = new PendingDiplomaticOffer(
                PendingDiplomaticOffer.OfferType.PEACE,
                proposerClientId,
                targetClientId
        );
        pendingPeaceOffers.put(key, offer);
        
        PeaceOfferBroadcast broadcast = new PeaceOfferBroadcast(
                "server",
                proposerClientId,
                proposer.getUsername(),
                targetClientId,
                target.getUsername()
        );
        
        return new OfferResult(true, "Peace offer sent", broadcast);
    }
    
    public synchronized ResponseResult handlePeaceResponse(MultiplayerGameState game,
                                                              String responderClientId,
                                                              String proposerClientId,
                                                              boolean accepted) {
        PlayerRelationMap relationMap = game.getRelationMap();
        if (relationMap == null) {
            return new ResponseResult(false, "Game does not support diplomacy");
        }
        
        String key = makeOfferKey(proposerClientId, responderClientId);
        PendingDiplomaticOffer offer = pendingPeaceOffers.get(key);
        if (offer == null) {
            offer = pendingPeaceOffers.get(makeOfferKey(responderClientId, proposerClientId));
        }
        
        if (offer == null || offer.isExpired()) {
            pendingPeaceOffers.remove(key);
            return new ResponseResult(false, "Offer not found or expired");
        }
        
        pendingPeaceOffers.remove(key);
        
        if (!accepted) {
            return new ResponseResult(true, "Peace offer rejected");
        }
        
        PlayerState proposer = game.getPlayer(proposerClientId);
        PlayerState responder = game.getPlayer(responderClientId);
        if (proposer == null || responder == null) {
            return new ResponseResult(false, "Invalid player");
        }
        
        relationMap.setRelation(proposerClientId, responderClientId, PlayerRelation.NEUTRAL);
        
        PeaceEstablishedBroadcast established = new PeaceEstablishedBroadcast(
                "server",
                proposerClientId,
                proposer.getUsername(),
                responderClientId,
                responder.getUsername()
        );
        
        RelationUpdateBroadcast update = new RelationUpdateBroadcast(
                "server",
                proposerClientId,
                proposer.getUsername(),
                responderClientId,
                responder.getUsername(),
                PlayerRelation.NEUTRAL
        );
        
        return new ResponseResult(true, "Peace established", established, update);
    }
    
    public synchronized OfferResult handleProposeAlliance(MultiplayerGameState game,
                                                            String proposerClientId,
                                                            String targetClientId) {
        PlayerRelationMap relationMap = game.getRelationMap();
        if (relationMap == null) {
            return new OfferResult(false, "Game does not support diplomacy");
        }
        
        PlayerState proposer = game.getPlayer(proposerClientId);
        PlayerState target = game.getPlayer(targetClientId);
        if (proposer == null || target == null) {
            return new OfferResult(false, "Invalid player");
        }
        
        PlayerRelation current = relationMap.getRelation(proposerClientId, targetClientId);
        if (current == PlayerRelation.WAR) {
            return new OfferResult(false, "Cannot ally while at war");
        }
        if (current == PlayerRelation.ALLY) {
            return new OfferResult(false, "Already allied");
        }
        if (current != PlayerRelation.FRIENDLY && current != PlayerRelation.NEUTRAL) {
            return new OfferResult(false, "Cannot propose alliance in current state");
        }
        
        String key = makeOfferKey(proposerClientId, targetClientId);
        if (pendingAllianceOffers.containsKey(key)) {
            return new OfferResult(false, "Alliance offer already pending");
        }
        
        PendingDiplomaticOffer offer = new PendingDiplomaticOffer(
                PendingDiplomaticOffer.OfferType.ALLIANCE,
                proposerClientId,
                targetClientId
        );
        pendingAllianceOffers.put(key, offer);
        
        AllianceProposalBroadcast broadcast = new AllianceProposalBroadcast(
                "server",
                proposerClientId,
                proposer.getUsername(),
                targetClientId,
                target.getUsername()
        );
        
        return new OfferResult(true, "Alliance proposal sent", broadcast);
    }
    
    public synchronized ResponseResult handleAllianceResponse(MultiplayerGameState game,
                                                                 String responderClientId,
                                                                 String proposerClientId,
                                                                 boolean accepted) {
        PlayerRelationMap relationMap = game.getRelationMap();
        if (relationMap == null) {
            return new ResponseResult(false, "Game does not support diplomacy");
        }
        
        String key = makeOfferKey(proposerClientId, responderClientId);
        PendingDiplomaticOffer offer = pendingAllianceOffers.get(key);
        if (offer == null) {
            offer = pendingAllianceOffers.get(makeOfferKey(responderClientId, proposerClientId));
        }
        
        if (offer == null || offer.isExpired()) {
            pendingAllianceOffers.remove(key);
            return new ResponseResult(false, "Offer not found or expired");
        }
        
        pendingAllianceOffers.remove(key);
        
        if (!accepted) {
            return new ResponseResult(true, "Alliance offer rejected");
        }
        
        PlayerState proposer = game.getPlayer(proposerClientId);
        PlayerState responder = game.getPlayer(responderClientId);
        if (proposer == null || responder == null) {
            return new ResponseResult(false, "Invalid player");
        }
        
        relationMap.setRelation(proposerClientId, responderClientId, PlayerRelation.ALLY);
        
        AllianceEstablishedBroadcast established = new AllianceEstablishedBroadcast(
                "server",
                proposerClientId,
                proposer.getUsername(),
                responderClientId,
                responder.getUsername()
        );
        
        RelationUpdateBroadcast update = new RelationUpdateBroadcast(
                "server",
                proposerClientId,
                proposer.getUsername(),
                responderClientId,
                responder.getUsername(),
                PlayerRelation.ALLY
        );
        
        return new ResponseResult(true, "Alliance established", established, update);
    }
    
    private void removePendingOffersBetween(String playerA, String playerB) {
        pendingPeaceOffers.remove(makeOfferKey(playerA, playerB));
        pendingPeaceOffers.remove(makeOfferKey(playerB, playerA));
        pendingAllianceOffers.remove(makeOfferKey(playerA, playerB));
        pendingAllianceOffers.remove(makeOfferKey(playerB, playerA));
    }
    
    public List<Broadcast> broadcastToAllPlayers(MultiplayerGameState game,
                                                  Broadcast broadcast,
                                                  List<ClientHandler> handlers) {
        List<Broadcast> failed = new ArrayList<>();
        for (int i = 0; i < handlers.size(); i++) {
            ClientHandler handler = handlers.get(i);
            if (handler == null) {
                continue;
            }
            try {
                handler.sendMessageObject(broadcast);
            } catch (NetworkException e) {
                failed.add(broadcast);
            }
        }
        return failed;
    }
    
    public void handleClientDisconnect(String clientId) {
        removePendingOffersInvolving(clientId, pendingPeaceOffers);
        removePendingOffersInvolving(clientId, pendingAllianceOffers);
    }
    
    private void removePendingOffersInvolving(String clientId, Map<String, PendingDiplomaticOffer> map) {
        Iterator<Map.Entry<String, PendingDiplomaticOffer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PendingDiplomaticOffer> entry = iterator.next();
            PendingDiplomaticOffer offer = entry.getValue();
            if (offer.getProposerClientId().equals(clientId) || offer.getTargetClientId().equals(clientId)) {
                iterator.remove();
            }
        }
    }
    
    public static class DeclareWarResult {
        private final boolean success;
        private final String message;
        private final DeclareWarBroadcast warBroadcast;
        private final RelationUpdateBroadcast updateBroadcast;
        
        public DeclareWarResult(boolean success, String message) {
            this(success, message, null, null);
        }
        
        public DeclareWarResult(boolean success, String message,
                                 DeclareWarBroadcast warBroadcast,
                                 RelationUpdateBroadcast updateBroadcast) {
            this.success = success;
            this.message = message;
            this.warBroadcast = warBroadcast;
            this.updateBroadcast = updateBroadcast;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public DeclareWarBroadcast getWarBroadcast() {
            return warBroadcast;
        }
        
        public RelationUpdateBroadcast getUpdateBroadcast() {
            return updateBroadcast;
        }
    }
    
    public static class OfferResult {
        private final boolean success;
        private final String message;
        private final Broadcast broadcast;
        
        public OfferResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public OfferResult(boolean success, String message, Broadcast broadcast) {
            this.success = success;
            this.message = message;
            this.broadcast = broadcast;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Broadcast getBroadcast() {
            return broadcast;
        }
    }
    
    public static class ResponseResult {
        private final boolean success;
        private final String message;
        private final Broadcast primaryBroadcast;
        private final RelationUpdateBroadcast updateBroadcast;
        
        public ResponseResult(boolean success, String message) {
            this(success, message, null, null);
        }
        
        public ResponseResult(boolean success, String message,
                               Broadcast primaryBroadcast,
                               RelationUpdateBroadcast updateBroadcast) {
            this.success = success;
            this.message = message;
            this.primaryBroadcast = primaryBroadcast;
            this.updateBroadcast = updateBroadcast;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Broadcast getPrimaryBroadcast() {
            return primaryBroadcast;
        }
        
        public RelationUpdateBroadcast getUpdateBroadcast() {
            return updateBroadcast;
        }
    }
}