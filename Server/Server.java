import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;


public class Server {
    private static final int MIN_PORT = 50000;
    private static final int MAX_PORT = 51000;
    private static final int MAX_BLOCK_SIZE = 1000;
    
    // format response: OK<filename>SIZE<size_bytes>PORT<port_number>
    private String fR(String filename, long size, int port) {
        return "OK<" + filename + ">SIZE<" + size + ">PORT<" + port + ">";
    }

    //format error response: ERR<filename>NOT_FOUND
    private String fE(String filename) {
        return "ERR<" + filename + ">NOT_FOUND";
    }
    // do server need download method?
    // private void downloadFile(String filename, String destination) {
    //     // This method would handle the file download logic


    // }
    private int findFreePort() {
        try (DatagramSocket socket = new DatagramSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            return -1;
        }
}
    // muiltithreaded server
    class ClientHandler implements Runnable {
        private final String filename;
        private final InetAddress clientAddress;
        private final int clientPort;
        //get client socket
        public ClientHandler(String filename, InetAddress clientAddress, int clientPort) {
            this.filename = filename;
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
        }
        

        public void run(){
            try (DatagramSocket dataSocket = new DatagramSocket(findFreePort())){
                // BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                // PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
                System.out.println("Serving"+filename+"on port"+dataSocket.getLocalPort());
                
                // String request = in.readLine();
                
                Path filePath = Paths.get(filename);
                long fileSize = Files.size(filePath);
                byte[] buffer = new byte[MAX_BLOCK_SIZE];
                try(InputStream fis = Files.newInputStream(filePath)){
                    while(true){
                        byte[]receiveData = new byte[2024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        dataSocket.receive(receivePacket);
                        String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        String[] parts = request.split("<>");
                        String command = parts[0];
                        String filename = parts[1];
                        if (parts[0].equals("FILE") && parts[2].equals("GET")) {
                            // get request
                            long start = Long.parseLong(parts[4]);
                            long end = Long.parseLong(parts[6]);
                            int length = (int) (end - start + 1);
                            
                            // file block
                            // fis.skipNBytes(start);
                            // int bytesRead = fis.read(buffer, 0, length);
                            try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
                                raf.seek(start);
                                int bytesRead = raf.read(buffer, 0, length);
                                
                                // Base64 encode
                                String base64Data = Base64.getEncoder().encodeToString(
                                    Arrays.copyOf(buffer, bytesRead)
                                );
                                
                                // send response
                                String response = "FILE<" + filename + ">OK<START<" + start + 
                                    ">END<" + end + ">DATA<" + base64Data + ">>";
                                byte[] sendData = response.getBytes();
                                
                                DatagramPacket sendPacket = new DatagramPacket(
                                    sendData, 
                                    sendData.length, 
                                    clientAddress, 
                                    clientPort
                                );
                                dataSocket.send(sendPacket);
                            }
                        } else if (parts[0].equals("FILE") && parts[2].equals("CLOSE")) {
                            // send close response
                            String closeResponse = "FILE<" + filename + ">CLOSE_OK";
                            byte[] sendData = closeResponse.getBytes();
                            
                            DatagramPacket sendPacket = new DatagramPacket(
                                sendData, 
                                sendData.length, 
                                clientAddress, 
                                clientPort
                            );
                            dataSocket.send(sendPacket);
                            break;
                        }
                    }
                } 

                // if (command.equals("DOWNLOAD")) {
                //     // Check if the file exists
                //     File file = new File(filename);
                //     if (file.exists()) {
                //         long size = file.length();
                //         //
                //         //downloadFile(filename, "destination_path"); // specify the destination path
                //         int port = findFreePort();
                //         String response = fR(filename, size, port);
                //         out.println(response);
                //     } else {
                //         String response = fE(filename);
                //         out.println(response);
                //     }
                // }

                // in.close();
                // out.close();
                // cs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    void startServer(int port){
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            System.out.println("Server started on port " + port);
            
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                
                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if (request.startsWith("DOWNLOAD<")) {
                    // analize filename
                    String filename = request.substring(9, request.length() - 1);
                    File file = new File(filename);
                    String response;
                    if (file.exists()) {
                        int dataPort = findFreePort();
                        response = fR(filename, file.length(), dataPort);

                        new Thread(new ClientHandler(
                            filename,
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                        )).start();
                    } else {
                        response = fE(filename);
                    }
                    // send response
                    byte[] sendData = response.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(
                        sendData, 
                        sendData.length,
                        receivePacket.getAddress(),
                        receivePacket.getPort()
                    );
                    serverSocket.send(sendPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
                    



    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }
        
        int port = Integer.parseInt(args[0]);
        new Server().startServer(port);
    


    }
    }
// 
 


