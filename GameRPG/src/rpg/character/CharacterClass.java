package rpg.character;

public enum CharacterClass {
    WARRIOR("Warrior", "⚔️",  120, 15, 10,  60,  8),
    MAGE   ("Mage",    "🔮",   80, 25,  3, 120,  7),
    ARCHER ("Archer",  "🏹",  100, 18,  5,  80, 12);

    public final String displayName, icon;
    public final int hpBonus, attackBonus, defenseBonus, maxMana, speed;

    CharacterClass(String displayName, String icon,
                   int hpBonus, int attackBonus, int defenseBonus, int maxMana, int speed) {
        this.displayName  = displayName; this.icon        = icon;
        this.hpBonus      = hpBonus;     this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus; this.maxMana    = maxMana;
        this.speed        = speed;
    }
}
