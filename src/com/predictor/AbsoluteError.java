package com.predictor;

public class AbsoluteError extends LossFn {

    // Constructor for an absolute error loss function.
    public AbsoluteError() {
        setFnName("absolute error");
    }

    // Applies the absolute error loss function on an outcome
    // vector and a prediction vector and returns the result vector.
    protected Vec apply(Vec outcome, Vec prediction) throws LossFnException {
        double outcomeElem;
        double predElem;
        Vec resultVector = new Vec(outcome.length());

        if (!haveEqualLengths(outcome, prediction)) {
            throw new LossFnException("The outcome and prediction vectors must"
                    + " have the same lengths to use a loss function.");
        }

        for (int i = 0; i < outcome.length(); i++) {
            outcomeElem = outcome.get(i);
            predElem = prediction.get(i);

            resultVector.set(i, absoluteError(outcomeElem, predElem));
        }

        return resultVector;
    }

    // Returns the output of the derivative of the absolute error loss function
    // with respect to the prediction given an outcome and a prediction.
    protected double derivative(double outcome, double prediction) {
        if (outcome - prediction <= 0) {
            return -1;
        }
        return 1;
    }

    // Returns the output of the absolute error loss
    // function given an outcome and a prediction.
    protected static double absoluteError(double outcome, double prediction) {
        return Math.abs(outcome - prediction);
    }
}
