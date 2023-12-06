/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBC;

import Interfaces.DBManager;
import Pojos.ECG;
import Pojos.Patient;
import static Pojos.Patient.formatDate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel
 */
public class JDBCManager implements DBManager {

    private Connection c;

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            //here we get the connection
            this.c = DriverManager.getConnection("jdbc:sqlite:./db/ArrhythmiaTelemedicine.db");
            c.createStatement().execute("PRAGMA foreign_keys=ON");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTables() {
        try {
            Statement stmt = c.createStatement();

            String sq1 = "CREATE TABLE IF NOT EXISTS PATIENT " + "(id     INTEGER  PRIMARY KEY AUTOINCREMENT,"
                    + " name  TEXT   NOT NULL, " + " lastname  TEXT   NOT NULL," + " email TEXT NOT NULL,"
                    + " gender TEXT CHECK (gender = 'M' OR gender = 'F')," + " date_of_birth TEXT NOT NULL) ";
            stmt.executeUpdate(sq1);
            sq1 = "CREATE TABLE IF NOT EXISTS ECG " + "(id     INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + " observation TEXT NOT NULL, " + " ecg TEXT NOT NULL, " + " date TEXT NOT NULL,"
                    + " patientId INTEGER REFERENCES PATIENT(id) ON UPDATE CASCADE ON DELETE CASCADE)";
            stmt.executeUpdate(sq1);
            sq1 = "CREATE TABLE IF NOT EXIST USER " + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " role TEXT CHECK (role = 'P' OR role = 'D'), " + "username TEXT NOT NULL,"
                    + " password BLOB NOT NULL," + " role_id INTEGER NOT NULL)";
            stmt.executeUpdate(sq1);
            sq1 = "CREATE TABLE IF NOT EXIST DOCTOR " + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " name TEXT NOT NULL," + " lastname TEXT NOT NULL," + " email TEXT NOT NULL,"
                    + " patient_id INTEGER REFERENCES PATIENT(id) ON UPDATE CASACDE ON DELETE CASCADE)";
            stmt.executeUpdate(sq1);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return c;
    }

    @Override
    public void addPatient(Patient p) {
        try {
            String sql = "INSERT INTO patients (name, lastname, date, gender, MAC) VALUES (?,?,?,?, ?)";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, p.getName());
            prep.setString(2, p.getLastName());
            prep.setString(3, formatDate(p.getDob()));
            prep.setString(4, p.getGender());
            //prep.setString(5, p.getEmail());
            //prep.setString(6, p.getUsername());
            //String password = p.getPassword();
            MessageDigest md = MessageDigest.getInstance("MD5");
            //md.update(password.getBytes());
            //byte[] hash = md.digest();
            //prep.setBytes(6, hash);
            prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*@Override
    public Patient searchPatient(String username, String password) {
        Patient p = null;
        try {
            String sql = "SELECT * FROM patients WHERE username = ? AND password = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            stmt.setString(1, username);
            stmt.setBytes(2, hash);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String gender = rs.getString("gender");
                String email = rs.getString("email");
                String fecha = rs.getString("date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(fecha, formatter);
                p = new Patient(name, lastname, date, email, gender, id, username, password);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }*/
    @Override
    public boolean verifyUsername(String username) {
        String sql = "SELECT username FROM patient WHERE username = ?";
        try {
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean verifyPassword(String username, String passwordIntroduced) {
        String sql = "SELECT password FROM patient WHERE username = ?";
        try {
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(passwordIntroduced.getBytes());
                byte[] hashIntroduced = md.digest();
                byte[] hashSaved = rs.getBytes("password");
                return Arrays.equals(hashIntroduced, hashSaved);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Patient> listAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            String sql = "SELECT * FROM patients";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String gender = rs.getString("gender");
                //String email = rs.getString("email");
                String fecha = rs.getString("date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(fecha, formatter);
                Patient p = new Patient(id, name, date, lastname, gender);
                patients.add(p);
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patients;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (!verifyPassword(username, oldPassword)) {
            System.out.println("ERROR: Username and/or current password are not correct");
        } else {
            try {
                String sql = "UPDATE Patient SET password = ? WHEN username = ?";
                PreparedStatement prep = c.prepareStatement(sql);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(newPassword.getBytes());
                byte[] hash = md.digest();
                prep.setBytes(1, hash);
                prep.setString(2, username);
                prep.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void addECG(ECG ecg) {
        try {
            String sql = "INSERT INTO ECG (ecg, patientId, date) VALUES (?, ?, ?)";
            PreparedStatement prep = c.prepareStatement(sql);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(ecg.getEcg());
            byte[] bytes = bos.toByteArray();
            prep.setString(1, String.valueOf(ecg.getEcg()));//Revisar que esté bien
            prep.setInt(2, ecg.getPatient_id());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            prep.setString(3, ecg.getStartDate().format(dtf));
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ECG findECG(int id) {
        ECG ecg = null;
        try {
            String sql = "SELECT * FROM ECG WHERE id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                int patient_id = rs.getInt("patientId");
                String ecgList = rs.getString("ecg");
                String date = rs.getString("date");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate startDate = LocalDate.parse(date, dtf);
                ecg = new ECG(id, patient_id, startDate, ecgList);
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ecg;
    }

    @Override
    public ArrayList<String> findECGByPatientId(int patient_id) {
        ArrayList<String> ecgs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM ECG WHERE patient_id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, patient_id);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                String ecgList = rs.getString("ecg");
                ecgs.add(ecgList);
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ecgs;
    }

    @Override
    public void deleteECG(int id) {
        try {
            String sql = "DELETE FROM ECG WHERE id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, id);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setECG(ECG ecg, int id) {
        try {
            String sql = "UPDATE ECG SET ecg = ?, patientId = ? WHERE id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(ecg.getEcg());
            byte[] bytes = bos.toByteArray();
            prep.setString(1, ecg.getEcg());//Revisar que esté bien
            prep.setInt(2, ecg.getPatient_id());
            prep.setInt(3, id);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
