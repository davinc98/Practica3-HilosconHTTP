
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorWeb implements Runnable{
    protected int puerto = 9000;
    protected ServerSocket ss = null;
    protected boolean      detenido    = false;
    protected Thread       runningThread= null;
    protected ExecutorService pool = Executors.newFixedThreadPool(2);
    class Manejador extends Thread {
        protected Socket socket = null;
        protected BufferedReader br;
        protected BufferedOutputStream bos;
        protected PrintWriter pw;
        protected String FileName;
        public Manejador(Socket _socket){
            this.socket = _socket;
        }
        public void run() {
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bos = new BufferedOutputStream(socket.getOutputStream());
                pw = new PrintWriter(new OutputStreamWriter(bos));
                while(true){
                    String line = br.readLine();
                    if (line == null) {
                        pw.print("<html><head><title>Servidor WEB");
                        pw.print("</title><body bgcolor=\"#AACCFF\"<br>Linea Vacia</br>");
                        pw.print("</body></html>");
                        socket.close();
                        return;
                    }
                    System.out.println("\nCliente Conectado desde: " + socket.getInetAddress());
                    System.out.println("Por el puerto: " + socket.getPort());
                    System.out.println("Datos: " + line + "\r\n\r\n");
                    if (line.indexOf("?") == -1) {//Si no tiene parametros en la URL
                        if (line.toUpperCase().startsWith("GET")) {
                            getArchivo(line);
                            if (FileName.compareTo("") == 0) {
                                SendArchivo("index.htm");
                            } else {
                                SendArchivo(FileName);
                            }
                            System.out.println(FileName);
                        } else if (line.toUpperCase().startsWith("HEAD")) {
                            //RESPUESTA
                            getArchivo(line);
                            if (FileName.compareTo("") == 0) {
                                SendArchivo2("index.htm");
                            } else {
                                SendArchivo2(FileName);
                            }
                            System.out.println(FileName);
                        } else if (line.toUpperCase().startsWith("POST")) {
                            String line2="";
                            int tamano = 0;
                            boolean b=true;
                            while(b){
                                line2 = br.readLine();
                                System.out.println(line2);
                                if(line2.length()>16){
                                    if(line2.substring(0,16).equals("Content-Length: "))
                                        tamano = Integer.valueOf(line2.substring(16));
                                }
                                if(line2.equals(""))
                                    b=false;
                            }
                            line2 = "";
                            b=true;
                            char aux;
                            for(int j=0;j<tamano;j++){
                                aux = (char)br.read();
                                line2 = line2+String.valueOf(aux);
                            }
                            System.out.println(String.valueOf(line2));
                            if(tamano>0){
                                System.out.println("Token 0:" + line2 + "\r\n");
                            }else
                                line2 = "Sin Parámetros";
                            //RESPUESTA
                            getArchivo(line);
                            if (FileName.compareTo("") == 0) {
                                SendArchivo("index.htm");
                            } else {
                                SendArchivo(FileName);
                            }
                            System.out.println(FileName);
                        } else if (line.toUpperCase().startsWith("DELETE")){
                            String line2="";
                            int tamano = 0;
                            boolean b=true;
                            while(b){
                                line2 = br.readLine();
                                if(line2.length()>16){
                                    if(line2.substring(0,16).equals("Content-Length: "))
                                        tamano = Integer.valueOf(line2.substring(16));
                                }
                                if(line2.equals(""))
                                    b=false;
                            }
                            line2 = "";
                            b=true;
                            char aux;
                            for(int j=0;j<tamano;j++){
                                aux = (char)br.read();
                                line2 = line2+String.valueOf(aux);
                            }
                            System.out.println(String.valueOf(line2));
                            if(tamano>0){
                                System.out.println("Token 0:" + line2 + "\r\n");
                            }else
                                line2 = "Sin Parámetros";
                            //RESPUESTA
                            FileName = "";
                            getArchivo(line);
                            if (FileName.compareTo("") == 0) {
                                SendArchivo2("index.htm");
                            } else {
                                SendArchivo2(FileName);
                            }
                            System.out.println(FileName);
                        } else {
                            pw.println("HTTP/1.0 501 Not Implemented");
                            pw.println();
                        }

                    } else if (line.toUpperCase().startsWith("GET")) {
                        StringTokenizer tokens = new StringTokenizer(line, "?");
                        System.out.println("Peticion: " + tokens + "\r\n");
                        String req_a = tokens.nextToken();
                        String req = tokens.nextToken();
                        System.out.println("Token1: " + req_a + "\r\n");
                        System.out.println("Token2: " + req + "\r\n");
                        //RESPUESTA
                        pw.println("HTTP/1.0 200 Okay");
                        pw.flush();
                        pw.println();
                        pw.flush();
                        pw.print("<html><head><title>SERVIDOR WEB");
                        pw.flush();
                        pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Respuesta GET, Parametros Obtenidos..</br></h1>");
                        pw.flush();
                        pw.print("<h3><b>" + req + "</b></h3>");
                        pw.flush();
                        pw.print("</center></body></html>");
                        pw.flush();
                    } else if (line.toUpperCase().startsWith("HEAD")) {
                        StringTokenizer tokens = new StringTokenizer(line, "?");
                        System.out.println("Peticion: " + tokens + "\r\n");
                        String req_a = tokens.nextToken();
                        String req = tokens.nextToken();
                        System.out.println("Token1: " + req_a + "\r\n");
                        System.out.println("Token2: " + req + "\r\n");
                        //RESPUESTA
                        pw.println("HTTP/1.0 200 Okay");
                        pw.flush();
                        pw.println();
                        pw.flush();
                    } else {
                        pw.println("HTTP/1.0 501 Not Implemented");
                        pw.println();
                    }
                    pw.flush();
                    bos.flush();
                    boolean seguir = true;
                    while(seguir){
                        line = br.readLine();
                        //System.out.println(line);
                        if(line==null)
                            seguir = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void getArchivo(String line) {
            int i;
            int f;
            //if (line.toUpperCase().startsWith("GET")) {
                i = line.indexOf("/");
                f = line.indexOf(" ", i);
                FileName = line.substring(i + 1, f);
            //}
        }
        public void SendArchivo(String fileName, Socket sc) {
            //System.out.println(fileName);
            int fSize = 0;
            byte[] buffer = new byte[4096];
            try {
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                //sendHeader();
                FileInputStream f = new FileInputStream(fileName);
                int x = 0;
                while ((x = f.read(buffer)) > 0) {
                    //		System.out.println(x);
                    out.write(buffer, 0, x);
                }
                out.flush();
                f.close();
            } catch (FileNotFoundException e) {
                //msg.printErr("Transaction::sendResponse():1", "El archivo no existe: " + fileName);
            } catch (IOException e) {
                //			System.out.println(e.getMessage());
                //msg.printErr("Transaction::sendResponse():2", "Error en la lectura del archivo: " + fileName);
            }
        }

        public void SendArchivo2(String arg) {
            int indice = arg.indexOf(".");
            String extension = arg.substring(indice + 1, arg.length());
            System.out.println("Extension de archivo: " + extension);
            try {
                File f = new File(arg);
                if(!f.exists()){
                    System.out.println("El recurso solicitado no existe");
                    String sb= "";
                    sb=sb+"HTTP/1.0 404 Recurso no encontrado\n";
                    sb=sb+"Server: JESUS JOSE/1.0\n";
                    sb=sb+"Date: " + new Date() + " \n\n";
                    bos.write(sb.getBytes());
                    bos.flush();
                }else{
                    int b_leidos = 0;
                    BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(arg));
                    byte[] buf = new byte[1024];
                    int tam_bloque = 0;
                    if (bis2.available() >= 1024) {
                        tam_bloque = 1024;
                    } else {
                        bis2.available();
                    }
                    int tam_archivo = bis2.available();
                    String sb = "";
                    sb = sb + "HTTP/1.0 200 ok\n";
                    sb = sb + "Server: JESUS JOSE/1.0 \n";
                    sb = sb + "Date: " + new Date() + " \n";
                    if (extension.equals("pdf")) {
                        sb = sb + "Content-Type: application/pdf \n";
                    } else if (extension.equals("jpg")) {
                        sb = sb + "Content-Type: image/jpeg \n";
                    } else {
                        sb = sb + "Content-Type: text/html \n";
                    }
                    sb = sb + "Content-Length: " + tam_archivo + " \n";
                    sb = sb + "\n";
                    bos.write(sb.getBytes());
                    bos.flush();
                    bis2.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        public void SendArchivo(String arg) {
            int indice = arg.indexOf(".");
            String extension = arg.substring(indice + 1, arg.length());
            System.out.println("Extension de archivo: " + extension);
            try {
                File f = new File(arg);
                if(!f.exists()){
                    System.out.println("El recurso solicitado no existe: "+arg);
                    String sb= "";
                    sb=sb+"HTTP/1.0 404 Recurso no encontrado\n";
                    sb=sb+"Server: JESUS JOSE/1.0\n";
                    sb=sb+"Date: " + new Date() + " \n";
                    sb=sb+"Content-Type: text/html \n";
                    sb=sb+"\n<html><head><title>SERVIDOR WEB\n";
                    sb=sb+"</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>El recurso solicitado no se ha encontrado en el servidor, error 404</br></h1>";
                    sb=sb+"</center></body></html>\n";
                    bos.write(sb.getBytes());
                    bos.flush();
                }else{
                    int b_leidos = 0;
                    BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(arg));
                    byte[] buf = new byte[1024];
                    int tam_bloque = 0;
                    if (bis2.available() >= 1024) {
                        tam_bloque = 1024;
                    } else {
                        bis2.available();
                    }
                    int tam_archivo = bis2.available();
                    String sb = "";
                    sb = sb + "HTTP/1.0 200 ok\n";
                    sb = sb + "Server: JESUS JOSE/1.0 \n";
                    sb = sb + "Date: " + new Date() + " \n";
                    if (extension.equals("pdf")) {
                        sb = sb + "Content-Type: application/pdf \n";
                    } else if (extension.equals("jpg")) {
                        sb = sb + "Content-Type: image/jpeg \n";
                    } else {
                        sb = sb + "Content-Type: text/html \n";
                    }
                    sb = sb + "Content-Length: " + tam_archivo + " \n";
                    sb = sb + "\n";
                    bos.write(sb.getBytes());
                    bos.flush();
                    while ((b_leidos = bis2.read(buf, 0, buf.length)) != -1) {
                        bos.write(buf, 0, b_leidos);
                    }
                    bos.flush();
                    bis2.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public ServidorWeb(int puerto){
        this.puerto = puerto;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        iniciaServidor();
        while(! detenido()){
            Socket cl = null;
            try {
                cl = this.ss.accept();
                System.out.println("Conexion aceptada..");
            } catch (IOException e) {
                if(detenido()) {
                    System.out.println("Servidor detenido.") ;
                    break;
                }throw new RuntimeException("Error al aceptar nueva conexion", e);
            }//catch
            this.pool.execute(new Manejador(cl));
        }//while
        this.pool.shutdown();
        System.out.println("Servidor detenido.") ;
    }
    private synchronized boolean detenido() {
        return this.detenido;
    }
    public synchronized void stop(){
        this.detenido = true;
        try {
            this.ss.close();
        } catch (IOException e) {
            throw new RuntimeException("Error al cerrar el socket del servidor", e);
        }
    }
    private void iniciaServidor() {
        try {
            this.ss = new ServerSocket(this.puerto);
            System.out.println("Servicio iniciado.. esperando cliente..");
        } catch (IOException e) {
            throw new RuntimeException("No puede iniciar el socket en el puerto: "+ss.getLocalPort(), e);
        }
    }
    public static void main(String[] args){
        ServidorWeb server = new ServidorWeb(8000);
        new Thread(server).start();
    }//main
}
