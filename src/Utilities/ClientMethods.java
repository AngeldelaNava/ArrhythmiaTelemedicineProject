/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

import JDBC.JDBCManager;
import Pojos.Doctor;
import Pojos.Patient;
import Pojos.User;
import static Utilities.UtilitiesRead.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
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
    /**
     *
     * @param br
     * @param pw
     * @param manager
     * @throws SQLException
     */
    public static void register(BufferedReader br, PrintWriter pw, JDBCManager manager) throws SQLException {
        try {
            Patient p = new Patient();
            Doctor d = new Doctor();
            User user = new User();

            System.out.println("Let's proceed with the registration:");
            System.out.print("1.Patient\n2. Doctor\nRole:");
            String roleString = br.readLine();
            int role = Integer.parseInt(roleString);
            user.setRole_id(role);

            String name, lastName, birthdate, email, userName;
            switch (role) {
                case 1:

                    System.out.print("Name:");
                    name = br.readLine();
                    p.setName(name);

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
                    //manager.addUser(user);
                    //Utilities.Communication.sendUser(pw, user, manager);

                    System.out.print("Introduce the date of birth [yyyy-mm-dd]: ");
                    birthdate = br.readLine();

                    LocalDate bdate = readDate(birthdate);
                    p.setDob(bdate);

                    System.out.print("Email: ");
                    email = br.readLine();
                    p.setEmail(email);

                    Utilities.Communication.sendUser(pw, user, manager);
                    manager.addUser(user);
                    User u = manager.getUser(userName);
                    p.setUserId(u.getId());

                    // 2. Agregar paciente y asignar el user_id después de obtener el ID del usuario
                    manager.addPatient(p);
                    //p.setUserId(user.getId());
                    Utilities.Communication.sendPatient(pw, p, manager);

                    // 3. Crear el vínculo entre el usuario y el paciente
                    //manager.createLinkUserPatient(user.getId(), p.getId());
                    System.out.print(p);
                    break;
                case 2:
                    name = readString("Name: ");
                    d.setName(name);
                    //user.setUsername(name);

                    lastName = readString("Last Name: ");
                    d.setLastName(lastName);

                    userName = readString("Usename: ");
                    user.setUsername(userName);

                    email = readString("Email: ");
                    d.setEmail(email);

                    System.out.print("password:");
                    String password1 = br.readLine();
                    MessageDigest md1 = MessageDigest.getInstance("MD5");
                    md1.update(password1.getBytes());
                    byte[] hash1 = md1.digest();
                    user.setPassword(hash1);
                    manager.addUser(user);
                    User u2 = manager.getUser(userName);
                    d.setUserId(u2.getId());
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

    /**
     *
     * @param br
     * @param pw
     * @param manager
     * @return User
     */
    public static User login(BufferedReader br, PrintWriter pw, JDBCManager manager) {
        try {
            // Obtener la lista de usuarios desde la base de datos

            System.out.print("Username:");

            String username = br.readLine();

            System.out.print("password:");
            String password = br.readLine();
            byte[] bytesDefaultCharset = password.getBytes();
            if (manager.verifyUsername(username) && manager.verifyPassword(username, password)) {

                return manager.getUser(manager.getId(username));
            }
            // Verificar si el usuario y la contraseña coinciden
            /*for (User user : users) {
                if (user.getUsername().equals(username) && Arrays.equals(user.getPassword(), hash)) {
                    return user; // Devolver el usuario si la autenticación es exitosa
                    // System.out.print(user.toString());
                }
            }
            System.out.println("Login failed. User not found or incorrect password.");*/
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
}
