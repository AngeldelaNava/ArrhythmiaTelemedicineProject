/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

/**
 *
 * @author maria
 */
public class StopServer implements Runnable {

    /**
     *
     */
    @Override
    public void run() {
        Server.ExitServer();
    }
}
