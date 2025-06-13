import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class Client {
    private String host;
    private int port;
    private String fileListname;
    private static final int MAX_RETRIES = 5;
    private static final int BASE_TIMEOUT = 1000;
    private static final int MAX_BLOCK_SIZE = 1000;

    public Client(String host, int port, String filename) {
        this.host = host;
        this.port = port;
        this.fileListname = filename;
    }
    //format request: DOWNLOAD<filename>
    private String fR(String filename) {
        return "DOWNLOAD<" + filename + ">";
    }

    private synchronized void downloadFile(String filename) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress ipAddress = InetAddress.getByName(host);
            
            // send download request
            String response = sendAndReceive(socket, fR(filename), ipAddress, port);
            
            if (response.startsWith("ERR")) {
                System.out.println("Error: " + response);
                return;
            }
            
            // analize OK: OK<filename>SIZE<size>PORT<port>
            String[] parts = response.split("<|>");
            String receivedFilename = parts[1];
            long fileSize = Long.parseLong(parts[3]);
            int dataPort = Integer.parseInt(parts[5]);
            System.out.println("Downloading " + receivedFilename + " (" + fileSize + " bytes)");
            
            // create file
            Path filePath = Paths.get(receivedFilename);
            try (OutputStream fos = Files.newOutputStream(filePath)) {
                long bytesReceived = 0;
                int blockCount = 0;
                
                while (bytesReceived < fileSize) {
                    // calculate start and end of block
                    long start = bytesReceived;
                    long end = Math.min(start + MAX_BLOCK_SIZE - 1, fileSize - 1);
                    
                    // send request for block
                    String request = "FILE<" + filename + ">GET<START<" + start + ">END<" + end + ">>";
                    response = sendAndReceive(socket, request, ipAddress, dataPort);
                    // analize FILE response: FILE<filename>OK<START<start>END<end>DATA<base64data>>
                    String[] respParts = response.split("<|>");
                    
                    // check response
                    if (respParts.length < 10 || !respParts[2].equals("OK")) {
                        System.out.println("Invalid response: " + response);
                        continue;
                    }
                    
                    long respStart = Long.parseLong(respParts[4]);
                    long respEnd = Long.parseLong(respParts[6]);
                    String base64Data = respParts[8];
                    
                    // decode
                    byte[] blockData = Base64.getDecoder().decode(base64Data);
                    fos.write(blockData);
                    bytesReceived += blockData.length;
                    blockCount++;
                    
                    // print progress
                    System.out.print(".");
                    if (blockCount % 50 == 0) System.out.println();
                }
                System.out.println("\nDownload complete");
                
                // send close request
                String closeRequest = "FILE<" + filename + ">CLOSE";
                socket.send(new DatagramPacket(
                    closeRequest.getBytes(), 
                    closeRequest.getBytes().length,
                    ipAddress,
                    dataPort
                ));
                byte[] closeResponseData = new byte[1024];
                DatagramPacket closeResponsePacket = new DatagramPacket(closeResponseData, closeResponseData.length);
                socket.setSoTimeout(2000);
                try {
                    socket.receive(closeResponsePacket);
                    String closeResponse = new String(closeResponsePacket.getData(), 0, closeResponsePacket.getLength());
                    if (closeResponse.startsWith("FILE<" + filename + ">CLOSE_OK")) {
                        System.out.println("File closed successfully");
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Close confirmation timeout (ignored)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            // read filelist
            List<String> filesToDownload = Files.readAllLines(Paths.get(fileListname));
            
            for (String filename : filesToDownload) {
                filename = filename.trim();
                if (!filename.isEmpty()) {
                    System.out.println("\nStarting download: " + filename);
                    downloadFile(filename);
                }
            }
            System.out.println("\nAll files downloaded!");
        } catch (IOException e) {
            System.err.println("Error reading file list: " + e.getMessage());
        }
                
    }




    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Client <host> <port> <filename>");
            return;
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String filename = args[2];
        
        new Client(host, port, filename).client_Request();

    }

    
}
