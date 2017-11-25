/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import objetosComunes.Nave;
import objetosComunes.Perro;
import objetosComunes.Persona;
import objetosComunes.SerVivo;

/**
 *
 * @author FAMILIA
 */
public class ServerPro extends Thread {

    int sessionID;
    TreeMap<String, SerVivo> seresVivos;
    TreeMap<String, Nave> naves;

    public ServerPro(TreeMap<String, SerVivo> seresVivos, TreeMap<String, Nave> naves) {
        this.sessionID = 0;
        this.seresVivos = seresVivos;
        this.naves = naves;
        this.setName("SERVER-PRO");
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(4000);
            while (true) {
                Socket socket = serverSocket.accept();
                Session session = new Session(socket, sessionID);
                session.start();
                sessionID++;
            }
        } catch (Exception e) {
            System.out.println("Error terminal del SERVIDOR-PRO");
        }
    }

    public class Session extends Thread {

        int sessionID;
        Socket socket;

        public Session(Socket socket, int sessionID) {
            this.socket = socket;
            this.sessionID = sessionID;
            this.setName("Session[" + sessionID + "]");
        }

        @Override
        public void run() {
            String respuesta = "";
            try {
//                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String operation = in.readLine();
                

                switch (operation) {
                    case "CREAR SER VIVO":
                        respuesta = "";
                        try {
                            String nombre = in.readLine();
                            String edad = in.readLine();
                            String tipo = in.readLine();
                            if (tipo.equals("Perro")) {
                                Perro perro = new Perro(nombre, Integer.parseInt(edad));
                                seresVivos.put(perro.getNombre(), perro);
                            } else if (tipo.equals("Humano")) {
                                Persona persona = new Persona(nombre, Integer.parseInt(edad));
                                seresVivos.put(persona.getNombre(), persona);
                            } else {
                                throw new Exception("Tipo de ser vivo no identificado [" + tipo + "]");
                            }
                            respuesta = "Ser Vivo Añadido";
                        } catch (Exception e) {
                            respuesta = e.getMessage();
                        }
                        break;

                    case "RECIBIR NAVE":
                        respuesta = "";
                        try {
                            ObjectOutputStream out2 = new ObjectOutputStream(socket.getOutputStream());
                            ObjectInputStream in2 = new ObjectInputStream(socket.getInputStream());

                            //System.out.println("DIGITADO [" + in.readLine() + "]");
                            Nave naveRecibida = (Nave) in2.readObject();
                            TreeMap<String, SerVivo> tripulantes = naveRecibida.getTripulantes();
                            seresVivos.putAll(tripulantes);
                            for (Map.Entry<String, SerVivo> entry : tripulantes.entrySet()) {
                                String key = entry.getKey();
                                SerVivo value = entry.getValue();
                                seresVivos.put(key, value);
                            }
                            naveRecibida.getTripulantes().clear();
                            naves.put(naveRecibida.getNombre(), naveRecibida);
                            respuesta = "Nave Recibida :[" + naveRecibida.getNombre() + "] agregada a la lista. ";
                        } catch (Exception e) {
                            respuesta = e.getMessage();
                        }

                        break;

                    default:
                        respuesta = "Operacion no reconocida";
                }

                System.out.println(respuesta);
                out.println(respuesta);

            } catch (Exception e) {
                System.out.println("Error terminal de Session[" + sessionID + "]");
            }
        }
    }

}
