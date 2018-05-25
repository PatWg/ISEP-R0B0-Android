package com.google.blockly.android;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class HexCode extends AppCompatActivity {

    private static final String SCRIPT = "from microbit import *\nwhile True:\n\tdisplay.set_pixel(0,0,9)\n\tdisplay.set_pixel(0,2,9)\n\tdisplay.set_pixel(0,4,9)\n\tdisplay.set_pixel(1,0,9)\n\tdisplay.set_pixel(1,1,9)\n\tdisplay.set_pixel(1,2,9)\n\tdisplay.set_pixel(1,3,9)\n\tdisplay.set_pixel(1,4,9)";
    private static final int MAX_SIZE = 8192;

    public static void main(String[] args) {
        try {
            String result = produceHex();
//            System.out.println(result);
            PrintWriter writer = new PrintWriter("other_script.hex", "UTF-8");
            writer.print(result);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String produceHex() throws Exception {
        String script = hexlify();
        Scanner scanner = new Scanner(new File("runtime.txt"));
        StringBuilder runtime = new StringBuilder();
        while (scanner.hasNext()) {
            runtime.append(scanner.nextLine())
                    .append("\n");
        }

        String[] run = runtime.toString().split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < run.length - 2; i++) {
            result.append(run[i])
                    .append("\n");
        }
        result.append(script);
        result.append(run[run.length-2])
                .append("\n")
                .append(run[run.length-1]);
        return result.toString();
    }

    /**
     * This method is the one taken from https://github.com/bbcmicrobit/PythonEditor/blob/1df10c07a271d9597eac318aef2e5dc1259af24a/python-main.js#L68
     * All the logic is what is necessary to produce a .hex file with a Python script
     * @return The Python script as a .hex file
     * @throws Exception
     */
    private static String hexlify() throws Exception {
        byte[] data = new byte[4 + SCRIPT.length() + (16 - (4 + SCRIPT.length()) % 16)];
        data[0] = 77;
        data[1] = 80;
        data[2] = (byte) (SCRIPT.length() & 0xff);
        data[3] = (byte) ((SCRIPT.length() >> 8) & 0xff);
        for (int i = 0; i < SCRIPT.length(); ++i) {
            data[4 + i] = (byte) SCRIPT.charAt(i);
        }

        if (data.length > MAX_SIZE) {
            throw new Exception("Script is too long");
        }
        int addr = 0x3e000;
        byte[] chunk = new byte[5 + 16];

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(":020000040003F7")
                .append("\n");
        for (int i = 0; i < data.length; i += 16) {
            chunk[0] = 16;
            chunk[1] = (byte) ((addr >> 8) & 0xff);
            chunk[2] = (byte) (addr & 0xff);
            chunk[3] = 0;
            for (int j = 0; j < 16; j++) {
                chunk[4 + j] = data[i + j];
            }
            byte checksum = 0;
            for (int j = 0; j < 4 + 16; j++) {
                checksum += chunk[j];
            }
            chunk[4 + 16] = (byte) ((-checksum) & 0xff);
            stringBuilder.append(':')
                    .append(hexlify(chunk).toUpperCase())
                    .append("\n");
            addr += 16;
        }
        return stringBuilder.toString();
    }

    private static String hexlify(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

}
