import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
    int sPort = 5106;    //The server will be listening on this port number
    ServerSocket sSocket;   //serversocket used to lisen on port number 8000
    Socket connection = null; //socket for the connection with the client
    String message;    //message received from the client
    String MESSAGE;    //uppercase message send to the client
    ObjectOutputStream out;  //stream write to the socket
    ObjectInputStream in;    //stream read from the socket
    String filename;
    FileInputStream fis;
    BufferedInputStream bis;
    BufferedOutputStream bos;
    public void Server() {}

    void run()
    {
        try{
            //create a serversocket
            sSocket = new ServerSocket(sPort, 10);
            //Wait for connection
            System.out.println("Waiting for connection");
            //accept a connection from the client
            connection = sSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            //initialize Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            try{
                //while(true)
                //{
                    //receive the message sent from the client
                    message = (String)in.readObject();
                    //show the message to the user
                    System.out.println("Receive message: " + message);
                    //Capitalize all letters in the message
                    MESSAGE = message.toUpperCase();
                    //send MESSAGE back to the client
                    sendMessage(MESSAGE);
                    filename = "/Users/leslie.smith1/IdeaProjects/project1/downloadTestFile.pptx";
                    File tmpFile = new File(filename);
                    byte[] data = new byte[1000];
                    try {
                        fis = new FileInputStream(tmpFile);
                        bis = new BufferedInputStream(fis);
                        bos = new BufferedOutputStream(connection.getOutputStream());
                        System.out.println("Got it 2.2");
                        int byteContent;
                        while ((byteContent = bis.read(data)) > 0) {
                            bos.write(data, 0, byteContent);
                        }
                        System.out.println("Got it 67");
                        bos.close();
                        bis.close();
                        fis.close();

                        System.out.println("Got it 3");
                        //message = (String)in.readObject();

                    } catch(EOFException eofe){
                        System.out.println("End of file reached.");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

              //  }

            }
            catch(ClassNotFoundException classnot){
                System.err.println("Data received in unknown format");
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //Close connections
            try{
                in.close();
                out.close();
                sSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
    //send a message to the output stream
    void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("Send message: " + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Server s = new Server();
        s.run();

    }

}
