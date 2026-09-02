package civgame.server.game;

import civgame.model.game.TurnEngine;
import civgame.model.game.TurnSummary;
import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.network.message.game.TurnUpdateBroadcast;

public class TurnManager {
    
    private final TurnEngine turnEngine;
    
    public TurnManager() {
        this.turnEngine = new TurnEngine();
    }
    
    public synchronized EndTurnResult handleEndTurn(MultiplayerGameState game, String clientId) {
        if (!clientId.equals(game.getCurrentTurnClientId())) {
            return new EndTurnResult(false, "It is not your turn", null);
        }
        
        PlayerState player = game.getPlayer(clientId);
        if (player == null) {
            return new EndTurnResult(false, "You are not in this game", null);
        }
        
        if (!player.isActive()) {
            return new EndTurnResult(false, "You are no longer an active player", null);
        }
        
        TurnSummary summary = turnEngine.executeTurn(player.getSession());
        game.advanceTurn();
        
        TurnUpdateBroadcast broadcast = buildTurnUpdateBroadcast(game, summary, player);
        
        return new EndTurnResult(true, "Turn completed", broadcast);
    }
    
    private TurnUpdateBroadcast buildTurnUpdateBroadcast(MultiplayerGameState game, 
                                                          TurnSummary summary,
                                                          PlayerState player) {
        TurnUpdateBroadcast broadcast = new TurnUpdateBroadcast("server");
        broadcast.setNewTurnNumber(game.getTurnNumber());
        
        PlayerState currentPlayer = game.getCurrentTurnPlayer();
        if (currentPlayer != null) {
            broadcast.setCurrentTurnClientId(currentPlayer.getClientId());
            broadcast.setCurrentTurnUsername(currentPlayer.getUsername());
        }
        
        TurnUpdateBroadcast.TurnSummaryDTO summaryDTO = new TurnUpdateBroadcast.TurnSummaryDTO();
        summaryDTO.setDestroyedBuildingIds(summary.getDestroyedBuildingIds());
        summaryDTO.setStarvationTriggered(summary.isStarvationTriggered());
        summaryDTO.setCompletedProductionDescription(summary.getCompletedProductionDescription());
        summaryDTO.setDepletedResourceDescriptions(summary.getDepletedResourceDescriptions());
        broadcast.setSummary(summaryDTO);
        
        ResourceStorage storage = player.getSession().getResourceStorage();
        TurnUpdateBroadcast.ResourceSnapshotDTO resourcesDTO = new TurnUpdateBroadcast.ResourceSnapshotDTO();
        resourcesDTO.setFood(storage.getAmount(ResourceType.FOOD));
        resourcesDTO.setWood(storage.getAmount(ResourceType.WOOD));
        resourcesDTO.setStone(storage.getAmount(ResourceType.STONE));
        resourcesDTO.setIron(storage.getAmount(ResourceType.IRON));
        broadcast.setResources(resourcesDTO);
        
        return broadcast;
    }
    
    public static class EndTurnResult {
        private final boolean success;
        private final String message;
        private final TurnUpdateBroadcast broadcast;
        
        public EndTurnResult(boolean success, String message, TurnUpdateBroadcast broadcast) {
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
        
        public TurnUpdateBroadcast getBroadcast() {
            return broadcast;
        }
    }
}