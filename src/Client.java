import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
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
    FileOutputStream fos;
    BufferedOutputStream bos;
    FileInputStream fis;
    BufferedInputStream bis;
    String continue_program;
    String exists;
    String port_command;
    String filename;
    String[] command_div;

    public void Client() {}

    void run()
    {
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            //create a socket to connect to the server
            requestSocket = new Socket("localhost", 5106);
            System.out.println("Connected to localhost in port 5106");
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

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
            //while(true)
           // {
                command = bufferedReader.readLine();
                command_div = command.split(" ");
                //message = (String)in.readObject();
                //Send the sentence to the server
                //sendMessage(message);
                //Receive the upperCase sentence from the server
                //MESSAGE = (String)in.readObject();
                //show the message to the user
                //System.out.println("Receive message: " + MESSAGE);
            switch (command_div[0]) {
                case "get":
                    try {
                        sendMessage(command);
                        message = (String) in.readObject(); // file exists
                        System.out.println(message);
                        if (message.equals("true")) {
                            System.out.println("File exists, beginning download.");

                            filename = "new_" + command_div[1];
                            in_stream = requestSocket.getInputStream();
                            fos = new FileOutputStream(filename);
                            bos = new BufferedOutputStream(fos);
                            byte data[] = new byte[1000];
                            int byteContent;
                            int count = 0;
                            //System.out.println("Stuck 1");
                            while ((byteContent = in_stream.read(data)) >= 0) {
                                bos.write(data, 0, byteContent);
                                count++;
                            }
                            System.out.println("count " + count);
                            bos.close();
                            fos.close();
                        } else {
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
            }
            //}
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
