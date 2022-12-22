package com.predictor;

public class NullSummonerException extends Exception {

    private static final String SUMM_ERR_MSG = "Load summoner first.";

    // Constructor for a NullSummonerException with an error message.
    public NullSummonerException() {
        super(SUMM_ERR_MSG);
    }
}
