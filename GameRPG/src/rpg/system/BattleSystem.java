package rpg.system;

import rpg.character.Character;
import rpg.character.Enemy;
import rpg.character.Player;
import rpg.item.Item;
import rpg.skill.Skills.Skill;
import rpg.skill.Skills.SkillResult;
import rpg.ui.*;

import java.util.List;

public class BattleSystem {

    public enum BattleResult { WIN, LOSE, RETREAT }

    private final InputHandler input;

    public BattleSystem(InputHandler input) { this.input = input; }

    // ── Main loop ─────────────────────────────────────────────

    public BattleResult run(Player player, Enemy enemy) {
        UI.clear();
        InputHandler.typewriter("  ⚔️  " + enemy.getType().icon + " " + enemy.getName() + " muncul!", 30);
        InputHandler.delay(800);

        boolean playerFirst = player.getSpeed() >= enemy.getSpeed();
        UI.info(playerFirst ? "Kamu lebih cepat! Giliran kamu duluan." : "Musuh lebih cepat!");
        InputHandler.delay(600);

        while (player.isAlive() && enemy.isAlive()) {
            UI.clear();
            UI.battlePanel(player, enemy);

            if (playerFirst) {
                BattleResult r = playerTurn(player, enemy);
                if (r != null) return r;
                if (enemy.isAlive()) enemyTurn(player, enemy);
            } else {
                enemyTurn(player, enemy);
                if (player.isAlive() && enemy.isAlive()) {
                    BattleResult r = playerTurn(player, enemy);
                    if (r != null) return r;
                }
            }

            UI.pressEnter();
            input.waitEnter();
        }

        return player.isAlive() ? BattleResult.WIN : BattleResult.LOSE;
    }

    // ── Player turn ───────────────────────────────────────────

    /**
     * Returns RETREAT if player retreats, null jika giliran selesai normal.
     * Bug rekursi dihapus — sekarang pakai loop.
     */
    private BattleResult playerTurn(Player player, Enemy enemy) {
        while (true) {
            System.out.println("\n  ┌─ Giliran Kamu ────────────────────────┐");
            System.out.println("  │  [1] ⚔️  Attack  [2] 💥 Skill         │");
            System.out.println("  │  [3] 🎒 Item     [4] 🏃 Retreat       │");
            System.out.println("  └───────────────────────────────────────┘");
            UI.prompt("Pilih aksi: ");

            int choice = input.readInt(1, 4);
            switch (choice) {
                case 1 -> { doAttack(player, enemy); return null; }
                case 2 -> { if (doPlayerSkill(player, enemy)) return null; }
                case 3 -> { if (doPlayerItem(player, enemy))  return null; }
                case 4 -> { UI.msg("🏃 Kamu melarikan diri!"); return BattleResult.RETREAT; }
            }
            // Jika skill/item gagal (return false), loop kembali ke menu
        }
    }

    private void doAttack(Character attacker, Character target) {
        Character.AttackResult r = attacker.attackTarget(target);
        UI.msg(switch (r.type()) {
            case MISS     -> "❌ Miss! Serangan meleset.";
            case DODGE    -> "💨 " + target.getName() + " menghindar!";
            case CRITICAL -> "🔥 CRITICAL HIT! " + r.damage() + " damage ke " + target.getName() + "!";
            default       -> "💥 " + attacker.getName() + " menyerang " + target.getName()
                             + " (" + r.damage() + " dmg)";
        });
    }

    private boolean doPlayerSkill(Player player, Enemy enemy) {
        List<Skill> skills = player.getSkills();
        if (skills.isEmpty()) { UI.warn("Kamu tidak punya skill!"); return false; }

        UI.showSkillMenu(player);
        UI.prompt("Pilih skill: ");
        int choice = input.readInt(0, skills.size());
        if (choice == 0) return false;

        Skill skill = skills.get(choice - 1);
        if (player.getMana() < skill.getManaCost()) {
            UI.warn("Mana tidak cukup! Butuh " + skill.getManaCost() + " MP.");
            return false;
        }

        player.useMana(skill.getManaCost());
        SkillResult result = skill.execute(player, enemy);
        UI.msg(result.message());
        return true;
    }

    private boolean doPlayerItem(Player player, Enemy enemy) {
        if (player.getInventorySize() == 0) { UI.warn("Inventory kosong!"); return false; }

        UI.showInventory(player);
        UI.prompt("Pilih item (0 batal): ");
        int choice = input.readInt(0, player.getInventorySize());
        if (choice == 0) return false;

        Item item = player.getItem(choice - 1);
        if (item == null) { UI.warn("Item tidak valid!"); return false; }

        String err = item.canUse(player, enemy);
        if (err != null) { UI.warn(err); return false; }

        UI.msg(item.apply(player, enemy));
        player.removeItem(choice - 1);
        return true;
    }

    // ── Enemy turn ────────────────────────────────────────────

    private void enemyTurn(Player player, Enemy enemy) {
        System.out.println();
        UI.msg("--- Giliran " + enemy.getType().icon + " " + enemy.getName() + " ---");
        InputHandler.delay(600);

        if (enemy.shouldUseSkill()) {
            Skill skill = enemy.getSkills().get(0);
            enemy.useMana(skill.getManaCost());
            SkillResult result = skill.execute(enemy, player);
            UI.msg(result.message());
        } else {
            doAttack(enemy, player);
            if (player.isAlive())
                UI.msg("  (HP Player: " + player.getHp() + "/" + player.getMaxHp() + ")");
        }
    }

    // ── Post-battle ───────────────────────────────────────────

    public boolean processVictory(Player player, Enemy enemy) {
        int exp  = enemy.getExpReward();
        int gold = enemy.getGoldReward();
        Item drop = enemy.dropItem();

        player.addGold(gold);
        boolean leveledUp = player.gainExp(exp);

        if (drop != null) {
            if (!player.isInventoryFull()) {
                player.addItem(drop);
                UI.info("Item " + drop.getIcon() + " " + drop.getName() + " masuk inventory!");
            } else {
                UI.warn("Inventory penuh! Drop " + drop.getName() + " hilang.");
            }
        }

        UI.victoryScreen(exp, gold, drop);
        SaveSystem.save(player);
        UI.info("💾 Progress tersimpan.");
        return leveledUp;
    }

    public void processDefeat(Player player) {
        player.heal((int)(player.getMaxHp() * 0.3));
        player.restoreMana((int)(player.getMaxMana() * 0.5));
        UI.defeatScreen();
        UI.info("HP dipulihkan 30%. Kembali ke desa...");
        SaveSystem.save(player);
        UI.info("💾 Progress tersimpan.");
    }
}
