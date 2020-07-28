package main;

import javax.swing.*;

public class Game {

    // emulator settings
    static final int SCREEN_WIDTH = 650;
    static final int SCREEN_HEIGHT = 450;
    static final int CPU_HZ = 500;

    JFrame jFrame;
    Display display = new Display(SCREEN_WIDTH, SCREEN_HEIGHT);
    Chip8 chip8 = new Chip8();

    private Game(String game) {
        chip8.loadGame(game);
    }

    private void delayLoop(long loopStartTime) {
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

    private void runGame() {
        while (true) {
            long loopStartTime = System.nanoTime();

            chip8.cycle();

            if (chip8.redraw) {
                display.redraw(chip8.gfx);
                chip8.redraw = false;
            }

            delayLoop(loopStartTime);
        }
    }

    //TODO: implement function to get key presses
    public static void main(String[] args) {
        String gameName = "PONG";
        Game game = new Game(gameName);
        game.runGame();
    }
}
