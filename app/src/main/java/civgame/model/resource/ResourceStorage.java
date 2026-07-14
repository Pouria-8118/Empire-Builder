package civgame.model.resource;

import civgame.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class ResourceStorage {

    private final Map<ResourceType, Integer> currentAmounts;
    private final Map<ResourceType, Integer> capacities;

    public ResourceStorage() {
        this.currentAmounts = new HashMap<ResourceType, Integer>();
        this.capacities = new HashMap<ResourceType, Integer>();
        setupDefaultCapacities();
        setupDefaultAmounts();
    }

    private void setupDefaultCapacities() {
        capacities.put(ResourceType.FOOD, Constants.INITIAL_FOOD_CAPACITY);
        capacities.put(ResourceType.WOOD, Constants.INITIAL_WOOD_CAPACITY);
        capacities.put(ResourceType.STONE, Constants.INITIAL_STONE_CAPACITY);
        capacities.put(ResourceType.IRON, Constants.INITIAL_IRON_CAPACITY);
    }

    private void setupDefaultAmounts() {
        currentAmounts.put(ResourceType.FOOD, 0);
        currentAmounts.put(ResourceType.WOOD, 0);
        currentAmounts.put(ResourceType.STONE, 0);
        currentAmounts.put(ResourceType.IRON, 0);
    }

    public int getAmount(ResourceType resourceType) {
        return currentAmounts.get(resourceType);
    }

    public int getCapacity(ResourceType resourceType) {
        return capacities.get(resourceType);
    }

    public void addResource(ResourceType resourceType, int amount) {
        int newAmount = getAmount(resourceType) + amount;
        int capacity = getCapacity(resourceType);
        if (newAmount > capacity) {
            newAmount = capacity;
        }
        currentAmounts.put(resourceType, newAmount);
    }

    public boolean hasEnough(ResourceType resourceType, int amount) {
        return getAmount(resourceType) >= amount;
    }

    public boolean removeResource(ResourceType resourceType, int amount) {
        if (!hasEnough(resourceType, amount)) {
            return false;
        }
        int newAmount = getAmount(resourceType) - amount;
        currentAmounts.put(resourceType, newAmount);
        return true;
    }

    public void increaseCapacity(ResourceType resourceType, int extraCapacity) {
        int newCapacity = getCapacity(resourceType) + extraCapacity;
        capacities.put(resourceType, newCapacity);
    }

    public int consumeAsMuchAsPossible(ResourceType resourceType, int amount) {
        int available = getAmount(resourceType);

        if (available >= amount) {
            currentAmounts.put(resourceType, available - amount);
            return 0;
        }

        currentAmounts.put(resourceType, 0);
        return amount - available;
    }
}