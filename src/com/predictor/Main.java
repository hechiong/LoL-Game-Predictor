package com.predictor;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.*;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import com.merakianalytics.orianna.types.core.staticdata.Champion;
import com.merakianalytics.orianna.types.core.staticdata.Patch;
import com.merakianalytics.orianna.types.core.summoner.Summoner;

import java.io.*;
import java.util.*;

import org.joda.time.DateTime;

public class Main {

    private static final ArrayList<String> alphanumericLowercasedChamps
            = new ArrayList<>();
    private static final ArrayList<String> champsArray = new ArrayList<>();
    private static final ArrayList<String> patchesPlayed = new ArrayList<>();
    private static final HashMap<Integer, String> champsMap = new HashMap<>();
    private static final int EARLIEST_SUBPATCH = 11;
    private static final int NUM_PLAYERS = 5;
    private static final Region NA = Region.NORTH_AMERICA;
    private static final Scanner keyboard = new Scanner(System.in);
    private static final String MATCH_HISTORY_SUFFIX = "MatchHistory.ser";
    private static final String WEIGHT_SUFFIX = "Weight.ser";
    private static final String[] roles = {"top", "jungle", "mid", "adc", "support"};

    private static ArrayList<Double> validationAccuracies = new ArrayList<>();
    private static ArrayList<Match> playedOutMatches = new ArrayList<>();
    private static DateTime earliestPatchStartTime;
    private static int lastSubpatchPlayed;
    private static int maxParticipantMatches;
    private static int numChamps;
    private static int numEpochs;
    private static int numMatchesToConsider;
    private static int numTrainMatches;
    private static LoLMatch[] shuffledProcessedMatches;
    private static LoLMatchHistory processedMatchHistory = null;
    private static LoLWeight weight = null;
    private static MatchHistory matchHistory = null;
    private static String currentSeason;
    private static String earliestPatch;
    private static Summoner summoner = null;

    // The user inputs commands and/or arguments to interact with the program.
    public static void main(String[] args) {
        Orianna.setRiotAPIKey(System.getenv("RIOT_API_KEY"));
        Orianna.setDefaultRegion(NA);

        String latestPatch = Patch.get().getName();
        currentSeason = latestPatch.split("\\.")[0];
        earliestPatch = currentSeason + "." + EARLIEST_SUBPATCH;
        earliestPatchStartTime = Patch.named(earliestPatch).get().getStartTime();

        for (final Champion champion : Orianna.getChampions()) {
            champsArray.add(champion.getName());
            champsMap.put(champion.getId(), champion.getName());
        }
        champsArray.sort(Comparator.naturalOrder());
        numChamps = champsArray.size();

        for (String champ : champsArray) {
            alphanumericLowercasedChamps.add(toAlphanumericLowercase(champ));
        }

        System.out.print("Command: ");
        String[] inputs = keyboard.nextLine().toLowerCase().split(" ", 2);
        String command = "", arg = "";

        switch (inputs.length) {
            case 2:
                arg = inputs[1];
            case 1:
                command = inputs[0];
        }

        while (!command.equals("quit")) {
            double duration;
            long endTime;
            long startTime = System.currentTimeMillis();

            try {
                if (command.equals("champions")) {
                    displayChampsInfo();
                } else if (command.equals("help")) {
                    System.out.println("Valid commands are champions, help, "
                            + "load, match (history), predict, process, save, "
                            + "set, summoner, train, trend, and weight.");
                } else if (command.equals("load")) {
                    if (arg.isBlank()) {
                        System.out.print("Which to load ('summoner' or "
                                + "'weight')? ");
                        arg = keyboard.nextLine().toLowerCase();
                    }

                    if (arg.equals("summoner")) {
                        loadSummoner();
                    } else if (arg.equals("weight")) {
                        loadWeight();
                    } else {
                        System.out.println("Only a summoner or weight can be "
                                + " loaded.");
                    }
                } else if (command.equals("match")) {
                    if (arg.isBlank()) {
                        displayMatch();
                    } else if (arg.equals("history")) {
                        displayMatchHistoryInfo();
                    } else {
                        System.out.println("Only a match or match history can "
                                + "be displayed.");
                    }
                } else if (command.equals("predict")) {
                    predict();
                } else if (command.equals("process")) {
                    processMatches();
                } else if (command.equals("save")) {
                    if (arg.isBlank()) {
                        System.out.print("Which to save ('match history' or "
                                + "'weight')? ");
                        arg = keyboard.nextLine().toLowerCase();
                    }

                    if (arg.equals("match history")) {
                        saveMatchHistory();
                    } else if (arg.equals("weight")) {
                        saveWeight();
                    } else {
                        System.out.println("Only a match history or weight can"
                                + " be saved.");
                    }
                } else if (command.equals("set")) {
                    if (arg.isBlank()) {
                        System.out.print("Which to set ('weight')? ");
                        arg = keyboard.nextLine().toLowerCase();
                    }

                    if (arg.equals("weight")) {
                        setWeightElement();
                    } else {
                        System.out.println("Only a weight can be set.");
                    }
                } else if (command.equals("summoner")) {
                    displaySummonerInfo();
                } else if (command.equals("train")) {
                    train();
                } else if (command.equals("trend")) {
                    for (int i = 0; i < validationAccuracies.size(); i++) {
                        System.out.println("Epoch " + i + ": " + validationAccuracies.get(i));
                    }
                } else if (command.equals("weight")) {
                    displayWeightInfo();
                } else {
                    System.out.println(command + " is an invalid command.");
                }
            } catch (FnException | NullSummonerException | NullWeightException e) {
                System.out.println(e.getMessage());
            }

            endTime = System.currentTimeMillis();
            duration = (endTime - startTime) / 1000.0;
            System.out.println("Command execution time: " + duration + " s");

            System.out.print("Command: ");
            inputs = keyboard.nextLine().toLowerCase().split(" ", 2);
            command = arg = "";

            switch (inputs.length) {
                case 2:
                    arg = inputs[1];
                case 1:
                    command = inputs[0];
            }
        }

        keyboard.close();
    }

    // Throws an error if there's no summoner loaded.
    private static void checkIfNullSummoner() throws NullSummonerException {
        if (summoner == null) {
            throw new NullSummonerException();
        }
    }

    // Throws an error if the weight is null.
    private static void checkIfNullWeight() throws NullWeightException {
        if (weight == null) {
            throw new NullWeightException();
        }
    }

    // Prints the names of all champions and the total number of them.
    private static void displayChampsInfo() {
        final int numCols = 5;
        int longestLength = 0;

        for (String champ: champsArray) {
            if (champ.length() > longestLength) {
                longestLength = champ.length();
            }
        }

        for (int i = 0; i < numChamps; i += numCols) {
            for (int j = 0; j < numCols && i + j < numChamps; j++) {
                System.out.printf("%3d. %-" + longestLength + "s   ",
                        i + j + 1, champsArray.get(i + j));
            }
            System.out.printf("%n");
        }

        System.out.println("Total number of champions: " + numChamps);
    }

    // Prints a match from the summoner's match history if it's not empty.
    private static void displayMatch() throws NullSummonerException {
        int index = -1;
        int pmhSize;

        checkIfNullSummoner();

        pmhSize = processedMatchHistory.size();
        if (pmhSize != 0) {
            while (index < 0 || index >= pmhSize) {
                try {
                    System.out.print("Index (0-" + (pmhSize - 1) + "): ");
                    index = Integer.parseInt(keyboard.nextLine());

                    if (index < 0 || index >= pmhSize) {
                        System.out.println("Chosen number is an invalid "
                                + "index.");
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Enter an integer for the index.");
                }
            }
            System.out.println(processedMatchHistory.get(index));
        } else {
            System.out.println("There are no processed matches to peek.");
        }
    }

    // Prints information about the summoner's match history.
    private static void displayMatchHistoryInfo() throws NullSummonerException {
        String userInput;

        checkIfNullSummoner();

        System.out.println(processedMatchHistory);
        System.out.print("Want to peek a match ('yes' or 'no')? ");
        userInput = keyboard.nextLine().toLowerCase();
        while (!userInput.equals("yes") && !userInput.equals("no")) {
            System.out.print("Choose 'yes' or 'no' to peek a "
                    + "processed match: ");
            userInput = keyboard.nextLine().toLowerCase();
        }

        if (userInput.equals("yes")) {
            displayMatch();
        }
    }

    // Prints information about the summoner.
    private static void displaySummonerInfo() throws NullSummonerException {
        int champIndex;
        String champ = "";
        String name;
        String side = "";
        String userInput;

        if (summoner != null) {
            name = summoner.getName();

            System.out.println("Summoner ID: " + summoner.getId());
            System.out.println("Summoner account ID: "
                    + summoner.getAccountId());
            System.out.println("Summoner PUUID: " + summoner.getPuuid());
            System.out.println("Summoner name: " + name);
            System.out.println("Summoner level: " + summoner.getLevel());
            System.out.println("Summoner region: " + summoner.getRegion());
            System.out.println("Mastery score: "
                    + summoner.getChampionMasteryScore().getScore());

            System.out.print("Want to see " + name + "'s win rate on a "
                    + "champion ('yes' or 'no')? ");
            userInput = keyboard.nextLine().toLowerCase();
            while (!userInput.equals("yes") && !userInput.equals("no")) {
                System.out.print("Choose 'yes' or 'no' to see " + name + "'s"
                        + " win rate on a specific champion: ");
                userInput = keyboard.nextLine().toLowerCase();
            }

            if (userInput.equals("yes")) {
                while (!alphanumericLowercasedChamps.contains(champ)) {
                    System.out.print("Champion: ");
                    champ = toAlphanumericLowercase(keyboard.nextLine());

                    if (!alphanumericLowercasedChamps.contains(champ)) {
                        System.out.println(champ + " is an invalid champion.");
                    }
                }
                champIndex = alphanumericLowercasedChamps.indexOf(champ);
                champ = champsArray.get(champIndex);

                while (!side.equals("Blue") && !side.equals("Red")
                        && !side.equals("Either")) {
                    System.out.print("Side ('Blue', 'Red', or 'Either'): ");
                    side = toCapitalizedLowercase(keyboard.nextLine());

                    if (!side.equals("Blue") && !side.equals("Red")
                            && !side.equals("Either")) {
                        System.out.println(side + " is an invalid side.");
                    }
                }

                System.out.println(
                        getLoadedSummonerChampWinRate(champ, side, null));
            }
        } else {
            System.out.println("No summoner is loaded.");
        }
    }

    // Prints information about the weight.
    private static void displayWeightInfo() {
        if (weight != null) {
            System.out.println(weight);
        } else {
            System.out.println("Weight is null.");
        }
    }

    // Extracts matches that weren't remakes from the
    // summoner's match history, and prints the number of
    // matches that were played out and remade respectively.
    private static void extractMatches() throws NullSummonerException {
        int numRemakes = 0;

        checkIfNullSummoner();

        playedOutMatches = new ArrayList<>();
        for (Match match : matchHistory) {
            if (!match.isRemake()) {
                playedOutMatches.add(match);
            } else {
                numRemakes += 1;
            }
        }

        System.out.println(playedOutMatches.size() + " matches were extracted,"
                + " and " + numRemakes + " remakes were found.");
    }

    // Returns the loaded summoner's win rate on a champion on some
    // side coming into the match given their match history, or returns
    // -1 if the match isn't null but not in their match history.
    private static double getLoadedSummonerChampWinRate(
            String champion, String side, Match match)
            throws NullSummonerException {
        boolean isFinished = false;
        boolean isOnCorrectSide = true;
        int games = 0;
        int wins = 0;
        LoLMatch lolMatch;
        long matchId = 0;
        String champ;

        checkIfNullSummoner();

        if (match != null) {
            matchId = match.getId();

            if (!processedMatchHistory.contains(match)) {
                System.out.println("Match " + matchId + " isn't in "
                        + summoner.getName() + "'s match history, so this "
                        + "match can't be used to determine their win rate on "
                        + champion + ".");
                return -1;
            }
        }

        for (int i = 0; i < processedMatchHistory.size() && !isFinished; i++) {
            lolMatch = processedMatchHistory.get(i);
            champ = lolMatch.getAllyChamp(lolMatch.getPlayerIndex());

            if (side.equals("Blue")) {
                isOnCorrectSide = lolMatch.isAllyBlueSide();
            } else if (side.equals("Red")) {
                isOnCorrectSide = !lolMatch.isAllyBlueSide();
            }

            if (lolMatch.getMatchId() == matchId) {
                isFinished = true;
            } else {
                if (champ.equals(champion) && isOnCorrectSide) {
                    if (lolMatch.isAllyWinner()) {
                        wins += 1;
                    }
                    games += 1;
                }
            }
        }

        if (games == 0) {
            return 0.5;     // Can return 0 or 0.5
        }
        return (double) wins / games;
    }

    // Returns the summoner's win rate on a champion on
    // some side coming into the match, or returns -1 if
    // the match isn't null but not in their match history.
    private static double getSummonerChampWinRate(
            Summoner s, String champion, String side, Match match)
            throws NullSummonerException {
        boolean isOnCorrectSide = true;
        boolean peekAll = match == null || maxParticipantMatches == 0;
        Champion c;
        DateTime dt = null;
        int games = 0;
        int lastIndex;
        int startIndex = 0;
        int wins = 0;
        MatchHistory mh;
        String champPlayed;

        checkIfNullSummoner();

        if (!s.exists()) {
            System.out.println("Summoner " + s.getName() + " doesn't exist (in"
                    + " NA), so their win rate on " + champion + " can't be " +
                    "determined.");
            return -1;
        } else if (s.equals(summoner)) {
            return getLoadedSummonerChampWinRate(champion, side, match);
        }

        // Logic for summoners who played with or against the loaded summoner
        mh = s.matchHistory()
                .withQueues(Queue.RANKED_SOLO)
                .withStartTime(earliestPatchStartTime).get();

        if (match != null && !mh.contains(match)) {
            System.out.println("Summoner " + s.getName() + " didn't play in "
                    + "match " + match.getId() + ", so this match can't be "
                    + "used to determine their win rate on " + champion + ".");
            return -1;
        } else {
            mh = s.matchHistory()
                    .withQueues(Queue.RANKED_SOLO)
                    .withStartTime(earliestPatchStartTime)
                    .withChampions(Champion.named(champion).get()).get();

            if (match != null) {
                c = match.getParticipants().find(s).getChampion();
                champPlayed = champsMap.get(c.getId());

                if (champPlayed.equals(champion)) {
                    startIndex = mh.indexOf(match) + 1;
                } else {
                    dt = match.getCreationTime();
                }
            }
        }

        if (peekAll) {
            lastIndex = mh.size();
        } else {
            lastIndex = maxParticipantMatches + startIndex;
        }

        for (int i = startIndex; i < lastIndex; i++) {
            Match m = mh.get(i);
            Participant p = m.getParticipants().find(s);
            Team t = p.getTeam();

            if (side.equals("Blue")) {
                isOnCorrectSide = t.getSide().equals(Side.BLUE);
            } else if (side.equals("Red")) {
                isOnCorrectSide = t.getSide().equals(Side.RED);
            }

            if (dt == null || dt.compareTo(m.getCreationTime()) > 0) {
                if (!m.isRemake() && isOnCorrectSide) {
                    System.out.print("Did " + s.getName()  + " win as "
                            + champion + " in match " + m.getId() + " on "
                            + side + " side? ");
                    if (t.isWinner()) {
                        wins++;
                        System.out.println("yes");
                    } else {
                        System.out.println("no");
                    }
                    games++;
                }
            }
        }

        if (games == 0) {
            return 0.5;     // Can return 0 or 0.5
        }
        return (double) wins / games;
    }

    // Initializes a new weight and list of validation accuracies.
    private static void initWeight()
            throws FnException, NullWeightException {
        String featType;
        int teamType;
        validationAccuracies = new ArrayList<>();

        System.out.print("Features to consider (champion win rates ('cwr'), "
                + "team composition ('tc'), or 'both'): ");
        featType = keyboard.nextLine().toLowerCase();
        while (!featType.equals("cwr") && !featType.equals("tc")
                && !featType.equals("both")) {
            System.out.print("Choose 'cwr', 'tc', or 'both' for features to "
                    + "consider: ");
            featType = keyboard.nextLine().toLowerCase();
        }

        System.out.print("How should this weight consider teams (enter an "
                + "integer between 1-5 where 1 considers teams solely by "
                + "alliance, 5 considers teams solely by color, and any "
                + "integer in between uses a mix of the two)? ");
        teamType = Integer.parseInt(keyboard.nextLine());
        while (!LoLWeight.isValidTeamType(teamType)) {
            System.out.print("Choose any integer between 1-5 to consider teams"
                    + " in a certain way: ");
            teamType = Integer.parseInt(keyboard.nextLine());
        }

        if (featType.equals("cwr")) {
            weight = new LoLWeight(LoLWeight.FeaturesType.CWR, teamType);
        } else if (featType.equals("tc")) {
            weight = new LoLWeight(LoLWeight.FeaturesType.TC, teamType);
        } else {
            weight = new LoLWeight(LoLWeight.FeaturesType.CWRTC, teamType);
        }

        setUpModel();
    }

    // Loads the summoner's processed match history if it exists.
    private static void loadMatchHistory() throws NullSummonerException {
        String mhFileName, playerPuuid;

        checkIfNullSummoner();

        mhFileName = summoner.getName() + MATCH_HISTORY_SUFFIX;
        playerPuuid  = summoner.getPuuid();

        try {
            // Deserialization
            // Reading the object from a file
            FileInputStream inMHFile = new FileInputStream(mhFileName);
            ObjectInputStream inMH = new ObjectInputStream(inMHFile);

            // Method for deserialization of the object
            processedMatchHistory = (LoLMatchHistory) inMH.readObject();

            if (playerPuuid.equals(processedMatchHistory.getPlayerPuuid())) {
                processedMatchHistory.resetIfOldMatchesExist();
            } else {
                processedMatchHistory = new LoLMatchHistory(playerPuuid);
            }

            inMH.close();
            inMHFile.close();
        } catch(IOException ex) {
            processedMatchHistory = new LoLMatchHistory(playerPuuid);
            System.out.println("No processed match history exists for "
                    + summoner.getName() + ".");
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFoundException is caught.");
        }
    }

    // Loads an existing summoner (in the NA region) and their match histories.
    private static void loadSummoner() throws NullSummonerException {
        String player;

        System.out.print("Summoner (in NA): ");
        player = keyboard.nextLine();
        summoner = Orianna.summonerNamed(player).withRegion(NA).get();

        while (!summoner.exists()) {
            System.out.println("Summoner " + player + " doesn't exist (in "
                    + "NA).");
            System.out.print("Choose an existing NA summoner: ");
            player = keyboard.nextLine();
            summoner = Orianna.summonerNamed(player).withRegion(NA).get();
        }

        matchHistory = summoner.matchHistory()
                .withQueues(Queue.RANKED_SOLO)
                .withStartTime(earliestPatchStartTime).get();
        extractMatches();

        if (playedOutMatches.isEmpty()) {
            System.out.println("This summoner can't be used because they "
                    + "haven't played any ranked solo/duo matches in season "
                    + currentSeason + ".");
            summoner = null;
            matchHistory = null;
        } else {
            loadMatchHistory();
        }
    }

    // Loads a weight and new list of validation
    // accuracies if the weight exists.
    private static void loadWeight() {
        String weightName, weightFileName;

        System.out.print("Weight name: ");
        weightName = keyboard.nextLine();
        weightFileName = weightName + WEIGHT_SUFFIX;

        try {
            // Deserialization
            // Reading the object from a file
            FileInputStream inWeightFile = new FileInputStream(weightFileName);
            ObjectInputStream inWeight = new ObjectInputStream(inWeightFile);

            // Method for deserialization of object
            weight = (LoLWeight) inWeight.readObject();
            weight.update();
            validationAccuracies = new ArrayList<>();

            inWeight.close();
            inWeightFile.close();
        } catch(IOException ex) {
            System.out.println("No weight with the name '" + weightName
                    + "' exists.");
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFoundException is caught.");
        }
    }

    // Computes a prediction on a side or a team
    // winning a game using the current weight.
    private static void predict() throws NullWeightException {
        Vec gameSample;
        double prediction;
        checkIfNullWeight();

        gameSample = new Vec(weight.length());

        queryTeam("Ally", gameSample);
        queryTeam("Enemy", gameSample);
        gameSample.set(weight.length() - 1, 1);

        prediction = weight.predict(gameSample);

        if (weight.getTeamType() != 5) {
            System.out.println("Prediction score for ally team winning: "
                    + prediction);
        } else {
            System.out.println("Prediction score for blue side winning: "
                    + prediction);
        }
    }

    // Collects information from every match in the current season of
    // the loaded summoner's match history, including the match ID,
    // patch in which the match was played, players' PUUID's, players'
    // champions, players' win rates on their respective champions, summoner's
    // side in the match, and match's outcome relative to the summoner.
    private static void processMatches() throws NullSummonerException {
        final long timeLimit = 5000;
        String summonerPuuid, userInput = "";
        double time;
        int pmhSize;
        int startIndex;
        long t0, t1;

        checkIfNullSummoner();

        pmhSize = processedMatchHistory.size();
        startIndex = playedOutMatches.size() - 1 - pmhSize;
        summonerPuuid = summoner.getPuuid();

        for (int i = startIndex; i >= 0; i--) {
            boolean complete = false;

            while (!complete) {
                try {
                    t0 = System.currentTimeMillis();

                    Match match = playedOutMatches.get(i);
                    String[] splitVersion = match.getVersion().split("\\.", 3);
                    String patch = splitVersion[0] + "." + splitVersion[1];
                    Team summonerTeam = match.getParticipants().find(summoner).getTeam();
                    boolean isBlueSide = summonerTeam.getSide() == Side.BLUE;
                    boolean isWinner = summonerTeam.isWinner();
                    long matchId = match.getId();
                    LoLMatch lolMatch = new LoLMatch(
                            matchId, patch, summonerPuuid, isBlueSide, isWinner);

                    for (Participant p : match.getParticipants()) {
                        Summoner s = p.getSummoner();
                        String puuid = s.getPuuid();
                        int champId = p.getChampion().getId();
                        String champ = champsMap.get(champId);
                        double champWinRate;

                        System.out.println("For summoner " + s.getName()
                                + " in match " + (pmhSize + 1) + ":");
                        champWinRate = getSummonerChampWinRate(
                                s, champ, "either", match);
                        System.out.println(s.getName() + "'s win rate on "
                                + champ + ": " + champWinRate);

                        if (summonerTeam.equals(p.getTeam())) {
                            lolMatch.addAlly(puuid, champ, champWinRate);
                        } else {
                            lolMatch.addEnemy(puuid, champ, champWinRate);
                        }
                    }
                    processedMatchHistory.add(lolMatch);

                    t1 = System.currentTimeMillis();
                    time = (t1 - t0) / 60000.0;
                    System.out.println("Match " + ++pmhSize + " took " + time
                            + " minutes to process.");

                    System.out.println(pmhSize + " matches have been "
                            + "processed.");
                    saveMatchHistory();
                    complete = true;
                } catch(NullPointerException e) {
                    System.out.println("Match " + (pmhSize + 1) + " is being "
                            + "processed again.");
                }
            }

            System.out.print("Want to process another match ('yes' or "
                    + "'no')? ");
            t0 = System.currentTimeMillis();
            t1 = System.currentTimeMillis();

            try {
                while (!userInput.equals("yes") && !userInput.equals("no")
                        && t1 - t0 < timeLimit) {
                    if (System.in.available() > 0) {
                        userInput = keyboard.nextLine().toLowerCase();

                        if (!userInput.equals("yes")
                                && !userInput.equals("no")) {
                            System.out.print("Choose 'yes' or 'no' to process "
                                    + "another match: ");
                        }
                    }

                    t1 = System.currentTimeMillis();
                }

                if (!userInput.equals("yes") && !userInput.equals("no")) {
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("\nIOException is caught.");
                System.out.println("Scanner must be open to wait for user "
                        + "input.");
            }
        }
    }

    // Queries certain information depending on the current weight about
    // some team in a game and sets up the data sample for that game.
    private static void queryTeam(String team, Vec gameSample)
            throws NullWeightException {
        assert team.equals("Ally") || team.equals("Enemy"): team + " team is "
                + "invalid.";

        String playerChamp = "", side = "";
        double featureValue = -1;
        int champIndex = -1;
        checkIfNullWeight();

        if (weight.getTeamType() != 1) {
            while (!side.equals("Blue") && !side.equals("Red")) {
                System.out.print(team + " team's side ('Blue'/'Red'): ");
                side = toCapitalizedLowercase(keyboard.nextLine());

                if (!side.equals("Blue") && !side.equals("Red")) {
                    System.out.println(side + " is an invalid side.");
                }
            }
        }

        for (int i = 0; i < NUM_PLAYERS; i++) {
            while (champIndex < 0) {
                System.out.print(team + " " + roles[i] + "'s champion: ");
                playerChamp = toAlphanumericLowercase(keyboard.nextLine());

                for (int j = 0; j < numChamps; j++) {
                    String champ = toAlphanumericLowercase(champsArray.get(j));
                    if (champ.equals(playerChamp)) {
                        champIndex = j;
                    }
                }

                if (champIndex < 0) {
                    System.out.println(playerChamp + " is an invalid "
                            + "champion.");
                }
            }

            if (weight.getFeatType() == LoLWeight.FeaturesType.TC) {
                featureValue = 1;
            } else {
                while (featureValue < 0 || featureValue > 100) {
                    try {
                        System.out.print(team + " " + roles[i] + "'s "
                                + playerChamp + " win rate percentage "
                                + "(0-100): ");
                        featureValue = Double.parseDouble(keyboard.nextLine()
                                .replaceFirst("%", ""));

                        if (featureValue < 0 || featureValue > 100) {
                            System.out.println("Win rate must be between 0 and"
                                    + " 100.");
                        }
                    } catch(NumberFormatException e) {
                        System.out.println("Enter a numerical win rate with an"
                                + " optional '%'.");
                    }
                }
                featureValue /= 100;
            }

            if ((weight.getTeamType() <= 3 && team.equals("Ally"))
                    || (weight.getTeamType() > 3 && side.equals("Blue"))) {
                gameSample.set(champIndex, featureValue);
                if (weight.getFeatType() == LoLWeight.FeaturesType.CWRTC) {
                    gameSample.set(champIndex + numChamps, 1);
                }
            } else {
                if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                    gameSample.set(champIndex + numChamps, featureValue);
                } else {
                    gameSample.set(champIndex + numChamps * 2, featureValue);
                    gameSample.set(champIndex + numChamps * 3, 1);
                }
            }

            if ((weight.getTeamType() == 2 || weight.getTeamType() == 4)
                    && team.equals("Ally")) {
                if (side.equals("Blue")) {
                    gameSample.set(weight.length() - 3, 1);
                } else {
                    gameSample.set(weight.length() - 2, 1);
                }
            }

            if (weight.getTeamType() == 3) {
                if (side.equals("Blue")) {
                    if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                        gameSample.set(champIndex + numChamps * 2, featureValue);
                    } else {
                        gameSample.set(champIndex + numChamps * 4, featureValue);
                        gameSample.set(champIndex + numChamps * 5, 1);
                    }
                } else {
                    if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                        gameSample.set(champIndex + numChamps * 3, featureValue);
                    } else {
                        gameSample.set(champIndex + numChamps * 6, featureValue);
                        gameSample.set(champIndex + numChamps * 7, 1);
                    }
                }
            }

            champIndex = -1;
            featureValue = -1;
        }
    }

    // Saves the processed matches of the loaded summoner to a file.
    private static void saveMatchHistory() throws NullSummonerException {
        FileOutputStream outMatchesFile;
        ObjectOutputStream outMatches;
        String matchesFileName;

        checkIfNullSummoner();

        matchesFileName = summoner.getName() + MATCH_HISTORY_SUFFIX;

        try {
            // Serialization
            // Saving of object in a file
            outMatchesFile = new FileOutputStream(matchesFileName);
            outMatches = new ObjectOutputStream(outMatchesFile);

            // Method for serialization of object
            outMatches.writeObject(processedMatchHistory);

            outMatches.close();
            outMatchesFile.close();
        } catch(IOException ex) {
            System.out.println("IOException is caught.");
        }
    }

    // Saves the current weight to either an existing file or a new file.
    private static void saveWeight() throws NullWeightException {
        File tempWeightFile;
        String tempFileName, tempName, userInput;
        boolean canOverwrite = true, saveAsNewWeight = true;
        checkIfNullWeight();

        System.out.print("Want to save to the 'current' weight or a 'new' "
                + "weight? ");
        userInput = keyboard.nextLine().toLowerCase();

        while (!userInput.equals("current") && !userInput.equals("new")) {
            System.out.print("Choose 'current' or 'new' to specify which "
                    + "weight to which to save: ");
            userInput = keyboard.nextLine().toLowerCase();
        }

        if (userInput.equals("current")) {
            saveAsNewWeight = false;
        }

        if (saveAsNewWeight || weight.getName().isBlank()) {
            System.out.print("What name to give the weight? ");
            tempName = keyboard.nextLine();
            tempFileName = tempName + WEIGHT_SUFFIX;
            tempWeightFile = new File(tempFileName);

            if (tempWeightFile.exists()) {
                System.out.print(tempFileName + " already exists. Want to "
                        + "overwrite the weight ('yes' or 'no')? ");
                userInput = keyboard.nextLine().toLowerCase();

                while (!userInput.equals("yes") && !userInput.equals("no")) {
                    System.out.print("Choose 'yes' or 'no' to overwrite the "
                            + "existing weight: ");
                    userInput = keyboard.nextLine().toLowerCase();
                }

                if (userInput.equals("yes")) {
                    weight.setName(tempName);
                } else {
                    canOverwrite = false;
                }
            } else {
                weight.setName(tempName);
            }
        }

        if (canOverwrite) {
            serializeWeight();
        }
    }

    // Saves the current weight to a file.
    private static void serializeWeight() throws NullWeightException {
        FileOutputStream outWeightFile;
        ObjectOutputStream outWeight;
        String weightFileName;
        checkIfNullWeight();

        weightFileName = weight.getName() + WEIGHT_SUFFIX;

        try {
            // Serialization
            // Saving of object in a file
            outWeightFile = new FileOutputStream(weightFileName);
            outWeight = new ObjectOutputStream(outWeightFile);

            // Method for serialization of object
            outWeight.writeObject(weight);

            outWeight.close();
            outWeightFile.close();
        } catch(IOException ex) {
            System.out.println("IOException is caught.");
        }
    }

    // Sets up the weight's activation function
    // if its model is a neural network.
    private static void setUpActivationFunction()
            throws FnException, NullWeightException {
        String userInput;
        checkIfNullWeight();

        if (weight.getModel() == Weight.Model.NN) {
            System.out.print("Activation function ('leaky relu', 'relu', "
                    + "'sigmoid', or 'tanh'): ");
            userInput = keyboard.nextLine().toLowerCase();

            while (!ActFn.isValidActFn(userInput)) {
                System.out.print("Only valid activation functions are "
                        + "'identity', 'leaky relu', 'relu', 'sigmoid', and "
                        + "'tanh': ");
                userInput = keyboard.nextLine().toLowerCase();
            }

            //weight.setActFn(new ActFn(userInput));
        }
    }

    // Sets up the batch size that'll be used to train the
    // weight based on the chosen type of gradient descent.
    private static void setUpBatchSize() throws NullWeightException {
        String userInput;
        int batchSize;
        checkIfNullWeight();

        System.out.print("Type of gradient descent ('batch', 'mini-batch', or "
                + "'stochastic'): ");
        userInput = keyboard.nextLine().toLowerCase();
        while (!userInput.equals("batch") && !userInput.equals("mini-batch")
                && !userInput.equals("stochastic")) {
            System.out.print("Choose 'batch', 'mini-batch', or 'stochastic' "
                    + "gradient descent: ");
            userInput = keyboard.nextLine().toLowerCase();
        }

        if (userInput.equals("batch")) {
            weight.setBatchSize(0);
        } else if (userInput.equals("mini-batch")) {
            batchSize = 0;
            if (weight.getBatchSize() > 0) {
                System.out.print("Want to use the current batch size of "
                        + weight.getBatchSize() + " ('yes' or 'no')? ");
                userInput = keyboard.nextLine().toLowerCase();

                while (!userInput.equals("yes") && !userInput.equals("no")) {
                    System.out.print("Choose 'yes' or 'no' to use a batch size"
                            + " of " + weight.getBatchSize() + ": ");
                    userInput = keyboard.nextLine().toLowerCase();
                }

                if (userInput.equals("yes")) {
                    batchSize = weight.getBatchSize();
                }
            }

            while (batchSize <= 0) {
                try {
                    System.out.print("Batch size: ");
                    batchSize = Integer.parseInt(keyboard.nextLine());
                    if (batchSize <= 0) {
                        System.out.println("Pick a positive batch size.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter an integer for the batch size.");
                }
            }
            weight.setBatchSize(batchSize);
        } else {
            weight.setBatchSize(1);
        }
    }

    // Sets up the number of epochs.
    private static void setUpEpochs() {
        numEpochs = 0;

        while (numEpochs <= 0) {
            try {
                System.out.print("Number of epochs: ");
                numEpochs = Integer.parseInt(keyboard.nextLine());
                if (numEpochs <= 0) {
                    System.out.print("Pick a positive number of epochs.");
                }
            } catch(NumberFormatException e) {
                System.out.println("Enter an integer for the number of "
                        + "epochs.");
            }
        }
    }

    // Sets up the size of the weight's hidden
    // layer if its model is a neural network.
    private static void setUpHiddenLayerSize() throws NullWeightException {
        int h = 0;
        checkIfNullWeight();

        if (weight.getModel() == Weight.Model.NN) {
            while (h <= 0) {
                try {
                    System.out.print("Hidden layer size: ");
                    h = Integer.parseInt(keyboard.nextLine());
                    if (h <= 0) {
                        System.out.println("Pick a positive hidden layer "
                                + "size.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter an integer for the size of the "
                            + "hidden layer.");
                }
            }
            weight.setHiddenLayerSize(h);
        }
    }

    // Sets up the weight's learning rate.
    private static void setUpLearningRate() throws NullWeightException {
        String userInput;
        double learningRate = 0;
        checkIfNullWeight();

        if (weight.getLearningRate() > 0) {
            System.out.print("Want to use the current learning rate of "
                    + weight.getLearningRate() + " ('yes' or 'no')? ");
            userInput = keyboard.nextLine().toLowerCase();

            while (!userInput.equals("yes") && !userInput.equals("no")) {
                System.out.print("Choose 'yes' or 'no' to use a learning rate "
                        + "of " + weight.getLearningRate() + ": ");
                userInput = keyboard.nextLine().toLowerCase();
            }

            if (userInput.equals("yes")) {
                learningRate = weight.getLearningRate();
            }
        }

        while (learningRate <= 0) {
            try {
                System.out.print("Learning rate: ");
                learningRate = Double.parseDouble(keyboard.nextLine());
                if (learningRate <= 0) {
                    System.out.println("Pick a positive learning rate.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a real number for the learning "
                        + "rate.");
            }
        }
        weight.setLearningRate(learningRate);
    }

    // Sets up the weight's loss function.
    private static void setUpLossFunction()
            throws FnException, NullWeightException {
        String userInput;
        checkIfNullWeight();

        System.out.print("Loss function ('logistic' or 'squared'): ");
        userInput = keyboard.nextLine().toLowerCase();

        while (!LossFn.isValidLossFn(userInput)) {
            System.out.print("Only valid loss functions are 'logistic' "
                        + "and 'squared': ");
            userInput = keyboard.nextLine().toLowerCase();
        }

        //weight.setLossFn(new LossFn(userInput));
    }

    // Sets up the maximum number of matches to load from the match
    // histories of summoners who played with or against the loaded summoner.
    private static void setUpMaxParticipantMatches() {
        maxParticipantMatches = -1;

        while (maxParticipantMatches < 0) {
            try {
                System.out.print("Maximum number of matches to peek from other"
                        + " players' match histories (0 to peek all "
                        + "matches): ");
                maxParticipantMatches = Integer.parseInt(keyboard.nextLine());
                if (maxParticipantMatches < 0) {
                    System.out.println("Pick a non-negative maximum number of "
                            + "matches.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer for the maximum number of"
                        + " matches to peek.");
            }
        }
    }

    // Sets up the model that'll be used with the weight.
    private static void setUpModel()
            throws FnException, NullWeightException {
        String userInput;
        checkIfNullWeight();

        System.out.print("Model (binary perceptron ('bp'), logistic regression"
                + " ('lr'), or neural network ('nn')): ");
        userInput = keyboard.nextLine().toLowerCase();

        while (!userInput.equals("bp") && !userInput.equals("lr")
                && !userInput.equals("nn")) {
            System.out.print("Choose 'bp', 'lr', or 'nn' model: ");
            userInput = keyboard.nextLine().toLowerCase();
        }

        if (userInput.equals("bp")) {
            weight.setModel(Weight.Model.BP);
            setUpLossFunction();
        } else if (userInput.equals("lr")) {
            weight.setModel(Weight.Model.LR);
            //weight.setLossFn(new LossFn("logistic"));
        } else {
            weight.setModel(Weight.Model.NN);
            setUpActivationFunction();
            setUpHiddenLayerSize();
            setUpLossFunction();
            weight.initNeuralNetwork();
        }
    }

    // Sets up the number of matches to consider for learning.
    private static void setUpNumMatchesToConsider()
            throws NullSummonerException {
        String userInput;
        int pmhSize;
        numMatchesToConsider = 0;

        checkIfNullSummoner();

        pmhSize = processedMatchHistory.size();

        System.out.print("Want to learn from 'all' matches, 'some' number of "
                + "recent matches, or matches after a specific 'patch'? ");
        userInput = keyboard.nextLine().toLowerCase();

        while (!userInput.equals("all") && !userInput.equals("some")
                && !userInput.equals("patch")) {
            System.out.print("Choose 'all', 'some', or 'patch' to specify "
                    + "matches from which to learn: ");
            userInput = keyboard.nextLine().toLowerCase();
        }

        if (userInput.equals("all")) {
            numMatchesToConsider = pmhSize;
        } else if (userInput.equals("some")) {
            while (numMatchesToConsider <= 0
                    || numMatchesToConsider > pmhSize) {
                try {
                    System.out.print("How many matches to consider (1-"
                            + pmhSize + ")? ");
                    numMatchesToConsider = Integer.parseInt(keyboard.nextLine());

                    if (numMatchesToConsider <= 0
                            || numMatchesToConsider > pmhSize) {
                        System.out.println("Number of matches to consider must"
                                + " be between 1 and " + pmhSize + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter an integer for the number of "
                            + "matches to consider.");
                }
            }
        } else {
            int inputSubpatch;

            System.out.print("Which patch to start learning (" + earliestPatch
                    + "-." + lastSubpatchPlayed + ")? ");
            userInput = keyboard.nextLine();

            while (!patchesPlayed.contains(userInput)) {
                System.out.print("Specify the patch in the form "
                        + currentSeason + ".(integer between "
                        + EARLIEST_SUBPATCH + " and " + lastSubpatchPlayed
                        + "): ");
                userInput = keyboard.nextLine();
            }

            inputSubpatch = Integer.parseInt(
                    userInput.substring(userInput.indexOf('.') + 1));
            for (int i = 0; i < pmhSize && numMatchesToConsider <= 0; i++) {
                LoLMatch match = processedMatchHistory.get(i);
                String patch = match.getPatch();
                int subpatch = Integer.parseInt(
                        patch.substring(patch.indexOf('.') + 1));

                if (inputSubpatch >= subpatch) {
                    numMatchesToConsider = pmhSize - i;
                }
            }

            if (numMatchesToConsider <= 0) {
                numMatchesToConsider = pmhSize;
            }
        }
    }

    // Sets up the number of matches on which to train.
    private static void setUpNumTrainMatches() {
        numTrainMatches = -1;

        while (numTrainMatches < 0 || numTrainMatches > numMatchesToConsider) {
            try {
                System.out.print("How many matches on which to train (between "
                        + "0 and " + numMatchesToConsider + ")? ");
                numTrainMatches = Integer.parseInt(keyboard.nextLine());
                if (numTrainMatches < 0
                        || numTrainMatches > numMatchesToConsider) {
                    System.out.println("Chosen number isn't in the valid "
                            + "range.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer for the number of matches"
                        + " on which to train.");
            }
        }
    }

    // Sets up the summoner's match history of processed matches.
    private static void setUpProcessedMatches() throws NullSummonerException {
        String userInput;

        checkIfNullSummoner();

        if (processedMatchHistory.isEmpty()) {
            processMatches();
        } else {
            System.out.print("Want to process more matches before training the"
                    + " weight ('yes' or 'no')? ");
            userInput = keyboard.nextLine().toLowerCase();

            while (!userInput.equals("yes") && !userInput.equals("no")) {
                System.out.print("Choose 'yes' or 'no' to process more "
                        + "matches: ");
                userInput = keyboard.nextLine().toLowerCase();
            }

            if (userInput.equals("yes")) {
                processMatches();
            }
        }
    }

    // Sets up the weight.
    private static void setUpWeight()
            throws FnException, NullWeightException {
        String userInput;
        boolean useCurrent = true;

        if (weight != null) {
            System.out.print("Want to train the 'current' weight or a 'new' "
                    + "weight? ");
            userInput = keyboard.nextLine().toLowerCase();

            while (!userInput.equals("current") && !userInput.equals("new")) {
                System.out.print("Choose 'current' or 'new' to specify which "
                        + "weight to train: ");
                userInput = keyboard.nextLine().toLowerCase();
            }

            if (userInput.equals("new")) {
                useCurrent = false;
            }
        }

        if (!useCurrent) {
            initWeight();
        }
    }

    // Set an element of the weight to a specified value.
    private static void setWeightElement() throws NullWeightException {
        double value;
        int index;
        checkIfNullWeight();

        try {
            System.out.println("Type invalid values, such as a letter, to "
                    + "cancel setting a weight element.");

            System.out.print("Index at which to set (0-"
                    + (weight.length() - 1) + "): ");
            index = Integer.parseInt(keyboard.nextLine());
            while (index < 0 || index >= weight.length()) {
                System.out.println("Index must be between 0 and "
                        + (weight.length() - 1) + ": ");
                index = Integer.parseInt(keyboard.nextLine());
            }

            System.out.print("Value to set at index " + index + ": ");
            value = Double.parseDouble(keyboard.nextLine());
            weight.set(index, value);
        } catch(NumberFormatException e) {
            System.out.println("Setting weight element has been cancelled.");
        }
    }

    // Returns the String input in lowercase and
    // containing only alphanumeric characters.
    private static String toAlphanumericLowercase(String s) {
        return s.toLowerCase().replaceAll("[\\W_]", "");
    }

    // Returns the String input with its first character
    // in uppercase and the rest of it in lowercase.
    private static String toCapitalizedLowercase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // Trains the weight on a subset of the loaded summoner's processed
    // matches and prints the total loss and training accuracy for each epoch.
    private static void train()
            throws FnException, NullSummonerException, NullWeightException {
        LoLMatch lastProcessedMatch;
        String lastPatch, userInput = "yes";

        checkIfNullSummoner();

        setUpMaxParticipantMatches();
        setUpProcessedMatches();

        lastProcessedMatch = processedMatchHistory.get(
                processedMatchHistory.size() - 1);
        lastPatch = lastProcessedMatch.getPatch();
        lastSubpatchPlayed = Integer.parseInt(
                lastPatch.substring(lastPatch.indexOf('.') + 1));
        for (int i = EARLIEST_SUBPATCH; i <= lastSubpatchPlayed; i++) {
            patchesPlayed.add(currentSeason + "." + i);
        }

        setUpWeight();
        setUpBatchSize();
        setUpLearningRate();
        setUpNumMatchesToConsider();
        setUpNumTrainMatches();

        while (userInput.equals("yes")) {
            LoLWeight minLossWeight = weight.copy();
            LoLWeight prevWeight = weight.copy();
            double lastLoss = -1, lastTrainAcc = -1;
            double minLoss = Double.MAX_VALUE, minTrainAcc = -1;
            int minEpoch = 0;

            setUpEpochs();
            for (int j = 0; j < numEpochs; j++) {
                LoLMatch[] shuffledTrainSet = new LoLMatch[numTrainMatches];
                Vec[] gameSamples = new Vec[numTrainMatches];
                double[] outcomes = new double[numTrainMatches];
                double totalLoss = 0, trainAcc;
                int totalCorrect = 0, totalMatches = 0;

                shuffledProcessedMatches = processedMatchHistory.shuffle();
                for (int i = 0; i < numTrainMatches; i++) {
                    shuffledTrainSet[i] = shuffledProcessedMatches[i];
                }

                for (int i = 0; i < numTrainMatches; i++) {
                    LoLMatch match = shuffledTrainSet[i];
                    Vec gameSample = new Vec(weight.length());
                    double outcome = 1, prediction;

                    for (int k = 0; k < NUM_PLAYERS; k++) {
                        String allyChamp = match.getAllyChamp(k);
                        String enemyChamp = match.getEnemyChamp(k);
                        double allyChampWinRate = match.getAllyChampWinRate(k);
                        double allyFeatureValue = 1;
                        double enemyChampWinRate = match.getEnemyChampWinRate(k);
                        double enemyFeatureValue = 1;
                        int allyChampIndex = champsArray.indexOf(allyChamp);
                        int enemyChampIndex = champsArray.indexOf(enemyChamp);

                        if (weight.getFeatType() != LoLWeight.FeaturesType.TC) {
                            allyFeatureValue = allyChampWinRate;
                            enemyFeatureValue = enemyChampWinRate;
                        }

                        // set up game sample
                        if (weight.getTeamType() <= 3 ||
                                (weight.getTeamType() > 3 && match.isAllyBlueSide())) {
                            gameSample.set(allyChampIndex, allyFeatureValue);
                            if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                                gameSample.set(enemyChampIndex + numChamps, enemyFeatureValue);
                            } else {
                                gameSample.set(allyChampIndex + numChamps, 1);
                                gameSample.set(enemyChampIndex + numChamps * 2, enemyFeatureValue);
                                gameSample.set(enemyChampIndex + numChamps * 3, 1);
                            }
                        } else {
                            gameSample.set(enemyChampIndex, enemyFeatureValue);
                            if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                                gameSample.set(allyChampIndex + numChamps, allyFeatureValue);
                            } else {
                                gameSample.set(enemyChampIndex + numChamps, 1);
                                gameSample.set(allyChampIndex + numChamps * 2, allyFeatureValue);
                                gameSample.set(allyChampIndex + numChamps * 3, 1);
                            }
                        }

                        if (weight.getTeamType() == 2 || weight.getTeamType() == 4) {
                            if (match.isAllyBlueSide()) {
                                gameSample.set(weight.length() - 3, 1);
                            } else {
                                gameSample.set(weight.length() - 2, 1);
                            }
                        }

                        if (weight.getTeamType() == 3) {
                            if (match.isAllyBlueSide()) {
                                if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                                    gameSample.set(allyChampIndex + numChamps * 2, allyFeatureValue);
                                    gameSample.set(enemyChampIndex + numChamps * 3, enemyFeatureValue);
                                } else {
                                    gameSample.set(allyChampIndex + numChamps * 4, allyFeatureValue);
                                    gameSample.set(allyChampIndex + numChamps * 5, 1);
                                    gameSample.set(enemyChampIndex + numChamps * 6, enemyFeatureValue);
                                    gameSample.set(enemyChampIndex + numChamps * 7, 1);
                                }
                            } else {
                                if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                                    gameSample.set(enemyChampIndex + numChamps * 2, enemyFeatureValue);
                                    gameSample.set(allyChampIndex + numChamps * 3, allyFeatureValue);
                                } else {
                                    gameSample.set(enemyChampIndex + numChamps * 4, enemyFeatureValue);
                                    gameSample.set(enemyChampIndex + numChamps * 5, 1);
                                    gameSample.set(allyChampIndex + numChamps * 6, allyFeatureValue);
                                    gameSample.set(allyChampIndex + numChamps * 7, 1);
                                }
                            }
                        }
                    }
                    gameSample.set(weight.length() - 1, 1);
                    gameSamples[i] = gameSample;

                    // set up outcome
                    if (((weight.getTeamType() != 5 || match.isAllyBlueSide())
                            && !match.isAllyWinner()) || (!match.isAllyBlueSide()
                            && match.isAllyWinner())) {
                        if (weight.getModel() == Weight.Model.LR) {
                            outcome = 0;
                        } else {
                            outcome = -1;
                        }
                    }
                    outcomes[i] = outcome;

                    prediction = weight.predict(gameSample);
                    System.out.println("Outcome of " + match.getMatchId()
                            + ": " + outcome + ". Prediction score: "
                            + prediction + ".");

                    if (weight.getModel() == Weight.Model.LR) {
                        if ((outcome <= 0 && prediction <= 0.5)
                                || (outcome > 0 && prediction > 0.5)) {
                            totalCorrect++;
                        }
                    } else {
                        if ((outcome <= 0 && prediction <= 0)
                                || (outcome > 0 && prediction > 0)) {
                            totalCorrect++;
                        }
                    }

                    totalLoss += weight.lossFn(prediction, outcome);
                    totalMatches++;
                }

                weight.update(gameSamples, outcomes);

                if (totalLoss < minLoss) {
                    minEpoch = j + 1;
                    minLoss = totalLoss;
                    minLossWeight = weight.copy();
                }

                if (j == numEpochs - 1) {
                    lastLoss = totalLoss;
                }

                if (totalMatches != 0) {
                    trainAcc = (double) totalCorrect / totalMatches;
                    System.out.println("Epoch " + j + ": total loss = "
                            + totalLoss + ", training accuracy = " + totalCorrect
                            + "/" + totalMatches + " = " + trainAcc);
                }

                validate();
            }

            System.out.println("Epoch " + minEpoch + " produced the weight "
                    + "associated with the minimum loss of " + minLoss + " and"
                    + " a training accuracy of " + minTrainAcc + ".");
            System.out.println("The last epoch produced the weight with a loss"
                    + " of " + lastLoss + " and a training accuracy of "
                    + lastTrainAcc + ".");
            System.out.print("Which weight shall be kept ('last', 'min', 'prev')?");
            userInput = keyboard.nextLine().toLowerCase();
            while (!userInput.equals("last") && !userInput.equals("min")
                    && !userInput.equals("prev")) {
                System.out.print("Choose 'last', 'min', or 'prev' to pick "
                        + "the weight to keep: ");
                userInput = keyboard.nextLine().toLowerCase();
            }

            if (userInput.equals("min")) {
                weight = minLossWeight;
            } else if (userInput.equals("prev")) {
                weight = prevWeight;
            }

            System.out.print("Want to train again with the same settings "
                    + "('yes' or 'no')? ");
            userInput = keyboard.nextLine().toLowerCase();
            while (!userInput.equals("yes") && !userInput.equals("no")) {
                System.out.print("Choose 'yes' or 'no' to train again with "
                        + "the same settings: ");
                userInput = keyboard.nextLine().toLowerCase();
            }
        }
    }

    // Predicts the outcome of each of the loaded summoner's
    // processed matches not used for training using the current
    // state of the weight and prints the validation accuracy.
    private static void validate() {
        LoLMatch[] shuffledValidSet;
        double validAcc;
        int numValidMatches = shuffledProcessedMatches.length - numTrainMatches;
        int totalCorrect = 0, totalMatches = 0;

        shuffledValidSet = new LoLMatch[numValidMatches];
        for (int i = 0; i < numValidMatches; i++) {
            shuffledValidSet[i] = shuffledProcessedMatches[i + numTrainMatches];
        }

        for (int i = 0; i < numValidMatches; i++) {
            LoLMatch match = shuffledValidSet[i];
            String outcomeStr = "won";
            Vec gameSample = new Vec(weight.length());
            double outcome = 1, prediction;

            for (int k = 0; k < NUM_PLAYERS; k++) {
                String allyChamp = match.getAllyChamp(k);
                String enemyChamp = match.getEnemyChamp(k);
                double allyChampWinRate = match.getAllyChampWinRate(k);
                double allyFeatureValue = 1;
                double enemyChampWinRate = match.getEnemyChampWinRate(k);
                double enemyFeatureValue = 1;
                int allyChampIndex = champsArray.indexOf(allyChamp);
                int enemyChampIndex = champsArray.indexOf(enemyChamp);

                if (weight.getFeatType() != LoLWeight.FeaturesType.TC) {
                    allyFeatureValue = allyChampWinRate;
                    enemyFeatureValue = enemyChampWinRate;
                }

                // set up game sample
                if (weight.getTeamType() <= 3 ||
                        (weight.getTeamType() > 3 && match.isAllyBlueSide())) {
                    gameSample.set(allyChampIndex, allyFeatureValue);
                    if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                        gameSample.set(enemyChampIndex + numChamps, enemyFeatureValue);
                    } else {
                        gameSample.set(allyChampIndex + numChamps, 1);
                        gameSample.set(enemyChampIndex + numChamps * 2, enemyFeatureValue);
                        gameSample.set(enemyChampIndex + numChamps * 3, 1);
                    }
                } else {
                    gameSample.set(enemyChampIndex, enemyFeatureValue);
                    if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                        gameSample.set(allyChampIndex + numChamps, allyFeatureValue);
                    } else {
                        gameSample.set(enemyChampIndex + numChamps, 1);
                        gameSample.set(allyChampIndex + numChamps * 2, allyFeatureValue);
                        gameSample.set(allyChampIndex + numChamps * 3, 1);
                    }
                }

                if (weight.getTeamType() == 2 || weight.getTeamType() == 4) {
                    if (match.isAllyBlueSide()) {
                        gameSample.set(weight.length() - 3, 1);
                    } else {
                        gameSample.set(weight.length() - 2, 1);
                    }
                }

                if (weight.getTeamType() == 3) {
                    if (match.isAllyBlueSide()) {
                        if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                            gameSample.set(allyChampIndex + numChamps * 2, allyFeatureValue);
                            gameSample.set(enemyChampIndex + numChamps * 3, enemyFeatureValue);
                        } else {
                            gameSample.set(allyChampIndex + numChamps * 4, allyFeatureValue);
                            gameSample.set(allyChampIndex + numChamps * 5, 1);
                            gameSample.set(enemyChampIndex + numChamps * 6, enemyFeatureValue);
                            gameSample.set(enemyChampIndex + numChamps * 7, 1);
                        }
                    } else {
                        if (weight.getFeatType() != LoLWeight.FeaturesType.CWRTC) {
                            gameSample.set(enemyChampIndex + numChamps * 2, enemyFeatureValue);
                            gameSample.set(allyChampIndex + numChamps * 3, allyFeatureValue);
                        } else {
                            gameSample.set(enemyChampIndex + numChamps * 4, enemyFeatureValue);
                            gameSample.set(enemyChampIndex + numChamps * 5, 1);
                            gameSample.set(allyChampIndex + numChamps * 6, allyFeatureValue);
                            gameSample.set(allyChampIndex + numChamps * 7, 1);
                        }
                    }
                }
            }
            gameSample.set(weight.length() - 1, 1);

            // set up outcome
            if (((weight.getTeamType() != 5 || match.isAllyBlueSide())
                    && !match.isAllyWinner()) || (!match.isAllyBlueSide()
                    && match.isAllyWinner())) {
                if (weight.getModel() == Weight.Model.LR) {
                    outcome = 0;
                } else {
                    outcome = -1;
                }
                outcomeStr = "lost";
            }

            prediction = weight.predict(gameSample);
            System.out.print("Prediction score: " + prediction + ". ");
            if (weight.getTeamType() != 5) {
                System.out.println(summoner.getName() + "'s team " + outcomeStr
                        + " in match " + match.getMatchId() + ".");
            } else {
                System.out.println("Blue side " + outcomeStr + " in match "
                        + match.getMatchId() + ".");
            }

            if (weight.getModel() == Weight.Model.LR) {
                if ((outcome <= 0 && prediction <= 0.5)
                        || (outcome > 0 && prediction > 0.5)) {
                    totalCorrect++;
                }
            } else {
                if ((outcome <= 0 && prediction <= 0)
                        || (outcome > 0 && prediction > 0)) {
                    totalCorrect++;
                }
            }

            totalMatches++;
        }

        if (totalMatches != 0) {
            validAcc = (double) totalCorrect / totalMatches;
            validationAccuracies.add(validAcc);
            System.out.println("Validation accuracy: " + totalCorrect + "/"
                    + totalMatches + " = " + validAcc);
        }
    }
}