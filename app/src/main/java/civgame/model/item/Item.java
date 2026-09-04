package civgame.model.item;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Item implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime createdAt;
    private final ItemType itemType;
    private boolean consumed;

    public Item(ItemType itemType) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.itemType = itemType;
        this.consumed = false;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void markConsumed() {
        this.consumed = true;
    }

    public abstract String getDisplayName();
}