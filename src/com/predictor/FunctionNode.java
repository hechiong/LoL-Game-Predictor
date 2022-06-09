package com.predictor;

import java.util.ArrayList;

public class FunctionNode extends Node {

    protected final ArrayList<Node> children = new ArrayList<>();
    private final String fn;
    protected boolean computed = false;

    // Constructor for a node associated with a function.
    public FunctionNode(String fn) throws NodeException {
        this.fn = fn;

        if (!Fn.isValidFn(fn)) {
            throw new NodeException("Function nodes can't be created with "
                    + "invalid functions.");
        } else if (ActFn.isValidActFn(fn)) {
            throw new NodeException("Activation functions can only be used "
                    + "in activation function nodes.");
        } else if (LossFn.isValidLossFn(fn)) {
            throw new NodeException("Loss functions can only be used in loss"
                    + " function nodes.");
        }
    }

    // Computes the vector this function node will represent
    // based on the function it represents and its children nodes.
    public Vec[] compute() throws FnException, NodeException {
        if (computed) {
            return getMatrix();
        }

        Node n0, n1;
        int numNodes = children.size();

        if (numNodes > 1) {
            n0 = children.get(0);
            n1 = children.get(1);

            if (fn.equals("add")) {
                m = new Vec[n0.numRows()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n0.numCols());
                }

                for (Node n : children) {
                    if (!hasEqualDims(n)) {
                        throw new NodeException("Nodes added together must "
                                + "have equal dimensions.");
                    }

                    for (int i = 0; i < numRows(); i++) {
                        for (int j = 0; j < numCols(); j++) {
                            set(i, j, get(i, j) + n.get(i, j));
                        }
                    }
                }
            } else if (fn.equals("dot") && numNodes == 2) {
                m = new Vec[n0.numRows()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n1.numCols());
                }

                if (n0.numCols() == n1.numRows()) {
                    for (int i = 0; i < numRows(); i++) {
                        for (int j = 0; j < numCols(); j++) {
                            for (int k = 0; k < n0.numCols(); k++) {
                                set(i, j, get(i, j) + n0.get(i, k) * n1.get(k, j));
                            }
                        }
                    }
                } else {
                    throw new NodeException("Nodes involved in matrix "
                            + "multiplication must have valid dimensions.");
                }
            } else {
                throw(new NodeException("Function nodes must have an "
                        + "appropriate number of node inputs."));
            }
        } else {
            throw new NodeException("Computations can't be made for invalid "
                    + "function nodes.");
        }

        return m;
    }

    // Returns a copy of the list of parents of this node.
    public ArrayList<Node> getChildren() {
        return new ArrayList<>(children);
    }

    // Returns the function this node represents.
    public String getFn() {
        return fn;
    }

    // Returns whether the contents of this function node is computed or not.
    public boolean isComputed() {
        return computed;
    }

    // Returns whether this node is a parent of the node or not.
    public boolean isParentOf(FunctionNode n) {
        return n.getParents().contains(this);
    }

    // Resets this function node by making it not computed yet.
    public void reset() {
        computed = false;
    }
}

