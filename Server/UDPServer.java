import java.io.*;
import java.net.*;

class UDPServer{
   private boolean loggedIn;

   public static void main(String args[]) throws Exception{
      UDPServer server = new UDPServer();
      server.loggedIn = false;
      server.listenMessage();
   }

   private void listenMessage() throws Exception{
      DatagramSocket serverSocket = new DatagramSocket(21);
      byte[] receiveData = new byte[1024];
      byte[] sendData = new byte[1024];
      while(true){
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         String message = new String(receivePacket.getData());
         message = message.trim();
         String response = response(message);
         InetAddress ipAddress = receivePacket.getAddress();
         int port = receivePacket.getPort();
         sendData = response.getBytes();
         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
         serverSocket.send(sendPacket);
      }
   }

   private void listenFile(){
   }

   private String response(String message){
      if(message.equals("OPEN"))
         return "220-Hola soy el servidor FTP de Dribyte!\nPor favor inicie sesion para acceder a las funcionalidades del servidor";
      else
         return "501-No entendi ._.";
   }

}