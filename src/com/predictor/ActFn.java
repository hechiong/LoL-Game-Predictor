package com.predictor;

import java.util.function.Consumer;

public class ActFn extends Fn {

    public static final String[] VALID_ACT_FNS = {"identity", "leaky relu",
                                                  "relu", "sigmoid", "tanh"};

    private final Consumer<Vec> fn;

    // Constructor for an activation function.
    public ActFn(String actFn) throws FnException {
        if (!isValidActFn(actFn))  {
            throw new FnException(actFn + " isn't a valid activation "
                    + "function.");
        }

        fnName = actFn;
        switch (actFn) {
            case "leaky relu":
                fn = this::leakyRelu;
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
                fn = this::identity;
        }
    }

    // Applies this activation function on the vector.
    public void accept(Vec v) {
        fn.accept(v);
    }

    // Returns whether the activation function is defined in this class.
    public static boolean contains(String actFn) {
        return actFn.equals("identity") || actFn.equals("leaky relu")
                || actFn.equals("relu") || actFn.equals("sigmoid")
                || actFn.equals("tanh");
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
            v.set(i, relu(0.01, v.get(i)));
        }
    }

    // Applies the ReLU function element-wise on the vector.
    private void relu(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, relu(0, v.get(i)));
        }
    }

    // Applies the sigmoid function element-wise on the vector.
    private void sigmoid(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, sigmoid(v.get(i)));
        }
    }

    // Applies the hyperbolic tangent function element-wise on the vector.
    private void tanh(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, tanh(v.get(i)));
        }
    }

    // Returns the String representation of this activation function.
    public String toString() {
        return "Activation function: " + fnName;
    }
}
