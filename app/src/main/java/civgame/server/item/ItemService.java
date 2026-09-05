package civgame.server.item;

import civgame.model.building.Building;
import civgame.model.building.TownHall;
import civgame.model.building.TownHallLevel;
import civgame.model.game.GameSession;
import civgame.model.game.ItemProductionCommand;
import civgame.model.item.Item;
import civgame.model.item.ItemType;
import civgame.model.item.MobilityItem;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.util.Constants;
import java.util.List;

public class ItemService {

    public boolean canProduceItem(GameSession session, ItemType itemType) {
        if (!session.hasActiveApothecary()) {
            return false;
        }
        TownHall townHall = session.getTownHall();
        if (!townHall.isAtLeastLevel(TownHallLevel.SETTLEMENT)) {
            return false;
        }
        if (session.getApothecaryQueue().isBusy()) {
            return false;
        }
        ResourceStorage storage = session.getResourceStorage();
        if (itemType == ItemType.TELEPORT) {
            return storage.hasEnough(ResourceType.FOOD, Constants.TELEPORT_ITEM_FOOD_COST)
                    && storage.hasEnough(ResourceType.WOOD, Constants.TELEPORT_ITEM_WOOD_COST);
        }
        if (itemType == ItemType.MOBILITY) {
            return storage.hasEnough(ResourceType.FOOD, Constants.MOBILITY_ITEM_FOOD_COST)
                    && storage.hasEnough(ResourceType.WOOD, Constants.MOBILITY_ITEM_WOOD_COST);
        }
        return storage.hasEnough(ResourceType.FOOD, Constants.COMBAT_ITEM_FOOD_COST)
                && storage.hasEnough(ResourceType.IRON, Constants.COMBAT_ITEM_IRON_COST);
    }

    public boolean startProduction(GameSession session, ItemType itemType) {
        if (!canProduceItem(session, itemType)) {
            return false;
        }
        ResourceStorage storage = session.getResourceStorage();
        if (itemType == ItemType.TELEPORT) {
            storage.removeResource(ResourceType.FOOD, Constants.TELEPORT_ITEM_FOOD_COST);
            storage.removeResource(ResourceType.WOOD, Constants.TELEPORT_ITEM_WOOD_COST);
        } else if (itemType == ItemType.MOBILITY) {
            storage.removeResource(ResourceType.FOOD, Constants.MOBILITY_ITEM_FOOD_COST);
            storage.removeResource(ResourceType.WOOD, Constants.MOBILITY_ITEM_WOOD_COST);
        } else {
            storage.removeResource(ResourceType.FOOD, Constants.COMBAT_ITEM_FOOD_COST);
            storage.removeResource(ResourceType.IRON, Constants.COMBAT_ITEM_IRON_COST);
        }
        ItemProductionCommand command = new ItemProductionCommand(itemType, session.getInventory());
        return session.getApothecaryQueue().startCommand(command);
    }

    public boolean canUseItem(GameSession session, String itemId, String unitId) {
        if (!session.getInventory().hasItem(itemId)) {
            return false;
        }
        if (session.isUnitAffectedThisTurn(unitId)) {
            return false;
        }
        Unit unit = findUnitById(session, unitId);
        return unit != null;
    }

    public boolean validateTeleportDestination(GameSession session, Unit unit, HexCoordinate destination) {
        HexMap map = session.getHexMap();
        if (!map.containsCoordinate(destination)) {
            return false;
        }
        HexTile tile = map.getTile(destination);
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            return false;
        }
        if (isOccupiedByUnit(session, destination)) {
            return false;
        }
        if (session.getWildAnimalAt(destination) != null) {
            return false;
        }
        if (session.getEnemyUnitAt(destination) != null) {
            return false;
        }
        if (tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
            return false;
        }
        if (tile.getTerrainType() == TerrainType.SEA && !unit.isFloating()) {
            return false;
        }
        if (tile.getTerrainType() == TerrainType.MOUNTAIN) {
            return false;
        }
        return true;
    }

    public void applyItem(GameSession session, String itemId, String unitId, ItemAction action) {
        Item item = session.getInventory().getItemById(itemId);
        if (item == null) {
            return;
        }
        Unit unit = findUnitById(session, unitId);
        if (unit == null) {
            return;
        }
        if (item.getItemType() == ItemType.MOBILITY) {
            if (action instanceof MobilityAction) {
                grantExtraAP(unit, MobilityItem.AP_BONUS);
            }
        } else if (item.getItemType() == ItemType.COMBAT) {
            markUnitAsCombatBoosted(session, unitId);
        }
        session.markUnitAsAffectedThisTurn(unitId);
        item.markConsumed();
        session.getInventory().removeItem(itemId);
    }

    private void grantExtraAP(Unit unit, int bonus) {
        unit.addExtraActionPoints(bonus);
    }

    private void markUnitAsCombatBoosted(GameSession session, String unitId) {
        Unit unit = findUnitById(session, unitId);
        if (unit != null) {
            unit.setCombatBoosted(true);
        }
    }

    private Unit findUnitById(GameSession session, String unitId) {
        List<Unit> units = session.getUnits();
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getId().equals(unitId)) {
                return units.get(i);
            }
        }
        return null;
    }

    private boolean isOccupiedByUnit(GameSession session, HexCoordinate coordinate) {
        List<Unit> units = session.getUnits();
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoordinate().equals(coordinate)) {
                return true;
            }
        }
        Building building = session.getBuildingAt(coordinate);
        if (building != null && !building.isDestroyed()) {
            return true;
        }
        return false;
    }

    public static class ItemAction implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
    }

    public static class MobilityAction extends ItemAction {
        private static final long serialVersionUID = 1L;
    }
}