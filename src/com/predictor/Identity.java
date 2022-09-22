package com.predictor;

public class Identity extends ActFn {

    // Constructor for an identity activation function.
    public Identity() {
        setFnName("identity");
    }

    /**
     * {@inheritDoc}
     * Applies the identity function element-wise on the vector.
     */
    @Override
    protected void accept(Vec v) {}

    // Returns the output of the derivative of the identity
    // function given and with respect to an input.
    protected double derivative(double x) {
        return 1;
    }
}

