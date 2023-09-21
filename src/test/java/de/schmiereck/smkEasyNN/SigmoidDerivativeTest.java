package de.schmiereck.smkEasyNN;

import de.schmiereck.smkEasyNN.mlp.original.DoubleNormNeuralNet;
import de.schmiereck.smkEasyNN.mlp.original.IntegerNorm2NeuralNet;
import de.schmiereck.smkEasyNN.mlp.original.IntegerNormNeuralNet;

public class SigmoidDerivativeTest {
    public static void main(String[] args) {
        System.out.print("Double/Integer-sigmoidDerivative: ------------------------------------------------");
        for (double x = -2.0D; x <= 2.0D; x += 0.1D) {
            System.out.printf("%f = %f    \t", x, DoubleNormNeuralNet.sigmoidDerivative(x));
            int y = (int) (x * 255);
            System.out.printf("%d = %d\t", y, IntegerNormNeuralNet.sigmoidDerivative(y));
            System.out.printf("%d = %d\n", y, IntegerNorm2NeuralNet.sigmoidDerivative(y));
        }
    }
}
