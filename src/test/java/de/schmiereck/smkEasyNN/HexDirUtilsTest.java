package de.schmiereck.smkEasyNN;

import de.schmiereck.smkEasyNN.genEden.service.HexDirUtils;
import org.junit.jupiter.api.Test;

public class HexDirUtilsTest {

    @Test
    public void test() {
        System.out.println("AngelTest.test");
        System.out.print("\t");
        for (int a1 = 0; a1 < 6; a1++) {
            System.out.printf("\ta1:%d", a1);
        }
        System.out.println();
        for (int a2 = 0; a2 < 6; a2++) {
            System.out.printf("a2:%d", a2);
            for (int a1 = 0; a1 < 6; a1++) {
                System.out.printf("\t%+,4d", HexDirUtils.calcAngelDiff(a1, a2));
            }
            System.out.println();
        }
        System.out.println();
    }
}
