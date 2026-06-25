package rpg.character;

import rpg.item.*;
import rpg.skill.Skills.*;
import rpg.skill.Skills;
import rpg.util.RNG;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Character {

    public enum EnemyType {
        SLIME   ("Slime",    "🟢",  50,  8,  1,  0,  6,  20,  15),
        GOBLIN  ("Goblin",   "👺",  70, 12,  3,  0,  9,  35,  25),
        ORC     ("Orc",      "👹", 110, 18,  7,  0,  5,  55,  40),
        SKELETON("Skeleton", "💀",  80, 14,  5,  0,  8,  40,  30),
        DRAGON  ("Dragon",   "🐉", 200, 28, 12, 50,  7, 150, 120);

        public final String displayName, icon;
        public final int maxHp, atk, def, mana, spd, expReward, goldReward;

        EnemyType(String dn, String icon, int maxHp, int atk, int def,
                  int mana, int spd, int expReward, int goldReward) {
            this.displayName = dn; this.icon = icon;
            this.maxHp = maxHp; this.atk = atk; this.def = def;
            this.mana  = mana;  this.spd = spd;
            this.expReward = expReward; this.goldReward = goldReward;
        }
    }

    private final EnemyType type;
    private final List<Skill> skills = new ArrayList<>();

    public Enemy(EnemyType type) {
        super(type.displayName, type.maxHp, type.atk, type.def, type.mana, type.spd);
        this.type = type;
        if (type == EnemyType.DRAGON) skills.add(new Skills.Fireball());
    }

    // ── Loot ──────────────────────────────────────────────────

    public Item dropItem() {
        double r = RNG.random();
        return switch (type) {
            case SLIME    -> r < 0.40 ? new Items.Potion() : null;
            case GOBLIN   -> r < 0.35 ? new Items.Potion() : r < 0.50 ? new Items.Elixir() : null;
            case ORC      -> r < 0.30 ? new Items.Potion() : r < 0.50 ? new Items.Elixir()
                                      : r < 0.60 ? new Items.ArmorPlate() : null;
            case SKELETON -> r < 0.40 ? new Items.Bomb() : null;
            case DRAGON   -> r < 0.50 ? new Items.Elixir() : r < 0.80 ? new Items.ArmorPlate() : new Items.Bomb();
        };
    }

    public boolean shouldUseSkill() {
        return !skills.isEmpty() && getMana() >= skills.get(0).getManaCost() && RNG.chance(0.35);
    }

    // ── Getters ───────────────────────────────────────────────

    public EnemyType getType()    { return type; }
    public int getExpReward()     { return type.expReward; }
    public int getGoldReward()    { return type.goldReward + RNG.range(0, 15); }

    @Override
    public List<Skill> getSkills() { return new ArrayList<>(skills); }
}
