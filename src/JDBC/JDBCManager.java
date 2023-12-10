/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBC;

import Interfaces.DBManager;
import Pojos.Doctor;
import Pojos.ECG;
import Pojos.Patient;
import Pojos.Role;
import Pojos.User;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
            if (c == null) {
                Class.forName("org.sqlite.JDBC");
                //here we get the connection
                this.c = DriverManager.getConnection("jdbc:sqlite:./db/ArrhythmiaTelemedicineProject.db");
                c.createStatement().execute("PRAGMA foreign_keys=ON");
            }
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

            String sql = "CREATE TABLE IF NOT EXISTS USER " + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL," + " password BLOB NOT NULL,"
                    + " role_id INTEGER REFERENCES ROLE(id) ON UPDATE CASCADE ON DELETE CASCADE)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS PATIENT " + "(id INTEGER  PRIMARY KEY AUTOINCREMENT,"
                    + " name  TEXT   NOT NULL, " + " lastname  TEXT   NOT NULL," + " date_of_birth TEXT NOT NULL,"
                    + " email TEXT NOT NULL, "
                    + " user_id INTEGER REFERENCES USER(id) ON UPDATE CASCADE ON DELETE CASCADE) ";
            stmt.executeUpdate(sql);
            /*sql = "CREATE TABLE IF NOT EXISTS CONEXION (" + "userId INTEGER," + " patientId INTEGER,"
                    + " FOREIGN KEY (userId) REFERENCES USER(id),"
                    + "FOREIGN KEY (patientId) REFERENCES PATIENT(id)" + "PRIMARY KEY (userId, patientId)" + " )";
            stmt.executeUpdate(sql);*/
            sql = "CREATE TABLE IF NOT EXISTS ECG " + "(id     INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + " ecg TEXT NOT NULL, " + " date TEXT NOT NULL,"
                    + " patientId INTEGER REFERENCES PATIENT(id) ON UPDATE CASCADE ON DELETE CASCADE)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS ROLE " + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " type TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS DOCTOR " + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " name TEXT NOT NULL," + " lastname TEXT NOT NULL," + " email TEXT NOT NULL,"
                    + " user_id INTEGER REFERENCES USER(id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS PATIENTDOCTOR " + "(patient_id INTEGER,"
                    + " doctor_id INTEGER, FOREIGN KEY (patient_id) REFERENCES PATIENT(id),"
                    + " FOREIGN KEY (doctor_id) REFERENCES DOCTOR(id),"
                    + " PRIMARY KEY(patient_id, doctor_id))";
            stmt.executeUpdate(sql);
            /*sql = "CREATE TABLE IF NOT EXISTS USER_PATIENT_RELATION " +
                "(user_id INTEGER REFERENCES USER(id), " +
                " patient_id INTEGER REFERENCES PATIENT(id), " +
                " PRIMARY KEY(user_id, patient_id), " +
                " FOREIGN KEY (user_id) REFERENCES USER(id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                " FOREIGN KEY (patient_id) REFERENCES PATIENT(id) ON UPDATE CASCADE ON DELETE CASCADE)";
          stmt.executeUpdate(sql);*/

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
            String sql = "INSERT INTO PATIENT (name, lastname, date_of_birth, email, user_id) VALUES (?,?,?,?,?)";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, p.getName());
            prep.setString(2, p.getLastName());
            prep.setString(3, p.getDob().toString());
            //prep.setString(4, p.getGender());
            prep.setString(4, p.getEmail());
            prep.setInt(5, p.getUserId());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*@Override
    public Patient searchPatient(String username, String password) {
        Patient p = null;
        try {
            String sql = "SELECT * FROM PATIENT WHERE username = ? AND password = ?";
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
        String sql = "SELECT username FROM USER WHERE username = ?";
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
        String sql = "SELECT password FROM USER WHERE username = ?";
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
            String sql = "SELECT * FROM PATIENT";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                //String gender = rs.getString("gender");
                String email = rs.getString("email");
                String fecha = rs.getString("date_of_birth");
                int userId = rs.getInt("user_id");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(fecha, formatter);
                Patient p = new Patient(id, name, date, lastname, email, userId);
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
    public List<User> listAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            String sql = "SELECT * FROM USER";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                byte[] bytes = password.getBytes();
                int roleId = rs.getInt("role_id");
                User u = new User(id, username, bytes, roleId);
                users.add(u);
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    @Override
    public List<ECG> listAllECG(Patient p) {
        List<ECG> ecgs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM ECG WHERE patientId = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, p.getId());
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String ecg = rs.getString("ecg");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(rs.getString("date"), dtf);
                //String ECGFile = rs.getString("ECGFile"); /////////////////////////////////////////////////////////////////////////
                ECG ecg1 = new ECG(id, ecg, startDate);
                ecgs.add(ecg1);
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ecgs;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (!verifyPassword(username, oldPassword)) {
            System.out.println("ERROR: Username and/or current password are not correct");
        } else {
            try {
                String sql = "UPDATE USER SET password = ? WHEN username = ?";
                PreparedStatement prep = c.prepareStatement(sql);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(newPassword.getBytes());
                byte[] hash = md.digest();
                prep.setBytes(1, hash);
                prep.setString(2, username);
                prep.executeUpdate();
                prep.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void addECG(ECG ecg, Patient p) {
        String sq1 = "INSERT INTO ecg (date, ecg, patientId) VALUES (?, ?, ?)";
        PreparedStatement template;
        try {
            template = c.prepareStatement(sq1);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            template.setString(1, ecg.getStartDate().format(dtf));
            template.setString(2, ecg.getEcg());
            template.setInt(3, p.getId());
            template.executeUpdate();
            template.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ECG selectSignalByName(String name) {
        ECG s = new ECG();
        String cadena1;
        String cadena2;
        String ruta1;
        String ruta2;
        List<Integer> values = new ArrayList();
        List<Integer> values2 = new ArrayList();

        LocalDate date;
        FileReader f = null;
        FileReader f2 = null;
        BufferedReader b = null;
        BufferedReader b2 = null;
        try {
            String SQL_code = "SELECT * FROM signal WHERE  ECGFilename = ? ";
            PreparedStatement template = this.c.prepareStatement(SQL_code);
            template.setString(1, name);

            ResultSet result_set = template.executeQuery();
            while (result_set.next()) {
                s.setId(result_set.getInt("signalId"));
                s.setStartDate(LocalDate.parse(result_set.getString("startDate")));
                s.setECGFile(result_set.getString("ECGFilename"));

                // Get the values of the ECG:
                if (name.contains("ECG")) {
                    ruta1 = "../Patient/" + s.getECGFile();
                    f = new FileReader(ruta1);
                    b = new BufferedReader(f);
                    while ((cadena1 = b.readLine()) != null) {
                        String[] separatedCadena = cadena1.replaceAll("\\[", "").replaceAll("]", "").replace(" ", "").split(",");
                        for (int i = 0; i < separatedCadena.length; i++) {
                            values.add(i, Integer.parseInt(separatedCadena[i]));
                        }
                        s.setEcg(values.toString());
                    }
                }
            }
            template.close();

            return s;
        } catch (SQLException selectSignalByType_error) {
            selectSignalByType_error.printStackTrace();
            return null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
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
    public void deletePatient(int id) {
        try {
            String sql = "DELETE FROM Patient WHERE id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, id);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteUserByUserId(int id) {
        try {
            String sql = "DELETE FROM User WHERE id = ?";
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

    @Override
    public void addUser(User user) {
        try {
            String sql = "INSERT INTO USER (username, password, role_id) VALUES (?, ?, ?)";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, user.getUsername());
            prep.setBytes(2, user.getPassword());
            prep.setInt(3, user.getRole_id());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public User getUser(int id) {
        try {
            String sql = "SELECT * FROM USER WHERE id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                //String role = rs.getString("role");
                int role_id = rs.getInt("role_id");
                String username = rs.getString("username");
                byte[] password = rs.getBytes("password");
                User user = new User(role_id, username, password, id);
                return user;
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public User getUser(String username) {
        try {
            String sql = "SELECT * FROM USER WHERE username = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                //String role = rs.getString("role");
                int role_id = rs.getInt("role_id");
                byte[] password = rs.getBytes("password");
                User user = new User(role_id, username, password, id);
                return user;
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void deleteUser(String username, String password) {
        try {
            String sql = "DELETE FROM USER WHERE usernsme = ? AND password = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, username);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            prep.setBytes(2, hash);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addDoctor(Doctor doctor) {
        try {
            String sql = "INSERT INTO DOCTOR (name, lastname, email, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, doctor.getName());
            prep.setString(2, doctor.getLastName());
            prep.setString(3, doctor.getEmail());
            prep.setInt(4, doctor.getUserId());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Doctor getDoctor(int id) {
        try {
            String sql = "SELECT * FROM DOCTOR WHERE id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                int patientId = rs.getInt("patient_id");
                Doctor doctor = new Doctor(id, name, lastName, email, patientId);
                return doctor;
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Doctor getDoctor(String name, String lastname) {
        try {
            String sql = "SELECT * FROM DOCTOR WHERE name = ? AND lastaname = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, name);
            prep.setString(2, lastname);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                int patientId = rs.getInt("patient_id");
                Doctor doctor = new Doctor(id, name, lastname, email, patientId);
                return doctor;
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ArrayList<Doctor> listAllDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            String sql = "SELECT * FROM DOCTOR";
            PreparedStatement prep = c.prepareStatement(sql);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                int userId = rs.getInt("user_id");
                Doctor doctor = new Doctor(id, name, lastName, email, userId);
                doctors.add(doctor);
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doctors;
    }

    @Override
    public ArrayList<Doctor> getDoctorsFromPatientId(int patientId) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            String sql = "SELECT * FROM PATIENTDOCTOR WHERE patient_id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, patientId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                int doctorID = rs.getInt("doctor_id");
                sql = "SELECT * FROM DOCTOR WHERE id = ?";
                prep = c.prepareStatement(sql);
                prep.setInt(1, doctorID);
                ResultSet rs2 = prep.executeQuery();
                if (rs2.next()) {
                    String name = rs2.getString("name");
                    String lastName = rs2.getString("lastname");
                    String email = rs2.getString("email");
                    Doctor doctor = new Doctor(doctorID, name, lastName, email, patientId);
                    doctors.add(doctor);
                }
                rs2.close();
            }

            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doctors;
    }

    @Override
    public void addRole(Role r) {

        String sq1 = "INSERT INTO role (type) VALUES (?)";
        try {
            PreparedStatement preparedStatement = c.prepareStatement(sq1);
            preparedStatement.setString(1, r.getRole());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Role selectRoleById(Integer roleid) {
        try {
            String sql = "SELECT * FROM ROLE WHERE id = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, roleid);
            ResultSet rs = p.executeQuery();
            Role role = null;
            if (rs.next()) {
                role = new Role(roleid, rs.getString("type"));
            }
            p.close();
            rs.close();
            return role;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int getId(String username) {
        String sql1 = "SELECT * FROM USER WHERE username = ?";
        int id = 0;
        try {
            PreparedStatement p = c.prepareStatement(sql1);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            id = rs.getInt("id");
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    //@Override
    /*public void createLinkUserRole(int roleId, int userId) {
        try {
            String sql1 = "UPDATE USER SET role_id = ? WHERE id = ? ";
            PreparedStatement pStatement = c.prepareStatement(sql1);
            pStatement.setInt(1, roleId);
            pStatement.setInt(2, userId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    //@Override
    /*public void createLinkUserDoctor(Integer userId, Integer doctorId) {
        try {
            String sql1 = "INSERT doctor SET userId = ? WHERE doctorId = ? ";
            PreparedStatement pStatement = c.prepareStatement(sql1);
            pStatement.setInt(1, userId);
            pStatement.setInt(2, doctorId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    //@Override
    /*public void createLinkUserPatient(Integer userId, Integer patientId) {
        try {
            String sql1 = "UPDATE PATIENT SET user_id = ? WHERE id = ? ";
            PreparedStatement pStatement = c.prepareStatement(sql1);
            pStatement.setInt(1, userId);
            pStatement.setInt(2, patientId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    @Override
    public void createLinkDoctorPatient(int patientId, int doctorId) {
        try {
            String sql = "INSERT INTO PATIENTDOCTOR (patient_id, doctor_id) VALUES (?,?)";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setInt(1, patientId);
            pStatement.setInt(2, doctorId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Patient selectPatientByUserId(Integer userId) {
        try {
            //Date date;
            String sql = "SELECT * FROM PATIENT WHERE user_id = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();
            Patient p1 = new Patient();
            if (rs.next()) {
                p1.setName(rs.getString("name"));
                p1.setLastName(rs.getString("lastname"));
                //Se utiliza esto para convertir una Date en una LocalDate
                String sqlDate = rs.getString("date_of_birth");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(sqlDate, formatter);
                p1.setEmail(rs.getString("email"));
                //p1.setGender(rs.getString("gender"));
                p1.setId(rs.getInt("id"));

            }
            p.close();
            rs.close();
            return p1;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Doctor selectDoctorByUserId(Integer userId) {
        try {
            String sql = "SELECT * FROM DOCTOR WHERE user_id = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();
            Doctor d = new Doctor();
            if (rs.next()) {
                d.setDoctorId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setLastName(rs.getString("lastname"));
                d.setEmail(rs.getString("email"));
                d.setUserId(userId);
            }
            p.close();
            rs.close();
            return d;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<Patient> selectPatientsByDoctorId(int doctorId) {
        try {
            String sql = "SELECT * FROM PATIENTDOCTOR WHERE doctor_id = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, doctorId);
            ResultSet rs = p.executeQuery();
            List<Patient> pList = new ArrayList<Patient>();

            while (rs.next()) {
                pList.add(selectPatient(rs.getInt("patient_id")));
            }
            p.close();
            rs.close();
            return pList;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Patient selectPatient(Integer id) {
        try {
            String sql = "SELECT * FROM PATIENT WHERE id = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, id);
            ResultSet rs = p.executeQuery();
            Patient patient = null;
            if (rs.next()) {
                patient = new Patient(rs.getString("name"), rs.getString("lastname"), LocalDate.parse(rs.getString("date_of_birth")),
                        rs.getString("email"), rs.getInt("user_id"));//rs.getString("gender"),
                patient.setId(id);
            }
            p.close();
            rs.close();
            return patient;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
