package rpg.character;

import rpg.item.Item;
import rpg.skill.Skills.*;
import java.util.ArrayList;
import java.util.List;

public class Player extends Character {

    private int level = 1, exp = 0, gold;
    private final CharacterClass cls;
    private final List<Item>  inventory = new ArrayList<>();
    private final List<Skill> skills    = new ArrayList<>();
    private static final int  MAX_INV   = 12;

    public Player(String name, CharacterClass cls) {
        super(name, 80 + cls.hpBonus, 10 + cls.attackBonus, 2 + cls.defenseBonus, cls.maxMana, cls.speed);
        this.cls  = cls;
        this.gold = 150;
        switch (cls) {
            case WARRIOR -> { skills.add(new Slash());        skills.add(new ShieldBash()); }
            case MAGE    -> { skills.add(new Fireball());     skills.add(new FrostNova()); }
            case ARCHER  -> { skills.add(new PiercingShot()); skills.add(new RainOfArrows()); }
        }
    }

    // ── Progression ───────────────────────────────────────────

    public boolean gainExp(int amount) {
        exp += amount;
        if (exp >= expToNext()) { levelUp(); return true; }
        return false;
    }

    public int expToNext() { return level * 100; }

    private void levelUp() {
        exp -= expToNext();
        level++;
        setMaxHp(getMaxHp()     + 15 + (cls == CharacterClass.WARRIOR ? 10 : 0));
        setAttack(getAttack()   +  3 + (cls == CharacterClass.MAGE    ?  3 : 0));
        setDefense(getDefense() +  1 + (cls == CharacterClass.WARRIOR ?  2 : 0));
        setMaxMana(getMaxMana() +  5 + (cls == CharacterClass.MAGE    ? 10 : 0));
        setHp(getMaxHp()); setMana(getMaxMana());
    }

    // ── Inventory ─────────────────────────────────────────────

    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INV) return false;
        return inventory.add(item);
    }

    public boolean removeItem(int index) {
        if (index < 0 || index >= inventory.size()) return false;
        inventory.remove(index); return true;
    }

    public Item getItem(int index) {
        return (index >= 0 && index < inventory.size()) ? inventory.get(index) : null;
    }

    public boolean isInventoryFull() { return inventory.size() >= MAX_INV; }
    public List<Item> getInventory() { return new ArrayList<>(inventory); }

    // ── Save/Restore ──────────────────────────────────────────

    public void restoreFromSave(int level, int exp, int gold,
                                int hp, int maxHp, int mana, int maxMana,
                                int attack, int defense) {
        this.level = level; this.exp = exp; this.gold = gold;
        setMaxHp(maxHp); setHp(hp);
        setMaxMana(maxMana); setMana(mana);
        setAttack(attack); setDefense(defense);
    }

    // ── Getters ───────────────────────────────────────────────

    public int getLevel()            { return level; }
    public int getExp()              { return exp; }
    public int getGold()             { return gold; }
    public CharacterClass getCls()   { return cls; }
    public int getInventorySize()    { return inventory.size(); }
    public int getMaxInventory()     { return MAX_INV; }
    public void addGold(int n)       { gold += n; }
    public boolean spendGold(int n)  { if (gold < n) return false; gold -= n; return true; }

    @Override
    public List<Skill> getSkills()   { return new ArrayList<>(skills); }
}
