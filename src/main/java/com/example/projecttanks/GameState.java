package com.example.projecttanks;

public class GameState {
    MainGame game;

    public static final int ROUNDS_TO_WIN = 2;
    public static final int LAST_SECONDS_TO_LIVE = 3;
    public static final int LAST_SECONDS_FOR_INFO = 2;

    public int currentRound = 1;
    public int roundsPlayed = 0;
    public boolean isRoundOver = false;
    public int countDown;
    public boolean isGameOver = false;
    public Player winner;

    public int roundsWon1 = 0;
    public int killCount1 = 0;
    public int deathCount1 = 0;
    public boolean isDead1 = false;

    public int roundsWon2 = 0;
    public int killCount2 = 0;
    public int deathCount2 = 0;
    public boolean isDead2 = false;

    GameState(MainGame game) {
        this.game = game;
    }

    public void murdered(Tank murderer, Tank victim) {
        if (victim.getPlayer() == Player.ONE){
            isDead1 = true;
            deathCount1++;
            if (murderer.getPlayer() != victim.getPlayer()){
                killCount2++;
            }
        } else if (victim.getPlayer() == Player.TWO){
            isDead2 = true;
            deathCount2++;
            if (murderer.getPlayer() != victim.getPlayer()){
                killCount1++;
            }
        }
        if (!isDead1 || !isDead2) oneLeft();
        else noOneLeft();
    }

    private void oneLeft(){
        isRoundOver = true;
        countDown = LAST_SECONDS_TO_LIVE;
    }

    private void noOneLeft(){
        isRoundOver = true;
        countDown = LAST_SECONDS_FOR_INFO;
    }

    public void evaluateRound() {
        if (!isDead1 || !isDead2){
            if (isDead1) roundsWon2++;
            if (isDead2) roundsWon1++;

            if (roundsWon1 >= ROUNDS_TO_WIN) {
                isGameOver = true;
                winner = Player.ONE;
            }
            if (roundsWon2 >= ROUNDS_TO_WIN) {
                isGameOver = true;
                winner = Player.TWO;
            }
        }
        resetForNewRound();
        if (isGameOver) game.displayWinner(winner);
    }

    private void resetForNewRound() {
        isDead1 = false;
        isDead2 = false;
        isRoundOver = false;
        roundsPlayed++;
        currentRound++;
    }

    public void newRound(){
        game.btnAction("New round");
    }

    public void decrementCountDown() {
        countDown--;
        if (countDown <= 0) {
            evaluateRound();
            if (!isGameOver) newRound();
        }
    }
}
