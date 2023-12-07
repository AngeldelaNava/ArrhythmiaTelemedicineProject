/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pojos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Signal;

/**
 *
 * @author maria
 */
public class ECG {

    private Integer id;
    private String ecg;
    private LocalDate startDate;
    private String ECGFile;
    private int patient_id;

    public ECG(Integer id, int patient_id, LocalDate startDate, String ecg) {
        this.id = id;
        this.patient_id = patient_id;
        this.startDate = startDate;
        this.ecg = ecg;
    }
    
    public ECG(Integer id, String ecg, LocalDate startDate, String ECGFile) {
        this.id = id;
        this.ecg=ecg;
        this.startDate = startDate;
        this.ECGFile=ECGFile;
    }

    public Integer getId() {
        return id;
    }

    public ECG() {
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getEcg() {
        return ecg;
    }
    public String getECGFile() {
        return ECGFile;
    }
    public void setECGFile(String ECGFile) {
        this.ECGFile=ECGFile;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void CreateECGFilename(String patientName) {
        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString(c.get(Calendar.MONTH));
        String year = Integer.toString(c.get(Calendar.YEAR));
        String hour = Integer.toString(c.get(Calendar.HOUR));
        String minute = Integer.toString(c.get(Calendar.MINUTE));
        String second = Integer.toString(c.get(Calendar.SECOND));
        String millisecond = Integer.toString(c.get(Calendar.MILLISECOND));
        this.ECGFile = patientName + "ECG" + day + month + year + "_" + hour + minute + second + millisecond + ".txt";
    }

    public void StartDate() {
        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString(c.get(Calendar.MONTH));
        String year = Integer.toString(c.get(Calendar.YEAR));
        String date = day + "/" + month + "/" + year;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.startDate = LocalDate.parse(year, dtf);
    }

    public void StoreECGinFile(String patientName) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            CreateECGFilename(patientName);
            String ruta = "../TelemedicinaConsola/" + this.ECGFile;
            String contenido = ecg.toString();
            File file = new File(ruta);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(contenido);

        } catch (IOException ex) {
            Logger.getLogger(ECG.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Signal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String toString() {

        return "Signal{" + "id=" + id + ", ecg=" + ecg + ", startDate=" + startDate + '}';
    }
}
