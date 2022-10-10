package com.predictor;

public class SquaredError extends LossFn {

    // Constructor for a squared error loss function.
    public SquaredError() {
        setFnName("squared error");
    }

    // Applies the squared error loss function on an outcome
    // vector and a prediction vector and returns the result vector.
    protected Vec apply(Vec outcome, Vec prediction) throws LossFnException {
        double outcomeElem;
        double predElem;
        Vec resultVector = new Vec(outcome.length());

        checkLengths(outcome, prediction);

        for (int i = 0; i < outcome.length(); i++) {
            outcomeElem = outcome.get(i);
            predElem = prediction.get(i);

            resultVector.set(i, squaredError(outcomeElem, predElem));
        }

        return resultVector;
    }

    // Returns the output of the derivative of the squared error loss function
    // with respect to the prediction given an outcome and a prediction.
    protected double derivative(double outcome, double prediction) {
        return -2 * (outcome - prediction);
    }

    // Returns the output of the squared error loss
    // function given an outcome and a prediction.
    protected static double squaredError(double outcome, double prediction) {
        return Math.pow(outcome - prediction, 2);
    }
}
