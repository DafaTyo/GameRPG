package rpg.item;

import rpg.character.Character;
import rpg.character.Player;

public abstract class Item {

    private final String name, icon, description;
    private final int    buyPrice;

    protected Item(String name, String icon, String description, int buyPrice) {
        this.name = name; this.icon = icon;
        this.description = description; this.buyPrice = buyPrice;
    }

    public abstract String canUse(Player player, Character target);
    public abstract String apply(Player player, Character target);

    public String getName()        { return name; }
    public String getIcon()        { return icon; }
    public String getDescription() { return description; }
    public int    getBuyPrice()    { return buyPrice; }
    public int    getSellPrice()   { return buyPrice / 2; }

    @Override public String toString() { return icon + " " + name + " – " + description; }
}
