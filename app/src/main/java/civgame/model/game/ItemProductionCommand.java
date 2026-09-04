package civgame.model.game;

import civgame.model.item.Inventory;
import civgame.model.item.Item;
import civgame.model.item.ItemType;

public class ItemProductionCommand implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final ItemType itemType;
    private final int totalTurns;
    private int turnsRemaining;
    private final Inventory targetInventory;

    public ItemProductionCommand(ItemType itemType, Inventory targetInventory) {
        this.itemType = itemType;
        this.totalTurns = 1;
        this.turnsRemaining = 1;
        this.targetInventory = targetInventory;
    }

    public void execute(GameSession gameSession) {
        Item newItem = createItem(itemType);
        targetInventory.addItem(newItem);
    }

    public void cancel(GameSession gameSession) {
    }

    public void advanceOneTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining = turnsRemaining - 1;
        }
    }

    public boolean isFinished() {
        return turnsRemaining <= 0;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public ItemType getItemType() {
        return itemType;
    }

    private Item createItem(ItemType itemType) {
        if (itemType == ItemType.TELEPORT) {
            return new civgame.model.item.TeleportItem();
        }
        if (itemType == ItemType.MOBILITY) {
            return new civgame.model.item.MobilityItem();
        }
        return new civgame.model.item.CombatItem();
    }
}