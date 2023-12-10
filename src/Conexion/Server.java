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
            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            serverSocketClient = new ServerSocket(9000);
            //ServerSocket serverSocket = new ServerSocket(9000);
            StopServer SThread = new StopServer();
            Thread stopServer = new Thread(SThread);//se inicia un hilo para detener al server
            stopServer.start();
            Socket socketClient;
            socketClient = serverSocketClient.accept();
            System.out.println("Client connected!");
            ArrayList<Socket> sockets = new ArrayList<>();
            sockets.add(socketClient);
            //client = new Client(socketClient);
            ArrayList<ObjectInputStream> ins = new ArrayList<>();
            ins.add(new ObjectInputStream(socketClient.getInputStream()));
            while (true) {//acepta conexiones de clientes dentro de un bucle infinito
                /*for (int i = 0; i < ins.size(); i++) {
                    if (ins.get(i).readObject() == "Client closed") {
                        //clients.remove(1);
                        ins.remove(i);

                    }
                }

                if (ins.size() == 0) {
                    System.out.println("There are no clients connected. If you want to add more clients, do not close the server.");
                    ExitServer();
                }*/
                Socket s = serverSocketClient.accept();
                System.out.println("Client connected");
                sockets.add(s);
                ins.add(new ObjectInputStream(s.getInputStream()));
                //BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                //String option = br.readLine();  // Leer la opción del menú
                //System.out.println("Option received from client: " + option);
                //Thread clientThread = new Thread(client);

                /*new Thread(new Client(socketClient)).start();*/
                //clientThread.start();
                //clientsThreadsList.add(clientThread);
                //contador++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);

        }/* catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }*/ finally {
            ReleaseResourcesServerClient(serverSocketClient);
        }
    }

    /**
     *
     */
    public static void ExitServer() {//para cerrar el server pulsar x
        Scanner sc = new Scanner(System.in);
        System.out.println("If you want to close the  server press 'x':");
        String line = sc.nextLine();
        if (line.equals("x")) {
            System.out.println("Closing Server . . . ");
            ReleaseResourcesServerClient(serverSocketClient);
        }
    }

    /**
     *
     * @param severSocketClient
     */
    public static void ReleaseResourcesServerClient(ServerSocket severSocketClient) {
        try {
            serverSocketClient.close();//libera recursos del servidor
            System.exit(0);//sale del programa
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param inputStream
     * @param outputStream
     * @param socket
     */
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
