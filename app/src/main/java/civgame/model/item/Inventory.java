package civgame.model.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime createdAt;
    private final List<Item> items;

    public Inventory() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<Item>();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(String itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(itemId)) {
                items.remove(i);
                return true;
            }
        }
        return false;
    }

    public Item getItemById(String itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(itemId)) {
                return items.get(i);
            }
        }
        return null;
    }

    public List<Item> getAllItems() {
        return new ArrayList<Item>(items);
    }

    public boolean hasItem(String itemId) {
        return getItemById(itemId) != null;
    }
}