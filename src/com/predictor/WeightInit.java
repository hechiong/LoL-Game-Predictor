package com.predictor;

import java.util.function.Consumer;
import java.util.Random;

public class WeightInit {

    private static final String[] VALID_WEIGHT_INITS = {"Glorot Normal",
            "Glorot Uniform", "He Normal", "He Uniform", "Uniform",
            "Xavier Normal", "Xavier Uniform"};

    private final Consumer<ParameterNode> init;
    private final Random rand = new Random();
    private final String initName;

    // Constructor for a weight initialization.
    public WeightInit(String weightInit) throws FnException {
        initName = weightInit;

        switch (initName) {
            case "Glorot Normal1":
            case "Xavier Normal1":
                init = this::glorotNormal1;
                break;
            case "Glorot Normal2":
            case "Xavier Normal2":
                init = this::glorotNormal2;
                break;
            case "Glorot Uniform":
            case "Xavier Uniform":
                init = this::glorotUniform;
                break;
            case "He Normal":
                init = this::heNormal;
                break;
            case "He Uniform":
                init = this::heUniform;
                break;
            case "Uniform":
                init = this::uniform;
                break;
            default:
                throw new FnException(weightInit + " isn't a valid weight "
                        + "initialization.");
        }
    }

    // Applies this weight initialization to the parameter.
    public void accept(ParameterNode p) {
        init.accept(p);
    }

    // Returns whether the function is a valid weight initialization.
    public static boolean isValidWeightInit(String init) {
        for (String validWeightInit : VALID_WEIGHT_INITS) {
            if (init.equals(validWeightInit)) {
                return true;
            }
        }
        return false;
    }

    //  (for Sigmoid/Tanh)
    // Applies the Glorot normal initialization with a standard
    // deviation of 'sqrt(1 / fanIn)' to the parameter.
    private void glorotNormal1(ParameterNode p) {
        normalWithMeanAndStdDev(p, 0, Math.sqrt(1.0 / p.numRows()));
    }

    //  (for Sigmoid/Tanh)
    // Applies the Glorot normal initialization with a standard
    // deviation of 'sqrt(2 / (fanIn + fanOut))' to the parameter.
    private void glorotNormal2(ParameterNode p) {
        normalWithMeanAndStdDev(p, 0, Math.sqrt(2.0 / (p.numRows() + p.numCols())));
    }

    //  (for Sigmoid/Tanh)
    // Applies the Glorot uniform initialization to the parameter.
    private void glorotUniform(ParameterNode p) {
        uniformWithBounds(p, 12 / Math.sqrt(p.numRows() + p.numCols()));
    }

    //  (for ReLU)
    // Applies the He normal initialization to the parameter.
    private void heNormal(ParameterNode p) {
        normalWithMeanAndStdDev(p, 0, Math.sqrt(2.0 / p.numRows()));
    }

    //  (for ReLU)
    // Applies the He uniform initialization to the parameter.
    private void heUniform(ParameterNode p) {
        uniformWithBounds(p, 12 / Math.sqrt(p.numRows()));
    }

    // Applies an initialization involving a normal distribution
    // with some mean and standard deviation to the parameter.
    private void normalWithMeanAndStdDev(ParameterNode p, double mean,
                                         double stdDev) {
        double val;

        for (int i = 0; i < p.numRows(); i++) {
            for (int j = 0; j < p.numCols(); j++) {
                val = rand.nextGaussian() * stdDev + mean;
                p.set(i, j, val);
            }
        }
    }

    //  (for Sigmoid)
    // Applies the uniform initialization to the parameter.
    private void uniform(ParameterNode p) {
        uniformWithBounds(p, 2 / Math.sqrt(p.numRows()));
    }

    // Applies an initialization involving a uniform
    // distribution with opposite bounds to the parameter.
    private void uniformWithBounds(ParameterNode p, double range) {
        double val;

        for (int i = 0; i < p.numRows(); i++) {
            for (int j = 0; j < p.numCols(); j++) {
                val = rand.nextDouble() * range - range / 2;
                p.set(i, j, val);
            }
        }
    }

    // Returns the String representation of this weight initialization.
    public String toString() {
        return initName;
    }
}
