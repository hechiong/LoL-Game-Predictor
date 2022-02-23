package com.predictor;

import java.io.Serializable;

public class Vec implements Serializable {

    protected double[] vec;

    // Default constructor for a vector.
    public Vec() {
        vec = new double[1];
    }

    // Constructor for a vector of some length.
    public Vec(int len) {
        assert len > 0: "Only a vector of positive length can be made.";
        vec = new double[len];
    }

    // Computes element-wise addition on this vector with the other vector.
    public void add(Vec v) {
        assert length() == v.length(): "Addition can't be applied to vectors "
                + "of different lengths.";

        for (int i = 0; i < length(); i++) {
            set(i, get(i) + v.get(i));
        }
    }

    // Returns a copy of this vector.
    public Vec copy() {
        Vec v = new Vec(length());

        for (int i = 0; i < length(); i++) {
            v.set(i, get(i));
        }

        return v;
    }

    // Returns the dot product of this vector with the other vector.
    public double dot(Vec v) {
        assert length() == v.length(): "Dot product can't be applied to "
                + "vectors of different lengths.";
        double dotProduct = 0;

        for (int i = 0; i < length(); i++) {
            dotProduct += get(i) * v.get(i);
        }

        return dotProduct;
    }

    // Returns the element at some index of this vector.
    public double get(int index) {
        assert index >= 0 && index < length(): index + "is an invalid index to"
                + " get from this vector.";

        return vec[index];
    }

    // Returns the length of this vector.
    public int length() {
        return vec.length;
    }

    // Scales each element of this vector by some scalar.
    public void scale(double scalar) {
        for (int i = 0; i < length(); i++) {
            set(i, get(i) * scalar);
        }
    }

    // Sets the element at some index of this vector.
    public void set(int index, double value) {
        assert index >= 0 && index < length(): index + "is an invalid index to"
                + " set for this vector.";

        vec[index] = value;
    }

    // Expands this vector's length and shifts elements at least after
    // some index by some amount and sets each of its new elements to 0.
    public void shift(int index, int amount) {
        assert index >= 0 && index < length(): index + "is an invalid index "
                + "from which to shift for this vector.";
        assert amount >= 0: "The element at the index can't be shifted by a "
                + "negative amount.";

        int newLen = length() + amount;
        double[] newVec = new double[newLen];

        for (int i = 0; i < index; i++) {
            newVec[i] = get(i);
        }

        for (int i = index + amount; i < newLen; i++) {
            newVec[i] = get(i - amount);
        }

        vec = newVec;
    }

    // Swaps the values of the two indices in this vector.
    public void swap(int index1, int index2) {
        assert index1 >= 0 && index1 < length(): "First index is an invalid "
                + "index of this vector.";
        assert index2 >= 0 && index2 < length(): "Second index is an invalid "
                + "index of this vector.";

        double temp = get(index1);
        set(index1, get(index2));
        set(index2, temp);
    }

    // Returns the String representation of this vector.
    public String toString() {
        final int numCols = 10;
        String formattedVal;
        String s = "[";

        for (int i = 0; i < length(); i += numCols) {
            for (int j = 0; j < numCols && i + j < length(); j++) {
                formattedVal = String.format("%3.3f" , get(i + j));

                if (i + j > 0) {
                    s = s.concat(" ");
                }
                s = s.concat(formattedVal);
            }

            if (i + numCols < length()) {
                s = s.concat("\n");
            } else {
                s = s.concat("]");
            }

        }

        return s;
    }
}
