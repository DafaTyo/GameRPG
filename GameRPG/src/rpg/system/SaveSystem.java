package rpg.system;

import rpg.character.CharacterClass;
import rpg.character.Player;
import rpg.item.Item;
import rpg.item.Items;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public final class SaveSystem {

    private static final Path SAVE = Paths.get("savegame.properties");
    private SaveSystem() {}

    public static boolean hasSaveFile() { return Files.exists(SAVE); }

    public static void save(Player p) {
        Properties props = new Properties();
        props.setProperty("name",    p.getName());
        props.setProperty("class",   p.getCls().name());
        props.setProperty("level",   String.valueOf(p.getLevel()));
        props.setProperty("exp",     String.valueOf(p.getExp()));
        props.setProperty("gold",    String.valueOf(p.getGold()));
        props.setProperty("hp",      String.valueOf(p.getHp()));
        props.setProperty("maxHp",   String.valueOf(p.getMaxHp()));
        props.setProperty("mana",    String.valueOf(p.getMana()));
        props.setProperty("maxMana", String.valueOf(p.getMaxMana()));
        props.setProperty("attack",  String.valueOf(p.getAttack()));
        props.setProperty("defense", String.valueOf(p.getDefense()));

        StringBuilder inv = new StringBuilder();
        for (Item item : p.getInventory()) {
            if (inv.length() > 0) inv.append(',');
            inv.append(item.getName());
        }
        props.setProperty("inventory", inv.toString());

        try (OutputStream out = Files.newOutputStream(SAVE)) {
            props.store(out, "RPG Save File");
        } catch (IOException e) {
            System.err.println("[Save] Gagal menyimpan: " + e.getMessage());
        }
    }

    public static Player load() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(SAVE)) {
            props.load(in);

            CharacterClass cls = CharacterClass.valueOf(props.getProperty("class"));
            Player p = new Player(props.getProperty("name"), cls);
            p.restoreFromSave(
                    Integer.parseInt(props.getProperty("level")),
                    Integer.parseInt(props.getProperty("exp")),
                    Integer.parseInt(props.getProperty("gold")),
                    Integer.parseInt(props.getProperty("hp")),
                    Integer.parseInt(props.getProperty("maxHp")),
                    Integer.parseInt(props.getProperty("mana")),
                    Integer.parseInt(props.getProperty("maxMana")),
                    Integer.parseInt(props.getProperty("attack")),
                    Integer.parseInt(props.getProperty("defense"))
            );

            String inv = props.getProperty("inventory", "").trim();
            if (!inv.isEmpty()) {
                for (String name : inv.split(",")) {
                    Item item = Items.byName(name.trim());
                    if (item != null) p.addItem(item);
                }
            }
            return p;

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[Save] Gagal memuat: " + e.getMessage());
            return null;
        }
    }

    public static void deleteSave() {
        try { Files.deleteIfExists(SAVE); }
        catch (IOException e) { System.err.println("[Save] Gagal hapus: " + e.getMessage()); }
    }
}
