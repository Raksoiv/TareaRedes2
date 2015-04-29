import java.io.*;
import java.net.*;

class UDPClient{
   private boolean conected;
   private boolean loggedIn;

   BufferedReader inByUser;

   private InetAddress serverIP;

   public static void main(String args[]) throws Exception{
      UDPClient clienteFTP = new UDPClient();
      clienteFTP.conected = false;
      clienteFTP.mainMenu();
   }

   private void mainMenu() throws Exception{
      String response = "";

      System.out.println("Bienvenido al cliente de servidor FTP LocalHost, de Dribyte!");
      this.inByUser = new BufferedReader(new InputStreamReader(System.in));
      String input = "0";
      System.out.println("Ingrese un comando (Para obtener informacion de los comandos disponibles tipee HELP)");
      while(!input.equals("QUIT")){
         System.out.print("$ ");
         input = this.inByUser.readLine();
         if(input.equals("HELP"))
            printHelp();
         else if(input.equals("QUIT")){
            response = send(input);
            System.out.println("Conexion terminada, cerrando programa\nAdios!");
         }
         else if(input.startsWith("OPEN")){
            if(!loggedIn){
               try{
                  String[] split = input.split(" ");
                  if(split[1].startsWith("localhost"))
                     this.serverIP = InetAddress.getByName("localhost");
                  else
                     this.serverIP = InetAddress.getByName(split[1]);
                  input = split[0];
                  response = send(input);
                  String[] responses = response.split("-");
                  System.out.println(responses[1]);

                  if(responses[0].equals("220")){
                     this.conected = true;
                     logIn();
                     continue;
                  }
                  else{
                     response = "No contestado por el servidor??";
                  }
               }
               catch(Exception e){
                  System.out.println("Error en el comando");
                  System.out.println("Recuerde: OPEN <ip>");
               }
            }
            else{
               System.out.println("Ya tiene la sesión iniciada en el servidor");
            }
         }
         else if(!this.conected){
            System.out.println("No se encuentra conectado al servidor, intente con OPEN");
         }
         else if(input.startsWith("CD")){
            response = send(input);
         }
         else if(input.startsWith("LS")){
            response = send(input);
         }
         else if(input.startsWith("GET")){
            fileGet(input);
         }
         else if(input.startsWith("PUT")){
         }
         else
            System.out.println("Operacion no encontrada: " + input);
         System.out.println(response);
      }
   }

   private void printHelp(){
      System.out.println("\nComandos disponibles:\n");
      System.out.println("OPEN <ip> -> Para abrir la conexion con el servidor FTP en la direccion ip <ip>");
      System.out.println("CD <directory> -> Para cambiar del directorio actual a <directory>");
      System.out.println("LS -> Para requerir informacion de los archivos que existen en la carpeta del servidor");
      System.out.println("GET <file> -> Para extraer el archivo <file> desde el servidor a la carpeta actual del cliente");
      System.out.println("PUT <file> -> Para subir el archivo <file> al servidor");
      System.out.println("QUIT -> Para cerrar la conexion y salir del sistema\n");
   }

   private String send(String message) throws Exception{
      DatagramSocket clientSocket = new DatagramSocket();

      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];

      sendData = message.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.serverIP, 21);
      clientSocket.send(sendPacket);

      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);

      String response = new String(receivePacket.getData());
      response = response.trim();

      return response;
   }

   private void logIn() throws Exception{
      String response;
      String[]  split;
      String in;

      System.out.print("Ingrese Usuario: ");
      in = inByUser.readLine();
      response = send(in);
      split = response.split("-");
      if(!split[0].equals("331")){
         System.out.println(response);
         System.out.println("Usuario incorrecto intente OPEN nuevamente!");
         return;
      }
      System.out.println(response);
      System.out.print("Ingrese Contraseña: ");
      in = "";
      in = inByUser.readLine();
      response = send(in);
      split = response.split("-");
      if(!split[0].equals("230")){
         System.out.println(response);
         System.out.println("Password incorrecta intente OPEN nuevamente!");
         return;
      }
      this.loggedIn = true;
      System.out.println(response);
      return;
   }

   private void fileGet(String input){
      response = send(input);
   }
}