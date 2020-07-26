package main;

import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {

    int scale = 10;
    int width = 64 * scale;
    int height = 32 * scale;

    byte[][] gfx;

    public void setGfx(byte[][] gfx) {
        this.gfx = gfx;
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
