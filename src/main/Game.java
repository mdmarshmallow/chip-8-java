package main;

import javax.swing.*;

public class Game {

    JFrame jFrame;
    Display display = new Display();
    Chip8 chip8 = new Chip8();

    private Game(String game) {
        jFrame = new JFrame("Chip8 - mdmarshmallow");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        byte[][] startScreen = new byte[64][32];
        for (int i = 0; i < startScreen.length; i++) {
            for (int j = 0; j < startScreen[i].length; j++) {
                startScreen[i][j] = 0;
            }
        }
        display.setGfx(startScreen);
        jFrame.add(display);
        jFrame.pack();
        jFrame.setSize(650, 400); // This will need to be changed depending on computer
        jFrame.setVisible(true);

        chip8.loadGame(game);
    }

    //TODO: implement function to get key presses
    public static void main(String[] args) {
        String gameName = "PONG";
        Game game = new Game(gameName);
    }
}
