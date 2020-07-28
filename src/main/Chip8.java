package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Chip8 {

    // Types are a lot larger than they need to be to negative overflows

    int[] memory = new int[4096]; // 8 bit RAM
    int[] V = new int[16]; // 8 bit general purpose registers
    int[] stack = new int[16];

    byte[] buffer; // stores the game file

    int delayTimer;
    int soundTimer;

    int opcode; // keep opcode an int to avoid negative values
    int I; // 16 bit index register
    int pc = 0x200; // 16 bit program counter

    short sp; // stack pointer

    boolean redraw;

    boolean[] keyIsPressed; // stores the state of the keypad

    public byte[][] gfx = new byte[64][32]; // the display

    int[] chip8FontSet = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    public Chip8() {
        // load the font set into memory
        for (int i = 0; i < 80; i++) {
            memory[i] = chip8FontSet[i];
        }
    }

    public void loadGame(String file) {
        try (InputStream inputStream = new FileInputStream("roms/" + file)) {
            buffer = inputStream.readAllBytes();
            for (int i = 0; i < buffer.length; i++) {
                memory[i + 512] = Byte.toUnsignedInt(buffer[i]);
            }
        } catch (IOException e) {
            System.out.println("Problem reading file. Error:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // the basic CPU cycle, should run at 60 Hz
    public void cycle(boolean[] keyIsPressed) {
        // fetch
        int inst1 = memory[pc] << 8;
        int inst2 = memory[pc + 1];
        opcode = inst1 | inst2;
        pc += 2;

        // decode and execute
        int x = (opcode & 0x0F00) >> 8;
        int nn = opcode & 0x00FF;
        int y = (opcode & 0x00F0) >> 4;
        int nnn = opcode & 0x0FFF;

        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode & 0x000F) {
                    case 0x0000: // 00E0: Clears the screen
                        for (int i = 0; i < gfx.length; i++) {
                            for (int j = 0; j < gfx[i].length; j++) {
                                gfx[i][j] = 0;
                            }
                        }
                        redraw = true;
                        break;
                    case 0x000E: // 00EE: Returns from subroutine
                        sp--;
                        pc = stack[sp];
                        break;
                    default:
                        System.out.println("Found unknown opcode " + opcode + ". Your ROM might be corrupted.");
                        System.exit(-1);
                }
                break;
            case 0x1000: // 1NNN: Jumps to address at NNN
                pc = nnn;
                break;
            case 0x2000: // 2NNN: Jumps to subroutine at NNN
                stack[sp] = pc;
                sp++;
                pc = nnn;
                break;
            case 0x3000: // 3XNN: Skips next instruction if VX equals NN
                if (V[x] == nn) {
                    pc += 2;
                }
                break;
            case 0x4000: // 4XNN: Skips next instruction if VX doesn't equal NN
                if (V[x] != nn) {
                    pc += 2;
                }
                break;
            case 0x5000: // 5XY0: Skips next instruction if VX == VY
                if (V[x] == V[y]) {
                    pc += 2;
                }
                break;
            case 0x6000: // 6XNN: Sets VX = NN
                V[x] = nn;
                break;
            case 0x7000: // 7XNN: Adds NN to VX
                V[x] += nn;
                if (V[x] > 255) {
                    V[x] &= 0xFF; // get the carry only
                }
                break;
            case 0x8000: // math operations
                switch (opcode & 0x000F) {
                    case 0x0000: // 8XY0: Sets VX = VY
                        V[x] = V[y];
                        break;
                    case 0x0001: // 8XY1: Sets VX = VX | VY
                        V[x] = V[x] | V[y];
                        break;
                    case 0x0002: // 8XY2: Sets VX = VX & VY
                        V[x] = V[x] & V[y];
                        break;
                    case 0x0003: // 8XY3: Sets VX = VX ^ VY (xor)
                        V[x] = V[x] ^ V[y];
                        break;
                    case 0x0004: // 8XY4: Sets VX += VY
                        V[x] += V[y];
                        if (V[x] > 255) {
                            V[x] &= 0xFF; // gets the carry only
                            V[0xF] = 1;
                        } else {
                            V[0xF] = 0;
                        }
                        break;
                    case 0x0005: // 8XY5: Sets VX -= Vy
                        V[x] -= V[y];
                        if (V[x] >= 0) {
                            V[0xF] = 1;
                        } else {
                            V[x] &= 0xFF; // gets the borrow only
                            V[0xF] = 0;
                        }
                        break;
                    case 0x0006: // 8XY6: Stores least sig bit of VX in VF then bit shifts right by 1
                        V[0xF] = V[x] & 0x1;
                        V[x] >>= 1;
                        break;
                    case 0x0007: // 8XY7: Sets VX = VY - VX
                        V[x] = V[y] - V[x];
                        if (V[x] >= 0) {
                            V[0xF] = 1;
                        } else {
                            V[x] &= 0xFF; // gets the borrow only
                            V[0xF] = 0;
                        }
                        break;
                    case 0x000E: // 8XYE: Stores most sig bit of VX in VF and bit shifts left by 1
                        V[0xF] = (V[x] & 0x80) >> 7;
                        V[x] <<= 1;
                        V[x] &= 0xFF; // gets the 8 bits (remember this represents an unsigned 8 bit number)
                        break;
                    default:
                        System.out.println("Found unknown opcode " + opcode + ". Your ROM might be corrupted.");
                        System.exit(-1);
                }
                break;
            case 0x9000: // 9XY0: Skips the next instruction if VX != VY
                if (V[x] != V[y]) {
                    pc += 2;
                }
                break;
            case 0xA000: // ANNN: sets I to NNN
                I = nnn;
                break;
            case 0xB000: // BNNN: Jumps to address NNN + V0
                pc = V[0x0] + nnn;
                break;
            case 0xC000: // CXNN: Sets VX to the result rand() & NN
                int random = (int) Math.round(255 * Math.random());
                V[x] = random & nn;
                break;
            case 0xD000: // DXYN: Draws a sprite at (VX, VY), if any bytes are flipped from 1 -> 0, VF = 1

                int height = opcode & 0x000F;
                int pixel;

                V[0xF] = 0;

                for (int i = 0; i < height; i++) { // i is the y-axis offset
                    pixel = memory[I + i]; // get the 8 bit row from memory
                    for (int j = 0; j < 8; j++) { // j is the x-axis offset
                        if ((pixel & (0x80 >> j)) != 0) { // checks bit by bit of the pixel (0x80 is mask)
                            int xCoordinate = (V[x] + j) % 64;
                            int yCoordinate = (V[y] + i) % 32;
                            if (gfx[xCoordinate][yCoordinate] == 1) {
                                V[0xF] = 1; // sets VF if there is a collision
                            }
                            gfx[xCoordinate][yCoordinate] ^= 1; // XOR the current screen value with the appropriate bit
                        }
                    }
                }

                redraw = true;

                break;
            case 0xE000:
                switch (opcode & 0x000F) {
                    case 0x000E: // EX9E: Skip the next instruction if key in VX is pressed
                        if (keyIsPressed[V[x]]) {
                            pc += 2;
                        }
                        break;
                    case 0x0001: // EXA1: Skip the next instruction if key in VX isn't pressed
                        if (!keyIsPressed[V[x]]) {
                            pc += 2;
                        }
                        break;
                    default:
                        System.out.println("Found unknown opcode " + opcode + ". Your ROM might be corrupted.");
                        System.exit(-1);
                }
                break;
            case 0xF000:
                switch (opcode & 0x0FF) {
                    case 0x0007: // FX07: Sets VX to the value of the delay timer
                        V[x] = delayTimer;
                        break;
                    case 0x000A: // FX0A: A key press is awaited and then stored in VX
                        boolean foundPressed = false;
                        for (byte i = 0; i < keyIsPressed.length; i++) {
                            if (keyIsPressed[i] && !foundPressed) {
                                foundPressed = true;
                                V[x] = i;
                            }
                        }
                        if (!foundPressed) {
                            pc -= 2;
                        }
                        break;
                    case 0x0015: // FX15: Sets delay timer to VX
                        delayTimer = V[x];
                        break;
                    case 0x0018: // FX18: Sets the sound timer to VX
                        soundTimer = V[x];
                        break;
                    case 0x001E: // FX1E: Adds VX to I
                        I += V[x];
                        break;
                    case 0x0029: // FX29: Sets I to the location of the sprite for a character in VX
                        I = V[x] * 5;
                        break;
                    case 0x0033: // FX33: Stores a binary coded decimal representation of VX
                        memory[I] = V[x] / 100;
                        memory[I + 1] = (V[x] / 10) % 10;
                        memory[I + 2] = (V[x] % 100) % 10;
                        break;
                    case 0x0055: // FX55: Stores registers from V0 to VX in memory starting from address I
                        for (int i = 0; i <= x; i++) {
                            memory[I + i] = V[i];
                        }
                        break;
                    case 0x0065: // FX65: Fills registers from V0 to VX with memory values starting from address I
                        for (int i = 0; i <= x; i++ ) {
                            V[i] = memory[I + i];
                        }
                        break;
                    default:
                        System.out.println("Found unknown opcode " + opcode + ". Your ROM might be corrupted.");
                        System.exit(-1);
                }
                break;
            default:
                System.out.println("Found unknown opcode " + opcode + ". Your ROM might be corrupted.");
                System.exit(-1);
        }

        // update timers
        if (delayTimer > 0) delayTimer--;
        if (soundTimer > 0) {
            System.out.println("BEEP!");
            soundTimer--;
        }
    }
}