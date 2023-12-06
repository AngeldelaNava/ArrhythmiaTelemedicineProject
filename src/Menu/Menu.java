/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Menu;

import Pojos.Doctor;
import Pojos.Patient;
import Pojos.User;
import static Utilities.UtilitiesRead.readDate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria
 */
public class Menu {

    public static InputStream console = (System.in);
    //aqui tenemos que crear los managers

    public static void main(String[] args) throws Exception {
        try {
            Socket socket = Utilities.Communication.connectToServer(); //te devuelve un socket en el port 9000 y te verifica si se ha podido coonectar
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            Scanner sc = new Scanner(System.in);
            int choice;
            while (true) {
                try {
                    System.out.println("                 ~~~~~~~Welcome to our Arrhythmia Menu~~~~~~~         ");
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    System.out.println("@@                                                                  @@");
                    System.out.println("@@                 Welcome.                                         @@");
                    System.out.println("@@                 1. Register                                      @@");
                    System.out.println("@@                 2. Login                                         @@");
                    System.out.println("@@                 0. Exit                                          @@");
                    System.out.println("@@                                                                  @@");
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    System.out.println("Realizado por Ángel De la Nava, Jaime Prats, Maria Rueda y Jaime Soler");
                    choice = sc.nextInt();
                    System.out.println(choice);
                    printWriter.println(choice); //se lo pasas al server
                    switch (choice) {
                        case 0:
                            Utilities.Communication.exitFromServer(printWriter, bf, inputStream, outputStream, socket);
                            System.exit(0); //sales de la aplicacion
                        case 1:
                            register(bf, printWriter);//pasas bf para escribir y printWriter para la conexión con servidor
                            break;
                        case 2:
                            login(socket, inputStream, outputStream, bf, printWriter);
                            break;
                        default:
                            System.out.println("Please introduce a valid option."); //si introduce otro numero que no sea [0,2]
                    }
                } catch (Exception e) {
                    System.out.println("Please introduce a valid option.");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void register(BufferedReader br, PrintWriter pw) {
        try {
            Scanner sc = new Scanner(System.in);//para escribir en consola
            System.out.println("Introduce your personal data: ");
            Patient p = createPatient(br, pw);
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Patient createPatient(BufferedReader br, PrintWriter pw) {
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
        System.out.print("D: Doctor: \n");
        System.out.print("P: Patient: \n");
        String role = sc.next();
        user.setRole(role);
        System.out.print("Username: ");
        String username = sc.next();
        user.setUsername(username);
        System.out.print("Password: ");
        String password = sc.next();
        user.setUsername(password);
        return p;
    }

    private static void login(Socket socket, InputStream inputStream, OutputStream outputStream, BufferedReader bf, PrintWriter pw) throws Exception {
        Scanner sc = new Scanner(System.in);
        User user = new User();
        System.out.println("Please enter your username and password:");
        System.out.println("Username:");
        String username = sc.next();
        System.out.println("Password:");
        String password = sc.next();
        user.setPassword(password);
        user.setUsername(username);
        Utilities.Communication.sendUser(pw, user);//se lo pasas al server
        String line = bf.readLine();
        if (line.equals("Wrong username or password")) {
            System.out.println("Wrong username or password");
        } else if (line.equals("patient")) {
            System.out.println("I received that user is a patient");
            patientMenu(socket, inputStream, outputStream, bf, pw, user.getId());
        } else if (line.equals("doctor")) {
            System.out.println("I received that user is a doctor");
            doctorMenu(socket, inputStream, outputStream, bf, pw);
        }
    }

    public static void patientMenu(Socket socket, InputStream inputStream, OutputStream outputStream, BufferedReader br, PrintWriter pw, int userId) throws Exception {
        Scanner sc = new Scanner(System.in);
        int option = 0;
        Patient patient = Utilities.Communication.receivePatient(br);
        do {
            System.out.println("Choose an option [0-3]:"
                    + "\n1. Start recording \n2. Consult my recordings \n0.Exit");
            option = sc.nextInt();
            pw.println(option);
            switch (option) {
                case 0:
                    Utilities.Communication.exitFromServer(pw, br, inputStream, outputStream, socket);
                    System.exit(0);
                case 1:
                    System.out.println("You are going to record your ECG signal");
                    Utilities.Communication.recordSignal(patient, pw);
                    break;
                case 2:
                    System.out.println("Here you can consult all your signals");
                    showSignals(br, pw);
                    break;
                default:
                    System.out.println("Not a valid option.");
                    break;
            }
        } while (true);
    }

    private static void doctorMenu(Socket socket, InputStream inputStream, OutputStream outputStream, BufferedReader bf, PrintWriter pw) throws Exception {
        Scanner sc = new Scanner(System.in);
        String nextLine;
        int option = 0;
        Doctor doctor = Utilities.Communication.receiveDoctor(bf);
        System.out.println("Hello Dr. " + doctor.getLastName());
        do {
            int a = 0;

            System.out.println("Choose an option[0-2]:");
            System.out.println("\n1. Register a new Doctor \n2. See list of all my patients \n3. Edit Patient \n4. Consult recordings of a patient \n5. Delete  \n 0. Exit");
            do {
                try {
                    option = sc.nextInt();
                    pw.println(option);
                    a = 1;
                } catch (Exception e) {
                    nextLine = sc.next();
                    System.out.println("Please select a valid option.");
                }
            } while (a == 0);

            switch (option) {
                case 0:
                    System.out.println("Thank you for using our system");
                    Utilities.Communication.exitFromServer(pw, bf, inputStream, outputStream, socket);
                    System.exit(0);
                case 1:
                    System.out.println("Register a new Doctor");
                    createDoctor(bf, pw);
                    break;
                case 2:
                    System.out.println("See list of all my patients");
                    Utilities.Communication.receivePatientList(bf);
                    break;
                case 3:
                    System.out.println("Consult recordings of a patient");
                    Utilities.Communication.receivePatientList(bf);
                    System.out.println("Introduce medcard of patient to update:");
                    int medcard2 = sc.nextInt();
                    pw.println(medcard2);
                    showSignals(bf, pw);
                    break;
                case 4:
                    System.out.println("Delete Patient");
                    Utilities.Communication.receivePatientList(bf);
                    System.out.println("Introduce medcard of patient to update:");
                    int medcard5 = sc.nextInt();
                    pw.println(medcard5);
                    String line = bf.readLine();
                    if (line.equalsIgnoreCase("success")) {
                        System.out.println("Patient succesfully deleted");
                    } else {
                        System.out.println("Error with deleting");
                    }
                    break;
                default:
                    System.out.println("Not a valid option.");
                    break;
            }
        } while (true);
    }

    private static Patient selectPatient(BufferedReader br, PrintWriter pw) throws Exception {
        Scanner sc = new Scanner(System.in);
        //Show list with all patients.
        List<String> CompletePatientList = Utilities.Communication.receivePatientList(br);
        for (int i = 0; i < CompletePatientList.size(); i++) {
            System.out.println(CompletePatientList.get(i));
        }
        //Chose a Patient
        List<Patient> patientList = new ArrayList();
        Patient patient = null;
        while (patientList.isEmpty()) {
            Integer medCard = null;
            System.out.println(patientList.toString());
            System.out.println("Enter the medical card number of the chosen patient: ");
            try {
                medCard = sc.nextInt();
            } catch (Exception ex) {
                System.out.println("Not a valid medical card number ONLY NUMBERS");
            }
            pw.print(medCard);
            patient = Utilities.Communication.receivePatient(br);
        }
        return patient;
    }

    private static void deletePatient(BufferedReader br, PrintWriter pw) throws Exception {
        Scanner sc = new Scanner(System.in);
        //Show list with all patients.
        List<String> CompletePatientList = Utilities.Communication.receivePatientList(br);
        for (int i = 0; i < CompletePatientList.size(); i++) {
            System.out.println(CompletePatientList.get(i));
        }

        //Chose a Patient to delete
        System.out.println("Introduce de medical card number of the patient to delete: ");
        String medcard = sc.nextLine();
        pw.println(medcard);
        String line = br.readLine();
        if (line.equalsIgnoreCase("success")) {
            System.out.println("Patient succesfully deleted");
        } else {
            System.out.println("Error with deleting");
        }
    }

    public static void createDoctor(BufferedReader br, PrintWriter pw) throws Exception {
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

        System.out.println("Let's proceed with the registration, the username and password will be autogenerated by the system:");
        Utilities.Communication.sendDoctor(pw, d);
        User user = Utilities.Communication.receiveUser(br);
        System.out.println("The autogenerated username is: " + user.getUsername());
        System.out.println("The autogenerated password is: " + user.getPassword());
        String line = br.readLine();
        if (line.equals("Doctor successfully registered")) {
            System.out.println("Success");
        } else {
            System.out.println("Doctor not registered");
        }
    }

    private static void showSignals(BufferedReader br, PrintWriter pw) throws Exception {
        //int size = Integer.parseInt(br.readLine());
        Scanner sc = new Scanner(System.in);
        //Show list with all signals
        List<String> signalFilenames = Utilities.Communication.ShowSignals(br, pw);
        System.out.println(signalFilenames.size());

        for (int i = 0; i < signalFilenames.size(); i++) {
            System.out.println(signalFilenames.get(i));
        }
        System.out.println("hola");
        //Choose a signal
        /*
        List<String> signalList = new ArrayList<>();
        Signal signal = null;
        while(signalList.isEmpty()){*/
        //System.out.println(signalList.toString());
        System.out.println("Introduce filename of the signal:");
        String signalName = sc.next();
        // Signal s = db.jdbc.SQLiteSignalManager.selectSignalByName
        pw.println(signalName);

        String signal = br.readLine();
        System.out.println(signal);

        //}
    }
}
