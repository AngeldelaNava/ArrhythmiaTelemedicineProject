/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Pojos.ECG;
import Pojos.Patient;
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

    public Patient searchPatient(String username, String password);

    public boolean verifyUsername(String username);

    public boolean verifyPassword(String username, String passwordIntroduced);

    public List<Patient> listAllPatients();

    public void changePassword(String username, String oldPassword, String newPassword);

    public void addECG(ECG ecg);

    public ECG findECG(int id);

    public ArrayList<String> findECGByPatientId(int patient_id);

    public void deleteECG(int id);

    public void setECG(ECG ecg, int id);
}
