/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BITalino;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;

/**
 *
 * @author maria
 */
public class BitalinoDemo {

    private static Frame[] frames;
    private static String signalData = "";

    public void recordSignal(String macAddress) {
        BITalino bitalino = null;
        try {
            bitalino = new BITalino();
            Vector<RemoteDevice> devices = bitalino.findDevices();
            int samplingRate = 10;
            bitalino.open(macAddress, samplingRate);

            int[] channelsToAcquire = {1, 2};
            bitalino.start(channelsToAcquire);

            int blockSize = 10;
            for (int j = 0; j < 1000; j++) {
                frames = bitalino.read(blockSize);
                for (int i = 0; i < frames.length; i++) {
                    System.out.println((j * blockSize + i) + " seq: " + frames[i].seq + " " + frames[i].analog[0] + " "
                            + frames[i].analog[1] + " ");
                    signalData += frames[i].analog[0] + " ";
                }
            }
            setSignalData(signalData);
            bitalino.stop();

        } catch (BITalinoException ex) {
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bitalino != null) {
                try {
                    bitalino.close();
                } catch (BITalinoException ex) {
                    Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void setSignalData(String signalData) {
        this.signalData = signalData;
    }

    public String getSignalData() {
        return this.signalData;
    }
}
