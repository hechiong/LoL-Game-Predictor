package com.predictor;

public class LeakyReLU extends ReLU {

    private static final double LEAKY_RELU_CONSTANT = 0.01;

    // Constructor for a leaky ReLU activation function.
    public LeakyReLU() {
        setFnName("leaky relu");
    }

    // Applies the leaky ReLU function element-wise on the vector.
    protected void actFn(Vec v) {
        for (int i = 0; i < v.length(); i++) {
            v.set(i, relu(LEAKY_RELU_CONSTANT, v.get(i)));
        }
    }

    // Returns the output of the derivative of
    // the leaky ReLU function given an input.
    protected double derivative(double x) {
        if (x <= 0) {
            return LEAKY_RELU_CONSTANT;
        }
        return 1;
    }
}
