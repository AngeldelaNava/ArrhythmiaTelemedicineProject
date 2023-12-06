/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import JDBC.JDBCManager;
import Pojos.Doctor;
import Pojos.ECG;
import Pojos.Patient;
import Pojos.Role;
import Pojos.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utilities.*;
import java.rmi.NotBoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author maria
 */
public class Client implements Runnable{
    public static Socket socket;
    public static JDBCManager patient;
    public static JDBCManager doctor;
    public static JDBCManager ecg;
    public static JDBCManager user;
    public static JDBCManager role;

    /*public static UserManager userman;
    public static RoleManager roleman;
    public static PatientTSManager patientman;
    public static DoctorManager doctorman;
    public static SignalManager signalman;*/
    public static JDBCManager manager;
    
    public static boolean exit; 

    public Client(Socket socket) {
        this.socket = socket;
    }

   @Override
    public void run() {
        
        manager = new JDBCManager();
        manager.connect();
        //Connection c = manager.getConnection();
        /*userman = manager.getUserManager();
        roleman = manager.getRoleManager();
        patientman = manager.getPatientManager();
        doctorman = manager.getDoctorManager();
        signalman = manager.getSignalManager();*/
        manager.createTables(); //creo las tablas      
        createRoles(role); //establezco los tipos de role que puede haber
        Utilities.ClientMethods.firstlogin(userman,doctorman,roleman);
      
        InputStream inputStream;
        OutputStream outputStream;

        try {
          
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter pw = new PrintWriter(outputStream, true);
            first(inputStream,outputStream,br, pw, userman, patientman, signalman, doctorman);

        } catch (IOException e) {
            System.out.println("An error has occured");
        }
    }

    public static void menu(InputStream inputStream,OutputStream outputStream,BufferedReader br, PrintWriter pw, JDBCManager userman, JDBCManager patientman, JDBCManager signalman, JDBCManager doctorman) {
        int option = 1;
        exit = false;
        do {
            try {
                option = Integer.parseInt(br.readLine());
               
                switch (option) {
                    case 1:
                        Utilities.ClientMethods.registerPatient(br, pw, userman, patientman, doctorman);
                        break;
                    case 2:
                        int a = Integer.parseInt(br.readLine());
                        if (a == 1){
                            break;
                        }else{
                            User user = Utilities.ClientMethods.login(br, pw, userman);

                            if (user.getRole_id() == 1) {
                                pw.println("patient");
                                int b = Integer.parseInt(br.readLine());
                                if(b==1){
                                    break;
                                }else{
                                Patient p = patientman.selectPatientByUserId(user.getUserId());
                                Utilities.Communication.sendPatient(pw, p);
                                patientMenu(user, br, pw, userman, patientman, signalman);
                                }
                            } else if (user.getRole() == 2) {
                                pw.println("doctor");
                                int c = Integer.parseInt(br.readLine());
                                if(c==1){
                                    break;
                                }else{
                                Doctor d = doctorman.selectDoctorByUserId(user.getUserId());
                                Utilities.Communication.sendDoctor(pw, d);
                                doctorMenu(user, br, pw, userman, patientman, signalman, doctorman);
                                }
                            }
                        }
                        break;
                    case 0: 
                        Server.releaseClientResources(inputStream,outputStream,socket);
                        break;   
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (option != 0);
    }

    public static void patientMenu(User u, BufferedReader br, PrintWriter pw, JDBCManager userman, JDBCManager patientman, JDBCManager signalman) {

        int option = 1;
            try {
                option = Integer.parseInt(br.readLine());
                switch (option) {
                    case 0:
                        exit= true;
                        Server.ReleaseClientThread(socket);
                        break;
                    case 1:
                        int userid1 = userman.getId(u.getUsername());
                        Patient p1 = patientman.selectPatientByUserId(userid1);
                        ECG s = Utilities.Communication.recieveSignal(br, pw);
                        s.CreateECGFilename(p1.getPatientName());
                        s.CreateEMGFilename(p1.getPatientName());
                        s.StartDate();
                        s.StoreECGinFile(p1.getPatientName());
                        s.StoreEMGinFile(p1.getPatientName());
                        signalman.addSignal(s, p1);
                        break;
                    case 3:
                        int userid3 = userman.getId(u.getUsername());
                        Patient p3 = patientman.selectPatientByUserId(userid3);
                        Utilities.Communication.sendAllSignal(br, pw, signalman, p3.getMedCardId());
                        String filename = br.readLine();
                        ECG s1 = signalman.selectSignalByName(filename);
                        pw.println(s1.toString());
                        break;
                    case 4:
                        Patient updatep = Utilities.Communication.receivePatient(br);
                        patientman.editPatient(updatep.getPatientName(), updatep.getPatientSurname(), updatep.getPatientDob(), updatep.getPatientEmail(), updatep.getPatientGender());
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public static void createRoles(JDBCManager roleman) {
        Role role1 = new Role("patient");
        roleman.addRole(role1);
        Role role2 = new Role("doctor");
        roleman.addRole(role2);
    }

    public static void doctorMenu(User u, BufferedReader br, PrintWriter pw, JDBCManager userman, JDBCManager patientman, JDBCManager signalman, JDBCManager doctorman) {
        int option = 1;
       
            try {
                option = Integer.parseInt(br.readLine());
                switch (option) {
                    case 0:
                        exit= true;
                        break;
                    case 1:
                        Utilities.ClientMethods.registerDoctor(br, pw, userman, doctorman);
                        break;
                    case 2:
                        int userid = userman.getId(u.getUsername());
                        Doctor d = doctorman.selectDoctorByUserId(userid);
                        List<Patient> patientList = patientman.selectPatientsByDoctorId(doctorman.getId(d.getName()));
                        Utilities.Communication.sendPatientList(patientList, pw, br);
                        break;
                    case 3:
                        int a = Integer.parseInt(br.readLine());
                        while (a != 0) {
                            int uid = userman.getId(u.getUsername());
                            Doctor d3 = doctorman.selectDoctorByUserId(uid);
                            List<Patient> pList = patientman.selectPatientsByDoctorId(doctorman.getId(d3.getDoctorName()));
                            Utilities.Communication.sendPatientList(pList, pw, br);
                            int medcard = Integer.parseInt(br.readLine());
                            Patient p = patientman.selectPatient(medcard);
                            Utilities.Communication.sendPatient(pw, p);
                            Patient updatep = Utilities.Communication.receivePatient(br);
                            patientman.editPatient(updatep.getPatientName(), updatep.getPatientSurname(), updatep.getPatientDob(), updatep.getPatientEmail(), updatep.getPatientGender());
                            a = Integer.parseInt(br.readLine());
                        }
                        break;
                    case 4:
                        int userid1 = userman.getId(u.getUsername());
                        Doctor d1 = doctorman.selectDoctorByUserId(userid1);
                        List<Patient> patientList1 = patientman.selectPatientsByDoctorId(doctorman.getId(d1.getDoctorName()));
                        Utilities.Communication.sendPatientList(patientList1, pw, br);
                        int medcard2 = Integer.parseInt(br.readLine());
                        if(medcard2==2){
                            option=2;
                            break;
                        }else if (medcard2==0){
                            break;
                        }
                        Utilities.Communication.sendAllSignal(br, pw, signalman, medcard2);
                        String filename = br.readLine();
                        ECG s1 = signalman.selectSignalByName(filename);
                        pw.println(s1.toString());
                        break;
                    case 5:
                        int userid2 = userman.getId(u.getUsername());
                        Doctor d2 = doctorman.selectDoctorByUserId(userid2);
                        List<Patient> patientList2 = patientman.selectPatientsByDoctorId(doctorman.getId(d2.getDoctorName()));
                        Utilities.Communication.sendPatientList(patientList2, pw, br);
                        int medcard3 = Integer.parseInt(br.readLine());
                        if(medcard3==2){
                            option=2;
                            break;
                        }else if (medcard3==0){
                            break;
                        }
                        Patient pToDelete = patientman.selectPatient(medcard3);
                        patientman.deletePatientByMedicalCardId(pToDelete.getMedCardId());
                        String medcard = ""+medcard3;
                        userman.deleteUserByUserName(medcard);
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
