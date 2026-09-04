package civgame.model.item;

public class TeleportItem extends Item {

    private static final long serialVersionUID = 1L;

    public TeleportItem() {
        super(ItemType.TELEPORT);
    }

    @Override
    public String getDisplayName() {
        return "Teleport Scroll";
    }
}