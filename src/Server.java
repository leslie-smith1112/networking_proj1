import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    FileInputStream fis_get;
    // for download
    BufferedInputStream bis;
    BufferedOutputStream bos_get;
    FileOutputStream fos_get;
    // for upload
    BufferedOutputStream bos_put;
    FileOutputStream fos_put;
    String exists_put;

    InputStream in_stream;
    String command;
    boolean exists;
    String[] command_div;
    public void Server() {}

    void run()
    {
        try
        {
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
            //for download
            bos_get = new BufferedOutputStream(connection.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            try
            {
                while (true)
                {
                    //receive the message sent from the client
                    command = (String) in.readObject();
                    command_div = command.split(" ");
                    //Capitalize all letters in the message
                    //MESSAGE = (String) in.readObject();
                    if(command_div[0].equals("get"))
                    {
                        filename = System.getProperty("user.dir") +"/" + command_div[1];
                        Path file_path = Paths.get(filename);
                        File tmpFile = new File(filename);
                        exists = tmpFile.exists();
                        if(exists){
                            sendMessage("true");
                            int file_size = (int) Files.size(file_path);

                            // first send file size
                            sendMessage(String.valueOf(file_size));



                            byte[] data = new byte[1000];
                            try
                            {
                                fis_get = new FileInputStream(tmpFile);
                                bis = new BufferedInputStream(fis_get);
                                int byteContent;
                                while ((byteContent = bis.read(data)) > 0)
                                {
                                    bos_get.write(data, 0, byteContent);
                                }
                                bos_get.flush();
                                bis.close();
                                fis_get.close();

                                //message = (String)in.readObject();

                            }
                            catch(EOFException eofe)
                            {
                                System.out.println("End of file reached.");

                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }else{
                            sendMessage("false");
                        }


                    }else if(command_div[0].equals("put")){

                        exists_put = (String)in.readObject();
                        if(exists_put.equals("true")){
                            filename = "new" + command_div[1];
                            String file_size_string = (String)in.readObject();
                            int file_size = Integer.parseInt(file_size_string);
                            int buffer_size = 1000;

                            //filename = "new.pptx";// + command_div[1];
                            in_stream = connection.getInputStream();

                            fos_put = new FileOutputStream(filename);
                            bos_put = new BufferedOutputStream(fos_put);
                            byte data[] = new byte[buffer_size];
                            long byteContent = 0;

                            // read first buffers
                            for (long i = 0; i < (file_size / buffer_size); i++)
                            {
                                byteContent += in_stream.read(data, 0, buffer_size);
                                bos_put.write(data, 0, buffer_size);
                            }
                            // read remaining bytes
                            byteContent += in_stream.read(data, 0, file_size % buffer_size);
                            bos_put.write(data, 0, file_size % buffer_size);

                            assert byteContent == file_size;
                            System.out.println("count " + byteContent);
                            bos_put.close();
                            fos_put.close();
                        }
                    }else{
                        //do nothing
                    }


                }

            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            bos_get.close();
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
        }
        finally
        {
            //Close connections
            try
            {
                in.close();
                out.close();
                sSocket.close();
            }
            catch(IOException ioException)
            {
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
