import java.awt.datatransfer.FlavorEvent;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


public class Main {
    String filename;

    public static void main(String[] args) {
        System.out.println("Hello world!");
        File file = new File("/Users/leslie.smith1/Grad_classes/Spring2023/Comp_Networking/Chap2.pptx");
        BufferedInputStream bis = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            FileOutputStream fileOS = new FileOutputStream("/Users/leslie.smith1/IdeaProjects/project1/src/success.pptx");
            byte data[] = new byte[1000];
            int byteContent;
            while((byteContent = bis.read(data, 0, 1000)) != -1){
                fileOS.write(data, 0, byteContent);
            }
           /* while (bis.available() > 0) {
                System.out.print((char) bis.read());

            }*/

        } catch (FileNotFoundException fnfe) {
            System.out.println("The specified file not found" + fnfe);
        } catch (IOException ioe) {
            System.out.println("I/O Exception: " + ioe);
        }
        finally {
            try{
                if(bis != null && fis != null){
                    fis.close();
                    bis.close();
                }
            }catch(IOException ioe)
            {
                System.out.println("Error in InputStream close(): " + ioe);
            }
        }
    }
}

