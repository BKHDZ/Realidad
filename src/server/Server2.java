/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import objetosComunes.Nave;
import objetosComunes.SerVivo;

/**
 *
 * @author jesus_000
 */
public class Server2 extends Thread {

    TreeMap<String, SerVivo> seresVivos;
    TreeMap<String, Nave> naves;

    public Server2(TreeMap<String, SerVivo> seresVivos, TreeMap<String, Nave> naves) {
        this.seresVivos = seresVivos;
        this.naves = naves;
        this.setName("SERVER");
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(9000);
            while (true) {
                Socket socket = serverSocket.accept();
                try {
//                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    //System.out.println("DIGITADO [" + in.readLine() + "]");
                    Nave naveRecibida = (Nave) in.readObject();
                    TreeMap<String, SerVivo> tripulantes = naveRecibida.getTripulantes();
                    seresVivos.putAll(tripulantes);
                    for (Map.Entry<String, SerVivo> entry : tripulantes.entrySet()) {
                        String key = entry.getKey();
                        SerVivo value = entry.getValue();
                        seresVivos.put(key, value);
                    }
                    naveRecibida.getTripulantes().clear();
                    
                    
                    
                    

                    naves.put(naveRecibida.getNombre(), naveRecibida);
                    System.out.println("Nave Recibida :[" + naveRecibida.getNombre() + "] agregada a la lista. ");

                    in.close();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Error SERVER [" + e.getMessage() + "]");
        }
    }

}
