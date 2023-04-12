package de.schmiereck.smkEasyNN;

public class SigmoidTest {
    public static void main(String[] args) {
        // Double: output 0.0 to 1.0
        // -10,000000 = 0,000045
        // 0,000000 = 0,500000
        // 10,000000 = 0,999955
        // Integer:
        // 0 = 127
        System.out.print("Double/Integer-sigmoid: ------------------------------------------------");
        for (double x = -10.0D; x <= 10.0D; x += 0.25D) {
            System.out.printf("%f = %f    \t", x, DoubleNormNeuralNet.sigmoid(x));
            int y = (int) (x * 255);
            System.out.printf("%d = %d\t", y, IntegerNormNeuralNet.sigmoid(y));
            System.out.printf("%d = %d\t", y, IntegerNormNeuralNet.sigmoidInteger(y));
            System.out.printf("%d = %d\n", y, IntegerNorm2NeuralNet.sigmoidInteger(y));
        }
        /*
        System.out.print("Double/Integer-sigmoidDerivative: ------------------------------------------------");
        for (double x = -10.0D; x <= 10.0D; x += 0.25D) {
            System.out.printf("%f = %f    \t", x, DoubleNormNeuralNet.sigmoidDerivative(x));
            int y = (int) (x * 255);
            System.out.printf("%d = %d\n", y, IntegerNormNeuralNet.sigmoidDerivative(y));
        }
        */
    }
}
