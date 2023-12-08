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
import static Utilities.Communication.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria
 */
public class Client implements Runnable, Serializable {

    public static Socket socket;

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

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9000);
            Client client = new Client(socket);
            Thread clientThread = new Thread(client);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(client);
            clientThread.start();
            //OutputStream os = socket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        createRoles(manager); //establezco los tipos de role que puede haber
        Utilities.ClientMethods.firstlogin(manager);

        InputStream inputStream;
        OutputStream outputStream;

        try {

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            //BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(outputStream, true);
            menu(inputStream, outputStream, br, pw, manager);

        } catch (IOException e) {
            System.out.println("An error has occured");
        }
    }

    public static void menu(InputStream inputStream, OutputStream outputStream, BufferedReader br, PrintWriter pw, JDBCManager manager) {
        int option = 1;
        exit = false;
        do {
            try {
                BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("@@                                                                  @@");
                System.out.println("@@                 Welcome.                                         @@");
                System.out.println("@@                 1. Register                                      @@");
                System.out.println("@@                 2. Login                                         @@");
                System.out.println("@@                 0. Exit                                          @@");
                System.out.println("@@                                                                  @@");
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.print("Select an option: ");
                option = Integer.parseInt(consola.readLine());
                switch (option) {
                    case 1:
                        pw.println("1");
                        Utilities.ClientMethods.register(br, pw, manager);
                        break;
                    case 2:
                        pw.println("2");
                        User user = Utilities.ClientMethods.login(br, pw, manager); //user hace log in
                        if (user.getRole_id() == 1) { //es paciente
                            pw.println("patient"); //envia patient al client
                            Patient p = manager.selectPatientByUserId(user.getId()); //selecciona paciente asociado al usuario userId=Id de la clase user
                            Utilities.Communication.sendPatient(pw, p, manager); //envía la información del paciente al cliente
                            patientMenu(user, br, pw, manager); //menu paciente
                        } else if (user.getRole_id() == 2) { //es medico
                            pw.println("doctor"); //envía doctor al client
                            Doctor d = manager.selectDoctorByUserId(user.getId()); //selecciona doctor asociado a usuario
                            Utilities.Communication.sendDoctor(pw, d, manager); //envia la inforamcion del doctor al cliente
                            doctorMenu(user, br, pw, manager); //menu doctor
                        }
                        break;
                    case 0:
                        pw.println("0");
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

    public static void patientMenu(User u, BufferedReader br, PrintWriter pw, JDBCManager manager) {
        Patient p = manager.selectPatientByUserId(u.getId());
        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
        int option;
        try {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@                                                                  @@");
            System.out.println("@@                 0. Exit                                          @@");
            System.out.println("@@                 1. Record new Signal                             @@");
            System.out.println("@@                 2. View my Signals                               @@");
            System.out.println("@@                 3. Assign to a new doctor                        @@");
            System.out.println("@@                                                                  @@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.print("Select an option: ");
            option = Integer.parseInt(consola.readLine());
            switch (option) {
                case 0:
                    exit = true;
                    Server.ReleaseClientThread(socket);
                    break;
                case 1:
                    /*int userid1 = manager.getId(u.getUsername()); //lee ID del user
                    Patient p1 = manager.selectPatientByUserId(userid1); //se selecciona al paciente asociado con el id del user
                    ECG s = Utilities.Communication.receiveSignal(br); //recive una señal ECG del cliente
                    s.CreateECGFilename(p1.getName()); //crea nombre de archivo para la señal ECG
                    s.StartDate(); //se inicia fecha
                    s.StoreECGinFile(p1.getName()); //almacena la señal ECG
                    manager.addECG(s, p1); //se añade la señal ECG al paciente*/
                    break;
                case 2:
                    /*int userid = manager.getId(u.getUsername()); //lee ID del user
                    Patient p = manager.selectPatientByUserId(userid); //se selecciona al paciente asociado con el id del user
                    System.out.println("You are going to record your ECG signal");
                    Utilities.Communication.recordSignal(p, pw);*/
                    break;
                /*case 3:
                    int userid3 = userman.getId(u.getUsername());
                    Patient p3 = patientman.selectPatientByUserId(userid3);
                    Utilities.Communication.sendAllSignal(br, pw, signalman, p3.getId()); //envía todas las señales ECG asociadas al paciente al cliente
                    String filename = br.readLine();
                    ECG s1 = signalman.selectSignalByName(filename); //selecciona la señak ECG por nombre
                    pw.println(s1.toString()); //envía la representación en acdena de la señal ECG al cliente
                    break;*/
                case 3:
                    recordSignal(p, pw, manager);
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createRoles(JDBCManager roleman) {
        if (manager.selectRoleById(1) == null) {
            Role role1 = new Role("patient");
            roleman.addRole(role1);
        }
        if (manager.selectRoleById(2) == null) {
            Role role2 = new Role("doctor");
            roleman.addRole(role2);
        }
    }

    public static void doctorMenu(User u, BufferedReader br, PrintWriter pw, JDBCManager manager) {
        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                 1. View data from one of my patients             @@");
        System.out.println("@@                 2. View signal from one of my patients           @@");
        System.out.println("@@                 3. List all my patients                          @@");
        System.out.println("@@                 4. Assign one patient                            @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("Select an option: ");
        int option;

        try {
            option = Integer.parseInt(consola.readLine());
            switch (option) {
                case 0:
                    exit = true;
                    break;
                case 1:
                    Utilities.ClientMethods.registerDoctor(br, pw, manager);
                    break;
                case 2:
                    int userid = manager.getId(u.getUsername());
                    Doctor d = manager.selectDoctorByUserId(userid);
                    List<Patient> patientList = manager.selectPatientsByDoctorId(manager.getId(d.getName()));
                    Utilities.Communication.sendPatientList(patientList, pw, br); //envía lista de pacientes al cliente
                    break;
                case 3:
                    int userid1 = manager.getId(u.getUsername());
                    Doctor d1 = manager.selectDoctorByUserId(userid1);
                    List<Patient> patientList1 = manager.selectPatientsByDoctorId(manager.getId(d1.getName()));
                    Utilities.Communication.sendPatientList(patientList1, pw, br);
                    Patient p1 = manager.selectPatientByUserId(userid1); //se selecciona al paciente asociado con el id del user
                    List<ECG> ecgs = manager.listAllECG(p1);
                    Utilities.Communication.sendAllSignals(pw, br, ecgs);
                    String filename = br.readLine();
                    ECG s1 = manager.selectSignalByName(filename);
                    pw.println(s1.toString());
                    break;
                case 4:
                    int userid2 = manager.getId(u.getUsername());
                    Doctor d2 = manager.selectDoctorByUserId(userid2);
                    List<Patient> patientList2 = manager.selectPatientsByDoctorId(manager.getId(d2.getName()));
                    Utilities.Communication.sendPatientList(patientList2, pw, br);
                    Patient pToDelete = manager.selectPatient(userid2);
                    manager.deletePatient(pToDelete.getId());
                    manager.deleteUserByUserId(userid2);
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
