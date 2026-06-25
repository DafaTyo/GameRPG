package rpg.ui;

import rpg.character.Enemy;
import rpg.character.Player;
import rpg.item.Item;
import rpg.skill.Skills.Skill;

import java.util.List;

public final class UI {
    private UI() {}

    private static final String SEP  = "═".repeat(54);
    private static final String THIN = "─".repeat(54);

    // ── Screen ───────────────────────────────────────────────
    public static void clear() { System.out.println("\n".repeat(40)); }

    // ── Bars ─────────────────────────────────────────────────
    private static String bar(int cur, int max, int len, char fill, char empty) {
        int n = max > 0 ? Math.min(len, cur * len / max) : 0;
        return "[" + String.valueOf(fill).repeat(n) + String.valueOf(empty).repeat(len - n)
               + "] " + cur + "/" + max;
    }
    public static String hpBar(int c, int m)   { return bar(c, m, 20, '█', '░'); }
    public static String manaBar(int c, int m) { return bar(c, m, 15, '▓', '░'); }

    // ── Panels ───────────────────────────────────────────────
    public static void battlePanel(Player p, Enemy e) {
        System.out.println("═══════════════════════ ⚔️  BATTLE ⚔️  ═══════════════════════");
        System.out.printf("  👤  %s [Lv.%d]%n", p.getName(), p.getLevel());
        System.out.println("  ❤️  HP   " + hpBar(p.getHp(), p.getMaxHp()));
        System.out.println("  💧  Mana " + manaBar(p.getMana(), p.getMaxMana()));
        System.out.println(THIN);
        System.out.printf("  %s %s%n", e.getType().icon, e.getName());
        System.out.println("  ❤️  HP   " + hpBar(e.getHp(), e.getMaxHp()));
        System.out.println(SEP);
    }

    public static void playerStatus(Player p) {
        System.out.println(SEP);
        System.out.printf("  📊 STATUS — %s %s [Lv.%d]%n",
                p.getCls().icon, p.getName(), p.getLevel());
        System.out.println(THIN);
        System.out.printf("  EXP  : %d / %d%n", p.getExp(), p.expToNext());
        System.out.printf("  ❤️  HP   : %s%n", hpBar(p.getHp(), p.getMaxHp()));
        System.out.printf("  💧 Mana  : %s%n", manaBar(p.getMana(), p.getMaxMana()));
        System.out.println(THIN);
        System.out.printf("  ⚔️  ATK: %-4d  🛡️  DEF: %-4d  💨 SPD: %d%n",
                p.getAttack(), p.getDefense(), p.getSpeed());
        System.out.printf("  💰 Gold: %d%n", p.getGold());
        System.out.println(SEP);
    }

    public static void showInventory(Player p) {
        List<Item> items = p.getInventory();
        System.out.println(SEP);
        System.out.printf("  🎒 INVENTORY  [%d/%d]%n", p.getInventorySize(), p.getMaxInventory());
        System.out.println(THIN);
        if (items.isEmpty()) {
            System.out.println("  (kosong)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item it = items.get(i);
                System.out.printf("  [%2d] %s %s — %s%n",
                        i + 1, it.getIcon(), it.getName(), it.getDescription());
            }
        }
        System.out.println("  [ 0] Kembali");
        System.out.println(SEP);
    }

    public static void showSkillMenu(Player p) {
        List<Skill> skills = p.getSkills();
        System.out.println(SEP);
        System.out.printf("  💥 SKILL  [Mana: %d/%d]%n", p.getMana(), p.getMaxMana());
        System.out.println(THIN);
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            String lack = p.getMana() < s.getManaCost() ? " (Mana kurang)" : "";
            System.out.printf("  [%d] %s %-20s [MP:%d]%s%n",
                    i + 1, s.getIcon(), s.getName(), s.getManaCost(), lack);
        }
        System.out.println("  [0] Batal\n" + SEP);
    }

    public static void showShop(int gold) {
        System.out.println(SEP);
        System.out.println("  🛒 SHOP  💰 Gold: " + gold);
        System.out.println(THIN);
        System.out.println("  [1] 🧪 Potion      Pulih 40 HP             Harga: 50");
        System.out.println("  [2] ✨ Elixir      Pulih 60HP + 30MP       Harga: 100");
        System.out.println("  [3] 💣 Bomb        45 dmg (ignore DEF)     Harga: 80");
        System.out.println("  [4] 🛡️ Armor Plate DEF +6 permanen         Harga: 120");
        System.out.println("  [0] Keluar\n" + SEP);
    }

    // ── Result screens ───────────────────────────────────────
    public static void victoryScreen(int exp, int gold, Item drop) {
        System.out.println("\n  ════════════════════════════════════\n"
                + "       🏆  V I C T O R Y !\n"
                + "    EXP  : +" + exp + "\n"
                + "    Gold : +" + gold + "\n"
                + "    Drop : " + (drop != null ? drop.getIcon() + " " + drop.getName() : "(tidak ada)")
                + "\n  ════════════════════════════════════");
    }

    public static void defeatScreen() {
        System.out.println("\n  ════════════════════════════════════\n"
                + "      💀  G A M E   O V E R\n"
                + "    Kamu telah dikalahkan...\n"
                + "  ════════════════════════════════════");
    }

    public static void levelUpScreen(Player p) {
        System.out.println("\n  ════════════════════════════════════\n"
                + "      ✨  L E V E L   U P !\n"
                + "    Level " + p.getLevel() + "! HP/Mana dipulihkan penuh!\n"
                + "  ════════════════════════════════════");
    }

    // ── Utilities ────────────────────────────────────────────
    public static void msg(String text)     { System.out.println("  " + text); }
    public static void info(String text)    { System.out.println("  ℹ️  " + text); }
    public static void warn(String text)    { System.out.println("  ⚠️  " + text); }
    public static void success(String text) { System.out.println("  ✅ " + text); }
    public static void pressEnter()         { System.out.print("\n  [Tekan ENTER untuk lanjut...]"); }
    public static void prompt(String text)  { System.out.print("\n  " + text); }
}
