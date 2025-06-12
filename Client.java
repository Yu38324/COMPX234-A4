import java.io.*;

public class Client {
    private String host;
    private int port;
    private String filename;

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
    private void sendAndReceive(){
        //transmit a packet,set timeout, retransmit if no response
        

    }
    
    public void client_Request(){

        
    }

// download
    private synchronized void downloadFile(String file, String destination) throws IOException {
    

    }

    public static void main(String[] args) {
        

    }

    
}
