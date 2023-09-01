package de.schmiereck.smkEasyNN.world;

public class CalcUtilsTest {

    public static void main(String[] args) {
        test_calcDiff2();
    }

    private static void test_calcDiff2() {
        assertEquals(0.0F, WorldMain.calcDiff2(0.0F, 0.5F));
        assertEquals(0.2F, WorldMain.calcDiff2(0.2F, 0.5F));
        assertEquals(0.5F, WorldMain.calcDiff2(0.5F, 0.5F));
        assertEquals(0.5F, WorldMain.calcDiff2(0.6F, 0.5F));

        assertEquals(-0.2F, WorldMain.calcDiff2(-0.2F, 0.5F));
        assertEquals(-0.5F, WorldMain.calcDiff2(-0.5F, 0.5F));
        assertEquals(-0.5F, WorldMain.calcDiff2(-0.6F, 0.5F));

        //--------
        assertEquals(0.0F, WorldMain.calcDiff2(0.5F, 0.0F));
        assertEquals(0.2F, WorldMain.calcDiff2(0.5F, 0.2F));
        assertEquals(0.5F, WorldMain.calcDiff2(0.5F, 0.5F));
        assertEquals(0.5F, WorldMain.calcDiff2(0.5F, 0.6F));

        assertEquals(-0.2F, WorldMain.calcDiff2(0.5F, -0.2F));
        assertEquals(-0.5F, WorldMain.calcDiff2(0.5F, -0.5F));
        //assertEquals(-0.5F, WorldMain.calcDiff2(0.5F, -0.6F));

    }

    private static void assertEquals(float expected, float value) {
        if (expected != value) {
            throw new RuntimeException(String.format("expected %f is not value %f.", expected, value));
        }
    }
}
