/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pojos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Signal;

/**
 *
 * @author maria
 */
public class ECG {
    private Integer id;
    private List<Integer> ecg; 
    private Date startDate;
    private String ECGFile;

    public Integer getId() {
        return id;
    }

    public ECG() {
    }
    
    public List<Integer> getEcg() {
        return ecg;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEcg(List<Integer> ecg) {
        this.ecg = ecg;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public void CreateECGFilename (String patientName){
        Calendar c = Calendar.getInstance();
        String day=Integer.toString(c.get(Calendar.DATE));
        String month=Integer.toString(c.get(Calendar.MONTH));
        String year=Integer.toString(c.get(Calendar.YEAR));
        String hour = Integer.toString(c.get(Calendar.HOUR));
        String minute = Integer.toString(c.get(Calendar.MINUTE));
        String second = Integer.toString(c.get(Calendar.SECOND));
        String millisecond = Integer.toString(c.get(Calendar.MILLISECOND));
        this.ECGFile=patientName+"ECG"+day+month+year+"_"+hour+minute+second+millisecond+".txt";     
    }
    
    public void StartDate(){
        Calendar c = Calendar.getInstance();
        String day=Integer.toString(c.get(Calendar.DATE));
        String month=Integer.toString(c.get(Calendar.MONTH));
        String year=Integer.toString(c.get(Calendar.YEAR));
        String date =year+"/"+month+"/"+day; 
        this.startDate= new Date (date);
    }
    
    public void StoreECGinFile(String patientName){
       FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            CreateECGFilename(patientName);
            String ruta = "../TelemedicinaConsola/"+this.ECGFile;
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
