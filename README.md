Name: Maksym Shaforostov <br>
Project: Proj25-F4 - Tanky

# Tanks

The project is a 2D tanks game. <br>
When the application is started, there appears a lobby with two buttons Stare and Quit that allow start the new game or 
leave the application. <br><br>
When user presses the start button, generates a new battlefield, which contains a randomly generated maze and two tanks
red and green. Each Tank has its own set of keys on the keyboard to be controlled: WASD + Q for the red tank and 
Arrows + ENTER for the green one. WASD and Arrows are used to control tanks' movement while Q and ENTER are used to
shoot the bullet. After the moment the game haa been started, two players can start to destroy the tank of their 
opponent by moving through the maze and shooting all around. <br><br>
On the top panel there is a statistic of the game: number of won rounds for each player and passed time of the round.
When round ends (one tanks is destroyed) there is some time while which the other player has to survive in order to win 
the round. If player dies, no one receives the point. <br><br>
After each round the battlefield is regenerated (both dimensions and maze).
When the game is over, the game is paused, so players can see the last round field (partially), game statistics on the panel and
the winner on the info Table in the middle of the screen.
Then players can use button "Home" to return to the lobby in order to start the new game, or button "Quit" in order to 
close the window.

The project can be started by running the MainGame.java file.
Java-doc generated documentation you can find at Java-Doc directory