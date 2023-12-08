/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria
 */
public class Server {

    //public static Socket socketClient; //maneja la conexión con el cliente
    public static ServerSocket serverSocketClient; //escucha las conexiones en el puerto 9000
    public static Client client;
    public static int contador; //para saber cuánto clientes hay conextados
    public static List<Thread> clientsThreadsList = new ArrayList(); //almacena los hilos de clientes

    public static void main(String[] args) {
        try {
            serverSocketClient = new ServerSocket(9000);

            //StopServer SThread = new StopServer();
            //Thread stopServer = new Thread(SThread);//se inicia un hilo para detener al server

            //stopServer.start();
            while (true) {//acepta conexiones de clientes dentro de un bucle infinito
                Socket socketClient = serverSocketClient.accept();
                System.out.println("Client connected!");
                client = new Client(socketClient);
                //ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                String option = br.readLine();  // Leer la opción del menú
                System.out.println("Option received from client: " + option);
                Thread clientThread = new Thread(client);

                clientThread.start();
                clientsThreadsList.add(clientThread);
                contador++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            ReleaseResourcesServerClient(serverSocketClient);
        }
    }
    
    

    public static void ExitServer() {//para cerrar el server pulsar x
        Scanner sc = new Scanner(System.in);
        System.out.println("If you want to close the  server press 'x':");
        String line = sc.nextLine();
        if (line.equals("x")) {
            ReleaseResourcesServerClient(serverSocketClient);
        }
    }

    public static void ReleaseResourcesServerClient(ServerSocket severSocketClient) {
        try {
            serverSocketClient.close();//libera recursos del servidor
            System.exit(0);//sale del programa
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ReleaseClientThread(Socket socket) {
        try {
            socket.close();//libera recursos asociados con un hilo de cliente cerrando el socket del cliente
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void releaseClientResources(InputStream inputStream, OutputStream outputStream, Socket socket) {
        //libera los recursos asociados con un cliente cerrando sus flujos de entrada y salida y el socket
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
