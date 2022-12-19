package com.predictor;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.staticdata.Patch;
import com.merakianalytics.orianna.types.core.summoner.Summoner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class LoLMatchHistory implements Iterable<LoLMatch>, Serializable {

    private final String playerPuuid;

    private ArrayList<LoLMatch> matchHistory;
    private String season;

    // Constructor for a simplified version of League API's
    // MatchHistory class that'll be associated with a specific player.
    public LoLMatchHistory(String puuid) {
        playerPuuid = puuid;
        matchHistory = new ArrayList<>();
        updateSeason();
    }

    // Adds the match to the player's match history if it belongs to them.
    public void add(LoLMatch match) {
        if (playerPuuid.equals(match.getPlayerPuuid())) {
            matchHistory.add(match);
        } else {
            System.out.println("Match can't be added because the player "
                    + "PUUID's differ.");
        }
    }

    // Returns whether the player's match history contains the match or not.
    public boolean contains(Match match) {
        return indexOf(match) != -1;
    }

    // Returns the match at some index in the player's match history.
    public LoLMatch get(int index) {
        return matchHistory.get(index);
    }

    // Returns the player's PUUID.
    public String getPlayerPuuid() {
        return playerPuuid;
    }

    // Returns the index at which the corresponding match
    // resides in the player's match history, or returns -1
    // if the match is null or isn't in this match history.
    public int indexOf(Match match) {
        LoLMatch m;

        if (match == null) {
            return -1;
        }

        for (int i = 0; i < size(); i++) {
            m = get(i);

            if (m.getMatchId() == match.getId()) {
                return i;
            }
        }
        return -1;
    }

    // Returns whether there are zero matches
    // or not in the player's match history.
    public boolean isEmpty() {
        return size() == 0;
    }

    // Returns the iterator for iterating matches in the player's match history.
    public Iterator<LoLMatch> iterator() {
        return new MatchHistoryIterator();
    }

    // Resets the player's match history if it
    // contains matches from previous seasons.
    public void resetIfOldMatchesExist() {
        if (!isEmpty()) {
            LoLMatch earliestMatch = matchHistory.get(0);
            String earliestPatch = earliestMatch.getPatch();
            String earliestSeason = earliestPatch.split("\\.")[0];

            updateSeason();
            if (!earliestSeason.equals(season)) {
                matchHistory = new ArrayList<>();
            }
        }
    }

    // Returns a shuffled array version of the player's match history.
    public LoLMatch[] shuffle() {
        int size = size();
        LoLMatch[] shuffledArray = new LoLMatch[size];

        for (int i = 0; i < size; i++) {
            shuffledArray[i] = get(i);
        }

        for (int i = 0; i < size; i++) {
            int randomIndex = (int) (size * Math.random());
            LoLMatch temp = shuffledArray[i];
            shuffledArray[i] = shuffledArray[randomIndex];
            shuffledArray[randomIndex] = temp;
        }

        return shuffledArray;
    }

    // Returns the current number of matches in the player's match history.
    public int size() {
        return matchHistory.size();
    }

    // Returns the String representation of the player's match history.
    public String toString() {
        return Summoner.withPuuid(playerPuuid).get().getName() + " has "
                + size() + " processed matches in their season " + season
                + " match history.";
    }

    // Updates this match history's season to the current season.
    private void updateSeason() {
        season = Patch.get().getName().split("\\.")[0];
    }

    public class MatchHistoryIterator implements Iterator<LoLMatch> {

        private int index = 0;

        // Returns whether there's a next match or not.
        public boolean hasNext() {
            return index < size();
        }

        // Returns the next match in the player's
        // match history and increments the index.
        public LoLMatch next() {
            return matchHistory.get(index++);
        }
    }
}
