package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Keyboard implements KeyListener {

    boolean[] keyIsPressed = new boolean[16];

    private final Map<Character, Integer> keyBindings = new HashMap<>();

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
     *
     */

    public Keyboard() {
        super();
        keyBindings.put('1', 0x1);
        keyBindings.put('2', 0x2);
        keyBindings.put('4', 0xC);
        keyBindings.put('q', 0x4);
        keyBindings.put('w', 0x5);
        keyBindings.put('e', 0x6);
        keyBindings.put('r', 0xD);
        keyBindings.put('a', 0x7);
        keyBindings.put('s', 0x8);
        keyBindings.put('d', 0x9);
        keyBindings.put('f', 0xE);
        keyBindings.put('z', 0xA);
        keyBindings.put('x', 0x0);
        keyBindings.put('c', 0xB);
        keyBindings.put('v', 0xF);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int index = keyBindings.getOrDefault(e.getKeyChar(), -1);
        if (index != -1) {
            keyIsPressed[index] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int index = keyBindings.getOrDefault(e.getKeyChar(), -1);
        if (index != -1) {
            keyIsPressed[index] = false;
        }
    }
}
