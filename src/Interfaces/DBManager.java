/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Pojos.Doctor;
import Pojos.ECG;
import Pojos.Patient;
import Pojos.Role;
import Pojos.User;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author angel
 */
public interface DBManager {

    public void connect();

    public void disconnect();

    public void createTables();

    public Connection getConnection();

    public void addPatient(Patient p);

    //public Patient searchPatient(String username, String password);
    public boolean verifyUsername(String username);

    public boolean verifyPassword(String username, String passwordIntroduced);

    public List<Patient> listAllPatients();

    public List<User> listAllUsers();

    public List<ECG> listAllECG(Patient p);

    public void changePassword(String username, String oldPassword, String newPassword);

    public void addECG(ECG ecg, Patient p);

    public ECG selectSignalByName(String name);

    public ECG findECG(int id);

    public ArrayList<String> findECGByPatientId(int patient_id);

    public void deleteECG(int id);

    public void deletePatient(int id);

    public void deleteUserByUserId(int id);

    public void setECG(ECG ecg, int id);

    public void addUser(User user);

    public User getUser(int id);

    public User getUser(String username);

    public void deleteUser(String username, String password);

    public void addDoctor(Doctor doctor);

    public Doctor getDoctor(int id);

    public Doctor getDoctor(String name, String lastname);

    public ArrayList<Doctor> listAllDoctors();

    public ArrayList<Doctor> getDoctorsFromPatientId(int patientId);

    public void addRole(Role r);

    public Role selectRoleById(Integer roleid);

    public int getId(String username);

    public void createLinkDoctorPatient(int patientId, int doctorId);

    public Patient selectPatientByUserId(Integer userId);

    public Doctor selectDoctorByUserId(Integer userId);

    public List<Patient> selectPatientsByDoctorId(int doctorId);

    public Patient selectPatient(Integer id);
}
