package main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {

    boolean[] keyIsPressed = new boolean[16];

    /**
     * Mapping from Chip 8 keypad to laptop keyboard
     *
     * Keypad                   Keyboard
     * +-+-+-+-+                +-+-+-+-+
     * |1|2|3|C|                |1|2|3|4|
     * +-+-+-+-+                +-+-+-+-+
     * |4|5|6|D|                |Q|W|E|R|
     * +-+-+-+-+       =>       +-+-+-+-+
     * |7|8|9|E|                |A|S|D|F|
     * +-+-+-+-+                +-+-+-+-+
     * |A|0|B|F|                |Z|X|C|V|
     * +-+-+-+-+                +-+-+-+-+
     */
    public void keyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getKeyChar()) {
            case '0':
                System.out.println("0 is pressed");
            default:
                return;
        }
    }
}
