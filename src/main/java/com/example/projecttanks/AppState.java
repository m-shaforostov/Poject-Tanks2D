package com.example.projecttanks;

/**
 * Represents the different states of the application.
 */
public enum AppState {
    /** The state when the lobby is displayed and players can start the game or leave the application */
    LOBBY,
    /** The state when main gameplay is running */
    GAME,
    /** The state when the game is paused */
    PAUSE,
    /** The state when the game has ended and players have time to check the game results */
    END
}
