import java.io.*;
import java.net.Socket;

public class ftp_client {
    Socket socket;
    InputStream in_stream;
    FileOutputStream fos;
    BufferedOutputStream bos;
    int buffer_size = 1000;

    ftp_client(Socket server_socket){
        socket = server_socket;
        in_stream = null;
        fos = null;
        bos = null;
    }
    void recieveFileServer(String filename){
        try{
            in_stream = socket.getInputStream();
            buffer_size = socket.getReceiveBufferSize();
            System.out.println("Buffer Size = " + buffer_size);
            fos = new FileOutputStream(filename);
            bos = new BufferedOutputStream(fos);
            byte data[] = new byte[buffer_size];
            int byteContent;
            System.out.println("Stuck 1");
            while((byteContent = in_stream.read(data)) >= 0){
                bos.write(data, 0, byteContent);
            }
            bos.close();
            fos.close();
            System.out.println("Closed client connections");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendFileServer(File file){
        //file.renameTo(new File("new"));
        FileInputStream fis;
        BufferedInputStream bis;
        BufferedOutputStream bos;
        byte[] data = new byte[1000];
        try{
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(socket.getOutputStream());
            int byteContent;
            while((byteContent = bis.read(data)) > 0) {
                bos.write(data, 0, byteContent);
            }
            bos.close();
            fis.close();
            bis.close();
            System.out.println("Closed server connections");

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


