package com.predictor;

import com.merakianalytics.orianna.types.core.staticdata.Champion;
import com.merakianalytics.orianna.types.core.staticdata.Champions;

import java.util.*;
import java.util.Map;

public class LoLNeuralNetwork extends NeuralNetwork {

    public enum FeaturesType { CWR, TC, CWRTC }

    private final ArrayList<String> champsArray = new ArrayList<>();
    private final HashMap<Integer, String> champsMap = new HashMap<>();
    private final FeaturesType featType;
    private final int teamType;

    // Constructor for a LoLNeuralNetwork associated with some
    // type of features and some way of considering teams.
    public LoLNeuralNetwork(int[] hiddenSizes, DataNode sample, DataNode outcome,
                            String hiddenActFn, String lossFn, String outputActFn,
                            String weightInit, FeaturesType featType, int teamType)
            throws FnException, NodeException {
        super(hiddenSizes, sample, outcome,
                hiddenActFn, lossFn, outputActFn, weightInit);

        assert featType != null: "Only valid types of features can be used.";
        assert isValidTeamType(teamType): "Only valid team types can be used.";

        updateWithAddedChamps();

        int numFeatures = champsMap.size() * 2;

        if (featType == FeaturesType.CWRTC) {
            numFeatures *= 2;
        }
        this.featType = featType;

        if (teamType == 2 || teamType == 4) {
            numFeatures += 2;
        } else if (teamType == 3) {
            numFeatures *= 2;
        }
        this.teamType = teamType;

        setSample(new Vec(++numFeatures));
    }

    // Returns a mapping of the ID's to the names
    // of the champions that currently exist.
    private HashMap<Integer, String> allChampsMap() {
        HashMap<Integer, String> allChampsMap = new HashMap<>();

        for (final Champion champion : Champions.get()) {
            allChampsMap.put(champion.getId(), champion.getName());
        }

        return allChampsMap;
    }

    // Returns and searches for the index where inserting
    // some element within some lower index bound (inclusive)
    // and some upper index bound (exclusive) of the sorted
    // array will keep the array sorted (in lexicographical order).
    private static int binarySearch(
            ArrayList<String> sortedArray, int start, int end, String element) {
        assert start >= 0 && start <= sortedArray.size(): "The start index "
                + "must be between 0 and " + sortedArray.size() + ".";
        assert end >= 0 && end <= sortedArray.size(): "The start index must be"
                + " between 0 and " + sortedArray.size() + ".";
        assert start <= end: "The start index mustn't be greater than the end "
                + "index.";

        int index = (end + start) / 2;
        String currElem;
        String prevElem = element;

        if (start == end) {
            return start;
        }

        if (index > 0) {
            prevElem = sortedArray.get(index - 1);
        }
        currElem = sortedArray.get(index);

        if (element.compareTo(prevElem) < 0) {
            return binarySearch(sortedArray, start, index - 1, element);
        } else if (element.compareTo(currElem) > 0) {
            return binarySearch(sortedArray, index + 1, end, element);
        }
        return index;
    }

    // Returns the String representation of displaying
    // the type of features this neural network has.
    private String featTypeString() {
        String featTypeStr = "Type of features: ";

        switch (featType) {
            case CWR:
                featTypeStr += "champion win rates";
                break;
            case CWRTC:
                featTypeStr += "champion win rates and team composition";
                break;
            case TC:
                featTypeStr += "team composition";
        }

        return featTypeStr;
    }

    // Returns the type of features this neural network has.
    public FeaturesType getFeatureType() {
        return featType;
    }

    // Returns how this neural network considers teams.
    public int getTeamType() {
        return teamType;
    }

    // Returns whether the given team type is valid.
    public static boolean isValidTeamType(int teamType) {
        return teamType >= 1 && teamType <= 5;
    }

    // Returns the String representation of displaying
    // how this neural network considers teams.
    private String teamTypeString() {
        String teamTypeStr = "This neural network considers teams by ";

        switch (teamType) {
            case 1:
                teamTypeStr += "alliance.";
                break;
            case 2:
                teamTypeStr += "alliance more than color.";
                break;
            case 3:
                teamTypeStr += "both alliance and color.";
                break;
            case 4:
                teamTypeStr += "color more than alliance.";
                break;
            case 5:
                teamTypeStr += "color.";
        }

        return teamTypeStr;
    }

    // Returns the String representation of this neural network.
    public String toString() {
        String featTypeStr = featTypeString() + "\n";
        String teamTypeStr = teamTypeString() + "\n";

        return super.toString() + "\n" + featTypeStr + teamTypeStr;
    }

    // Updates this neural network with
    // champions that have been added or changed.
    public void update() {
        updateWithChangedChamps();
        updateWithAddedChamps();
    }

    // Updates this neural network with new champions if any have been added.
    private void updateWithAddedChamps() {
        int numInsertions = 2;

        if (getFeatureType() == FeaturesType.CWRTC) {
            numInsertions *= 2;
        }

        if (getTeamType() == 3) {
            numInsertions *= 2;
        }

        for (final Champion champion : Champions.get()) {
            int champId = champion.getId();
            String champName = champion.getName();

            if (!champsMap.containsKey(champId)) {
                int champIndex = binarySearch(
                        champsArray, 0, champsArray.size(), champName);
                champsArray.add(champIndex, champName);
                champsMap.put(champion.getId(), champion.getName());

                for (int i = 0; i < numInsertions; i++) {
                    insertParamRow(0, i * champsMap.size() + i + champIndex);
                }
            }
        }
    }

    // Updates this neural network with
    // champions if any whose names have changed.
    private void updateWithChangedChamps() {
        HashMap<Integer, String> allChampsMap = allChampsMap();
        String champName, updatedChampName;
        boolean isUpdated = false;
        int champId, champIndex;

        for (Map.Entry<Integer, String> champEntry: champsMap.entrySet()) {
            champId = champEntry.getKey();
            champName = champEntry.getValue();
            champIndex = champsArray.indexOf(champName);

            if (!allChampsMap.containsValue(champName)) {
                updatedChampName = allChampsMap.get(champId);
                champsArray.set(champIndex, updatedChampName);
                champsMap.put(champId, updatedChampName);

                while (!isUpdated) {
                    String nextChamp = updatedChampName;
                    String prevChamp = updatedChampName;

                    if (champIndex > 0) {
                        prevChamp = champsArray.get(champIndex - 1);
                    }

                    if (champIndex < champsMap.size() - 1) {
                        nextChamp = champsArray.get(champIndex + 1);
                    }

                    if (updatedChampName.compareTo(prevChamp) < 0) {
                        champsArray.set(champIndex, prevChamp);
                        champsArray.set(champIndex - 1, updatedChampName);
                        swap(champIndex - 1, champIndex);
                        champIndex--;
                    } else if (updatedChampName.compareTo(nextChamp) > 0) {
                        champsArray.set(champIndex, nextChamp);
                        champsArray.set(champIndex + 1, updatedChampName);
                        swap(champIndex, champIndex + 1);
                        champIndex++;
                    } else {
                        isUpdated = true;
                    }
                }

                isUpdated = false;
            }
        }
    }
}