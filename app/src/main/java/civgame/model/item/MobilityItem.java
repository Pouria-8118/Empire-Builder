package civgame.model.item;

public class MobilityItem extends Item {

    private static final long serialVersionUID = 1L;

    public static final int AP_BONUS = 2;

    public MobilityItem() {
        super(ItemType.MOBILITY);
    }

    @Override
    public String getDisplayName() {
        return "Mobility Potion";
    }
}