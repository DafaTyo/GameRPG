package rpg.ui;

import java.util.Scanner;

public final class InputHandler {

    private final Scanner scanner = new Scanner(System.in);

    public int readInt(int min, int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) { UI.warn("Input tidak boleh kosong!"); UI.prompt("Pilih (" + min + "-" + max + "): "); continue; }
                int val = Integer.parseInt(line);
                if (val < min || val > max) { UI.warn("Pilihan harus " + min + "–" + max + "!"); UI.prompt("Pilih (" + min + "-" + max + "): "); }
                else return val;
            } catch (NumberFormatException e) {
                UI.warn("Input harus angka!"); UI.prompt("Pilih (" + min + "-" + max + "): ");
            }
        }
    }

    public String readString(int maxLen) {
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty())              { UI.warn("Input tidak boleh kosong!"); UI.prompt("> "); }
            else if (line.length() > maxLen) { UI.warn("Maks " + maxLen + " karakter!"); UI.prompt("> "); }
            else return line;
        }
    }

    public void waitEnter() { scanner.nextLine(); }

    public static void delay(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void typewriter(String text, int msPerChar) {
        for (char c : text.toCharArray()) { System.out.print(c); delay(msPerChar); }
        System.out.println();
    }
}
