package civgame.server.game;

import civgame.model.building.TownHall;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.network.message.game.GameStartBroadcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateSnapshotBuilder {

    public GameStartBroadcast buildSnapshotForPlayer(MultiplayerGameState game, PlayerState targetPlayer) {
        GameStartBroadcast broadcast = new GameStartBroadcast("server", game.getGameId());
        broadcast.setMyClientId(targetPlayer.getClientId());
        broadcast.setMyUsername(targetPlayer.getUsername());
        broadcast.setMyPlayerIndex(targetPlayer.getPlayerIndex());
        broadcast.setInitialTurnNumber(game.getTurnNumber());
        broadcast.setInitialTurnClientId(game.getCurrentTurnClientId());

        GameSession session = targetPlayer.getSession();
        HexMap map = session.getHexMap();
        broadcast.setMapRadius(map.getMapRadius());

        FogOfWarCalculator calculator = new FogOfWarCalculator();
        calculator.updateVisibility(session);

        broadcast.setTiles(buildTilesMap(map));
        broadcast.setMySnapshot(buildPlayerSnapshot(session));
        broadcast.setOpponents(buildOpponentsList(game, targetPlayer));
        broadcast.setInitialRelations(buildInitialRelationsList(game));

        return broadcast;
    }
    
    private List<GameStartBroadcast.RelationEntryDTO> buildInitialRelationsList(MultiplayerGameState game) {
        List<GameStartBroadcast.RelationEntryDTO> relations = new ArrayList<>();
        civgame.model.diplomacy.PlayerRelationMap relationMap = game.getRelationMap();
        if (relationMap == null) {
            return relations;
        }
        List<PlayerState> players = game.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                PlayerState playerA = players.get(i);
                PlayerState playerB = players.get(j);
                civgame.model.diplomacy.PlayerRelation relation = relationMap.getRelation(
                        playerA.getClientId(), playerB.getClientId()
                );
                relations.add(new GameStartBroadcast.RelationEntryDTO(
                        playerA.getClientId(),
                        playerB.getClientId(),
                        relation.name()
                ));
            }
        }
        return relations;
    }

    private Map<String, GameStartBroadcast.TileDTO> buildTilesMap(HexMap map) {
        Map<String, GameStartBroadcast.TileDTO> result = new HashMap<>();
        for (Map.Entry<HexCoordinate, HexTile> entry : map.getAllTiles().entrySet()) {
            HexCoordinate coordinate = entry.getKey();
            HexTile tile = entry.getValue();
            String key = coordinate.getQ() + "," + coordinate.getR();
            GameStartBroadcast.TileDTO dto = new GameStartBroadcast.TileDTO(
                    coordinate.getQ(),
                    coordinate.getR(),
                    tile.getTerrainType().name(),
                    tile.getResourceType().name(),
                    tile.getVisibility().name()
            );
            result.put(key, dto);
        }
        return result;
    }

    private GameStartBroadcast.PlayerSnapshotDTO buildPlayerSnapshot(GameSession session) {
        GameStartBroadcast.PlayerSnapshotDTO snapshot = new GameStartBroadcast.PlayerSnapshotDTO();
        ResourceStorage storage = session.getResourceStorage();

        snapshot.setFood(storage.getAmount(ResourceType.FOOD));
        snapshot.setWood(storage.getAmount(ResourceType.WOOD));
        snapshot.setStone(storage.getAmount(ResourceType.STONE));
        snapshot.setIron(storage.getAmount(ResourceType.IRON));
        snapshot.setFoodCapacity(storage.getCapacity(ResourceType.FOOD));
        snapshot.setWoodCapacity(storage.getCapacity(ResourceType.WOOD));
        snapshot.setStoneCapacity(storage.getCapacity(ResourceType.STONE));
        snapshot.setIronCapacity(storage.getCapacity(ResourceType.IRON));

        TownHall townHall = session.getTownHall();
        snapshot.setTownHallLevel(townHall.getLevel().name());
        snapshot.setTownHallCurrentHp(townHall.getCurrentHitPoints());
        snapshot.setTownHallMaxHp(townHall.getMaxHitPoints());
        snapshot.setTownHallDefense(townHall.getDefense());
        snapshot.setTownHallQ(townHall.getCoordinate().getQ());
        snapshot.setTownHallR(townHall.getCoordinate().getR());

        List<GameStartBroadcast.UnitDTO> unitDTOs = new ArrayList<>();
        List<Unit> units = session.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            GameStartBroadcast.UnitDTO unitDTO = new GameStartBroadcast.UnitDTO();
            unitDTO.setId(unit.getId());
            unitDTO.setUnitType(unit.getUnitType().name());
            unitDTO.setQ(unit.getCoordinate().getQ());
            unitDTO.setR(unit.getCoordinate().getR());
            unitDTO.setCurrentAp(unit.getCurrentActionPoints());
            unitDTO.setMaxAp(unit.getMaxActionPoints());
            unitDTO.setCurrentHp(unit.getCurrentHitPoints());
            unitDTO.setMaxHp(unit.getMaxHitPoints());
            unitDTOs.add(unitDTO);
        }
        snapshot.setUnits(unitDTOs);

        return snapshot;
    }

    private List<GameStartBroadcast.OpponentInfoDTO> buildOpponentsList(MultiplayerGameState game, PlayerState targetPlayer) {
        List<GameStartBroadcast.OpponentInfoDTO> opponents = new ArrayList<>();
        for (PlayerState player : game.getAllPlayers()) {
            if (player.getClientId().equals(targetPlayer.getClientId())) {
                continue;
            }
            GameStartBroadcast.OpponentInfoDTO dto = new GameStartBroadcast.OpponentInfoDTO(
                    player.getClientId(),
                    player.getUsername(),
                    player.getPlayerIndex(),
                    "NEUTRAL"
            );
            opponents.add(dto);
        }
        return opponents;
    }
}