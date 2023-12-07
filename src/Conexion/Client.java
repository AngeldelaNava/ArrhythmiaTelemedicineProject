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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria
 */
public class Client implements Runnable {

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
        Utilities.ClientMethods.firstlogin(user, doctor, role);

        InputStream inputStream;
        OutputStream outputStream;

        try {

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter pw = new PrintWriter(outputStream, true);
            menu(inputStream, outputStream, br, pw, user, patient, ecg, doctor);

        } catch (IOException e) {
            System.out.println("An error has occured");
        }
    }

    public static void menu(InputStream inputStream, OutputStream outputStream, BufferedReader br, PrintWriter pw, JDBCManager userman, JDBCManager patientman, JDBCManager signalman, JDBCManager doctorman) {
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
                            User user = Utilities.ClientMethods.login(br, pw, userman); //user hace log in
                            if (user.getRole_id() == 1) { //es paciente
                                pw.println("patient"); //envia patient al client
                                Patient p = patientman.selectPatientByUserId(user.getId()); //selecciona paciente asociado al usuario userId=Id de la clase user
                                Utilities.Communication.sendPatient(pw, p); //envía la información del paciente al cliente
                                patientMenu(user, br, pw, userman, patientman, signalman); //menu paciente
                            } else if (user.getRole_id() == 2) { //es medico
                                pw.println("doctor"); //envía doctor al client
                                Doctor d = doctorman.selectDoctorByUserId(user.getId()); //selecciona doctor asociado a usuario
                                Utilities.Communication.sendDoctor(pw, d); //envia la inforamcion del doctor al cliente
                                doctorMenu(user, br, pw, userman, patientman, signalman, doctorman); //menu doctor
                            }
                        break;
                    case 0:
                        Server.releaseClientResources(inputStream, outputStream, socket); //terminar conexión con servidor
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
                    exit = true;
                    Server.ReleaseClientThread(socket);
                    break;
                case 1:
                    int userid1 = userman.getId(u.getUsername()); //lee ID del user
                    Patient p1 = patientman.selectPatientByUserId(userid1); //se selecciona al paciente asociado con el id del user
                    ECG s = Utilities.Communication.receiveSignal(br); //recive una señal ECG del cliente
                    s.CreateECGFilename(p1.getName()); //crea nombre de archivo para la señal ECG
                    s.StartDate(); //se inicia fecha
                    s.StoreECGinFile(p1.getName()); //almacena la señal ECG
                    signalman.addECG(s, p1); //se añade la señal ECG al paciente
                    break;
                case 2:
                    int userid = userman.getId(u.getUsername()); //lee ID del user
                    Patient p = patientman.selectPatientByUserId(userid); //se selecciona al paciente asociado con el id del user
                    System.out.println("You are going to record your ECG signal");
                    Utilities.Communication.recordSignal(p, pw);
                    break;
                case 3:
                    int userid3 = userman.getId(u.getUsername());
                    Patient p3 = patientman.selectPatientByUserId(userid3);
                    Utilities.Communication.sendAllSignal(br, pw, signalman, p3.getId()); //envía todas las señales ECG asociadas al paciente al cliente
                    String filename = br.readLine();
                    ECG s1 = signalman.selectSignalByName(filename); //selecciona la señak ECG por nombre
                    pw.println(s1.toString()); //envía la representación en acdena de la señal ECG al cliente
                    break;

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
                    exit = true;
                    break;
                case 1:
                    Utilities.ClientMethods.registerDoctor(br, pw, userman, doctorman);
                    break;
                case 2:
                    int userid = userman.getId(u.getUsername());
                    Doctor d = doctorman.selectDoctorByUserId(userid);
                    List<Patient> patientList = patientman.selectPatientsByDoctorId(doctorman.getId(d.getName()));
                    Utilities.Communication.sendPatientList(patientList, pw, br); //envía lista de pacientes al cliente
                    break;
                case 3:
                    int userid1 = userman.getId(u.getUsername());
                    Doctor d1 = doctorman.selectDoctorByUserId(userid1);
                    List<Patient> patientList1 = patientman.selectPatientsByDoctorId(doctorman.getId(d1.getName()));
                    Utilities.Communication.sendPatientList(patientList1, pw, br);
                    Utilities.Communication.sendAllSignals(br, pw, signalman);
                    String filename = br.readLine();
                    ECG s1 = signalman.selectSignalByName(filename);
                    pw.println(s1.toString());
                    break;
                case 4:
                    int userid2 = userman.getId(u.getUsername());
                    Doctor d2 = doctorman.selectDoctorByUserId(userid2);
                    List<Patient> patientList2 = patientman.selectPatientsByDoctorId(doctorman.getId(d2.getName()));
                    Utilities.Communication.sendPatientList(patientList2, pw, br);
                    int medcard3 = Integer.parseInt(br.readLine());
                    if (medcard3 == 2) {
                        option = 2;
                        break;
                    } else if (medcard3 == 0) {
                        break;
                    }
                    Patient pToDelete = patientman.selectPatient(medcard3);
                    patientman.deletePatient(pToDelete.getId());
                    String medcard = "" + medcard3;
                    userman.deleteUserByUserName(medcard);
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
