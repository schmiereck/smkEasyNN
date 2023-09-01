package de.schmiereck.smkEasyNN;

public class MultTest {
    public static void main(String[] args) {
        // 0.25 = 0.5 * 0.5
        System.out.printf("%.3f = %.2f * %.2f\n", 0.5D * 0.5D, 0.5D, 0.5D);
        // 0.125 = 0.5 * 0.5 * 0.5
        System.out.printf("%.3f = %.2f * %.2f * %.2f\n", 0.5D * 0.5D * 0.5D, 0.5D, 0.5D, 0.5D);

        // 63 = 127 * 127
        System.out.printf("%d = %d * %d\n", (127 * 127) / 255, 127, 127);
        // 31 = 127 * 127 * 127
        System.out.printf("%d = %d * %d * %d\n", (((127 * 127) / 255) * 127) / 255, 127, 127, 127);
    }
}
