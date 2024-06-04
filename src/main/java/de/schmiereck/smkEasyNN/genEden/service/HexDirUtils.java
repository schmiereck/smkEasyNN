package de.schmiereck.smkEasyNN.genEden.service;

public abstract class HexDirUtils {
    public static final int MAX_ANGEL = 6;
    public static final int MAX_ANGEL_DIFF = 3;

    private HexDirUtils() {
    }

    static HexDir calcOppositeDir(final HexDir hexDir) {
        final HexDir outDir =
                switch (hexDir) {
                    case InDir0 -> HexDir.InDir3;
                    case InDir1 -> HexDir.InDir4;
                    case InDir2 -> HexDir.InDir5;
                    case InDir3 -> HexDir.InDir0;
                    case InDir4 -> HexDir.InDir1;
                    case InDir5 -> HexDir.InDir2;
                };
        return outDir;
    }

    public static int calcAngelDiff(final int a1, final int a2) {
        int diff = a2 - a1;

        // Normalisieren Sie den Winkel auf 0 bis 6
        diff = (diff + MAX_ANGEL) % MAX_ANGEL;

        // Passen Sie den Winkel an, um den kleinsten Winkel zu erhalten
        if (diff > MAX_ANGEL_DIFF) {
            diff -= MAX_ANGEL;
        }

        return diff;
    }

    public static HexDir calcOffDir(final HexDir hexDir, final int dirOff) {
        return HexDir.values()[(hexDir.ordinal() + dirOff + HexDir.values().length) % HexDir.values().length];
    }

    public static HexDir calcRotateDir(final HexDir hexDir, final HexDir rotateHexDir) {
        return calcRotateDir(hexDir, rotateHexDir.ordinal());
    }
    public static HexDir calcRotateDir(final HexDir hexDir, final int dirRotate) {
        return HexDir.values()[(hexDir.ordinal() + dirRotate + HexDir.values().length) % HexDir.values().length];
    }
}
