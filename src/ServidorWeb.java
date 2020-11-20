
import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class ServidorWeb {

    public static final int PUERTO = 8000;
    ServerSocket ss;

    //Gson gson = new Gson();

    class Manejador extends Thread {

        protected Socket socket;
        protected PrintWriter pw;
        protected BufferedOutputStream bos;
        protected BufferedReader br;
        protected String FileName;

        public Manejador(Socket _socket) throws Exception {
            this.socket = _socket;
        }

        public void run() {

            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bos = new BufferedOutputStream(socket.getOutputStream());
                pw = new PrintWriter(new OutputStreamWriter(bos));
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

                    } else if (line.toUpperCase().startsWith("POST")) {

                        StringTokenizer tokens = new StringTokenizer(line);

                        String req = tokens.nextToken();

                        int i = 0;
                        while (req != null) {
                            //String peticion = gson.toJson(req);
                            System.out.println("Token " + i + ":" + req + "\r\n");

                            try {
                                req = tokens.nextToken();
                            } catch (NoSuchElementException e) {
                                req = null;
                            }
                            i++;
                        }

                        //RESPUESTA
                        pw.println("HTTP/1.0 200 Okay");
                        pw.flush();
                        pw.println();
                        pw.flush();
                        pw.print("<html><head><title>SERVIDOR WEB");
                        pw.flush();
                        pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
                        pw.flush();
                        pw.print("<h3><b>RESPUESTA POST SIN ARGUMENTOS EN URL</b></h3>");
                        pw.flush();
                        pw.print("</center></body></html>");
                        pw.flush();

                    } else if (line.toUpperCase().startsWith("PUT")) {

                        StringTokenizer tokens = new StringTokenizer(line);

                        String req = tokens.nextToken();

                        int i = 0;
                        while (req != null) {
                            //String peticion = gson.toJson(req);
                            System.out.println("Token " + i + ":" + req + "\r\n");

                            try {
                                req = tokens.nextToken();
                            } catch (NoSuchElementException e) {
                                req = null;
                            }
                            i++;
                        }

                        //RESPUESTA
                        pw.println("HTTP/1.0 200 Okay");
                        pw.flush();
                        pw.println();
                        pw.flush();
                        pw.print("<html><head><title>SERVIDOR WEB");
                        pw.flush();
                        pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
                        pw.flush();
                        pw.print("<h3><b>RESPUESTA PUT SIN ARGUMENTOS EN URL</b></h3>");
                        pw.flush();
                        pw.print("</center></body></html>");
                        pw.flush();

                    }else if (line.toUpperCase().startsWith("HEAD")) {

                        StringTokenizer tokens = new StringTokenizer(line);

                        String req = tokens.nextToken();
                        int i = 0;
                        while (req != null) {
                            //String peticion = gson.toJson(req);
                            System.out.println("Token " + i + ":" + req + "\r\n");
                            try {
                                req = tokens.nextToken();
                            } catch (NoSuchElementException e) {
                                req = null;
                            }
                            i++;
                        }

                        //RESPUESTA
                        pw.println("HTTP/1.0 200 Okay");
                        pw.flush();
                        pw.println();
                        pw.flush();
//                        pw.print("<html><head><title>SERVIDOR WEB");
//                        pw.flush();
//                        pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
//                        pw.flush();
//                        pw.print("<h3><b>RESPUESTA HEAD SIN ARGUMENTOS EN URL</b></h3>");
//                        pw.flush();
//                        pw.print("</center></body></html>");
//                        pw.flush();

                    }

                } else if (line.toUpperCase().startsWith("GET")) {
                    StringTokenizer tokens = new StringTokenizer(line, "?");

                    //String peticion = gson.toJson(tokens);
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
                    pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
                    pw.flush();
                    pw.print("<h3><b>" + req + "</b></h3>");
                    pw.flush();
                    pw.print("</center></body></html>");
                    pw.flush();

                } else {
                    pw.println("HTTP/1.0 501 Not Implemented");
                    pw.println();
                }
                pw.flush();
                bos.flush();
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
            if (line.toUpperCase().startsWith("GET")) {
                i = line.indexOf("/");
                f = line.indexOf(" ", i);
                FileName = line.substring(i + 1, f);
            }
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

        public void SendArchivo(String arg) {
            int indice = arg.indexOf(".");
            String extension = arg.substring(indice + 1, arg.length());
            System.out.println("Extension de archivo: " + extension);

            try {
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

                /**
                 * ********************************************
                 */
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

                //out.println("HTTP/1.0 200 ok");
                //out.println("Server: Axel Server/1.0");
                //out.println("Date: " + new Date());
                //out.println("Content-Type: text/html");
                //out.println("Content-Length: " + mifichero.length());
                //out.println("\n");
                /**
                 * ********************************************
                 */
                while ((b_leidos = bis2.read(buf, 0, buf.length)) != -1) {
                    bos.write(buf, 0, b_leidos);

                }
                bos.flush();
                bis2.close();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public ServidorWeb() throws Exception {
        System.out.println("Iniciando Servidor.......");
        this.ss = new ServerSocket(PUERTO);
        System.out.println("Servidor iniciado:---OK");
        System.out.println("Esperando por Cliente....");
        for (;;) {
            Socket accept = ss.accept();
            new Manejador(accept).start();
        }
    }

    public static void main(String[] args) throws Exception {
        ServidorWeb sWEB = new ServidorWeb();
    }

}
