package rpg.character;

import rpg.skill.Skills.Skill;
import rpg.util.RNG;
import java.util.List;

public abstract class Character {

    private String name;
    private int hp, maxHp, mana, maxMana, attack, defense, speed;

    protected Character(String name, int maxHp, int attack, int defense, int maxMana, int speed) {
        this.name    = name;
        this.maxHp   = maxHp;   this.hp   = maxHp;
        this.maxMana = maxMana; this.mana = maxMana;
        this.attack  = attack;  this.defense = defense; this.speed = speed;
    }

    // ── Combat ────────────────────────────────────────────────

    public record AttackResult(Type type, int damage) {
        public enum Type { NORMAL, CRITICAL, MISS, DODGE }
    }

    public AttackResult attackTarget(Character target) {
        if (RNG.chance(0.10)) return new AttackResult(AttackResult.Type.MISS,  0);
        if (RNG.chance(0.15)) return new AttackResult(AttackResult.Type.DODGE, 0);

        int raw = attack + RNG.range(-3, 3);
        boolean crit = RNG.chance(0.20);
        if (crit) raw = (int)(raw * 1.8);

        int dmg = Math.max(1, raw - target.defense);
        target.takeDamage(dmg);
        return new AttackResult(crit ? AttackResult.Type.CRITICAL : AttackResult.Type.NORMAL, dmg);
    }

    public void takeDamage(int amount)   { hp   = Math.max(0, hp - amount); }
    public void heal(int amount)         { hp   = Math.min(maxHp, hp + amount); }
    public void restoreMana(int amount)  { mana = Math.min(maxMana, mana + amount); }
    public boolean useMana(int cost)     { if (mana < cost) return false; mana -= cost; return true; }
    public boolean isAlive()             { return hp > 0; }

    public abstract List<Skill> getSkills();

    // ── Getters / Setters ─────────────────────────────────────

    public String getName()  { return name; }
    public int getHp()       { return hp; }
    public int getMaxHp()    { return maxHp; }
    public int getMana()     { return mana; }
    public int getMaxMana()  { return maxMana; }
    public int getAttack()   { return attack; }
    public int getDefense()  { return defense; }
    public int getSpeed()    { return speed; }

    public void setMaxHp(int v)   { maxHp   = v; }
    public void setMaxMana(int v) { maxMana  = v; }
    public void setAttack(int v)  { attack   = v; }
    public void setDefense(int v) { defense  = v; }
    public void setHp(int v)      { hp       = Math.min(v, maxHp); }
    public void setMana(int v)    { mana     = Math.min(v, maxMana); }
}
