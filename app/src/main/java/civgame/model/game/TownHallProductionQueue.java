package civgame.model.game;

public class TownHallProductionQueue {

    private ProductionQueueItem currentItem;

    public boolean isBusy() {
        return currentItem != null;
    }

    public ProductionQueueItem getCurrentItem() {
        return currentItem;
    }

    public boolean startItem(ProductionQueueItem item) {
        if (isBusy()) {
            return false;
        }
        currentItem = item;
        return true;
    }

    public ProductionQueueItem advanceOneTurn() {
        if (currentItem == null) {
            return null;
        }

        currentItem.advanceOneTurn();

        if (currentItem.isFinished()) {
            ProductionQueueItem finishedItem = currentItem;
            currentItem = null;
            return finishedItem;
        }

        return null;
    }
}