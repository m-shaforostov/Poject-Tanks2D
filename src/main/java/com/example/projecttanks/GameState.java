package com.example.projecttanks;

/**
 * Manages the game state, including round progression, player statistics and win conditions for the game.
 */
public class GameState {
    private final MainGame game;

    /** Represents number of rounds a tank has tow win to win the whole game */
    public static final int ROUNDS_TO_WIN = 5;
    private static final int LAST_SECONDS_TO_LIVE = 3;
    private static final int LAST_SECONDS_FOR_INFO = 2;

    /** Indicates whether the current round has ended */
    public boolean isRoundOver = false;
    /** Indicates whether the current gane has ended */
    public boolean isGameOver = false;
    /** Countdown timer. While this time alive tank must survive to win. */
    public int countDown;

    private Player winner;

    /** Shows number of rounds the first player has won */
    public int roundsWon1 = 0;
    private int killCount1 = 0;
    private int deathCount1 = 0;
    private boolean isDead1 = false;

    /** Shows number of rounds the second player has won */
    public int roundsWon2 = 0;
    private int killCount2 = 0;
    private int deathCount2 = 0;
    private boolean isDead2 = false;

    /**
     * Constructs a GameState controller for a given game.
     * @param game the {@link MainGame} instance.
     */
    GameState(MainGame game) {
        this.game = game;
    }

    /**
     * Manages a murder of a tank for given murderer and victim.
     * Updates kill counts and determines whether and when the round should end.
     * @param murderer the tank that killed
     * @param victim the tank that was killed
     */
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

    /**
     * Determines the winner of the round and checks if the game is over.
     * If the game ends, notifies the game for winner display.
     */
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
    }

    /**
     * Decrements count down to round ending.
     * When the count reaches zero point, evaluates the round and starts new one if the game is not over.
     */
    public void decrementCountDown() {
        countDown--;
        if (countDown <= 0) {
            evaluateRound();
            if (!isGameOver) game.btnAction("New round");
        }
    }
}
