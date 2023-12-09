/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.System.console;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author jsold
 */
public class UtilitiesRead {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

   
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
                stringReaded = reader.readLine();
                return stringReaded;

            } catch (Exception error) {
                System.out.println("Error reading the String; please try again" + error);
            }
        }
    }

    public static LocalDate readDate(String birthdate) {
        while (true) {
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dob = LocalDate.parse(birthdate, dtf);
                return dob;
            } catch (DateTimeException e) {
                System.out.println("Incorrect date");
                birthdate = UtilitiesRead.readString("Introduce the date of birth [yyyy-mm-dd]: ");
            }
        }
    }
}
