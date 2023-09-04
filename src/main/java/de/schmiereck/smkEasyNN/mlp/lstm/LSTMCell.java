package de.schmiereck.smkEasyNN.mlp.lstm;

import java.util.Arrays;

public class LSTMCell {
    private double[] inputGateWeights;
    private double[] forgetGateWeights;
    private double[] outputGateWeights;
    private double[] cellStateWeights;

    private double[] inputGate;
    private double[] forgetGate;
    private double[] outputGate;
    private double[] cellState;

    private double[] candidateCellState;

    public LSTMCell(int inputSize, int hiddenSize) {
        // Initialisiere die Gewichtungen und Gatter mit zufälligen Werten
        inputGateWeights = new double[inputSize + hiddenSize];
        forgetGateWeights = new double[inputSize + hiddenSize];
        outputGateWeights = new double[inputSize + hiddenSize];
        cellStateWeights = new double[inputSize + hiddenSize];

        inputGate = new double[hiddenSize];
        forgetGate = new double[hiddenSize];
        outputGate = new double[hiddenSize];
        cellState = new double[hiddenSize];

        initializeWeights();
    }

    private void initializeWeights() {
        // Hier kannst du die Gewichtungen initialisieren, z.B. mit zufälligen Werten.
        Arrays.fill(inputGateWeights, 0.5);
        Arrays.fill(forgetGateWeights, 0.5);
        Arrays.fill(outputGateWeights, 0.5);
        Arrays.fill(cellStateWeights, 0.5);
    }

    public void forward(double[] input, double[] prevState) {
        // Berechne die Eingänge für die LSTM-Zelle (z.B. elementweise Multiplikation mit Gewichten)
        double[] totalInput = new double[input.length + prevState.length];
        System.arraycopy(input, 0, totalInput, 0, input.length);
        System.arraycopy(prevState, 0, totalInput, input.length, prevState.length);

        // Berechne die Aktivierungen der Gatter (z.B. Sigmoid-Funktion)
        for (int i = 0; i < inputGate.length; i++) {
            inputGate[i] = sigmoid(dotProduct(totalInput, inputGateWeights));
            forgetGate[i] = sigmoid(dotProduct(totalInput, forgetGateWeights));
            outputGate[i] = sigmoid(dotProduct(totalInput, outputGateWeights));
        }

        // Berechne den neuen Zellzustand (z.B. Elementweise Operationen)
        candidateCellState = new double[inputGate.length];
        for (int i = 0; i < candidateCellState.length; i++) {
            candidateCellState[i] = Math.tanh(dotProduct(totalInput, cellStateWeights));
        }

        for (int i = 0; i < cellState.length; i++) {
            cellState[i] = forgetGate[i] * prevState[i] + inputGate[i] * candidateCellState[i];
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double dotProduct(double[] a, double[] b) {
        double result = 0.0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    // Trainingsfunktion für eine Zeitschritt
    public void trainStep(double[] input, double[] prevState, double[] targetOutput, double learningRate) {
        // Führe einen Vorwärtsdurchlauf (Forward Pass) durch
        forward(input, prevState);

        // Berechne den Fehler zwischen der berechneten Ausgabe und dem Zielwert
        double[] error = new double[outputGate.length];
        for (int i = 0; i < outputGate.length; i++) {
            error[i] = targetOutput[i] - outputGate[i];
        }

        // Berechne die Gradienten für die Gewichtungen und Zellzustand
        double[] outputGateGradient = new double[outputGate.length];
        double[] cellStateGradient = new double[cellState.length];

        for (int i = 0; i < outputGate.length; i++) {
            outputGateGradient[i] = error[i] * sigmoidDerivative(outputGate[i]);
            cellStateGradient[i] = outputGateGradient[i] * Math.tanh(cellState[i]);
        }

        // Aktualisiere die Gewichtungen mithilfe des Gradienten und der Lernrate
        for (int i = 0; i < outputGateWeights.length; i++) {
            outputGateWeights[i] += learningRate * outputGateGradient[i] * prevState[i];
        }

        for (int i = 0; i < cellStateWeights.length; i++) {
            cellStateWeights[i] += learningRate * cellStateGradient[i] * prevState[i];
        }

        // Berechne den Gradienten für die Eingabe
        double[] inputGateGradient = new double[inputGate.length];
        double[] forgetGateGradient = new double[forgetGate.length];
        double[] candidateCellStateGradient = new double[candidateCellState.length];

        for (int i = 0; i < inputGate.length; i++) {
            inputGateGradient[i] = cellStateGradient[i] * candidateCellState[i] * sigmoidDerivative(inputGate[i]);
            forgetGateGradient[i] = cellStateGradient[i] * prevState[i] * sigmoidDerivative(forgetGate[i]);
            candidateCellStateGradient[i] = cellStateGradient[i] * inputGate[i] * tanhDerivative(candidateCellState[i]);
        }

        // Aktualisiere die Gewichtungen für die Gatter mithilfe des Gradienten und der Lernrate
        for (int i = 0; i < inputGateWeights.length; i++) {
            inputGateWeights[i] += learningRate * inputGateGradient[i] * prevState[i];
            forgetGateWeights[i] += learningRate * forgetGateGradient[i] * prevState[i];
        }
    }

    // ... (Vorherige Hilfsfunktionen hier)

    // Ableitungen der Aktivierungsfunktionen
    private double sigmoidDerivative(double x) {
        return x * (1.0 - x);
    }

    private double tanhDerivative(double x) {
        return 1.0 - x * x;
    }
}
