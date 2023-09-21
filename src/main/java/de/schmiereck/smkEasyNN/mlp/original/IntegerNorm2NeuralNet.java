package de.schmiereck.smkEasyNN.mlp.original;

import java.util.Random;

public class IntegerNorm2NeuralNet {
    private int[] layers;
    private int[][] neurons;
    private int[][][] weights;
    private int[][] bias;

    public static int MAX_NEURON_PREC = 15;
    public static double MAX_NEURON = 255.0D * MAX_NEURON_PREC;
    public static int MAX_NEURON_INT = (int)MAX_NEURON;

    public IntegerNorm2NeuralNet(int... layers) {
        this.layers = layers;
        this.neurons = new int[layers.length][];
        this.weights = new int[layers.length - 1][][];
        this.bias = new int[layers.length - 1][];
        for (int layerPos = 0; layerPos < layers.length; layerPos++) {
            final int childLayerPos = layerPos + 1;
            this.neurons[layerPos] = new int[layers[layerPos]];
            if (layerPos < layers.length - 1) {
                this.weights[layerPos] = new int[layers[layerPos]][layers[childLayerPos]];
                this.bias[layerPos] = new int[layers[childLayerPos]];
            }
        }
        initWeightsAndBias();
    }

    private void initWeightsAndBias() {
        for (int layerPos = 0; layerPos < layers.length - 1; layerPos++) {
            for (int neuronPos = 0; neuronPos < weights[layerPos].length; neuronPos++) {
                for (int k = 0; k < weights[layerPos][neuronPos].length; k++) {
                    weights[layerPos][neuronPos][k] = (int)((Math.random() - 0.5D) * MAX_NEURON_INT);
                }
            }
            for (int neuronPos = 0; neuronPos < bias[layerPos].length; neuronPos++) {
                bias[layerPos][neuronPos] = (int)((Math.random() - 0.5D) * MAX_NEURON_INT);
            }
        }
    }

    private double sigmoidDouble(double x) {
        return 1.0D / (1.0D + Math.exp(-x));
    }

    public static int sigmoidInteger(int x) {
        //double y = 1.0D / (1.0D + Math.exp(-x / 128.0D));
         double y = 1.0D / (1.0D + Math.exp(-x /  MAX_NEURON));
        //return (int) (255.0D * y) + 1;
        return (int) Math.round(MAX_NEURON * y) + 1;
    }

    public static int sigmoidIntegerSpeed(int x) {
        if (x < -128) {
            return 0;
        } else if (x > 127) {
            return 255;
        } else {
            int y = x * 256;
            y = ((y + 32768) >> 16) & 0xFFFF;
            y = (y * y) >> 8;
            y = (y * y) >> 8;
            y = (y * y) >> 8;
            return y;
        }
    }
    private double sigmoidDerivativeDouble(double x) {
        return x * (1.0D - x);
    }

    public static int sigmoidDerivative(int x) {
        return (x * (MAX_NEURON_INT - x)) / MAX_NEURON_INT;
        //return (x * (MAX_NEURON_INT - x));
    }

    public static int sigmoidDerivative2(int y) {
        // Convert the input back to the range [-255, 255]
        int x = y - 128;

        // Compute the derivative of the sigmoid function
        int d = (int) (256 * sigmoidInteger(x) * (255 - sigmoidInteger(x)));

        // Scale the result back to the range [0, 255]
        return d >> 8;
    }

    public int[] forward(int[] input) {
        for (int inputPos = 0; inputPos < input.length; inputPos++) {
            neurons[0][inputPos] = input[inputPos];
        }
        for (int layerPos = 1; layerPos < layers.length; layerPos++) {
            final int parentLayerPos = layerPos - 1;
            for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                int sum = 0;
                for (int parentLayerNeuronPos = 0; parentLayerNeuronPos < layers[parentLayerPos]; parentLayerNeuronPos++) {
                    sum += (neurons[parentLayerPos][parentLayerNeuronPos] * weights[parentLayerPos][parentLayerNeuronPos][layerNeuronPos]) / MAX_NEURON_INT;
                    //sum += (neurons[parentLayerPos][parentLayerNeuronPos] * weights[parentLayerPos][parentLayerNeuronPos][layerNeuronPos]);
                }
                sum += bias[parentLayerPos][layerNeuronPos];
                //neurons[layerPos][layerNeuronPos] = sigmoidInteger(sum / layers[parentLayerPos]);
                neurons[layerPos][layerNeuronPos] = sigmoidInteger(sum);
            }
        }
        return neurons[layers.length - 1];
    }

    /**
     * Die Methode beginnt damit, den Fehler zwischen den erwarteten und den tatsächlichen Ausgabewerten zu berechnen. Anschließend werden die Delta-Werte für jede Schicht berechnet, beginnend mit der Ausgabeschicht und dann rückwärts durch die Netzwerk-Schichten. Für jede Schicht wird zuerst das Delta-Array initialisiert und dann jedes Delta-Wert berechnet, indem die Gewichte des nächsten Layers mit den Delta-Werten des nächsten Layers multipliziert werden und das Ergebnis mit der Ableitung der Aktivierungsfunktion der aktuellen Schicht multipliziert wird.
     *
     * Sobald die Delta-Werte für eine Schicht berechnet wurden, können die Gewichte und Bias-Werte aktualisiert werden. Für jedes Gewicht wird das entsprechende Neuron in der vorherigen Schicht mit dem Delta-Wert des aktuellen Neurons in der aktuellen Schicht multipliziert und mit der Lernrate multipliziert. Das Bias-Wert des aktuellen Neurons wird einfach mit dem Delta-Wert multipliziert und mit der Lernrate aktualisiert.
     *
     * In dieser Methode wird der Fehler im Output-Layer und den Hidden-Layern berechnet und die Gewichte entsprechend angepasst. Das Update der Gewichte erfolgt basierend auf dem Gradientenabstiegsverfahren und der Lernrate.
     */
    // https://towardsdatascience.com/simple-neural-network-implementation-in-c-663f51447547
    // https://stackoverflow.com/questions/9951487/implementing-a-neural-network-in-java-training-and-backpropagation-issues
    public void backward(int[] expectedOutput, double learningRateDouble) {
        int learningRate = (int)Math.round(learningRateDouble * MAX_NEURON);
        final int outputLayerPos = layers.length - 1;
        final int[] outputNeuronLayer = neurons[outputLayerPos];
        int[] outputErrorArr = new int[layers[outputLayerPos]];
        for (int outputNeuronPos = 0; outputNeuronPos < outputErrorArr.length; outputNeuronPos++) {
            outputErrorArr[outputNeuronPos] = expectedOutput[outputNeuronPos] - outputNeuronLayer[outputNeuronPos];
        }
        for (int layerPos = layers.length - 2; layerPos >= 0; layerPos--) {
            final int childLayerPos = layerPos + 1;

            int[] delta = new int[layers[childLayerPos]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                int derivative = sigmoidDerivative(neurons[childLayerPos][childLayerNeuronPos]);
                if (layerPos == layers.length - 2) {
                    delta[childLayerNeuronPos] = (outputErrorArr[childLayerNeuronPos] * derivative) / MAX_NEURON_INT;
                } else {
                    final int childChildLayerPos = layerPos + 2;
                    int sum = 0;
                    for (int childChildLayerNeuronPos = 0; childChildLayerNeuronPos < layers[childChildLayerPos]; childChildLayerNeuronPos++) {
                        sum +=( ((weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * outputErrorArr[childChildLayerNeuronPos]) / MAX_NEURON_INT) * derivative) / MAX_NEURON_INT;
                    }
                    //delta[childLayerNeuronPos] = (sum) / (MAX_NEURON_INT * layers[childChildLayerPos]);
                    delta[childLayerNeuronPos] = (sum);// * derivative) / MAX_NEURON_INT;
                }
            }

            final int[] layerNeuronArr = neurons[layerPos];
            final int[][] layerWeightArr = weights[layerPos];
            final int[] layerBiasArr = bias[layerPos];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                    int weightOff = (((layerNeuronArr[layerNeuronPos] * delta[childLayerNeuronPos]) / MAX_NEURON_INT) * learningRate) / MAX_NEURON_INT;
                    layerWeightArr[layerNeuronPos][childLayerNeuronPos] += weightOff;
                }
                int biasOff = (delta[childLayerNeuronPos] * learningRate) / MAX_NEURON_INT;
                layerBiasArr[childLayerNeuronPos] += biasOff;
            }
        }
    }

    public void backward_new_error(int[] expectedOutput, double learningRateDouble) {
        int learningRate = (int)Math.round(learningRateDouble * MAX_NEURON);
        final int outputLayerPos = layers.length - 1;
        final int[] outputNeuronLayer = neurons[outputLayerPos];
        int[] outputErrorArr = new int[layers[outputLayerPos]];
//        int[] deltaOutput = new int[layers[outputLayerPos]];
        for (int outputNeuronPos = 0; outputNeuronPos < outputErrorArr.length; outputNeuronPos++) {
            outputErrorArr[outputNeuronPos] = expectedOutput[outputNeuronPos] - outputNeuronLayer[outputNeuronPos];
//            int derivative = sigmoidDerivative(outputNeuronLayer[outputNeuronPos]);
//            deltaOutput[outputNeuronPos] = (outputErrorArr[outputNeuronPos] * derivative) / MAX_NEURON_INT;
        }
        for (int layerPos = layers.length - 2; layerPos >= 0; layerPos--) {
            final int childLayerPos = layerPos + 1;

            int[] deltaOutput = new int[layers[childLayerPos]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                int errorVal = neurons[layerPos][childLayerNeuronPos] - neurons[childLayerPos][childLayerNeuronPos];
                int derivative = sigmoidDerivative(neurons[childLayerPos][childLayerNeuronPos]);
                deltaOutput[childLayerNeuronPos] = (errorVal * derivative) / MAX_NEURON_INT;
            }

            int[] delta = new int[layers[childLayerPos]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                int derivative = sigmoidDerivative(neurons[childLayerPos][childLayerNeuronPos]);
                if (layerPos == layers.length - 2) {
                    //delta[childLayerNeuronPos] = deltaOutput[childLayerNeuronPos];

                    delta[childLayerNeuronPos] = (outputErrorArr[childLayerNeuronPos] * derivative) / MAX_NEURON_INT;
                } else {
                    final int childChildLayerPos = layerPos + 2;
                    int sum = 0;
                    for (int childChildLayerNeuronPos = 0; childChildLayerNeuronPos < layers[childChildLayerPos]; childChildLayerNeuronPos++) {
                        sum += (weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * deltaOutput[childChildLayerNeuronPos]);
                        //sum += (weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * deltaOutput[childChildLayerNeuronPos]) / MAX_NEURON_INT;

                        sum +=( ((weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * outputErrorArr[childChildLayerNeuronPos]) / MAX_NEURON_INT) * derivative) / MAX_NEURON_INT;
                    }
                    //delta[childLayerNeuronPos] = (sum * derivative) / (MAX_NEURON_INT * layers[childChildLayerPos]);
                    delta[childLayerNeuronPos] = (sum);// * derivative) / MAX_NEURON_INT;
                }
            }

            final int[] layerNeuronArr = neurons[layerPos];
            final int[][] layerWeightArr = weights[layerPos];
            final int[] layerBiasArr = bias[layerPos];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                    int weightOff = (((layerNeuronArr[layerNeuronPos] * delta[childLayerNeuronPos]) / MAX_NEURON_INT) * learningRate) / MAX_NEURON_INT;
                    layerWeightArr[layerNeuronPos][childLayerNeuronPos] += weightOff;
                }
                int biasOff = (delta[childLayerNeuronPos] * learningRate) / MAX_NEURON_INT;
                layerBiasArr[childLayerNeuronPos] += biasOff;
            }
        }
    }

    public int[] predict(int[] input) {
        int[] output = forward(input);
        return output;
    }

    /**
     * Die Belohnungsfunktion, auch Fitnessfunktion oder Kostenfunktion genannt, dient dazu, die Leistung des neuronalen Netzes zu bewerten und während des Lernprozesses zu optimieren.
     *
     * Im Fall eines überwachten Lernens, bei dem ein Eingang mit einem erwarteten Ausgang verglichen wird, kann die Belohnungsfunktion die Summe der quadrierten Differenzen (Mean Squared Error) zwischen dem erwarteten und tatsächlichen Ausgang des Netzes sein.
     *
     * Diese Methode berechnet den mittleren quadratischen Fehler (MSE) des Netzes für einen gegebenen Eingang und den erwarteten Ausgang. Der Rückgabewert ist ein Maß dafür, wie gut das Netz den gegebenen Eingang verarbeitet hat und wie weit es vom erwarteten Ausgang entfernt ist.
     *
     * Während des Lernprozesses wird die Belohnungsfunktion regelmäßig aufgerufen, um den Fortschritt des Netzes zu messen und die Gewichte entsprechend anzupassen, um den Fehler zu minimieren.
     */
    public int calculateMSE(int[] input, int[] output, int[] expectedOutput) {
        int error = 0;
        for (int i = 0; i < output.length; i++) {
            int outputValue = output[i];
            //error += Math.pow(expectedOutput[i] - outputValue, 2);
            int diff = expectedOutput[i] - outputValue;
            error += (diff * diff);// / MAX_NEURON_INT;
        }
        return error / output.length;
    }
}
