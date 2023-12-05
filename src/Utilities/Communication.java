/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;
//import BITalino.*;

import Pojos.*;
import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria
 */
public class Communication {

    public static Socket connectToServer() throws IOException {
        boolean connected = false;
        Socket socket = null;
        do {

            try {
                socket = new Socket("localhost", 9000);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                connected = socket.isConnected();
            } catch (IOException ex) {
                if (!connected) {
                    System.out.println("Connection failed");
                }

            }
        } while (!connected);

        return socket;
    }

    public static void sendDoctor(PrintWriter pw, Doctor doctor) {
        pw.println(doctor.toString());
        System.out.println("doctor sended");
    }

    public static void sendPatient(PrintWriter pw, Patient patient) {
        System.out.println(patient.toString()); //SE MANDA BIEN
        pw.println(patient.toString());

    }

    public static void sendSignal(PrintWriter printWriter, ECG signal) {
        printWriter.println(signal.toString());
    }

    public static void sendUser(PrintWriter printWriter, User user) {
        printWriter.println(user.toString());
    }

    public static Patient receivePatient(BufferedReader bf) {
        Patient p = new Patient();

        try {
            String line = bf.readLine();
            line = line.replace("{", "");
            line = line.replace("Patient", "");
            line = line.replace("}", "");
            String[] atribute = line.split(",");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (int i = 0; i < atribute.length; i++) {
                String[] data2 = atribute[i].split("=");
                for (int j = 0; j < data2.length - 1; j++) {
                    data2[j] = data2[j].replace(" ", "");
                    switch (data2[j]) {
                        case "name":
                            p.setName(data2[j + 1]);
                            break;
                        case "surname":
                            p.setLastName(data2[j + 1]);
                            break;
                        case "dob":
                            p.setDob(LocalDate.parse(data2[j + 1], formatter));
                            //p.setDob((LocalDate) formatter.parse(data2[j + 1]));//cambiado a LocalDate
                            break;
                        case "email":
                            p.setEmail(data2[j + 1]);
                            break;
                        case "gender":
                            p.setGender(data2[j + 1]);
                            break;

                        case "userId":
                            p.setId(Integer.parseInt(data2[j + 1]));
                            break;
                    }
                }
            }
            System.out.println("Patient received:");
            System.out.println(p.toString());
            return p;
        } catch (IOException ex) {
            return null;
        }

    }

    public static Doctor receiveDoctor(BufferedReader bufferReader) {
        Doctor d = new Doctor();
        try {
            String line = bufferReader.readLine();
            line = line.replace("{", "");
            line = line.replace("Doctor", "");
            line = line.replace("}", "");
            String[] atribute = line.split(",");
            for (int i = 0; i < atribute.length; i++) {
                String[] data2 = atribute[i].split("=");
                for (int j = 0; j < data2.length - 1; j++) {
                    data2[j] = data2[j].replace(" ", "");
                    switch (data2[j]) {
                        case "name":
                            d.setName(data2[j + 1]);
                            break;
                        case "surname":
                            d.setLastName(data2[j + 1]);
                            break;
                        case "email":
                            d.setEmail(data2[j + 1]);
                            break;
                        case "id":
                            d.setDoctorId(Integer.parseInt(data2[j + 1]));
                            break;
                    }
                }
            }
            System.out.println("Doctor recieved:");
            System.out.println(d.toString());
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return d;
    }

    public static ECG receiveSignal(BufferedReader br) {
        ECG s = new ECG();
        try {
            String line = br.readLine();
            line = line.replace("{", "");
            line = line.replace("Signal", "");
            String[] atribute = line.split(",");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            for (int i = 0; i < atribute.length; i++) {
                String[] data2 = atribute[i].split("=");
                for (int j = 0; j < data2.length - 1; j++) {
                    data2[j] = data2[j].replace(" ", "");
                    switch (data2[j]) {
                        case "signalId":
                            s.setId(Integer.parseInt(data2[j + 1]));
                            break;
                        case "ECG_values":
                            String[] separatedString = data2[j + 1].split(",");
                            List<Integer> ECG = new ArrayList();
                            for (int k = 0; k < separatedString.length; k++) {
                                ECG.add(k, Integer.parseInt(separatedString[k]));
                            }
                            s.setEcg(ECG);
                            break;
                        case "startDate":
                            try {
                                s.setStartDate(format.parse(data2[j + 1]));
                            } catch (ParseException ex) {
                                Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(s.toString());
        return s;
    }

    public static User receiveUser(BufferedReader br) {
        User u = new User();
        try {
            String line = br.readLine();
            System.out.println(line);
            line = line.replace("{", "");
            line = line.replace("User", "");
            line = line.replace("}", "");
            String[] atribute = line.split(",");
            for (int i = 0; i < atribute.length; i++) {
                String[] data2 = atribute[i].split("=");
                for (int j = 0; j < data2.length - 1; j++) {
                    data2[j] = data2[j].replace(" ", "");
                    switch (data2[j]) {
                        case "username":
                            u.setUsername(data2[j + 1]);
                            break;
                        case "password":
                            u.setPassword(data2[j + 1]);
                            break;
                        case "role":
                            u.setRole(Integer.parseInt(data2[j + 1]));
                            break;
                        case "userId":
                            u.setId(Integer.parseInt(data2[j + 1]));
                            break;
                    }
                }
            }
            System.out.println(u.toString());
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    public static void recordSignal(Patient p, PrintWriter pw) {
        /*Frame[] frame;
        BITalino bitalino = null;
        Signal s = new Signal();
        ArrayList<Integer> ecg_vals = new ArrayList<Integer>();
        ArrayList<Integer> emg_vals = new ArrayList<Integer>();
        try {
            bitalino = new BITalino();
            Vector<RemoteDevice> devices = bitalino.findDevices();
            System.out.println(devices);

            String macAddress = p.getMacAddress();

            bitalino.open(macAddress, 100);

            int[] channelsToAcquire = {1, 2}; //for EMG and ECG
            bitalino.start(channelsToAcquire);

            //Read in total 10000000 times --> por que elegimos este num
            //Each time read a block of 10 samples to make the trials easier, but we plan to change it
            int block_size = 16;

            // Start loop to calculate time to send signal TODO
            for (int j = 0; j < 750; j++) {
                frame = bitalino.read(block_size);

                for (int i = 0; i < frame.length; i++) {
                    //pw.println(frame[i].analog[0]);
                    System.out.println(frame[i].analog[0]);
                    ecg_vals.add(frame[i].analog[0]);
                }


                for (int a = 0; a < frame.length; a++) {
                    System.out.println(frame[a].analog[1]);
                    emg_vals.add(frame[a].analog[1]);
                }

            }
            bitalino.stop();
            pw.println(ecg_vals.size());
            for (int k = 0; k < ecg_vals.size(); k++) {
                pw.println(ecg_vals.get(k));
            }
            pw.println(emg_vals.size());
            for (int m = 0; m < emg_vals.size(); m++) {
                pw.println(emg_vals.get(m));
            }

            System.out.println("Ok");
        } catch (BITalinoException ex) {
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bitalino != null) {
                    bitalino.close();
                }
            } catch (BITalinoException ex) {
                Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }

    public static List<String> ShowSignals(BufferedReader bf, PrintWriter pw) {
        try {
            List<String> filenames = new ArrayList();

            int size = Integer.parseInt(bf.readLine());
            for (int i = 0; i < size; i++) {
                filenames.add(bf.readLine());
            }
            return filenames;
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<String> receivePatientList(BufferedReader bf) {
        List<String> patientList = new ArrayList();
        boolean stop = true;
        try {
            while (stop) {
                String line = bf.readLine();
                if (!line.equalsIgnoreCase("End of list")) {
                    stop = true;
                    System.out.println(line);
                    patientList.add(line);
                } else {
                    stop = false;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patientList;
    }

    public static void exitFromServer(PrintWriter pw, BufferedReader br, InputStream inputStream, OutputStream outputStream, Socket socket) {
        pw.close();
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
