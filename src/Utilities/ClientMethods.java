/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

import JDBC.JDBCManager;
import Pojos.Doctor;
import Pojos.Patient;
import Pojos.Role;
import Pojos.User;
import static Utilities.UtilitiesRead.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria
 */
public class ClientMethods {

    /*private static Scanner sc = new Scanner(System.in);
    private static JDBCManager user;
    private static JDBCManager role;
    private static JDBCManager patient;
    private static JDBCManager doctor;
    public static String trashcan;*/
    public static void registerDoctor(BufferedReader br, PrintWriter pw, JDBCManager manager) {
        Scanner sc = new Scanner(System.in);
        Doctor d = new Doctor();
        System.out.println("Please, input the doctor info:");
        System.out.print("Name: ");
        String name = sc.next();
        d.setName(name);
        System.out.print("Surname: ");
        String surname = sc.next();
        d.setLastName(surname);
        System.out.print("Email: ");
        String email = sc.next();
        d.setEmail(email);
        System.out.println("Let's proceed with the registration, the username and password:");
        Utilities.Communication.sendDoctor(pw, d, manager);
        User user = new User();
        System.out.print("Role: \n");
        System.out.print("1: Patient: \n");
        System.out.print("2: Doctor: \n");
        int role = Integer.parseInt(sc.next());
        user.setRole_id(role);
        System.out.print("Username: ");
        String username = sc.next();
        user.setUsername(username);
        System.out.print("Password: ");
        String password = sc.next();
        byte[] passwordBytes = password.getBytes();
        user.setPassword(passwordBytes);
        manager.addDoctor(d);
        manager.addUser(user);
    }

    public static void register(BufferedReader br, PrintWriter pw, JDBCManager manager) throws SQLException {
        try {
            Patient p = new Patient();
            Doctor d = new Doctor();
            User user = new User();

            System.out.println("Let's proceed with the registration:");
            //Utilities.Communication.sendPatient(pw, p);
            //String roleString = readString("1. Patient\n2. Doctor\nRole: ");
            System.out.print("1.Patient\n2. Doctor\nRole:");
            String roleString = br.readLine();
            int role = Integer.parseInt(roleString);
            user.setRole_id(role);
            //System.out.print("Username: ");
            //String username = sc.next();
            //String username = readString("Username: ");
           // System.out.print("Username:");
            //String username = br.readLine();
            //user.setUsername(username);
            //System.out.print("Password: ");
            //String password = sc.next();
            //String password = readString("Password: ");
            //System.out.print("Password:");
           
            //Utilities.Communication.sendUser(pw, user);
            //manager.addUser(user);
            //User u= new User();
            //User u = manager.getUser(username);
            String name, lastName, gender, birthdate, email, userName;
            switch (role) {
                case 1:
                    //System.out.print("Name: ");
                    //String name = sc.next();
                    //name = readString("Name: ");
                    System.out.print("Name:");
                    name = br.readLine();
                    p.setName(name);
                    
                    //System.out.print("LastName: ");
                    //String lastName = sc.next();
                    //lastName = readString("Last Name: ");
                    System.out.print("Last Name:");
                    lastName = br.readLine();
                    p.setLastName(lastName);
                    
                    
                    System.out.print("Username:");
                    userName = br.readLine();
                    user.setUsername(userName);
                    
                    System.out.print("password:");
                    String password = br.readLine();
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(password.getBytes());
                    byte[] hash = md.digest();
                    user.setPassword(hash);
                    //System.out.print("Gender: ");
                    //String gender = sc.next();
                    //gender = readString("Gender (male OR female): ");
                    System.out.print("Gender (male OR female):");
                    gender = br.readLine();
                    do {
                        if (gender.equalsIgnoreCase("male")) {
                            gender = "Male";
                        } else if (gender.equalsIgnoreCase("female")) {
                            gender = "Female";
                        } else {
                            //System.out.print("Not a valid gender. Please introduce a gender (male or female): ");
                            //gender = sc.next();
                            System.out.print("Not a valid gender.\nGender (male OR female):");
                            gender = br.readLine();
                           
                        }
                    } while (!(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female")));
                    p.setGender(gender);

                    //System.out.print("Date of birth [yyyy-mm-dd]: ");
                    //String birthdate = sc.next();
                    System.out.print("Introduce the date of birth [yyyy-mm-dd]: ");
                    birthdate = br.readLine();
                    //birthdate = readString("Introduce the date of birth [yyyy-mm-dd]: ");
                    //System.out.print("Please introduce a valid date [yyyy-mm-dd]: ");
                    //birthdate = sc.next();
                    LocalDate bdate = readDate(birthdate);
                    p.setDob(bdate);

                    //System.out.print("Email: ");
                    //String email = sc.next();
                    System.out.print("Email: ");
                    email = br.readLine();
                    //email = readString("Email: ");
                    p.setEmail(email);

                    p.setUserId(user.getId());
                    Utilities.Communication.sendPatient(pw, p, manager);
                    //manager.addPatient(p);
                    break;
                case 2:
                    name = readString("Name: ");
                    d.setName(name);
                    user.setUsername(name);
                    
                    lastName = readString("Last Name: ");
                    d.setLastName(lastName);

                    email = readString("Email: ");
                    d.setEmail(email);
                    
                     System.out.print("password:");
                     String password1 = br.readLine();
                     MessageDigest md1 = MessageDigest.getInstance("MD5");
                     md1.update(password1.getBytes());
                     byte[] hash1 = md1.digest();
                     user.setPassword(hash1);
                    
                    d.setUserId(user.getId());
                    Utilities.Communication.sendDoctor(pw, d, manager);
                //manager.addDoctor(d);
            }

            //p.setUserId(u.getId());
            //manager.add //manager.addPatient(p);
            //List<Patient> patients = manager.listAllPatients();
            /*for(Patient patient : patients){
                if (p.getName() == patient.getName() &&
                        p.getLastName() == patient.getLastName() &&
                        p.getEmail() == patient.getEmail() &&
                        p.getGender() == patient.getGender() &&
                        p.getDob() == patient.getDob()) {
                    user
                }
            }*/
        } catch (IOException ex) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public static User login(BufferedReader br, PrintWriter pw, JDBCManager manager) {
        try {
            // Obtener la lista de usuarios desde la base de datos
            
            System.out.print("Username:");
            
            String username = br.readLine();
            
            System.out.print("password:");
            String password = br.readLine();
            
            
            List<User> users = manager.listAllUsers();
            
            // Verificar si el usuario y la contraseña coinciden
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return user; // Devolver el usuario si la autenticación es exitosa
                   // System.out.print(user.toString());
                }
            }

            // Devolver null si no se encuentra el usuario en la base de datos
            
        } catch (IOException ex) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return null;
        }
    
    
    
    /*public static User login(BufferedReader bf, PrintWriter pw, JDBCManager userman) {
        User u = Utilities.Communication.receiveUser(bf);
        String str = new String(u.getPassword(), StandardCharsets.UTF_8);//transform a byte[] in a string;
        User user1 = new User();
        
        System.out.print("Username:");
        name = br.readLine();
                    p.setName(name);
                    user.setUsername(name);
        
                System.out.print("password:");
                    String password = br.readLine();
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(password.getBytes());
                    byte[] hash = md.digest();
                    user1.setPassword(hash);
             
        //UTF-8,which is a common way to encode Unicode characters into byte sequences
        User user = userman.checkPassword(u.getUsername(), str);
        if (user == null) {
            pw.println("Wrong username or password");
        } else {
            if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) {
                int id = userman.getId(u.getUsername());
                User u2 = userman.selectUserByUserId(id);
                return u2;
            } else {
                pw.println("Wrong username or password");
            }
        }
        return null;
    }
    */
    public static void firstlogin(JDBCManager manager) {
        try {
            if (!manager.verifyUsername("admin")) {
                String username = "admin";
                String password = "admin";
                Role role = manager.selectRoleById(2);
                User user = new User(username, password.getBytes(), 2);
                manager.addUser(user);
                user.setId(manager.getId(username));
                manager.createLinkUserRole(role.getId(), user.getId());
                Doctor doctor = new Doctor(2, "admin", "admin", "admin");
                manager.addDoctor(doctor);
                doctor.setDoctorId(manager.getId(doctor.getName()));
                manager.createLinkUserDoctor(user.getId(), doctor.getDoctorId());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
