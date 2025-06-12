import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.net.*;


public class Server {
    
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
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Indicate failure to find a free port
        }
    }



    // muiltithreaded server
    class ClientHandler implements Runnable {
        private Socket cs;

        //get client socket
        public ClientHandler(Socket cs) {
            
        }

        public void run(){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                PrintWriter out = new PrintWriter(cs.getOutputStream(), true);

                String request = in.readLine();
                String[] parts = request.split("<>");
                String command = parts[0];
                String filename = parts[1];

                if (command.equals("DOWNLOAD")) {
                    // Check if the file exists
                    File file = new File(filename);
                    if (file.exists()) {
                        long size = file.length();
                        //
                        downloadFile(filename, "destination_path"); // specify the destination path
                        int port = findFreePort();
                        String response = fR(filename, size, port);
                        out.println(response);
                    } else {
                        String response = fE(filename);
                        out.println(response);
                    }
                }

                in.close();
                out.close();
                cs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    void startServer(int port){



    }

    public static void main(String[] args) {
        


    }
}



