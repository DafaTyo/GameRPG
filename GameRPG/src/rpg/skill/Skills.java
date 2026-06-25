package rpg.skill;

import rpg.character.Character;
import rpg.util.RNG;

/** Single file containing the Skill interface, SkillResult, and all implementations. */
public final class Skills {

    private Skills() {}

    // ── Result ────────────────────────────────────────────────

    public record SkillResult(String message, int damage, int healAmount) {
        public static SkillResult ofDamage(String msg, int dmg) { return new SkillResult(msg, dmg, 0); }
        public static SkillResult ofHeal(String msg, int heal)  { return new SkillResult(msg, 0, heal); }
    }

    // ── Interface ─────────────────────────────────────────────

    public interface Skill {
        String getName();
        String getDescription();
        String getIcon();
        int    getManaCost();
        SkillResult execute(Character user, Character target);
    }

    // ── Implementations ───────────────────────────────────────

    /** ⚔️ Slash – 150% ATK + 20% lifesteal */
    public static class Slash implements Skill {
        public String getName()        { return "Slash"; }
        public String getIcon()        { return "⚔️"; }
        public String getDescription() { return "150% ATK + lifesteal 20%"; }
        public int getManaCost()       { return 20; }
        public SkillResult execute(Character u, Character t) {
            int dmg = Math.max(1, (int)(u.getAttack() * 1.5) - t.getDefense());
            t.takeDamage(dmg);
            int heal = (int)(dmg * 0.2);
            if (u instanceof rpg.character.Player) u.heal(heal);
            return new SkillResult("⚔️ Slash! " + dmg + " damage, lifesteal +" + heal + " HP", dmg, heal);
        }
    }

    /** 🛡️ ShieldBash – 80% ATK + pulih 25 HP */
    public static class ShieldBash implements Skill {
        public String getName()        { return "Shield Bash"; }
        public String getIcon()        { return "🛡️"; }
        public String getDescription() { return "80% ATK + pulih 25 HP"; }
        public int getManaCost()       { return 15; }
        public SkillResult execute(Character u, Character t) {
            int dmg = Math.max(1, (int)(u.getAttack() * 0.8) - t.getDefense());
            t.takeDamage(dmg);
            u.heal(25);
            return new SkillResult("🛡️ Shield Bash! " + dmg + " damage + pulih 25 HP", dmg, 25);
        }
    }

    /** 🔥 Fireball – 200% ATK, abaikan 50% DEF */
    public static class Fireball implements Skill {
        public String getName()        { return "Fireball"; }
        public String getIcon()        { return "🔥"; }
        public String getDescription() { return "200% ATK, abaikan 50% DEF"; }
        public int getManaCost()       { return 35; }
        public SkillResult execute(Character u, Character t) {
            int dmg = Math.max(1, (int)(u.getAttack() * 2.0) - (t.getDefense() / 2));
            t.takeDamage(dmg);
            return SkillResult.ofDamage("🔥 Fireball! " + dmg + " damage!", dmg);
        }
    }

    /**
     * ❄️ FrostNova – 130% ATK + 40% chance bonus damage.
     * Freeze mechanic dihapus; efek dingin sekarang jadi bonus damage murni.
     */
    public static class FrostNova implements Skill {
        public String getName()        { return "Frost Nova"; }
        public String getIcon()        { return "❄️"; }
        public String getDescription() { return "130% ATK + 40% chance bonus 50% ATK"; }
        public int getManaCost()       { return 30; }
        public SkillResult execute(Character u, Character t) {
            int dmg = Math.max(1, (int)(u.getAttack() * 1.3) - t.getDefense());
            t.takeDamage(dmg);
            boolean bonus = RNG.chance(0.40);
            int bonusDmg = 0;
            if (bonus) {
                bonusDmg = Math.max(1, (int)(u.getAttack() * 0.5));
                t.takeDamage(bonusDmg);
            }
            int total = dmg + bonusDmg;
            String msg = "❄️ Frost Nova! " + dmg + " damage"
                    + (bonus ? " + SHARD HIT! +" + bonusDmg + " bonus = " + total + " total" : "");
            return SkillResult.ofDamage(msg, total);
        }
    }

    /** 🏹 PiercingShot – 160% ATK, abaikan semua DEF */
    public static class PiercingShot implements Skill {
        public String getName()        { return "Piercing Shot"; }
        public String getIcon()        { return "🏹"; }
        public String getDescription() { return "160% ATK, abaikan SEMUA DEF"; }
        public int getManaCost()       { return 28; }
        public SkillResult execute(Character u, Character t) {
            int dmg = (int)(u.getAttack() * 1.6);
            t.takeDamage(dmg);
            return SkillResult.ofDamage("🏹 Piercing Shot! " + dmg + " (armor pierced!)", dmg);
        }
    }

    /** 🌧️ RainOfArrows – 3× tembakan, setiap 70% ATK */
    public static class RainOfArrows implements Skill {
        public String getName()        { return "Rain of Arrows"; }
        public String getIcon()        { return "🌧️"; }
        public String getDescription() { return "3× tembakan, tiap 70% ATK"; }
        public int getManaCost()       { return 32; }
        public SkillResult execute(Character u, Character t) {
            int total = 0;
            StringBuilder sb = new StringBuilder("🌧️ Rain of Arrows! [");
            for (int i = 0; i < 3; i++) {
                int dmg = Math.max(1, (int)(u.getAttack() * 0.7) + RNG.range(-2, 4) - t.getDefense());
                t.takeDamage(dmg);
                total += dmg;
                if (i > 0) sb.append(", ");
                sb.append(dmg);
            }
            sb.append("] = ").append(total).append(" total");
            return SkillResult.ofDamage(sb.toString(), total);
        }
    }
}
