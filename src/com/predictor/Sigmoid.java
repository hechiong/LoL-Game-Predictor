package com.predictor;

public class Sigmoid extends ActFn {

    // Constructor for a sigmoid activation function.
    public Sigmoid() {
        setFnName("sigmoid");
    }

    // Applies the sigmoid function element-wise on the vector.
    protected void accept(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, sigmoid(v.get(i)));
        }
    }
    // Returns the output of the derivative of the sigmoid
    // function given and with respect to an input.
    protected double derivative(double x) {
        return sigmoid(x) * (1 - sigmoid(x));
    }


    // Returns the output of the sigmoid function given an input.
    protected static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}

