package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.model.unit.BorderExpander;
import civgame.util.Constants;

import java.util.List;

public class BorderExpansionService {

    public boolean canExpand(GameSession gameSession, BorderExpander expander) {
        if (!expander.hasEnoughActionPoints(Constants.BORDER_EXPAND_ACTION_POINT_COST)) {
            return false;
        }

        HexTile centerTile = gameSession.getHexMap().getTile(expander.getCoordinate());
        if (centerTile == null) {
            return false;
        }

        return centerTile.getVisibility() != TileVisibility.UNSEEN;
    }

    public boolean expand(GameSession gameSession, BorderExpander expander) {
        if (!canExpand(gameSession, expander)) {
            return false;
        }

        HexCoordinate centerCoordinate = expander.getCoordinate();
        claimIfExplored(gameSession, centerCoordinate);

        List<HexCoordinate> neighbors = centerCoordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            claimIfExplored(gameSession, neighbors.get(i));
        }

        expander.consumeActionPoints(Constants.BORDER_EXPAND_ACTION_POINT_COST);
        gameSession.removeUnit(expander);

        return true;
    }

    private void claimIfExplored(GameSession gameSession, HexCoordinate coordinate) {
        if (!gameSession.getHexMap().containsCoordinate(coordinate)) {
            return;
        }

        HexTile tile = gameSession.getHexMap().getTile(coordinate);
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            return;
        }

        gameSession.claimBorderTile(coordinate);
    }

    // TODO (bonus - visual polish): show a preview highlight of the
    // hexes that would be claimed before the player confirms the action
}