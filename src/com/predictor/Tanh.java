package com.predictor;

public class Tanh extends ActFn {

    // Constructor for a hyperbolic tangent activation function.
    public Tanh() {
        setFnName("tanh");
    }

    // Applies the hyperbolic tangent function element-wise on the vector.
    protected void accept(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, tanh(v.get(i)));
        }
    }

    // Returns the output of the derivative of the hyperbolic
    // tangent function given and with respect to an input.
    protected double derivative(double x) {
        return 1 - Math.pow(tanh(x), 2);
    }

    // Returns the output of the hyperbolic tangent function given an input.
    protected static double tanh(double x) {
        return (2 / (1 + Math.exp(-x * 2))) - 1;
    }
}

