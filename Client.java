import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server
    String command;
    // my edits below
    String start_command = "ftpclient"; //needed to start server
    Boolean start_program = false;
    int sPort;
    // for recieving fiels from the server
    InputStream in_stream;
    BufferedOutputStream bos_put;
    FileOutputStream fos_get;
    BufferedOutputStream bos_get;
    FileInputStream fis_put;
    BufferedInputStream bis;
    String continue_program;
    boolean exists;
    String port_command;
    String filename;
    String[] command_div;
    String exists_get;
    boolean needed = true;



    public void Client() {}


    void run()
    {
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            //create a socket to connect to the server
            requestSocket = new Socket("localhost", 5106);
            System.out.println("Connected to localhost, please enter ftpclient command. Server runs on 5106.");
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            bos_put = new BufferedOutputStream(requestSocket.getOutputStream());

            while (start_program == false) {
                port_command = bufferedReader.readLine();
                System.out.println(port_command);
                String[] port_div = port_command.split(" ");
                if (port_div[0].equals(start_command)) { // if the ftp client was input continue
                    try {
                        sPort = Integer.parseInt(port_div[1] );
                        if(sPort == 5106){
                            start_program = true;
                        }else{
                            System.out.println("Server runs on port number 5106, please reenter with correct port number.");
                            start_program = false;
                        }
                    }
                    catch( Exception e ) {
                        System.out.println("Please make sure the port number is an integer");
                        start_program = false;
                    }
                } else {
                    System.out.println("Incorrect start command - this server runs on port  5106, please enter command " +
                            "ftpclient before the correct port number. EX: ftpclient 5106");
                }
            }
            //get Input from standard input
            while(true)
            {
                command = bufferedReader.readLine();
                command_div = command.split(" ");

                if(command_div[0].equals("get")){
                    sendMessage(command);
                    exists_get = (String)in.readObject();
                    if(exists_get.equals("true")){
                        filename = "new" + command_div[1];
                        String file_size_string = (String)in.readObject();
                        int file_size = Integer.parseInt(file_size_string);
                        int buffer_size = 1000;

                        //filename = "new.pptx";// + command_div[1];
                        in_stream = requestSocket.getInputStream();

                        fos_get = new FileOutputStream(filename);
                        bos_get = new BufferedOutputStream(fos_get);
                        byte data[] = new byte[buffer_size];
                        long byteContent = 0;

                        // read first buffers
                        for (long i = 0; i < (file_size / buffer_size); i++)
                        {
                            byteContent += in_stream.read(data, 0, buffer_size);
                            bos_get.write(data, 0, buffer_size);
                        }
                        // read remaining bytes
                        byteContent += in_stream.read(data, 0, file_size % buffer_size);
                        bos_get.write(data, 0, file_size % buffer_size);

                        assert byteContent == file_size;
                        System.out.println("count " + byteContent);
                        bos_get.close();
                        fos_get.close();
                    }else{
                        System.out.println("Please enter valid command and existing file.");
                    }


                }else if(command_div[0].equals("put")){
                    sendMessage(command);
                    filename = System.getProperty("user.dir") +"/" + command_div[1];
                    Path file_path = Paths.get(filename);
                    File tmpFile = new File(filename);
                    exists = tmpFile.exists();
                    if(exists){
                        sendMessage("true");
                    }else{
                        sendMessage("false");
                    }
                    if(exists){
                        int file_size = (int) Files.size(file_path);
                        // first send file size
                        sendMessage(String.valueOf(file_size));

                        byte[] data = new byte[1000];
                        try
                        {
                            fis_put = new FileInputStream(tmpFile);
                            bis = new BufferedInputStream(fis_put);
                            int byteContent;
                            while ((byteContent = bis.read(data)) > 0)
                            {
                                bos_put.write(data, 0, byteContent);
                            }
                            bos_put.flush();
                            bis.close();
                            fis_put.close();
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
                        System.out.println("Please enter valid command and existing file.");
                    }


                }else{
                    System.out.println("Please enter valid command and existing file.");
                }


                /////////////////


                // get header, i.e. just the size
                /*String file_size_string = (String)in.readObject();
                int file_size = Integer.parseInt(file_size_string);
                int buffer_size = 1000;
                //filename = "new.pptx";// + command_div[1];
                in_stream = requestSocket.getInputStream();
                fos = new FileOutputStream(filename);
                bos = new BufferedOutputStream(fos);
                byte data[] = new byte[buffer_size];
                long byteContent = 0;
                // read first buffers
                for (long i = 0; i < (file_size / buffer_size); i++)
                {
                    byteContent += in_stream.read(data, 0, buffer_size);
                    bos.write(data, 0, buffer_size);
                }
                // read remaining bytes
                byteContent += in_stream.read(data, 0, file_size % buffer_size);
                bos.write(data, 0, file_size % buffer_size);
                assert byteContent == file_size;
                System.out.println("count " + byteContent);
                bos.close();
                fos.close();*/
                            /*} else {
                                System.out.println("Please enter a file that exits");
                                break;
                            }
                        } catch(EOFException eofe){
                            System.out.println("End of file reached.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Get out ");
                        break;
                    case "put":
                        System.out.println("Put out ");
                        break;
                    default:
                        System.out.println("Please enter a valid command and existing file.");
                }*/
            }
        }
        catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }
        catch ( ClassNotFoundException e ) {
            System.err.println("Class not found");
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //Close connections
            try{
                in.close();
                out.close();
                requestSocket.close();
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
            //stream write the message
            out.writeObject(msg);
            out.flush();
            System.out.println("Send message: " + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    //main method
    public static void main(String args[])
    {
        Client client = new Client();
        client.run();
    }

}