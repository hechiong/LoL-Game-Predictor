package com.predictor;

// for 0,1 class labels
public class CrossEntropy extends LossFn {

    // Constructor for a cross-entropy loss function.
    public CrossEntropy() {
        setFnName("cross-entropy");
    }

    // Applies the cross-entropy loss function on an outcome vector
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

            resultVector.set(i, crossEntropy(outcomeElem, predElem));
        }

        return resultVector;
    }

    // Returns the output of the derivative of the cross-entropy loss function
    // with respect to the prediction given an outcome and a prediction.
    protected double derivative(double outcome, double prediction) {
        double sigPred = Sigmoid.sigmoid(prediction);
        Sigmoid sigmoidFn = new Sigmoid();

        return (-outcome/sigPred + (1-outcome)/(1-sigPred))
                * sigmoidFn.derivative(prediction);
    }

    // Returns the output of the cross-entropy loss
    // function given an outcome and a prediction.
    protected static double crossEntropy(double outcome, double prediction) {
        double sigPred = Sigmoid.sigmoid(prediction);

        return -outcome * Math.log(sigPred)
                - (1-outcome) * Math.log(1-sigPred);
    }
}
