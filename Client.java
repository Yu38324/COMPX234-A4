import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Client {
    private String host;
    private int port;
    private String filename;
    private static final int MAX_RETRIES = 5;
    private static final int BASE_TIMEOUT = 1000;
    private static final int MAX_BLOCK_SIZE = 1000;

    public Client(String host, int port, String filename) {
        this.host = host;
        this.port = port;
        this.filename = filename;
    }
    //format request: DOWNLOAD<filename>
    private String fR() {
        return "DOWNLOAD<" + filename + ">";
    }

    //client timeouts and retransmits the request
    private String sendAndReceive(DatagramSocket socket, String message, InetAddress address, int port)throws IOException {
        //transmit a packet,set timeout, retransmit if no response
        byte[] sendData = message.getBytes();
        byte[] receiveData = new byte[2048]; 
        int retries = 0;
        int currentTimeout = BASE_TIMEOUT;
        while (retries < MAX_RETRIES) {
            try {
                //send request
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);
                //set timeout
                socket.setSoTimeout(currentTimeout);
                //receive response
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                //return response
                return new String(receivePacket.getData(),0, receivePacket.getLength());

            } catch (Exception e) {
                //increment retries and timeout
                retries++;
                currentTimeout *= 2;
            }
        }
        throw new SocketTimeoutException("Max retries exceeded");
    }
    
    public void client_Request(){

        
    }

// download
    private synchronized void downloadFile(String file, String destination) throws IOException {
    

    }

    public static void main(String[] args) {
        

    }

    
}
