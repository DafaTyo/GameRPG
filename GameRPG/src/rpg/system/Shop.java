package rpg.system;

import rpg.character.Player;
import rpg.item.*;
import rpg.item.Items.*;
import rpg.ui.*;

public class Shop {

    private final InputHandler input;

    public Shop(InputHandler input) { this.input = input; }

    public void open(Player player) {
        boolean open = true;
        while (open) {
            UI.clear();
            UI.showShop(player.getGold());
            UI.prompt("Pilih: ");
            int choice = input.readInt(0, 4);

            if (choice == 0) { open = false; continue; }

            Item item = switch (choice) {
                case 1 -> new Potion();
                case 2 -> new Elixir();
                case 3 -> new Bomb();
                case 4 -> new ArmorPlate();
                default -> null;
            };

            if (item != null) buy(player, item);
            UI.pressEnter();
            input.waitEnter();
        }
    }

    private void buy(Player player, Item item) {
        int price = item.getBuyPrice();
        if (!player.spendGold(price)) {
            UI.warn("Gold tidak cukup! Butuh " + price + " Gold.");
            return;
        }
        if (!player.addItem(item)) {
            player.addGold(price); // refund
            UI.warn("Inventory penuh!");
            return;
        }
        UI.success("Beli " + item.getIcon() + " " + item.getName() + "! Gold sisa: " + player.getGold());
        SaveSystem.save(player);
        UI.info("💾 Progress tersimpan.");
    }
}
