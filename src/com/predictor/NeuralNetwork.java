package com.predictor;

import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork {

    private final ActFnNode outputNode;
    private final LossFnNode lossNode;
    private final ArrayList<ParameterNode> paramNodes = new ArrayList<>();
    private final HashMap<Node, HashMap<Node, DataNode>> gradientCache =
            new HashMap<>();
    private DataNode sampleNode;
    private DataNode[] nodesChanges;
    private double learningRate = 1;

    // output layer
    // Regression: linear (because values are unbounded)
    // Classification: softmax (simple sigmoid works too but softmax works better)
    // Will let user choose, but will offer above recommendations as print statements

    // Remember to include loss function and its derivative wrt parameters in backward pass

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

    // how to set up nodesChanges and iterating over samples

    // implement initializing/preprocessing data

    // Default constructor for a neural network with an activation function
    // for each of its layers, each layer being of some specified size.
    public NeuralNetwork(int[] hiddenSizes, DataNode sample, DataNode outcome,
                         String hiddenActFn, String lossFn, String outputActFn)
            throws FnException, NodeException {
        if (!ActFn.isValidActFn(hiddenActFn)) {
            throw new FnException("Only valid activation functions can be used"
                    + " for the hidden layers.");
        } else if (!ActFn.isValidActFn(outputActFn)) {
            throw new FnException("Only valid activation functions can be used"
                    + " for the output layer.");
        }

        sampleNode = sample;
        outputNode = new ActFnNode(outputActFn);
        lossNode = new LossFnNode(lossFn);

        for (int i = 0; i < hiddenSizes.length * 2 + 2; i++) {
            if (i % 2 == 0) {
                // adding weight parameter node
                if (i / 2 == 0) {
                    paramNodes.add(new ParameterNode(hiddenSizes[i / 2], sample.numCols()));
                } else {
                    paramNodes.add(new ParameterNode(hiddenSizes[i / 2], hiddenSizes[(i / 2) - 1]));
                }
            } else {
                // adding bias parameter node
                paramNodes.add(new ParameterNode(hiddenSizes[i / 2], 1));
            }
        }

        for (int i = 0; i < hiddenSizes.length + 1; i++) {
            FunctionNode dotNode = new FunctionNode("dot");
            FunctionNode addNode = new FunctionNode("add");
            ActFnNode hiddenActFnNode = new ActFnNode(hiddenActFn);
            ParameterNode weightNode = paramNodes.get(i * 2);
            ParameterNode biasNode = paramNodes.get(i * 2 + 1);

            if (i == 0) {
                sampleNode.addParent(dotNode);
            } else {
                paramNodes.get(i * 2 - 1).getParents().get(0).getParents().get(0).addParent(dotNode);
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
        DataNode chainGradNode, directGradNode, resultNode;
        Node parentNode = outputNode;
        Node childNode = parentNode.getChildren().get(0);

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

            gradientCache.putIfAbsent(lossNode, new HashMap<>());
            gradientCache.get(lossNode).put(paramNode, resultNode);
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

    // Puts a gradient in the gradient cache.
    private void putGradient(FunctionNode parentNode, Node childNode)
            throws NodeException {
        gradientCache.put(parentNode, new HashMap<>());
        gradientCache.get(parentNode).put(
                childNode, new Gradient(parentNode, childNode).compute());
    }

    // Sets the learning rate of this neural network.
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    // Updates the parameters of the neural network by using gradient descent.
    public void updateParameters() {
        // consider different types of gradient descent
        DataNode nodeChanges;
        double currParamElem, currChangeElem;

        for (ParameterNode paramNode : paramNodes) {
            nodeChanges = gradientCache.get(lossNode).get(paramNode);

            for (int i = 0; i < paramNode.numRows(); i++) {
                for (int j = 0; j < paramNode.numCols(); j++) {
                    currParamElem = paramNode.get(i, j);
                    currChangeElem = nodeChanges.get(i, j);

                    paramNode.set(i, j,
                            currParamElem - learningRate * currChangeElem);
                }
            }
        }
    }
}
