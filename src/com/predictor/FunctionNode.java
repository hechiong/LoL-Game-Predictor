package com.predictor;

import java.util.ArrayList;

public class FunctionNode extends Node {

    protected final ArrayList<Node> children = new ArrayList<>();
    private final String fn;
    private boolean computed = false;

    // Constructor for a node associated with a function.
    public FunctionNode(String fn) throws FnException {
        this.fn = fn;

        if (!Fn.isValidFn(fn)) {
            throw new FnException("Nodes can't be created with invalid " +
                    "functions.");
        }
    }

    // Computes the vector this function node will represent
    // based on the function it represents and its children nodes.
    public Vec[] compute() throws FnException, NodeException {
        if (computed) {
            return m;
        }

        Node n0, n1;
        int numNodes = children.size();

        if (ActFn.isValidActFn(fn) && numNodes == 1) {
            ActFn actFn = new ActFn(fn);
            n0 = children.get(0);
            m = new Vec[n0.numCols()];

            for (int i = 0; i < numCols(); i++) {
                m[i] = new Vec(n0.numRows());

                for (int j = 0; j < numRows(); j++) {
                    m[i].set(j, n0.get(i, j));
                }
                actFn.accept(m[i]);
            }
        } else if (Fn.isValidFn(fn) && numNodes > 1) {
            n0 = children.get(0);
            n1 = children.get(1);

            if (fn.equals("add")) {
                m = new Vec[n0.numCols()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n0.numRows());
                }

                for (Node n : children) {
                    if (!hasEqualDims(n)) {
                        throw(new NodeException("Nodes added together must "
                                + " have equal dimensions."));
                    }

                    for (int i = 0; i < numCols(); i++) {
                        for (int j = 0; j < numRows(); j++) {
                            set(i, j, get(i, j) + n.get(i, j));
                        }
                    }
                }
            } else if (fn.equals("dot") && numNodes == 2) {
                m = new Vec[n1.numCols()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n0.numRows());
                }

                if (n0.numCols() == n1.numRows()) {
                    for (int i = 0; i < numCols(); i++) {
                        for (int j = 0; j < numRows(); j++) {
                            for (int k = 0; k < n0.numCols(); k++) {
                                set(i, j, get(i, j) + n0.get(k, j) * n1.get(i, k));
                            }
                        }
                    }
                } else {
                    throw(new NodeException("Nodes involved in a "
                            + "multiplication must have valid dimensions."));
                }
            } else {
                throw(new NodeException("Only valid operations with an "
                        + "appropriate number of node inputs can be used."));
            }
        } else {
            throw new NodeException("Computations can't be made for invalid nodes.");
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

    // Returns whether this node is a parent of the node or not.
    public boolean isParentOf(FunctionNode n) {
        return n.parents.contains(this);
    }

    // Resets this function node by making it not computed yet.
    public void reset() {
        computed = false;
    }
}

