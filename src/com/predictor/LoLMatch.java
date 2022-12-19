package com.predictor;

import com.merakianalytics.orianna.types.core.summoner.Summoner;

public class LoLMatch {

    private final long matchId;
    private final String patch;
    private final String playerPuuid;
    private final boolean isAllyBlueSide;
    private final boolean isAllyWinner;
    private int playerIndex = -1;

    private static final int NUM_PLAYERS = 5;
    private int numAllies = 0;
    private int numEnemies = 0;

    private final String[] allyPuuids = new String[NUM_PLAYERS];
    private final String[] enemyPuuids = new String[NUM_PLAYERS];

    private final String[] allyChamps = new String[NUM_PLAYERS];
    private final String[] enemyChamps = new String[NUM_PLAYERS];

    private final double[] allyChampWinRates = new double[NUM_PLAYERS];
    private final double[] enemyChampWinRates = new double[NUM_PLAYERS];

    // Constructor for a simplified version of League API's
    // Match class that'll be associated with a specific player.
    public LoLMatch(long matchId, String patch, String playerPuuid,
                    boolean isAllyBlueSide, boolean isAllyWinner) {
        this.matchId = matchId;
        this.patch = patch;
        this.playerPuuid = playerPuuid;
        this.isAllyBlueSide = isAllyBlueSide;
        this.isAllyWinner = isAllyWinner;
    }

    // Adds the ally's PUUID, the champion they played in this match, and their
    // win rate on that champion coming into this match, as well as sets the
    // index associated with the ally if they're the player of interest.
    public void addAlly(String puuid, String champ, double champWinRate) {
        if (numAllies < NUM_PLAYERS) {
            allyPuuids[numAllies] = puuid;
            allyChamps[numAllies] = champ;
            allyChampWinRates[numAllies] = champWinRate;

            if (playerPuuid.equals(puuid)) {
                playerIndex = numAllies;
            }
            numAllies += 1;
        }
    }

    // Adds the enemy's PUUID, the champion they played in this match,
    // and their win rate on that champion coming into this match.
    public void addEnemy(String puuid, String champ, double champWinRate) {
        if (numEnemies < NUM_PLAYERS) {
            enemyPuuids[numEnemies] = puuid;
            enemyChamps[numEnemies] = champ;
            enemyChampWinRates[numEnemies] = champWinRate;
            numEnemies += 1;
        }
    }

    // Returns information about the player's team.
    private String alliesInfoString() {
        String alliesInfo = "";
        String summoner;

        for (int i = 0; i < numAllies; i++) {
            summoner = Summoner.withPuuid(allyPuuids[i]).get().getName();
            alliesInfo += "Ally " + summoner + " had a win rate of "
                    + (allyChampWinRates[i] * 100) + "% on " + allyChamps[i]
                    + " coming into match " + matchId + ".\n";
        }

        return alliesInfo;
    }

    // Returns information about the enemy team.
    private String enemiesInfoString() {
        String enemiesInfo = "";
        String summoner;

        for (int i = 0; i < numEnemies; i++) {
            summoner = Summoner.withPuuid(enemyPuuids[i]).get().getName();
            enemiesInfo += "Enemy " + summoner + " had a win rate of "
                    + (enemyChampWinRates[i] * 100) + "% on " + enemyChamps[i]
                    + " coming into this match " + matchId + ".\n";
        }

        return enemiesInfo;
    }

    // Returns the champion played in this match by the corresponding ally.
    public String getAllyChamp(int index) {
        return allyChamps[index];
    }

    // Returns the corresponding ally's win rate on
    // the corresponding champion coming into this match.
    public double getAllyChampWinRate(int index) {
        return allyChampWinRates[index];
    }

    // Returns the champion played in this match by the corresponding enemy.
    public String getEnemyChamp(int index) { return enemyChamps[index]; }

    // Returns the corresponding enemy's win rate on
    // the corresponding champion coming into this match.
    public double getEnemyChampWinRate(int index) {
        return enemyChampWinRates[index];
    }

    // Returns this match's ID.
    public long getMatchId() {
        return matchId;
    }

    // Return the patch in which this match was played.
    public String getPatch() {
        return patch;
    }

    // Returns the index associated with the player of interest in this match.
    public int getPlayerIndex() {
        return playerIndex;
    }

    // Returns the player's PUUID.
    public String getPlayerPuuid() {
        return playerPuuid;
    }

    // Returns whether the player's team was
    // on the blue side or not in this match.
    public boolean isAllyBlueSide() {
        return isAllyBlueSide;
    }

    // Returns the String representation displaying the
    // side on which the player's team was in this match.
    private String isAllyBlueSideString() {
        if (isAllyBlueSide) {
            return "Allies' side: blue";
        }
        return "Allies' side: red";
    }

    // Returns whether the player's team won this match or not.
    public boolean isAllyWinner() {
        return isAllyWinner;
    }

    // Returns the String representation displaying
    // whether the player's team won this match or not.
    private String isAllyWinnerString() {
        if (isAllyWinner) {
            return "Allies won: yes";
        }
        return "Allies won: no";
    }

    // Returns the String representation of this match.
    public String toString() {
        String matchIdStr = "Match ID: " + matchId + "\n";
        String playerPuuidStr = "Player PUUID: " + playerPuuid + "\n";
        String isAllyBlueSideStr = isAllyBlueSideString() + "\n";
        String isAllyWinnerStr = isAllyWinnerString() + "\n";
        String alliesInfo = alliesInfoString();
        String enemiesInfo = enemiesInfoString();

        return matchIdStr + playerPuuidStr + isAllyBlueSideStr
                + isAllyWinnerStr + alliesInfo + enemiesInfo;
    }
}
