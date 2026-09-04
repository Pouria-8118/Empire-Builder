package civgame.server.handler;

import civgame.model.item.ItemType;
import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.network.message.item.ItemActionBroadcast;
import civgame.network.message.item.ItemProduceRequest;
import civgame.network.message.item.ItemUseRequest;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;
import civgame.server.item.ItemService;

import java.util.List;

public class ItemMessageHandler {

    private final ItemService itemService;

    public ItemMessageHandler() {
        this.itemService = new ItemService();
    }

    public void handleItemProduce(MultiplayerGameState game, PlayerState sender,
                                  ItemProduceRequest request, MessageBroadcaster broadcaster) {
        ItemType itemType;
        try {
            itemType = ItemType.valueOf(request.getItemType());
        } catch (IllegalArgumentException e) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Invalid item type");
            return;
        }
        if (!itemService.canProduceItem(sender.getSession(), itemType)) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Cannot produce item - check prerequisites");
            return;
        }
        itemService.startProduction(sender.getSession(), itemType);
        broadcaster.sendSuccess(sender.getClientId(), request.getRequestId());
        broadcaster.broadcastGameStateUpdate(game);
    }

    public void handleItemUse(MultiplayerGameState game, PlayerState sender,
                              ItemUseRequest request, MessageBroadcaster broadcaster) {
        String itemId = request.getItemId();
        String unitId = request.getTargetUnitId();
        if (!itemService.canUseItem(sender.getSession(), itemId, unitId)) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Cannot use item on this unit");
            return;
        }
        Unit unit = findUnitById(sender, unitId);
        if (unit == null) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Unit not found");
            return;
        }

        civgame.model.item.Item item = sender.getSession().getInventory().getItemById(itemId);
        if (item == null) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Item not found");
            return;
        }

        if (item.getItemType() == ItemType.TELEPORT) {
            HexCoordinate destination = new HexCoordinate(request.getTargetQ(), request.getTargetR());
            if (!itemService.validateTeleportDestination(sender.getSession(), unit, destination)) {
                broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                        "Invalid teleport destination");
                return;
            }
            unit.setCoordinate(destination);
            itemService.applyItem(sender.getSession(), itemId, unitId, new ItemService.MobilityAction());
        } else {
            itemService.applyItem(sender.getSession(), itemId, unitId, new ItemService.MobilityAction());
        }

        ItemActionBroadcast broadcast = new ItemActionBroadcast("server");
        broadcast.setActionType(item.getItemType().name());
        broadcast.setActorClientId(sender.getClientId());
        broadcast.setActorUsername(sender.getUsername());
        broadcast.setTargetUnitId(unitId);
        broadcast.setTargetClientId(sender.getClientId());
        broadcast.setTargetQ(request.getTargetQ());
        broadcast.setTargetR(request.getTargetR());

        broadcaster.broadcastToGame(game, broadcast);
        broadcaster.sendSuccess(sender.getClientId(), request.getRequestId());
        broadcaster.broadcastGameStateUpdate(game);
    }

    private Unit findUnitById(PlayerState player, String unitId) {
        List<Unit> units = player.getSession().getUnits();
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getId().equals(unitId)) {
                return units.get(i);
            }
        }
        return null;
    }
}