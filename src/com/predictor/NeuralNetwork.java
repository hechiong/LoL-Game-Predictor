package com.predictor;

import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork {

    private final FunctionNode outputNode;
    private final ArrayList<ParameterNode> paramNodes = new ArrayList<>();
    private final HashMap<Node, HashMap<Node, HashMap<Integer, Double>>> partDerivCache = new HashMap<>();

    public NeuralNetwork(int numLayers, FunctionNode... actFns) {
        //q
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
