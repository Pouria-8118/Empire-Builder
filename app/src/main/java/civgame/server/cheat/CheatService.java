package civgame.server.cheat;

import civgame.model.building.TownHall;
import civgame.model.building.TownHallLevel;
import civgame.model.game.GameSession;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.model.unit.UnitFactory;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheatService {

    private final UnitFactory unitFactory;

    public CheatService() {
        this.unitFactory = new UnitFactory();
    }

    public CheatResult executeCheat(MultiplayerGameState game, String clientId, String cheatName) {
        PlayerState player = game.getPlayer(clientId);
        if (player == null) {
            return new CheatResult(false, "Player not found");
        }

        switch (cheatName.toLowerCase()) {
            case "/greedisgood":
                return executeGreedIsGood(player);
            case "/marco":
                return executeMarco(player);
            case "/redbull":
                return executeRedBull(player);
            case "/gg":
                return executeGG(game, player);
            case "/iknowkungfu":
                return executeIKnowKungFu(player);
            case "/upgradeth":
                return executeUpgradeTH(player);
            default:
                return new CheatResult(false, "Unknown cheat code");
        }
    }

    private CheatResult executeGreedIsGood(PlayerState player) {
        ResourceStorage storage = player.getSession().getResourceStorage();
        storage.addResource(ResourceType.FOOD, 500);
        storage.addResource(ResourceType.WOOD, 500);
        storage.addResource(ResourceType.STONE, 500);
        storage.addResource(ResourceType.IRON, 500);
        return new CheatResult(true, "Added 500 of each resource");
    }

    private CheatResult executeMarco(PlayerState player) {
        GameSession session = player.getSession();
        HexMap map = session.getHexMap();
        for (Map.Entry<civgame.model.map.HexCoordinate, HexTile> entry : map.getAllTiles().entrySet()) {
            HexTile tile = entry.getValue();
            tile.setVisibility(TileVisibility.EXPLORED);
        }
        return new CheatResult(true, "Fog of War removed for your empire");
    }

    private CheatResult executeRedBull(PlayerState player) {
        List<Unit> units = player.getSession().getUnits();
        for (int i = 0; i < units.size(); i++) {
            units.get(i).refreshActionPoints();
        }
        return new CheatResult(true, "All units AP refilled");
    }

    private CheatResult executeGG(MultiplayerGameState game, PlayerState winner) {
        for (PlayerState other : game.getAllPlayers()) {
            if (!other.getClientId().equals(winner.getClientId())) {
                other.setEliminated(true);
                other.getSession().clearAllUnits();
                other.getSession().clearAllBuildings();
                TownHall th = other.getSession().getTownHall();
                if (th != null) {
                    th.applyCombatDamage(th.getMaxHitPoints() + 100);
                }
                game.eliminatePlayer(other.getClientId());
            }
        }
        return new CheatResult(true, "All enemies eliminated! Instant Victory!");
    }

    private CheatResult executeIKnowKungFu(PlayerState player) {
        GameSession session = player.getSession();
        List<Unit> originalUnits = new ArrayList<>(session.getUnits());
        
        for (int i = 0; i < originalUnits.size(); i++) {
            Unit unit = originalUnits.get(i);
            if (unit.getUnitType() == UnitType.WORKER) {
                session.removeUnit(unit);
                Unit cavalry = unitFactory.createUnit(UnitType.CAVALRY, unit.getCoordinate());
                session.addUnit(cavalry);
            }
        }
        return new CheatResult(true, "All workers converted to Cavalry!");
    }

    private CheatResult executeUpgradeTH(PlayerState player) {
        TownHall th = player.getSession().getTownHall();
        if (th != null) {
            th.setLevel(TownHallLevel.CAPITAL);
            th.heal(10000);
        }
        return new CheatResult(true, "Town Hall upgraded to max level and fully healed!");
    }

    public static class CheatResult {
        private final boolean success;
        private final String message;

        public CheatResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}