package rpg.system;

import rpg.character.*;
import rpg.item.Item;
import rpg.ui.*;
import rpg.util.RNG;

public class GameEngine {

    private Player       player;
    private BattleSystem battle;
    private Shop         shop;
    private InputHandler input;
    private boolean      running = true;

    // Pool semua musuh reguler (Battle + Dungeon digabung)
    private static final Enemy.EnemyType[] ADVENTURE_POOL = {
        Enemy.EnemyType.SLIME,
        Enemy.EnemyType.GOBLIN,
        Enemy.EnemyType.ORC,
        Enemy.EnemyType.SKELETON
    };

    public void start() {
        input  = new InputHandler();
        battle = new BattleSystem(input);
        shop   = new Shop(input);

        showIntro();

        if (SaveSystem.hasSaveFile()) handleSavePrompt();
        else setupNewPlayer();

        gameLoop();
    }

    // ── Intro ─────────────────────────────────────────────────

    private void showIntro() {
        UI.clear();
        System.out.println("""
          ╔═════════════════════════════════════════════╗
          ║       ██████╗ ██████╗  ██████╗              ║
          ║       ██╔══██╗██╔══██╗██╔════╝              ║
          ║       ██████╔╝██████╔╝██║  ███╗             ║
          ║       ██╔══██╗██╔═══╝ ██║   ██║             ║
          ║       ██║  ██║██║     ╚██████╔╝             ║
          ║       ╚═╝  ╚═╝╚═╝      ╚═════╝              ║
          ║         C L I   A D V E N T U R E           ║
          ║             Java Edition  v2.3              ║
          ╚═════════════════════════════════════════════╝
        """);
        InputHandler.delay(800);
    }

    // ── Save/Load ─────────────────────────────────────────────

    private void handleSavePrompt() {
        UI.clear();
        System.out.println("  ╔══════════ 💾 SAVE FILE DITEMUKAN ══════════╗");
        System.out.println("  ║  [1] Continue  [2] New Game (hapus save)   ║");
        System.out.println("  ╚════════════════════════════════════════════╝");
        UI.prompt("Pilih: ");

        if (input.readInt(1, 2) == 1) {
            loadGame();
        } else {
            UI.warn("Data lama akan dihapus! Yakin? (1=Ya, 0=Tidak)");
            UI.prompt("> ");
            if (input.readInt(0, 1) == 1) {
                SaveSystem.deleteSave();
                UI.success("Data dihapus. Mulai baru...");
                InputHandler.delay(800);
                setupNewPlayer();
            } else {
                loadGame();
            }
        }
    }

    private void loadGame() {
        Player loaded = SaveSystem.load();
        if (loaded == null) {
            UI.warn("Gagal memuat save! Memulai baru...");
            InputHandler.delay(1000);
            setupNewPlayer();
            return;
        }
        player = loaded;
        UI.clear();
        InputHandler.typewriter("  Selamat datang kembali, "
                + player.getCls().icon + " " + player.getName()
                + " [Lv." + player.getLevel() + "]!", 30);
        InputHandler.delay(1000);
    }

    // ── New Player ────────────────────────────────────────────

    private void setupNewPlayer() {
        UI.msg("Masukkan nama karaktermu:");
        UI.prompt("> ");
        String name = input.readString(15);

        UI.clear();
        System.out.println("  ╔════════════════ Pilih Kelas ═════════════════╗");
        System.out.println("  ║  [1] ⚔️  Warrior  HP+++ | DEF++              ║");
        System.out.println("  ║  [2] 🔮  Mage     ATK++ | Mana++             ║");
        System.out.println("  ║  [3] 🏹  Archer   SPD++ | ATK+               ║");
        System.out.println("  ╚══════════════════════════════════════════════╝");
        UI.prompt("Pilih kelas (1-3): ");

        CharacterClass cls = switch (input.readInt(1, 3)) {
            case 2  -> CharacterClass.MAGE;
            case 3  -> CharacterClass.ARCHER;
            default -> CharacterClass.WARRIOR;
        };

        player = new Player(name, cls);
        UI.clear();
        InputHandler.typewriter("  Selamat datang, " + cls.icon + " " + name + " sang " + cls.displayName + "!", 35);
        InputHandler.delay(1000);
        SaveSystem.save(player);
    }

    // ── Game Loop ─────────────────────────────────────────────

    private void gameLoop() {
        while (running) {
            showMainMenu();
            handleMainMenu(input.readInt(1, 6));
        }
    }

    private void showMainMenu() {
        UI.clear();
        System.out.printf("  ═══════════════ 🗺️  MENU UTAMA ════════════════%n");
        System.out.printf("  %s %s  [Lv.%d]  ❤️ %d/%d  💰 %d%n",
                player.getCls().icon, player.getName(), player.getLevel(),
                player.getHp(), player.getMaxHp(), player.getGold());
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  [1] ⚔️  Petualangan");
        System.out.println("  [2] 🐉 Boss");
        System.out.println("  [3] 🎒 Inventory");
        System.out.println("  [4] 🛒 Shop");
        System.out.println("  [5] 📊 Status");
        System.out.println("  [6] 🚪 Keluar");
        System.out.println("  ═══════════════════════════════════════════════");
        UI.prompt("Pilih: ");
    }

    private void handleMainMenu(int choice) {
        switch (choice) {
            case 1 -> doAdventure();
            case 2 -> doBossBattle();
            case 3 -> doInventory();
            case 4 -> shop.open(player);
            case 5 -> { UI.clear(); UI.playerStatus(player); UI.pressEnter(); input.waitEnter(); }
            case 6 -> {
                SaveSystem.save(player);
                running = false;
                UI.clear();
                System.out.println("  💾 Tersimpan. Sampai jumpa! 👋");
            }
        }
    }

    // ── Adventure (gabungan Battle + Dungeon) ─────────────────

    private void doAdventure() {
        Enemy enemy = new Enemy(ADVENTURE_POOL[RNG.range(0, ADVENTURE_POOL.length - 1)]);
        doBattle(enemy);
    }

    // ── Helpers ───────────────────────────────────────────────

    private void doBattle(Enemy enemy) {
        BattleSystem.BattleResult result = battle.run(player, enemy);
        if      (result == BattleSystem.BattleResult.WIN)  {
            if (battle.processVictory(player, enemy)) UI.levelUpScreen(player);
        } else if (result == BattleSystem.BattleResult.LOSE) {
            battle.processDefeat(player);
        }
        UI.pressEnter();
        input.waitEnter();
    }

    private void doBossBattle() {
        if (player.getLevel() < 3) {
            UI.warn("Kamu harus minimal Level 3 untuk menghadapi Boss!");
            UI.pressEnter(); input.waitEnter();
            return;
        }
        UI.clear();
        UI.warn("Kamu akan menghadapi BOSS DRAGON! Yakin? (1=Ya, 0=Tidak)");
        UI.prompt("> ");
        if (input.readInt(0, 1) == 1) doBattle(new Enemy(Enemy.EnemyType.DRAGON));
    }

    private void doInventory() {
        UI.clear();
        UI.showInventory(player);
        if (player.getInventorySize() == 0) { UI.pressEnter(); input.waitEnter(); return; }

        UI.prompt("Pilih item untuk digunakan (0 kembali): ");
        int choice = input.readInt(0, player.getInventorySize());
        if (choice == 0) return;

        Item item = player.getItem(choice - 1);
        if (item == null) return;

        String err = item.canUse(player, null);
        if (err != null) { UI.warn(err); }
        else {
            UI.msg(item.apply(player, null));
            player.removeItem(choice - 1);
            SaveSystem.save(player);
            UI.info("💾 Progress tersimpan.");
        }
        UI.pressEnter(); input.waitEnter();
    }
}
