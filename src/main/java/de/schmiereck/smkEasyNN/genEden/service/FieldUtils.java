package de.schmiereck.smkEasyNN.genEden.service;

public abstract class FieldUtils {
    private FieldUtils() {
    }

    public static void transferFieldInToOut(final Field field) {
        field.outValueArr[0] = field.inValueArr[0];
        field.outValueArr[1] = field.inValueArr[1];
        field.outValueArr[2] = field.inValueArr[2];

        field.outComArr[0] = field.inComArr[0];
        field.outComArr[1] = field.inComArr[1];
        field.outComArr[2] = field.inComArr[2];
    }

    public static void transferValueFieldOutToIn(final Field outField, final Field inField, final double factor) {
        inField.inValueArr[0] += outField.outValueArr[0] * factor;
        inField.inValueArr[1] += outField.outValueArr[1] * factor;
        inField.inValueArr[2] += outField.outValueArr[2] * factor;
    }

    public static void transferComFieldOutToIn(final Field outField, final Field inField, final double factor) {
        inField.inComArr[0] += outField.outComArr[0] * factor;
        inField.inComArr[1] += outField.outComArr[1] * factor;
        inField.inComArr[2] += outField.outComArr[2] * factor;
    }

    public static void resetInField(Field field) {
        field.inValueArr[0] = 0.0D;
        field.inValueArr[1] = 0.0D;
        field.inValueArr[2] = 0.0D;

        field.inComArr[0] = 0.0D;
        field.inComArr[1] = 0.0D;
        field.inComArr[2] = 0.0D;
    }
}
