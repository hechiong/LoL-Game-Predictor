package com.predictor;

public class FunctionNode extends Node {

    private final String fn;

    // Constructor for a function with node argument(s).
    public FunctionNode(String fn, Node... nodes)
            throws FnException, NodeException {
        Node n0, n1;
        int numNodes = nodes.length;
        this.fn = fn;

        if (ActFn.isValidActFn(fn) && numNodes == 1) {
            ActFn actFn = new ActFn(fn);
            n0 = nodes[0];
            m = new Vec[n0.numCols()];

            for (int i = 0; i < numCols(); i++) {
                m[i] = new Vec(n0.numRows());

                for (int j = 0; j < numRows(); j++) {
                    m[i].set(j, n0.get(i, j));
                }
                actFn.accept(m[i]);
            }

            addChild(n0);
            n0.addParent(this);
        } else if (Fn.isValidFn(fn) && numNodes > 1) {
            n0 = nodes[0];
            n1 = nodes[1];

            if (fn.equals("add")) {
                m = new Vec[n0.numCols()];
                for (int i = 0; i < m.length; i++) {
                    m[i] = new Vec(n0.numRows());
                }

                for (Node n : nodes) {
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

            for (Node n : nodes) {
                addChild(n);
                n.addParent(this);
            }
        } else {
            throw new NodeException("Invalid nodes can't be created.");
        }
    }

    // Returns the function this node represents.
    public String getFn() {
        return fn;
    }

    // Returns whether this node is a parent of the node or not.
    public boolean isParentOf(FunctionNode n) {
        return n.parents.contains(this);
    }
}

