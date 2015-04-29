import java.io.*;
import java.net.*;

class UDPClient{
   private boolean conected;

   public static void main(String args[]) throws Exception{
      UDPClient clienteFTP = new UDPClient();
      clienteFTP.conected = false;
      clienteFTP.mainMenu();
   }

   private void mainMenu() throws Exception{
      System.out.println("Bienvenido al cliente de servidor FTP LocalHost, de Dribyte!");
      BufferedReader inByUser = new BufferedReader(new InputStreamReader(System.in));
      String input = "0";
      System.out.println("Ingrese un comando (Para obtener informacion de los comandos disponibles tipee Help)");
      while(!input.equals("QUIT")){
         System.out.print("$ ");
         input = inByUser.readLine();
         if(input.equals("Help"))
            printHelp();
         else if(input.equals("QUIT"))
            System.out.println("Conexion terminada, cerrando programa\nAdios!");
         else if(input.equals("OPEN") || (input.equals("CD") || input.equals("LS") || input.equals("GET") || input.equals("PUT") && conected))
            server(input);
         else
            System.out.println("Operacion no encontrada: " + input);
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

   private void server(String message) throws Exception{
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");

      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];

      sendData = message.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 21);
      clientSocket.send(sendPacket);

      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);

      String response = new String(receivePacket.getData());
      response = response.trim();

      String[] responses = response.split("-");

      if(responses[0].equals("220"))
         conected = true;

      System.out.println(responses[1]);
      clientSocket.close();
   }
}