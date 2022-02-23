package com.predictor;

import java.util.ArrayList;

public class Weight extends Vec {

    public enum Model { BP, LR, NN }

    protected int batchSize = 0;
    protected double learningRate = 0;
    protected LossFn lossFn = null;
    protected Model model = null;
    protected String name = "";
    private Vec weightChanges;

    // Parameters only for neural network model
    protected ActFn actFn = null;
    protected ArrayList<Vec> w1 = new ArrayList<>();
    protected Vec b1;
    protected Vec w2;
    protected double b2;
    protected int h = 0;

    // Default constructor for a weight which will have one element of value 0.
    public Weight() {
        super(1);
    }

    // Applies this weight's activation function on the vector.
    public void actFn(Vec v) {
        if (model == Model.NN && actFn != null) {
            actFn.accept(v);
        }
    }

    // Returns the String representation displaying
    // this weight's activation function.
    public String actFnStr() {
        if (actFn != null) {
            return actFn.toString();
        }
        return "Activation function: null";
    }

    // Adds changes for a binary perceptron
    // given a game sample and its outcome.
    private void addBPChanges(Vec gameSample, double outcome) {
        double prediction = dot(gameSample);
        double multiplier = learningRate * outcome;

        if ((outcome < 0 || prediction < 0)
                && (outcome > 0 || prediction > 0)) {
            gameSample.scale(multiplier);
            weightChanges.add(gameSample);
        }
    }

    // Adds changes for logistic regression
    // given a game sample and its outcome.
    private void addLRChanges(Vec gameSample, double outcome) {
        double prediction = dot(gameSample);
        double multiplier = learningRate * (outcome - Fn.sigmoid(prediction));

        gameSample.scale(multiplier);
        weightChanges.add(gameSample);
    }

    // Adds changes for a neural network
    // given a game sample and its outcome.
    private void addNNChanges(Vec gameSample, double outcome) {
        return;
    }

    // Returns the size of the batches with which this weight works.
    public int getBatchSize() {
        return batchSize;
    }

    // Returns the size of this weight's hidden layer.
    public int getHiddenLayerSize() {
        return h;
    }

    // Returns the learning rate of this weight.
    public double getLearningRate() {
        return learningRate;
    }

    // Returns the model associated with this weight.
    public Model getModel() {
        return model;
    }

    // Returns the name of this weight.
    public String getName() {
        return name;
    }

    // Initializes a two-layer neural network if it's uninitialized.
    public void initNeuralNetwork() {
        if (model == Model.NN && h > 0) {
            for (int i = 0; i < h; i++) {
                w1.add(new Vec(length()));
            }
            b1 = new Vec(h);
            w2 = new Vec(h + 1);
            b2 = 1;
        }
    }

    // Returns the loss given a prediction and an
    // outcome using this weight's loss function.
    public double lossFn(double prediction, double outcome) {
        return lossFn.apply(prediction, outcome);
    }

    // Returns the String representation
    // displaying this weight's loss function.
    public String lossFnStr() {
        if (lossFn != null) {
            return lossFn.toString();
        }
        return "Loss function: null";
    }

    // Returns the String representation displaying
    // the model associated with this weight.
    private String modelString() {
        String modelStr = "Model: ";

        if (model == null) {
            modelStr += "null";
        } else {
            switch (model) {
                case BP:
                    modelStr += "binary perceptron";
                    break;
                case LR:
                    modelStr += "logistic regression";
                    break;
                case NN:
                    modelStr += "(two-layer) neural network";
            }
        }

        return modelStr;
    }

    // Computes and returns a prediction on the
    // vector sample depending on the model used.
    public double predict(Vec sample) {
        switch (model) {
            case BP:
                return dot(sample);
            case LR:
                return Fn.sigmoid(dot(sample));
            case NN:
                return predictWithNeuralNetwork(sample);
            default:
                return 0;
        }
    }

    // Computes and returns a prediction on the
    // vector sample using the neural network model.
    public double predictWithNeuralNetwork(Vec x1) {
        Vec x2;
        double prediction = 0;

        if (getModel() == Model.NN) {
            // first layer
            x2 = new Vec(h);
            for (int i = 0; i < w1.size(); i++) {
                x2.set(i, x1.dot(w1.get(i)));
            }
            x2.add(b1);

            // apply activation function and then append bias
            actFn(x2);
            //x2.expand(1);
            x2.set(h, 1);

            // second layer
            prediction = Fn.sigmoid(x2.dot(w2) + b2);
        }

        return prediction;
    }

    // Sets this weight's activation function if it's uninitialized.
    public void setActFn(ActFn actFn) {
        if (this.actFn == null && actFn != null) {
            this.actFn = actFn;
        }
    }

    // Sets the size of the batches with which this weight works.
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    // Sets the size of this weight's hidden layer if it's uninitialized.
    public void setHiddenLayerSize(int size) {
        if (h <= 0 && size > 0) {
            h = size;
        }
    }

    // Sets the learning rate of this weight.
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    // Sets this weight's loss function if it's uninitialized.
    public void setLossFn(LossFn lossFn) {
        if (this.lossFn == null && lossFn != null) {
            this.lossFn = lossFn;
        }
    }

    // Sets this weight's model if it's uninitialized.
    public void setModel(Model model) {
        if (this.model == null && model != null) {
            this.model = model;
        }
    }

    // Sets the name of this weight.
    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    // Returns the String representation of this weight.
    public String toString() {
        String lengthStr = "Number of features: " + length() + "\n";
        String batchSizeStr = "Batch size: " + batchSize + "\n";
        String learningRateStr = "Learning rate: " + learningRate + "\n";
        String lossFnStr = lossFnStr() + "\n";
        String modelStr = modelString() + "\n";
        String actFnStr = actFnStr() + "\n";
        String hiddenLayerSizeStr = "Hidden layer size: " + h;
        String s;

        if (name.isBlank()) {
            s = "Weight doesn't have name yet.\n";
        } else {
            s = "Name: " + name + "\n";
        }

        s += lengthStr + batchSizeStr + learningRateStr + lossFnStr + modelStr;
        if (model == Model.NN) {
            return s + actFnStr + hiddenLayerSizeStr;
        } else {
            return s + super.toString();
        }
    }

    // Trains this weight's neural network
    // given a vector sample and its outcome.
    public void trainNeuralNetwork(Vec x1, double outcome) {
        Vec x2;
        double error, gradient, prediction;

        if (getModel() == Model.NN) {
            x2 = new Vec(h);

            // predict
            for (int i = 0; i < w1.size(); i++) {
                x2.set(i, x1.dot(w1.get(i)));
            }
            x2.add(b1);
            actFn(x2);
            prediction = Fn.sigmoid(x2.dot(w2) + b2);
            error = prediction - outcome;
            gradient = error * prediction * (1 - prediction);

            // train
            // squared error gradients
            /*double b2Grad = prediction - outcome; // originally 1
            Vec w2Grad = x2.copy();
            w2Grad.scale(b2Grad); // originally no scale
            Vec b1Grad = new Vec(hiddenLayerSize);
            for (int i = 0; i < b1.length(); i++) {
                if (x2.get(i) > 0) {
                    b1Grad.set(i, w2Grad.get(i)); // originally set num to 1
                }
            }
            ArrayList<Vec> w1Grad = new ArrayList<>();
            for (int i = 0; i < hiddenLayerSize; i++) {
                if (x2.get(i) <= 0) {
                    w1Grad.add(new Vec(weight.length()));
                } else {
                    Vec v = x1.copy();
                    v.scale(w2Grad.get(i)); // originally no scale
                    w1Grad.add(v);
                }
            }*/

            // sigmoid gradients
            double b2Grad = gradient;
            Vec w2Grad = x2.copy();
            w2Grad.scale(b2Grad);

            Vec hidden_errors = w2.copy();
            hidden_errors.scale(error);

            Vec b1Grad = new Vec(h);
            for (int i = 0; i < b1Grad.length(); i++) {
                double elem1 = x2.get(i);
                double elem2 = hidden_errors.get(i);
                b1Grad.set(i, elem1 * (1 - elem1) * elem2);
            }
            ArrayList<Vec> w1Grad = new ArrayList<>();
            for (int i = 0; i < h; i++) {
                Vec v = x1.copy();
                v.scale(b1Grad.get(i));
                w1Grad.add(v);
            }

            updateNeuralNetworkParameters(w1Grad, b1Grad, w2Grad, b2Grad);
        }
    }

    // Updates this weight's parameters given a game
    // sample and its outcome depending on the model used.
    public void update(Vec[] gameSamples, double[] outcomes) {
        assert gameSamples.length == outcomes.length: "Number of game samples "
                + "must be equal to the number of outcomes.";

        if (batchSize == 0) {
            batchSize = gameSamples.length;
        }

        for (int i = 0; i < gameSamples.length; i += batchSize) {
            weightChanges = new Vec(length());

            for (int j = 0; j < batchSize && i + j < gameSamples.length; j++) {
                switch (model) {
                    case BP:
                        addBPChanges(gameSamples[i + j], outcomes[i + j]);
                        break;
                    case LR:
                        addLRChanges(gameSamples[i + j], outcomes[i + j]);
                        break;
                    case NN:
                        addNNChanges(gameSamples[i + j], outcomes[i + j]);
                }
            }
            add(weightChanges);
        }
    }

    // Updates the neural network parameters using the provided gradients.
    private void updateNeuralNetworkParameters(
            ArrayList<Vec> w1Grad, Vec b1Grad, Vec w2Grad, double b2Grad) {
        if (getModel() == Model.NN) {
            for (int i = 0; i < w1.size(); i++) {
                Vec v = w1.get(i);
                Vec g = w1Grad.get(i);

                g.scale(-learningRate);
                v.add(g);

                w1.set(i, v);
            }

            b1Grad.scale(-learningRate);
            b1.add(b1Grad);

            w2Grad.scale(-learningRate);
            w2.add(w2Grad);

            b2 += -learningRate * b2Grad;
        }
    }
}
