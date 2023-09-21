package de.schmiereck.smkEasyNN.mlp.original;

public class IntegerNormNeuralNet {
    private int[] layers;
    private int[][] neurons;
    private double[][][] weights;
    private double[][] bias;

    public static double MAX_NEURON = 255.0D;
    public static int MAX_NEURON_INT = (int)MAX_NEURON;

    public IntegerNormNeuralNet(int... layers) {
        this.layers = layers;
        this.neurons = new int[layers.length][];
        this.weights = new double[layers.length - 1][][];
        this.bias = new double[layers.length - 1][];
        for (int layerPos = 0; layerPos < layers.length; layerPos++) {
            final int childLayerPos = layerPos + 1;
            this.neurons[layerPos] = new int[layers[layerPos]];
            if (layerPos < layers.length - 1) {
                this.weights[layerPos] = new double[layers[layerPos]][layers[childLayerPos]];
                this.bias[layerPos] = new double[layers[childLayerPos]]; // ??? new double[layers[layerPos + 1]];
            }
        }
        initWeightsAndBias();
    }

    private void initWeightsAndBias() {
        for (int layerPos = 0; layerPos < layers.length - 1; layerPos++) {
            for (int neuronPos = 0; neuronPos < weights[layerPos].length; neuronPos++) {
                for (int k = 0; k < weights[layerPos][neuronPos].length; k++) {
                    weights[layerPos][neuronPos][k] = (Math.random() - 0.5D) * MAX_NEURON;
                }
            }
            for (int neuronPos = 0; neuronPos < bias[layerPos].length; neuronPos++) {
                bias[layerPos][neuronPos] = (Math.random() - 0.5D) * MAX_NEURON;
            }
        }
    }

    private double sigmoidDouble(double x) {
        return 1.0D / (1.0D + Math.exp(-x));
    }

    public static int sigmoid(int x) {
        //double y = 1.0D / (1.0D + Math.exp(-x / 128.0D));
        double y = 1.0D / (1.0D + Math.exp(-x /  (128.0D)));
        //return (int) (255.0D * y) + 1;
        return (int) (255.0D * y);
    }

    public static int sigmoidInteger(int x2) {
        /*
        //if (x < -128) {
        //    return 0;
        //} else if (x > 127) {
        //    return 255;
        //} else {
            //int y = x * 256;
            int y = x * 256 * 2*2*2*2;
            //y = ((y + 32768) >> 16) & 0xFFFF;
            y = ((y + 32768) >> 16) & 0xFFFF;
            y = (y * y) >> 8;
            y = (y * y) >> 8;
            y = (y * y) >> 8;
            return y;
        //}
        */
        final int ret;
        int x = x2 * 12000; // 16384 = (16*16*16*4);
        // x is the input signal, we return the output signal.
        if( x < -0x800000 ) {
            ret = 0x0;
        } else {
            if (x < 0x0) {
                // Scale x down to a max of 2^15 so it won't overflow when we square it.
                // Within this condition part, x has a max of 2^23, 23-15=8, so divide by
                // 2^8. Then translate the value up into the +ve.
                int tmp = (x >> 8) + 0x8000;
                // Square tmp to generate the curve. max result is 2^30. Expected max output
                // for this half of the curve is 2^11. 30-11=19, so...
                ret = ((tmp * tmp) >> 19);
            } else {
                if (x < 0x800000) {
                    // Same thing again except we flip the curve and translate it at the same time
                    // by subtracting the result from 2^12.
                    int tmp = (x >> 8) - 0x8000;
                    ret = 0x1000 - ((tmp * tmp) >> 19);
                } else {
                    ret = 0x1000;
                }
            }
        }
        return (ret / 16) + 1;
    }
    private double sigmoidDerivative(double x) {
        return x * (1.0D - x);
    }

    public static int sigmoidDerivative(int x) {
        return (x * (MAX_NEURON_INT - x)) / MAX_NEURON_INT;
    }

    public static int sigmoidDerivative2(int y) {
        // Convert the input back to the range [-255, 255]
        int x = y - 128;

        // Compute the derivative of the sigmoid function
        int d = (int) (256 * sigmoid(x) * (255 - sigmoid(x)));

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
                    sum += (neurons[parentLayerPos][parentLayerNeuronPos] * weights[parentLayerPos][parentLayerNeuronPos][layerNeuronPos]) / MAX_NEURON;
                }
                sum += bias[parentLayerPos][layerNeuronPos];
                neurons[layerPos][layerNeuronPos] = sigmoid(sum);
                //neurons[layerPos][layerNeuronPos] = sigmoidInteger(sum);
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
    public void backward(int[] expectedOutput, double learningRateDouble) {
        int learningRate = (int)Math.round(learningRateDouble * MAX_NEURON);
        final int outputLayerPos = layers.length - 1;
        double[] error = new double[layers[outputLayerPos]];
        for (int outputNeuronPos = 0; outputNeuronPos < error.length; outputNeuronPos++) {
            final int[] outputNeuronLayer = neurons[outputLayerPos];
            error[outputNeuronPos] = expectedOutput[outputNeuronPos] - outputNeuronLayer[outputNeuronPos];
        }
        for (int layerPos = layers.length - 2; layerPos >= 0; layerPos--) {
            final int childLayerPos = layerPos + 1;
            double[] delta = new double[layers[childLayerPos]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                double derivative = sigmoidDerivative(neurons[layerPos + 1][childLayerNeuronPos]);
                double sum = 0;
                if (layerPos == layers.length - 2) {
                    delta[childLayerNeuronPos] = (error[childLayerNeuronPos] * derivative) / MAX_NEURON;
                } else {
                    final int childChildLayerPos = layerPos + 2;
                    for (int childChildLayerNeuronPos = 0; childChildLayerNeuronPos < layers[childChildLayerPos]; childChildLayerNeuronPos++) {
                        sum += (weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * delta[childLayerNeuronPos]) / MAX_NEURON;
                    }
                    delta[childLayerNeuronPos] = (sum * derivative) / MAX_NEURON;
                }
                for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                    weights[layerPos][layerNeuronPos][childLayerNeuronPos] += (((neurons[layerPos][layerNeuronPos] * delta[childLayerNeuronPos]) / MAX_NEURON) * learningRate) / MAX_NEURON;
                }
                //bias[layerPos][childLayerNeuronPos] += (delta[childLayerNeuronPos] * learningRate) / MAX_NEURON;
                bias[layerPos][childLayerNeuronPos] += (delta[childLayerNeuronPos] * learningRate);
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
        for(int i = 0; i < output.length; i++) {
            int outputValue = output[i];
            //error += Math.pow(expectedOutput[i] - outputValue, 2);
            int diff = expectedOutput[i] - outputValue;
            error += (diff * diff);
        }
        return error / output.length;
    }
}
