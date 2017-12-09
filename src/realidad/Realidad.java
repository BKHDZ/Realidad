/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realidad;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import database.ConnectionParameters;
import database.mysql.ManagerMysql;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import objetosComunes.Nave;
import objetosComunes.NaveEscape;
import objetosComunes.NaveNodriza;
import objetosComunes.Perro;
import objetosComunes.Persona;
import objetosComunes.SerVivo;
import server.Server2;
import server.ServerPro;

/**
 *
 * @author FAMILIA
 */
public class Realidad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //<editor-fold defaultstate="collapsed" desc="DECLARACION">
        TreeMap<String, SerVivo> seresVivos = new TreeMap<String, SerVivo>();
        TreeMap<String, Nave> naves = new TreeMap<String, Nave>();

        LinkedHashMap<String, String> opciones = opciones = new LinkedHashMap<String, String>();
        opciones.put("S", "Salir del programa");
        opciones.put("SV", "Crear Ser Vivo");
        opciones.put("LSV", "Lista Seres Vivos");
        opciones.put("CLSV", "Carga Lista Seres Vivos");
        opciones.put("ELSV", "Exportar Lista Seres Vivos");
        opciones.put("EN", "Conecta a servidor");
        opciones.put("CN", "Crear Nave");
        opciones.put("LN", "Lista Nave");
        opciones.put("SVAN", "Ser Vivo Abordar Nave");
        opciones.put("SLSVBD", "Salvar lista seres vivos en Base de Datos");
        LinkedHashMap<String, String> opcionesSerVivo = opcionesSerVivo = new LinkedHashMap<String, String>();
        opcionesSerVivo.put("H", "Humano");
        opcionesSerVivo.put("P", "Perro");

        LinkedHashMap<String, String> opcionesNave = opcionesNave = new LinkedHashMap<String, String>();
        opcionesNave.put("NE", "Nave Escape");
        opcionesNave.put("NN", "Nave Nodriza");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String opcionDigitada;
        String tipo;
        String ruta;
        String nombre;
        String nombreDeArchivo;
        String ip;
        int edad;
        int record;
        String naveSeleccionada;
        String nombreNave;
        String serVivoSeleccionado;
        ConnectionParameters connectionParameters = new ConnectionParameters("training_usser", "training_usser1", "192.168.100.5", "3306", "training", null);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SERVER">
        Server2 server = new Server2(seresVivos, naves);
        server.start();
        ServerPro serverPro = new ServerPro(seresVivos, naves);
        serverPro.start();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="PROGRAMA">
        try {
            System.out.println("Inicia el programa");
            while (true) {
                try {
                    System.out.println("------------------------");
                    for (Map.Entry<String, String> entry : opciones.entrySet()) {
                        System.out.println("Opciones[" + entry.getKey() + "] Operacion[" + entry.getValue() + "]");

                    }
                    opcionDigitada = reader.readLine();
                    //<editor-fold defaultstate="collapsed" desc="OPCIONES">
                    if (opciones.containsKey(opcionDigitada)) {
                        //<editor-fold defaultstate="collapsed" desc="SALIR">
                        if (opcionDigitada.equals("S")) {
                            System.out.println("Hasta Luego");
                            break;
                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="CREAR SER VIVO">
                        if (opcionDigitada.equals("SV")) {
                            System.out.println("Que desea crear?");
                            for (Map.Entry<String, String> entry : opcionesSerVivo.entrySet()) {
                                System.out.println("Opciones[" + entry.getKey() + "] Operacion[" + entry.getValue());

                            }
                            opcionDigitada = reader.readLine();
                            if (opcionesSerVivo.containsKey(opcionDigitada)) {
                                if (opcionDigitada.equals("H")) {
                                    System.out.println("Digita el nombre");
                                    nombre = reader.readLine();
                                    System.out.println("Digite la edad");
                                    edad = Integer.parseInt(reader.readLine());
                                    seresVivos.put(nombre, new Persona(nombre, edad));
                                }
                                if (opcionDigitada.equals("P")) {
                                    System.out.println("Digita el nombre");
                                    nombre = reader.readLine();
                                    System.out.println("Digite la edad");
                                    edad = Integer.parseInt(reader.readLine());
                                    seresVivos.put(nombre, new Perro(nombre, edad));
                                }

                            } else {
                                System.out.println("Opcion no valida intente nuevamente");
                            }
                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="LISTA SERES VIVOS">
                        if (opcionDigitada.equals("LSV")) {
                            for (Map.Entry<String, SerVivo> entry : seresVivos.entrySet()) {
                                System.out.println("Opciones[" + entry.getValue().getTipo() + "] Nombre [" + entry.getValue().getNombre() + "] Edad: [" + entry.getValue().getEdad() + "]");

                            }

                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="CARGAR LISTA">
                        if (opcionDigitada.equals("CLSV")) {
                            System.out.println("Digita la ruta");
                            ruta = reader.readLine();

                            CsvReader csvReader = new CsvReader(ruta);
                            System.out.println("Archico encontrado se produce la carga");
                            record = 1;
                            while (csvReader.readRecord()) {
                                try {
                                    tipo = csvReader.get(0);
                                    nombre = csvReader.get(1);
                                    edad = Integer.parseInt(csvReader.get(2));
                                    if (tipo.equals("Humano")) {
                                        seresVivos.put(nombre, new Persona(nombre, edad));
                                    } else if (tipo.equals("Perro")) {
                                        seresVivos.put(nombre, new Perro(nombre, edad));
                                    } else {
                                        throw new Exception("Tipo no definido");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Error [" + e.getMessage() + "] en registro #[" + record + "]");
                                }
                                record++;
                            }
                            csvReader.close();
                            System.out.println("Lista cargada");
                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="EXPORTAR LISTA">
                        if (opcionDigitada.equals("ELSV")) {
                            System.out.println("Digita la ruta para salvar");
                            ruta = reader.readLine();
                            System.out.println("Digite el nombre del archivo");
                            nombreDeArchivo = reader.readLine();
                            try {
                                File rutaDeArchivo = new File(ruta);
                                if (!rutaDeArchivo.exists()) {
                                    rutaDeArchivo.mkdirs();
                                }
                                File rutaDeArchivoYArchivo = new File(ruta + "/" + nombreDeArchivo);
                                if (rutaDeArchivoYArchivo.exists()) {
                                    rutaDeArchivoYArchivo.mkdirs();
                                }

                                CsvWriter csvWriter = new CsvWriter(rutaDeArchivoYArchivo.getAbsolutePath());
                                for (Map.Entry<String, SerVivo> entry : seresVivos.entrySet()) {
                                    SerVivo serVivo = entry.getValue();
                                    csvWriter.write(serVivo.getTipo());
                                    csvWriter.write(serVivo.getNombre());
                                    csvWriter.write(serVivo.getEdad() + "");
                                    csvWriter.endRecord();
                                }
                                csvWriter.close();
                                System.out.println("Lista exportada");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="ENVIA NAVE">
                        if (opcionDigitada.equals("EN")) {
                            System.out.println("Digite la ip");
                            ip = reader.readLine();
                            try {
                                Socket socket = new Socket(ip, 9000);
                                //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                                System.out.println("Estas son las naves Disponibles:");
                                for (Map.Entry<String, Nave> entry : naves.entrySet()) {
                                    String key = entry.getKey();
                                    Nave value = entry.getValue();
                                    System.out.println(value.getNombre());
                                }
                                System.out.println("Cual es la que desea enviar.");
                                naveSeleccionada = reader.readLine();
                                if (!naves.containsKey(naveSeleccionada)) {
                                    throw new Exception("La nave no existe");
                                }

                                out.writeObject(naves.get(naveSeleccionada));

                                for (Map.Entry<String, SerVivo> entry : naves.get(naveSeleccionada).getTripulantes().entrySet()) {
                                    seresVivos.remove(entry.getKey());
                                }
                                naves.remove(naveSeleccionada);
                                in.close();
                                out.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Error hacia Server [" + e.getMessage() + "]");
                            }

                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="CREAR NAVE">
                        if (opcionDigitada.equals("CN")) {
                            System.out.println("Que desea crear?");
                            for (Map.Entry<String, String> entry : opcionesNave.entrySet()) {
                                System.out.println("Opciones[" + entry.getKey() + "] Operacion[" + entry.getValue());

                            }
                            opcionDigitada = reader.readLine();
                            if (opcionesNave.containsKey(opcionDigitada)) {
                                if (opcionDigitada.equals("NE")) {
                                    System.out.println("Digita el nombre");
                                    nombreNave = reader.readLine();
                                    naves.put(nombreNave, new NaveEscape(nombreNave));
                                }
                                if (opcionDigitada.equals("NN")) {
                                    System.out.println("Digita el nombre");
                                    nombreNave = reader.readLine();
                                    naves.put(nombreNave, new NaveNodriza(nombreNave));
                                }

                            } else {
                                System.out.println("Opcion no valida intente nuevamente");
                            }
                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="LISTA NAVE">
                        if (opcionDigitada.equals("LN")) {
                            for (Map.Entry<String, Nave> entry : naves.entrySet()) {
                                System.out.println("Opciones[" + entry.getValue().getNombre() + "]");

                            }

                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="ABORDA NAVE">
                        if (opcionDigitada.equals("SVAN")) {
                            try {

                                System.out.println("Estas son las naves Disponibles:");
                                for (Map.Entry<String, Nave> entry : naves.entrySet()) {
                                    String key = entry.getKey();
                                    Nave value = entry.getValue();
                                    System.out.println("[" + value.getNombre() + "]");
                                }

                                System.out.println("Cual es la que desea abordar.");
                                naveSeleccionada = reader.readLine();
                                if (!naves.containsKey(naveSeleccionada)) {
                                    throw new Exception("La nave no existe");
                                }
                                System.out.println("Estas los seres vivos Disponibles:");
                                for (Map.Entry<String, SerVivo> entry : seresVivos.entrySet()) {
                                    String key = entry.getKey();
                                    SerVivo value = entry.getValue();
                                    System.out.println(value.getTipo());
                                    System.out.println(value.getNombre());
                                    System.out.println(value.getEdad());
                                }
                                System.out.println("Cual el ser vivo que abordara la nave [" + naveSeleccionada + "] .");
                                serVivoSeleccionado = reader.readLine();

                                naves.get(naveSeleccionada).abordar(seresVivos.get(serVivoSeleccionado));
                                System.out.println("[" + serVivoSeleccionado + "]abordardo la nave [" + naveSeleccionada + "] .");

                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Error hacia Server [" + e.getMessage() + "]");
                            }

                        }
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="SALVAR LISTA SERES VIVOS BD">
                        if (opcionDigitada.equals("SLSVBD")) {
                            ManagerMysql managerMysql = new ManagerMysql();

                            managerMysql.getConnection(connectionParameters);
                            for (Map.Entry<String, SerVivo> entry : seresVivos.entrySet()) {
                                String key = entry.getKey();
                                SerVivo serVivo = entry.getValue();
                                try{
                                managerMysql.executeDML("INSERT INTO tr_ser_vivo(NOBRE, EDAD, TIPO) VALUES('"+serVivo.getNombre()+"',"+serVivo.getEdad()+",'"+serVivo.getTipo()+"')");
                                }catch(Exception e){
                                    System.out.println("Error de insercion [('"+serVivo.getNombre()+"',"+serVivo.getEdad()+",'"+serVivo.getTipo()+"')] ["+e.getMessage()+"]");
                                }
                            }
                            managerMysql.closeConnection();

                        }

                        //</editor-fold>
                    } else {
                        System.out.println("Opcion no valida intente nuevamente");
                    }
                    //</editor-fold>
                } catch (Exception e) {
                    System.out.println("Error:[" + e.getMessage() + "]");
                }

            }

            System.out.println("Termino el programa");
        } catch (Exception e) {
            System.out.println("Error:[" + e.getMessage() + "]");
        }
        //</editor-fold>

    }

}
