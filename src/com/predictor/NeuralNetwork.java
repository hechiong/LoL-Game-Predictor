package com.predictor;

import java.io.Serializable;
import java.util.HashMap;

public class NeuralNetwork implements Serializable {

    private final ActFnNode outputNode;
    private final DataNode sampleNode;
    private final HashMap<Node, HashMap<Node, DataNode>> gradientCache;
    private final LossFnNode lossNode;
    private final ParameterNode[] paramNodes;
    private final WeightInit wtInit;

    private double learningRate = 1;
    private int batchSize = 0;

    // output layer
    // Regression: linear (because values are unbounded)
    // Classification: softmax (simple sigmoid works too but softmax works better)
    // Will let user choose, but will offer above recommendations as print statements

    // When using the ReLU function for hidden layers, it is a good practice
    // to use a “He Normal” or “He Uniform” weight initialization and scale
    // input data to the range 0-1 (normalize) prior to training.

    // When using the Sigmoid or TanH functions for hidden layers, it is a good
    // practice to use a “Xavier Normal” or “Xavier Uniform” weight
    // initialization (also referred to Glorot initialization, named for Xavier
    // Glorot) and scale input data to the range of the activation function
    // (0 to 1 or -1 to 1 respectively) prior to training.

    // For multi-layer perceptron, use ReLU for hidden layers
    // (in project, make it recommended)

    // Target values used to train a model with a linear activation function in
    // the output layer are typically scaled prior to modeling using
    // normalization or standardization transforms.

    // Target labels used to train a model with a sigmoid activation function
    // in the output layer will have the values 0 or 1.

    // Target labels used to train a model with the softmax activation function
    // in the output layer will be vectors with 1 for the target class and 0
    // for all other classes.

    // For binary classification, use sigmoid activation function for output layer
    // (in project, make it recommended)

    // Default constructor for a neural network with an activation function
    // for each of its layers, each layer being of some specified size.
    public NeuralNetwork(int[] hiddenSizes, DataNode sample, DataNode outcome,
                         String hiddenActFn, String lossFn, String outputActFn,
                         String weightInit)
            throws FnException, NodeException {
        if (!ActFn.isValidActFn(hiddenActFn)) {
            throw new FnException("Only valid activation functions can be used"
                    + " for the hidden layers.");
        } else if (!ActFn.isValidActFn(outputActFn)) {
            throw new FnException("Only valid activation functions can be used"
                    + " for the output layer.");
        } else if (!WeightInit.isValidWeightInit(weightInit)) {
            throw new FnException("Only valid activation functions can be used"
                    + " for the hidden layers.");
        }

        outputNode = new ActFnNode(outputActFn);
        gradientCache = new HashMap<>();
        lossNode = new LossFnNode(lossFn);
        paramNodes = new ParameterNode[hiddenSizes.length * 2 + 2];
        wtInit = new WeightInit(weightInit);
        sampleNode = sample;

        // preparing parameters and their respective changes
        for (int i = 0; i < hiddenSizes.length * 2 + 2; i++) {
            if (i % 2 == 0) {
                // adding weight parameter node and its respective changes
                if (i == 0) {
                    paramNodes[i] = new ParameterNode(sample.numCols(), hiddenSizes[i / 2]);
                } else {
                    paramNodes[i] = new ParameterNode(hiddenSizes[(i / 2) - 1], hiddenSizes[i / 2]);
                }
            } else {
                // adding bias parameter node and its respective changes
                paramNodes[i] = new ParameterNode(1, hiddenSizes[i / 2]);
            }

            // initializing each weight
            wtInit.accept(paramNodes[i]);
        }

        // connecting nodes
        for (int i = 0; i < hiddenSizes.length + 1; i++) {
            OperatorFnNode dotNode = new OperatorFnNode("dot");
            OperatorFnNode addNode = new OperatorFnNode("add");
            ActFnNode hiddenActFnNode = new ActFnNode(hiddenActFn);
            ParameterNode weightNode = paramNodes[i * 2];
            ParameterNode biasNode = paramNodes[i * 2 + 1];

            if (i == 0) {
                sampleNode.addParent(dotNode);
            } else {
                paramNodes[i * 2 - 1].getParents().get(0).getParents().get(0).addParent(dotNode);
            }

            weightNode.addParent(dotNode);
            dotNode.addParent(addNode);
            biasNode.addParent(addNode);

            if (i != hiddenSizes.length) {
                addNode.addParent(hiddenActFnNode);
            } else {
                addNode.addParent(outputNode);
            }
        }

        outcome.addParent(lossNode);
        outputNode.addParent(lossNode);
    }

    // Computes the respective gradients of the loss node
    // with respect to each other node and stores them in
    // a cache for the backward pass of backpropagation.
    private void backwardPass() throws NodeException {
        DataNode chainGradNode;
        DataNode directGradNode;
        DataNode resultNode;
        Node childNode = outputNode.getChildren().get(0);
        Node parentNode = outputNode;

        while (childNode != sampleNode) {
            chainGradNode = gradientCache.get(lossNode).get(parentNode);
            directGradNode = gradientCache.get(parentNode).get(childNode);
            resultNode = new DataNode(1, directGradNode.numCols());

            if (parentNode.getFn().equals("dot")) {
                for (int i = 0; i < directGradNode.numRows(); i++) {
                    for (int j = 0; j < directGradNode.numCols(); j++) {
                        resultNode.set(0, j,
                                resultNode.get(0, j) + chainGradNode.get(0, i)
                                        * directGradNode.get(i, j));
                    }
                }
            } else {
                for (int j = 0; j < resultNode.numCols(); j++) {
                    resultNode.set(0, j,
                            chainGradNode.get(0, j)
                                    * directGradNode.get(0, j));
                }
            }

            gradientCache.putIfAbsent(lossNode, new HashMap<>());
            gradientCache.get(lossNode).put(childNode, resultNode);

            parentNode = childNode;
            childNode = childNode.getChildren().get(0);
        }

        for (ParameterNode paramNode : paramNodes) {
            parentNode = paramNode.getParents().get(0);
            chainGradNode = gradientCache.get(lossNode).get(parentNode);
            directGradNode = gradientCache.get(parentNode).get(paramNode);
            resultNode = new DataNode(paramNode.numRows(), paramNode.numCols());

            if (parentNode.getFn().equals("dot")) {
                for (int i = 0; i < resultNode.numRows(); i++) {
                    for (int j = 0; j < resultNode.numCols(); j++) {
                        resultNode.set(i, j,
                                chainGradNode.get(0, j)
                                        * directGradNode.get(0, i));
                    }
                }
            } else {
                for (int j = 0; j < resultNode.numCols(); j++) {
                    resultNode.set(0, j,
                            chainGradNode.get(0, j)
                                    * directGradNode.get(0, j));
                }
            }

            gradientCache.get(lossNode).put(paramNode, resultNode);

            paramNode.addChanges(resultNode);
        }
    }

    // Computes the gradients for nodes who have a direct
    // relationship with each other and stores them in
    // a cache for the forward pass of backpropagation.
    private void forwardPass() throws FnException, NodeException {
        FunctionNode parentNode;
        Node childNode = sampleNode;

        while (childNode != lossNode) {
            parentNode = childNode.getParents().get(0);

            parentNode.compute();
            putGradient(parentNode, childNode);

            childNode = parentNode;
        }

        for (ParameterNode paramNode : paramNodes) {
            putGradient(paramNode.getParents().get(0), paramNode);
        }
    }

    // Returns the batch size this neural network uses.
    public int getBatchSize() {
        return batchSize;
    }

    // Returns the learning rate this neural network uses.
    public double getLearningRate() {
        return learningRate;
    }

    // Returns the weight initialization this neural network uses.
    public String getWeightInit() {
        return wtInit.toString();
    }

    // Puts a gradient in the gradient cache.
    private void putGradient(FunctionNode parentNode, Node childNode)
            throws FnException, NodeException {
        gradientCache.put(parentNode, new HashMap<>());
        gradientCache.get(parentNode).put(
                childNode, new Gradient(parentNode, childNode).compute());
    }

    // Processes the list of samples into this neural network.
    public void processSamples(Vec[] samples)
            throws FnException, NeuralNetworkException, NodeException {
        Vec sample;

        for (int i = 0; i < samples.length; i++) {
            sample = samples[i];

            if (sample.length() != sampleNode.numCols()) {
                throw new NeuralNetworkException("The size of the sample "
                        + "vector must match the size of the sample node.");
            }

            setSample(sample);

            forwardPass();
            backwardPass();

            if (i % batchSize == batchSize - 1 || i == samples.length - 1) {
                updateParameters();
            }
        }
    }

    // Sets the batch size this neural network uses.
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    // Sets the learning rate this neural network uses.
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    // Sets the sample to be used for this neural network.
    public void setSample(Vec sample) {
        sampleNode.setRow(0, sample);
    }

    // Updates the parameters of this neural network.
    private void updateParameters() {
        for (ParameterNode paramNode : paramNodes) {
            paramNode.scaleChanges(-learningRate / batchSize);
            paramNode.updateParameterAndResetChanges();
        }
    }
}
