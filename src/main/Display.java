package main;

import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {

    int scale = 10;
    int width = 64 * scale;
    int height = 32 * scale;

    byte[][] gfx = new byte[64][32];

    JFrame jFrame;

    public Display(int screenWidth, int screenHeight, Keyboard keyboard) {
        jFrame = new JFrame("Chip8 - mdmarshmallow");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        byte[][] startScreen = new byte[64][32];
        for (int i = 0; i < startScreen.length; i++) {
            for (int j = 0; j < startScreen[i].length; j++) {
                gfx[i][j] = 0;
            }
        }
        jFrame.addKeyListener(keyboard);
        jFrame.add(this);
        jFrame.pack();
        jFrame.setSize(screenWidth, screenHeight); // This will need to be changed depending on computer
        jFrame.setVisible(true);
    }

    public void redraw(byte[][] gfx) {
        this.gfx = gfx;
        repaint();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // fill background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 32; y++) {
                byte val = gfx[x][y];
                if (val == 0) {
                    g.setColor(Color.black);
                } else {
                    g.setColor(Color.white);
                }
                g.fillRect(x * scale, y * scale, scale, scale);
            }
        }
    }

}
