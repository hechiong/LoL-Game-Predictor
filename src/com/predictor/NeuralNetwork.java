package com.predictor;

import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork {

    private final FunctionNode outputNode;
    private final ArrayList<ParameterNode> paramNodes = new ArrayList<>();
    private final HashMap<Node, HashMap<Node, HashMap<Integer, Double>>> partDerivCache = new HashMap<>();

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

    // Default constructor for a neural network with
    // an activation function for each of its layers.
    public NeuralNetwork(int numHiddenLayers, int[] hiddenSizes,
                         FunctionNode hiddenActFn, FunctionNode outputActFn,
                         DataNode sample) throws FnException {
        // numParamNodes = 2*numHiddenLayers + 2
        for (int i = 0; i < numHiddenLayers * 2 + 2; i++) {
            if (i % 2 == 0) {
                if (i / 2 == 0) {
                    // adding weight parameter node
                    paramNodes.add(new ParameterNode(hiddenSizes[i / 2], sample.numCols()));
                } else {
                    // adding bias parameter node
                    paramNodes.add(new ParameterNode(hiddenSizes[i / 2], hiddenSizes[(i / 2) - 1]));
                }
            } else {
                paramNodes.add(new ParameterNode(hiddenSizes[i / 2], 1));
            }
        }

        for (int i = 0; i < numHiddenLayers + 1; i++) {
            paramNodes.get(i).addParent(new FunctionNode("dot"));
            paramNodes.get(i).addParent(new FunctionNode("add"));
        }
    }

    public void forwardPass() {
        Vec paramGrad = new Vec();
    }

    // Returns the partial derivative of the output node with
    // respect to the node for the backward pass  of backpropagation.
    public Vec backward(Node n) {
        Vec result = new Vec(n.length());

        for (int i = 0; i < n.length(); i++) {
            result.set(i, 1);
        }

        if (outputNode.equals(n)) {
            for (int i = 0; i < n.length(); i++) {
                result.set(i, 1);
            }

            return result;
        } else if (partDerivCache.get())

        for (Node parent : n.getParents()) {
            result.add(backward(parent).dot(parent.forward(n)));
        }

        return result;
    }
}
