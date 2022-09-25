package com.predictor;

// for -1,1 class labels for SVM's
public class Hinge extends LossFn {

    // Constructor for a hinge loss function.
    public Hinge() {
        setFnName("hinge");
    }

    // Applies the hinge loss function on an outcome vector
    // and a prediction vector and returns the result vector.
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

            resultVector.set(i, hinge(outcomeElem, predElem));
        }

        return resultVector;
    }

    // Returns the output of the derivative of the hinge loss function
    // with respect to the prediction given an outcome and a prediction.
    protected double derivative(double outcome, double prediction) {
        if (Math.max(0, 1 - outcome * prediction) == 0) {
            return 0;
        }
        return -outcome;
    }

    // Returns the output of the hinge loss
    // function given an outcome and a prediction.
    protected static double hinge(double outcome, double prediction) {
        return Math.max(0, 1 - outcome * prediction);
    }
}
