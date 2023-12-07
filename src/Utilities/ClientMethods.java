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
import static Utilities.UtilitiesRead.readDate;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

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
        /*try {
        Doctor d = Utilities.Communication.receiveDoctor(br);

        //autogenerate username
        String username = "" + d.getName().charAt(0) + "." + d.getLastName() + "" + Integer.valueOf(d.getLastName().charAt(0));
        //autogenerated password
        String[] symbols = {"0", "1", "9", "7", "K", "Q", "a", "b", "c", "U", "w", "3", "0"};
        int length = 14;
        Random random;
        random = SecureRandom.getInstanceStrong();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
        int indexRandom = random.nextInt(symbols.length);
        sb.append(symbols[indexRandom]);
        }
        String password = sb.toString();
        //generate the hash
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hash = md.digest();
        User user = new User(username, password.getBytes(), 2);
        userman.addUser(user);
        user.setId(userman.getId(username));
        userman.createLinkUserRole(2, user.getId());
        Utilities.Communication.sendUser(pw, user);
        doctorman.addDoctor(d);
        d.setDoctorId(doctorman.getId(d.getName()));
        doctorman.createLinkUserDoctor(user.getId(), d.getDoctorId());
        pw.println("Doctor successfully registered");
        } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, ex);
        pw.println("Doctor not registered");
        }*/
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
        Utilities.Communication.sendDoctor(pw, d);
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

    public static void registerPatient(BufferedReader br, PrintWriter pw, JDBCManager manager) throws SQLException {
        /*try {
            //autogenerate username
            Patient p = Utilities.Communication.receivePatient(br); //receivePatient and doctor deberia devolver un objeto paciente o doctor
            String username = Integer.toString(p.getId());
            //autogenerated password
            String[] symbols = {"0", "1", "9", "7", "K", "Q", "a", "b", "c", "U", "w", "3", "0"};
            int length = 14;
            Random random;
            random = SecureRandom.getInstanceStrong();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int indexRandom = random.nextInt(symbols.length);
                sb.append(symbols[indexRandom]);
            }
            String password = sb.toString();
            //generate the hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            String pass = new String(hash, StandardCharsets.UTF_8);
            User user = new User(username, password.getBytes(), 1);
            userman.addUser(user);
            user.setId(userman.getId(username));
            userman.createLinkUserRole(1, user.getId());
            Utilities.Communication.sendUser(pw, user);
            patientman.addPatient(p);
            patientman.createLinkUserPatient(user.getId(), p.getId());
            pw.println("Patient successfully registered");
            List<Doctor> doctorl = doctorman.selectAllDoctors();
            pw.println(doctorl.size());
            for (int i = 0; i < doctorl.size(); i++) {
                pw.println(doctorl.get(i));
            }
            int doctorid = Integer.parseInt(br.readLine());
            patientman.createLinkDoctorPatient(p.getId(), doctorid);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, ex);
            pw.println("Patient not registered");
        } catch (IOException ex) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        Scanner sc = new Scanner(System.in);
        Patient p = new Patient();

        System.out.print("Name: ");
        String name = sc.next();
        p.setName(name);

        System.out.print("LastName: ");
        String lastName = sc.next();
        p.setLastName(lastName);

        System.out.print("Gender: ");
        String gender = sc.next();
        do {
            if (gender.equalsIgnoreCase("male")) {
                gender = "Male";
            } else if (gender.equalsIgnoreCase("female")) {
                gender = "Female";
            } else {
                System.out.print("Not a valid gender. Please introduce a gender (Male or Female): ");
                gender = sc.next();
            }
        } while (!(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female")));
        p.setGender(gender);

        System.out.print("Date of birth [yyyy-mm-dd]: ");
        String birthdate = sc.next();
        LocalDate bdate;
        System.out.print("Please introduce a valid date [yyyy-mm-dd]: ");
        birthdate = sc.next();
        bdate = readDate(birthdate);
        p.setDob(bdate);

        System.out.print("Email: ");
        String email = sc.next();
        p.setEmail(email);

        System.out.println("Let's proceed with the registration:");
        Utilities.Communication.sendPatient(pw, p);
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
    }

    public static User login(BufferedReader bf, PrintWriter pw, JDBCManager userman) {
        User u = Utilities.Communication.receiveUser(bf);
        String str = new String(u.getPassword(), StandardCharsets.UTF_8);//transform a byte[] in a string;
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
