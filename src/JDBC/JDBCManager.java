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
import static Pojos.Patient.formatDate;
import Pojos.Role;
import Pojos.User;
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
            String sql = "INSERT INTO PATIENT (name, lastname, date, gender, MAC) VALUES (?,?,?,?, ?)";
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
            String sql = "SELECT * FROM patients";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String gender = rs.getString("gender");
                String email = rs.getString("email");
                String fecha = rs.getString("date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(fecha, formatter);
                Patient p = new Patient(id, name, date, lastname, gender, email);
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
                String sql = "UPDATE USER SET password = ? WHEN username = ?";
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

    @Override
    public void addUser(User user) {
        try {
            String sql = "INSERT INTO USER (role, username, password, role_id) VALUES (?, ?, ?, ?)";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, user.getRole());
            prep.setString(2, user.getUsername());
            prep.setBytes(3, user.getPassword());
            prep.setInt(4, user.getRole_id());
            prep.executeUpdate();
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
                String role = rs.getString("role");
                int role_id = rs.getInt("role_id");
                String username = rs.getString("username");
                byte[] password = rs.getBytes("password");
                User user = new User(role_id, role, username, password, id);
                return user;
            }
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
                String role = rs.getString("role");
                int role_id = rs.getInt("role_id");
                byte[] password = rs.getBytes("password");
                User user = new User(role_id, role, username, password, id);
                return user;
            }
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
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addDoctor(Doctor doctor) {
        try {
            String sql = "INSERT INTO DOCTOR (name, lastname, email, patient_id) VALUES (?, ?, ?, ?)";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setString(1, doctor.getName());
            prep.setString(2, doctor.getLastName());
            prep.setString(3, doctor.getEmail());
            prep.setInt(4, doctor.getUserId());
            prep.executeUpdate();
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
                int patientId = rs.getInt("patient_id");
                Doctor doctor = new Doctor(id, name, lastName, email, patientId);
                doctors.add(doctor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doctors;
    }

    @Override
    public ArrayList<Doctor> getDoctorsFromPatientId(int patientId) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            String sql = "SELECT * FROM DOCTOR WHERE patient_id = ?";
            PreparedStatement prep = c.prepareStatement(sql);
            prep.setInt(1, patientId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                Doctor doctor = new Doctor(id, name, lastName, email, patientId);
                doctors.add(doctor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doctors;
    }

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

    public Role selectRoleById(Integer roleid) {
        try {
            String sql = "SELECT * FROM role WHERE roleid = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, roleid);
            ResultSet rs = p.executeQuery();
            Role role = null;
            if (rs.next()) {
                role = new Role(rs.getInt("roleid"), rs.getString("type"));
            }
            p.close();
            rs.close();
            return role;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    //@Override

    public int getId(String username) {
        String sql1 = "SELECT * FROM users WHERE userName = ?";
        int id = 0;
        try {
            PreparedStatement preparedStatement = c.prepareStatement(sql1);
            PreparedStatement p = c.prepareStatement(sql1);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            id = rs.getInt("userid");
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    //@Override
    public void createLinkUserRole(int roleId, int userId) {
        try {
            String sql1 = "INSERT users SET userRoleid = ? WHERE userid = ? ";
            PreparedStatement pStatement = c.prepareStatement(sql1);
            pStatement.setInt(1, roleId);
            pStatement.setInt(2, userId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //@Override
    public void createLinkUserDoctor(Integer userId, Integer doctorId) {
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
    }

    public void createLinkUserPatient(Integer userId, Integer patientId) {
        try {
            String sql1 = "INSERT patient SET userId = ? WHERE id = ? ";
            PreparedStatement pStatement = c.prepareStatement(sql1);
            pStatement.setInt(1, userId);
            pStatement.setInt(2, patientId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createLinkDoctorPatient(int medCardNumber, int doctorId) {
        try {
            String sql = "INSERT INTO doctor_patient (patient_id, doctor_id) VALUES (?,?)";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setInt(1, medCardNumber);
            pStatement.setInt(2, doctorId);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // @Override
    public User checkPassword(String username, String password) {
        User user = new User();
        try {
            String sql = "SELECT * FROM users WHERE userName = ? AND userPassword = ? ";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                user.setPassword(rs.getBytes("userPassword"));
                user.setUsername(rs.getString("userName"));
            }
            preparedStatement.close();
            rs.close();
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            user = null;
        }
        return user;
    }

    public User selectUserByUserId(Integer userId) {
        try {
            //Date date;
            String sql = "SELECT * FROM users WHERE userid = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();
            User u = new User();
            if (rs.next()) {
                u.setPassword(rs.getBytes("userPassword"));
                u.setRole_id(rs.getInt("userRoleid"));
                u.setId(userId);
                u.setUsername(rs.getString("userName"));
            }
            p.close();
            rs.close();
            return u;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Patient selectPatientByUserId(Integer userId) {
        try {
            //Date date;
            String sql = "SELECT * FROM patient WHERE userid = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();
            Patient p1 = new Patient();
            if (rs.next()) {
                p1.setName(rs.getString("Name"));
                p1.setLastName(rs.getString("LastName"));
                //Se utiliza esto para convertir una Date en una LocalDate
                java.sql.Date sqlDate = rs.getDate("Date of Birth");
                LocalDate localDate = sqlDate.toLocalDate();
                p1.setDob(localDate);
                p1.setEmail(rs.getString("Email"));
                p1.setGender(rs.getString("Gender"));
                p1.setId(rs.getInt("patientId"));
                p1.setId(userId);

            }
            p.close();
            rs.close();
            return p1;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    //@Override

    public List<Doctor> selectAllDoctors() throws SQLException {
        String sql = "SELECT * FROM doctor";
        PreparedStatement p = c.prepareStatement(sql);

        ResultSet rs = p.executeQuery();
        List<Doctor> dList = new ArrayList<Doctor>();
        while (rs.next()) {
            dList.add(new Doctor(rs.getInt("doctorId"), rs.getString("name"), rs.getString("lastName"), rs.getString("email")));
        }
        p.close();
        rs.close();
        return dList;
    }

    public Doctor selectDoctorByUserId(Integer userId) {
        try {
            String sql = "SELECT * FROM doctor WHERE userid = ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();
            Doctor d = new Doctor();
            if (rs.next()) {
                d.setDoctorId(rs.getInt("doctorId"));
                d.setName(rs.getString("Name"));
                d.setLastName(rs.getString("lastName"));
                d.setEmail(rs.getString("Email"));
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

}
