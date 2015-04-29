import java.io.*;
import java.net.*;

class UDPServer{
   private boolean loggedIn;

   private DatagramSocket controlSocket;
   private DatagramSocket transferSocket;

   private String path;
   private String realPath;

   public static void main(String args[]) throws Exception{
      UDPServer server = new UDPServer();
      server.loggedIn = false;
      server.controlSocket = new DatagramSocket(20);
      server.transferSocket = new DatagramSocket(21);
      server.path = "/home/admin/";
      server.realPath = "";
      server.listenMessage();
   }

   private void listenMessage() throws Exception{
      byte[] receiveData = new byte[1024];
      while(true){
         receiveData = new byte[1024];
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         this.transferSocket.receive(receivePacket);
         this.response(receivePacket);
      }
   }

   private void listenFile(){
   }

   private void response(DatagramPacket receivePacket) throws Exception{
      byte[] sendData = new byte[1024];

      String message = new String(receivePacket.getData());
      message = message.trim();

      String answer = "";

      if(message.startsWith("OPEN")){
         if(!loggedIn){
            logIn(receivePacket);
            return;
         }
         else
            answer = "230-Ya se inicio sesion en el servidor!";
      }
      else if(!loggedIn){
         answer = "530-No se ha iniciado sesion aun";
      }
      else if(message.startsWith("CD")){
         answer = changeDirectory(message);
      }
      else if(message.startsWith("LS")){
         answer = listDirectory();
      }
      else if(message.startsWith("GET")){
         fileSend(message);
      }
      else
         answer = "501-No entendi ._.";

      InetAddress ipAddress = receivePacket.getAddress();
      int port = receivePacket.getPort();
      sendData = answer.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
      this.controlSocket.send(sendPacket);
   }

   private void logIn(DatagramPacket receivePacket) throws Exception{
      byte[] receiveData = new byte[1024];
      byte[] sendData = new byte[1024];
      String answer;
      boolean flag;

      //Preparando el inicio de sesion
      answer = "220-Hola soy el servidor FTP de Dribyte!\nPor favor inicie sesion para acceder a las funcionalidades del servidor";
      InetAddress ipAddress = receivePacket.getAddress();
      int port = receivePacket.getPort();
      sendData = answer.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
      this.controlSocket.send(sendPacket);

      receiveData = new byte[1024];
      sendData = new byte[1024];

      //Recibiendo el usuario
      flag = false;
      receivePacket = new DatagramPacket(receiveData, receiveData.length);
      this.transferSocket.receive(receivePacket);
      String user = new String(receivePacket.getData());
      user = user.trim();
      if(!user.equals("admin")){
         answer = "430-Usuario Invalido!";
         flag = true;
      }
      else
         answer = "331-Usuario Correcto!";
      ipAddress = receivePacket.getAddress();
      port = receivePacket.getPort();
      sendData = answer.getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
      this.controlSocket.send(sendPacket);
      if(flag)
         return;

      receiveData = new byte[1024];
      sendData = new byte[1024];

      //Recibiendo la password
      receivePacket = new DatagramPacket(receiveData, receiveData.length);
      this.transferSocket.receive(receivePacket);
      String password = new String(receivePacket.getData());
      password = password.trim();
      if(!password.equals("1234")){
         answer = "430-Contraseña incorrecta!";
      }
      else{
         answer = "230-Inicio de Sesion Completado!";
         this.loggedIn = true;
      }
      ipAddress = receivePacket.getAddress();
      port = receivePacket.getPort();
      sendData = answer.getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
      this.controlSocket.send(sendPacket);
      return;
   }

   private String changeDirectory(String message){
      String[] split = message.split(" ");
      if(split[1].equals("..")){
         String[] splitPath = this.path.split("/");
         String[] splitRealPath = this.realPath.split("/");
         this.path = "/home/admin/";
         this.realPath = "";
         if(splitRealPath.length > 2){
            for(int i = 0; i < splitPath.length - 2; i++){
               this.path += splitPath[i];
            }
            for(int i = 0; i < splitRealPath.length - 2; i++){
               this.realPath += splitRealPath[i];
            }
         }
         return "250-El nuevo directorio actual es: '" + this.path + "'";
      }

      File folder = new File(realPath + split[1]);
      if(folder.exists()){
         this.path += (split[1] + "/");
         this.realPath += (split[1] + "/");
         return "250-El nuevo directorio actual es: '" + this.path + "'";
      }
      else{
         return "550-Directorio no encontrado: " + split[1];
      }
   }

   private String listDirectory(){
      String listOfFiles = "";
      String localPath;

      if(realPath.equals(""))
         localPath = ".";
      else
         localPath = realPath;

      try{
         File folder = new File(localPath);
         listOfFiles = "250-Lista de Archivos\n";
         File[] files = folder.listFiles();
         for(File file: files){
            listOfFiles += file.getName() + "\n";
         }
      }
      catch(Exception e){
         return "550-Problemas en el servidor";
      }
      return listOfFiles;
   }

   private void fileSend(String message){
      
   }
}