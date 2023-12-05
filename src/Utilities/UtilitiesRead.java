/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import static java.lang.System.console;

/**
 *
 * @author jsold
 */
public class UtilitiesRead {

    public static int readInt(String text) {

        int number;
        while (true) {
            try {

                number = Integer.parseInt(readString(text));
                return number;
            } catch (NumberFormatException error) {
                System.out.println("Error reading an int; please try again." + error);
            }
        }
    }

    public static String readString(String text) {
        System.out.print(text);
        while (true) {
            try {

                String stringReaded;
                stringReaded = console().readLine();
                return stringReaded;

            } catch (Exception error) {
                System.out.println("Error reading the String; please try again" + error);
            }
        }
    }
}
