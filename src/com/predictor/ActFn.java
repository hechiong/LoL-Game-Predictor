package com.predictor;

import java.util.function.Consumer;

public class ActFn extends Fn {

    private static final String[] VALID_ACT_FNS = {"identity", "leaky relu",
                                                  "relu", "sigmoid", "tanh"};
    private static final double LEAKY_RELU_CONSTANT = 0.01;

    private final Consumer<Vec> fn;

    // Constructor for an activation function.
    public ActFn(String actFn) throws FnException {
        fnName = actFn;

        switch (actFn) {
            case "leaky relu":
                fn = this::leakyRelu;
                break;
            case "identity":
                fn = this::identity;
                break;
            case "relu":
                fn = this::relu;
                break;
            case "sigmoid":
                fn = this::sigmoid;
                break;
            case "tanh":
                fn = this::tanh;
                break;
            default:
                throw new FnException(actFn + " isn't a valid activation "
                        + "function.");
        }
    }

    // Applies this activation function on the vector.
    public void accept(Vec v) {
        fn.accept(v);
    }

    // Applies the identity function element-wise on the vector.
    private void identity(Vec v) {}

    // Returns whether the function is a valid activation function.
    public static boolean isValidActFn(String fn) {
        for (String validActFn : VALID_ACT_FNS) {
            if (fn.equals(validActFn)) {
                return true;
            }
        }
        return false;
    }

    // Applies the leaky ReLU function element-wise on the vector.
    private void leakyRelu(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, relu(LEAKY_RELU_CONSTANT, v.get(i)));
        }
    }

    // Applies the ReLU function element-wise on the vector.
    private void relu(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, relu(0, v.get(i)));
        }
    }

    // Returns the output of the ReLU function given an
    // input and a scalar in case the input is negative.
    public static double relu(double scalar, double x) {
        if (x < 0) {
            return scalar * x;
        }
        return x;
    }

    // Returns the output of the derivative
    // of the ReLU function given an input.
    public static double reluDerivative(String reluType, double x) {
        if (reluType.equals("leaky") && x <= 0) {
            return LEAKY_RELU_CONSTANT;
        } else if (reluType.equals("") && x <= 0) {
            return 0;
        }
        return 1;
    }

    // Returns the output vector of the gradient of
    // the ReLU function given an input vector.
    public static Vec reluGradient(String reluType, Vec v) {
        Vec resultVector = v.copy();

        for (int i = 0; i < v.length(); i++) {
            resultVector.set(i, reluDerivative(reluType, v.get(i)));
        }

        return resultVector;
    }

    // Applies the sigmoid function element-wise on the vector.
    private void sigmoid(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, sigmoid(v.get(i)));
        }
    }

    // Returns the output of the sigmoid function given an input.
    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    // Returns the output of the derivative
    // of the sigmoid function given an input.
    public static double sigmoidDerivative(double x) {
        return sigmoid(x) * (1 - sigmoid(x));
    }

    // Returns the output vector of the gradient of
    // the sigmoid function given an input vector.
    public static Vec sigmoidGradient(Vec v) {
        Vec resultVector = v.copy();

        for (int i = 0; i < v.length(); i++) {
            resultVector.set(i, sigmoidDerivative(v.get(i)));
        }

        return resultVector;
    }

    // Applies the hyperbolic tangent function element-wise on the vector.
    private void tanh(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, tanh(v.get(i)));
        }
    }

    // Returns the output of the hyperbolic tangent function given an input.
    public static double tanh(double x) {
        return (2 / (1 + Math.exp(-x * 2))) - 1;
    }

    // Returns the output of the derivative of the
    // hyperbolic tangent function given an input.
    public static double tanhDerivative(double x) {
        return 1 - Math.pow(tanh(x), 2);
    }

    // Returns the output vector of the gradient of the
    // hyperbolic tangent function given an input vector.
    public static Vec tanhGradient(Vec v) {
        Vec resultVector = v.copy();

        for (int i = 0; i < v.length(); i++) {
            resultVector.set(i, tanhDerivative(v.get(i)));
        }

        return resultVector;
    }

    // Returns the String representation of this activation function.
    public String toString() {
        return "Activation function: " + fnName;
    }
}
