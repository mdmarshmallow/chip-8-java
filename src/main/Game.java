package main;

import javax.swing.*;

public class Game {

    // emulator settings
    static final int SCREEN_WIDTH = 650;
    static final int SCREEN_HEIGHT = 450;
    static final int CPU_HZ = 500;

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
        jFrame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT); // This will need to be changed depending on computer
        jFrame.setVisible(true);

        chip8.loadGame(game);
    }

    private void runGame() {
        while (true) {
            long loopStartTime = System.nanoTime();

            chip8.cycle();

            if (chip8.redraw) {
                display.setGfx(chip8.gfx);
                display.repaint();
                chip8.redraw = false;
            }

            //code for mantaining target hz
            long loopEndTime = System.nanoTime();
            long period = 1000000000 / CPU_HZ;
            long timeLeftInPeriod = period - (loopEndTime - loopStartTime);
            long timeStartNextLoop = timeLeftInPeriod + System.nanoTime();
            while (System.nanoTime() < timeStartNextLoop) {
                try {
                    Thread.sleep(0);
                } catch(InterruptedException e) {
                    System.out.println("Something went wrong. Exiting emulator.");
                    System.exit(-1);
                }
            }
        }
    }

    //TODO: implement function to get key presses
    public static void main(String[] args) {
        String gameName = "PONG";
        Game game = new Game(gameName);
        game.runGame();
    }
}
