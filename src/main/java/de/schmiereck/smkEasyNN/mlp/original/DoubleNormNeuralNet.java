package de.schmiereck.smkEasyNN.mlp.original;

/**
 * Norm: neurons use values between 0.0 to 1.0
 */
public class DoubleNormNeuralNet {
    private int[] layers;
    private double[][] neurons;
    private double[][][] weights;
    private double[][] bias;
    private double[][] errorToPropagate;

    public static double MAX_NEURON = 1.0D;

    public DoubleNormNeuralNet(int... layers) {
        this.layers = layers;
        this.neurons = new double[layers.length][];
        this.weights = new double[layers.length - 1][][];
        this.bias = new double[layers.length][];
        this.errorToPropagate = new double[layers.length][];
        for (int layerPos = 0; layerPos < layers.length; layerPos++) {
            this.neurons[layerPos] = new double[layers[layerPos]];
            this.errorToPropagate[layerPos] = new double[layers[layerPos]];
            if (layerPos < layers.length - 1) {
                this.weights[layerPos] = new double[layers[layerPos]][layers[layerPos + 1]];
                this.bias[layerPos] = new double[layers[layerPos + 1]];
            }
        }
        initWeightsAndBias();
    }

    private void initWeightsAndBias() {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = Math.random() - 0.5;
                }
                bias[i][j] = Math.random() - (0.5D * MAX_NEURON);
            }
        }
    }

    public static double sigmoid(double x) {
        return 1.0D / (1.0D + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        return x * (1.0D - x);
    }

    public double[] forward(double[] input) {
        for (int i = 0; i < input.length; i++) {
            neurons[0][i] = input[i];
        }
        for (int i = 1; i < layers.length; i++) {
            for (int j = 0; j < layers[i]; j++) {
                double sum = 0;
                for (int k = 0; k < layers[i - 1]; k++) {
                    sum += neurons[i - 1][k] * weights[i - 1][k][j];
                }
                sum += bias[i - 1][j];
                neurons[i][j] = sigmoid(sum);
            }
        }
        return neurons[layers.length - 1];
    }

    //public void backward_orig(double[] expectedOutput, double learningRate) {
    public void backward(double[] expectedOutput, double learningRate) {
        final int outputLayerPos = layers.length - 1;
        final double[] outputNeuronLayer = neurons[outputLayerPos];
        double[] outputErrorArr = new double[layers[outputLayerPos]];
        for (int outputNeuronPos = 0; outputNeuronPos < outputErrorArr.length; outputNeuronPos++) {
            outputErrorArr[outputNeuronPos] = expectedOutput[outputNeuronPos] - outputNeuronLayer[outputNeuronPos];
        }
        for (int layerPos = layers.length - 2; layerPos >= 0; layerPos--) {
            final int childLayerPos = layerPos + 1;

            double[] delta = new double[layers[layerPos + 1]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                double derivative = sigmoidDerivative(neurons[childLayerPos][childLayerNeuronPos]);
                if (layerPos == layers.length - 2) {
                    delta[childLayerNeuronPos] = outputErrorArr[childLayerNeuronPos] * derivative;
                } else {
                    final int childChildLayerPos = layerPos + 2;
                    double sum = 0;
                    for (int childChildLayerNeuronPos = 0; childChildLayerNeuronPos < layers[childChildLayerPos]; childChildLayerNeuronPos++) {
                        sum += weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * outputErrorArr[childChildLayerNeuronPos] * derivative;
                    }
                    delta[childLayerNeuronPos] = sum;// * derivative;
                }
            }

            final double[] layerNeuronArr = neurons[layerPos];
            final double[][] layerWeightArr = weights[layerPos];
            final double[] layerBiasArr = bias[layerPos];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                    layerWeightArr[layerNeuronPos][childLayerNeuronPos] += layerNeuronArr[layerNeuronPos] * delta[childLayerNeuronPos] * learningRate;
                }
                layerBiasArr[childLayerNeuronPos] += delta[childLayerNeuronPos] * learningRate;
            }
        }
    }

    // https://towardsdatascience.com/simple-neural-network-implementation-in-c-663f51447547
    // https://stackoverflow.com/questions/27280750/trouble-understanding-the-backpropagation-algorithm-in-neural-network
    public void backward_new_error(double[] expectedOutput, double learningRate) {
        final int outputLayerPos = layers.length - 1;

        for (int outputNeuronPos = 0; outputNeuronPos < layers[outputLayerPos]; outputNeuronPos++) {
            final double[] outputNeuronLayer = neurons[outputLayerPos];
            errorToPropagate[outputLayerPos][outputNeuronPos] = expectedOutput[outputNeuronPos] - outputNeuronLayer[outputNeuronPos];
        }

        for (int layerPos = layers.length - 1; layerPos >= 0; layerPos--) {
            final int childLayerPos = layerPos + 1;

        }

        for (int layerPos = layers.length - 2; layerPos >= 0; layerPos--) {
            final int childLayerPos = layerPos + 1;

            double[] deltaOutput2 = new double[layers[childLayerPos]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                double error2 = neurons[layerPos][childLayerNeuronPos] - neurons[childLayerPos][childLayerNeuronPos];
                double derivative = sigmoidDerivative(neurons[childLayerPos][childLayerNeuronPos]);
                deltaOutput2[childLayerNeuronPos] = (error2 * derivative);
            }

            for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                double derivative = sigmoidDerivative(neurons[layerPos][layerNeuronPos]);
                double errorToPropagateVal = derivative;

                if (layerPos == layers.length - 2) {
                    errorToPropagateVal *= (expectedOutput[layerNeuronPos] - neurons[layerPos][layerNeuronPos]);
                } else {
                    double sumFromLastLayer = 0;

                    //for (Neuron lastLayerNeuron : lastLayer.getNeurons()) {
                    for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                        final int childChildLayerPos = layerPos + 2;
                        //for (Synapse synapse : lastLayerNeuron.getSynapses()) {
                        for (int childChildLayerNeuronPos = 0; childChildLayerNeuronPos < layers[childChildLayerPos]; childChildLayerNeuronPos++) {
                            //if (synapse.getSourceNeuron() == neuron) {
                            //sumFromLastLayer += (synapse.getWeight() * lastLayerNeuron.getErrorToPropagate());
                            sumFromLastLayer += weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * errorToPropagate[childLayerPos][childChildLayerNeuronPos];
                            //    break;
                            //}
                        }
                    }
                    errorToPropagateVal *= sumFromLastLayer;
                }
                errorToPropagate[layerPos][layerNeuronPos] = errorToPropagateVal;
            }

            double[] delta = new double[layers[childLayerPos]];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                double derivative = sigmoidDerivative(neurons[childLayerPos][childLayerNeuronPos]);
                if (layerPos == layers.length - 2) {
                    //delta[childLayerNeuronPos] = deltaOutput2[childLayerNeuronPos];
                    //delta[childLayerNeuronPos] = error[childLayerNeuronPos] * derivative;
                    delta[childLayerNeuronPos] = errorToPropagate[childLayerPos][childLayerNeuronPos] * derivative;
                } else {
                    final int childChildLayerPos = layerPos + 2;
                    double sum = 0;
                    for (int childChildLayerNeuronPos = 0; childChildLayerNeuronPos < layers[childChildLayerPos]; childChildLayerNeuronPos++) {
                        //sum += weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * deltaOutput2[childChildLayerNeuronPos];
                        sum += weights[childLayerPos][childLayerNeuronPos][childChildLayerNeuronPos] * errorToPropagate[childLayerPos][childChildLayerNeuronPos] * derivative;
                    }
                    delta[childLayerNeuronPos] = sum;// * derivative;
                }
            }
            final double[] layerNeuronArr = neurons[layerPos];
            final double[][] layerWeightArr = weights[layerPos];
            final double[] layerBiasArr = bias[layerPos];
            for (int childLayerNeuronPos = 0; childLayerNeuronPos < layers[childLayerPos]; childLayerNeuronPos++) {
                for (int layerNeuronPos = 0; layerNeuronPos < layers[layerPos]; layerNeuronPos++) {
                    layerWeightArr[layerNeuronPos][childLayerNeuronPos] += layerNeuronArr[layerNeuronPos] * delta[childLayerNeuronPos] * learningRate;
                }
                layerBiasArr[childLayerNeuronPos] += delta[childLayerNeuronPos] * learningRate;
            }
        }
    }
    public double[] predict(double[] input) {
        double[] output = forward(input);
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
    public double calculateMSE(double[] input, double[] outputLayer, double[] expectedOutput) {
        double error = 0;
        for(int i = 0; i < outputLayer.length; i++) {
            double output = outputLayer[i];
            error += Math.pow(expectedOutput[i] - output, 2);
        }
        return error / outputLayer.length;
    }

}
