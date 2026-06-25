# ⚔️ GameRPG — CLI Adventure (Java Edition v2.1)

Game RPG berbasis teks (CLI) yang dibuat dengan Java. Fitur turn-based combat, 3 kelas karakter, sistem inventory, skill, shop, dan autosave.

---

## 📦 Ekstrak File

File dikirim dalam format `.rar`. Ekstrak dulu sebelum dijalankan:

**Windows** — klik kanan → *Extract Here* (butuh WinRAR / 7-Zip)

**Linux / WSL:**
```bash
sudo apt install unrar    # install jika belum ada
unrar x GameRPG.rar
```

Setelah diekstrak, struktur folder yang penting:
```
GameRPG/
└── src/                  ← source code Java
    └── rpg/              ← package utama
```

---

## ▶️ Cara Menjalankan

> **Requirement:** Java 11 atau lebih baru  
> Cek versi: `java -version`

### 🖥️ Windows — Command Prompt (CMD)

```cmd
:: Aktifkan UTF-8 agar emoji tampil dengan benar
chcp 65001

cd GameRPG\src

:: Compile semua file Java
for /r rpg %f in (*.java) do javac -d out "%f"

:: Jalankan
java -cp out rpg.Main
```

> 💡 **Catatan CMD:**
> - Jalankan CMD sebagai **Administrator** jika muncul error permission
> - Jika emoji masih tidak tampil, coba font **Consolas** atau **Lucida Console** (klik kanan title bar CMD → Properties → Font)
> - Untuk pengalaman terbaik di Windows, gunakan **Windows Terminal** (bisa install dari Microsoft Store) — sudah support UTF-8 secara default

### 🐧 Linux / WSL / macOS

```bash
cd GameRPG/src

# Compile semua file Java
find rpg -name "*.java" | xargs javac -d out

# Jalankan
java -cp out rpg.Main
```

> 💡 **Catatan:** File save (`savegame.json`) akan dibuat otomatis di direktori tempat kamu menjalankan perintah `java`.

---

## 🗺️ Alur Program

```
Mulai
  │
  ├─► Cek save file
  │     ├─ Ada    → Pilih: Continue / New Game
  │     └─ Tidak  → Setup karakter baru
  │
  ├─► Input nama + pilih kelas (Warrior / Mage / Archer)
  │
  └─► Game Loop (Village Hub)
        │
        ├─ [1] Explore  → spawn musuh acak → Battle
        │                     ├─ Menang  → EXP + Gold + item drop → Autosave
        │                     ├─ Kalah   → HP dipulihkan 1/3 → Autosave
        │                     └─ Kabur   → kembali ke hub
        │
        ├─ [2] Shop     → beli Potion / Elixir / Bomb / Armor Plate
        │
        ├─ [3] Inventory → pakai item di luar battle
        │
        ├─ [4] Status    → lihat stat karakter
        │
        └─ [5] Quit      → keluar game
```

### ⚔️ Alur Battle (Turn-Based)

Setiap giliran player bisa memilih:
- **Attack** — serangan normal (ada chance miss, dodge, critical)
- **Skill** — skill khusus sesuai kelas (butuh Mana)
- **Item** — pakai item dari inventory
- **Retreat** — kabur dari pertempuran

Giliran ditentukan oleh **Speed** — karakter dengan speed lebih tinggi menyerang duluan.

---

## 🧙 Kelas Karakter

| Kelas | Keunggulan | Skills |
|-------|-----------|--------|
| ⚔️ Warrior | HP & DEF tinggi | Slash (lifesteal), Shield Bash |
| 🔮 Mage | ATK & Mana tinggi | Fireball, Frost Nova (freeze) |
| 🏹 Archer | Speed tinggi | Piercing Shot, Rain of Arrows |

---

## 👾 Musuh

| Musuh | HP | Reward |
|-------|----|--------|
| 🟢 Slime | 50 | 20 EXP / 15 Gold |
| 👺 Goblin | 70 | 35 EXP / 25 Gold |
| 💀 Skeleton | 80 | 40 EXP / 30 Gold |
| 👹 Orc | 110 | 55 EXP / 40 Gold |
| 🐉 Dragon | 200 | 150 EXP / 120 Gold *(boss)* |

---

## 💾 Save System

Progress disimpan **otomatis** ke `savegame.json` setelah setiap battle selesai (menang maupun kalah). Tidak perlu save manual.

---

## 🏗️ Struktur Package

```
rpg/
├── Main.java              Entry point
├── character/             Character, Player, Enemy, CharacterClass
├── skill/                 Interface Skill + implementasi tiap skill
├── item/                  Abstract Item + Potion, Elixir, Bomb, ArmorPlate
├── system/                GameEngine, BattleSystem, Shop, SaveSystem
├── ui/                    UI (tampilan CLI), InputHandler
└── util/                  RNG (random number generator)
```