package civgame.model.item;

public class CombatItem extends Item {

    private static final long serialVersionUID = 1L;

    public static final int DICE_MODIFIER = 1;
    public static final int BUILDING_DAMAGE_BONUS = 5;

    public CombatItem() {
        super(ItemType.COMBAT);
    }

    @Override
    public String getDisplayName() {
        return "Combat Elixir";
    }
}