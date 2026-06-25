package rpg.item;

import rpg.character.Character;
import rpg.character.Player;

/** All concrete item types. */
public final class Items {
    private Items() {}

    public static class Potion extends Item {
        public Potion() { super("Potion", "🧪", "Pulihkan 40 HP", 50); }
        @Override public String canUse(Player p, Character t) {
            return p.getHp() >= p.getMaxHp() ? "HP kamu sudah penuh!" : null;
        }
        @Override public String apply(Player p, Character t) {
            p.heal(40);
            return "🧪 Potion! Pulih 40 HP. (HP: " + p.getHp() + "/" + p.getMaxHp() + ")";
        }
    }

    public static class Elixir extends Item {
        public Elixir() { super("Elixir", "✨", "Pulihkan 60 HP + 30 Mana", 100); }
        @Override public String canUse(Player p, Character t) {
            return (p.getHp() >= p.getMaxHp() && p.getMana() >= p.getMaxMana())
                   ? "HP dan Mana sudah penuh!" : null;
        }
        @Override public String apply(Player p, Character t) {
            p.heal(60); p.restoreMana(30);
            return "✨ Elixir! +60 HP +30 Mana. (HP:" + p.getHp() + " MP:" + p.getMana() + ")";
        }
    }

    public static class Bomb extends Item {
        public Bomb() { super("Bomb", "💣", "45 damage ke musuh (ignore DEF)", 80); }
        @Override public String canUse(Player p, Character t) {
            return (t == null || !t.isAlive()) ? "Tidak ada target musuh!" : null;
        }
        @Override public String apply(Player p, Character t) {
            t.takeDamage(45);
            return "💣 BOM! 45 damage ke " + t.getName() + "! (HP sisa: " + t.getHp() + ")";
        }
    }

    public static class ArmorPlate extends Item {
        private static final int DEF_CAP = 40;
        @Override public String canUse(Player p, Character t) {
            return p.getDefense() >= DEF_CAP ? "DEF sudah maksimal (" + DEF_CAP + ")!" : null;
        }
        public ArmorPlate() { super("Armor Plate", "🛡️", "DEF +6 permanen", 120); }
        @Override public String apply(Player p, Character t) {
            int newDef = Math.min(DEF_CAP, p.getDefense() + 6);
            p.setDefense(newDef);
            return "🛡️ Armor Plate! DEF sekarang: " + p.getDefense() + "/" + DEF_CAP;
        }
    }

    /** Factory: buat item dari nama string (untuk SaveSystem). */
    public static Item byName(String name) {
        return switch (name) {
            case "Potion"      -> new Potion();
            case "Elixir"      -> new Elixir();
            case "Bomb"        -> new Bomb();
            case "Armor Plate" -> new ArmorPlate();
            default            -> null;
        };
    }
}
