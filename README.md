# COMPX234-A4
assignment4
##
6/11 21:35  

Take a look at the assignment3.Create synchronous behaviour to download and muliti-threaded server.  
And some structures like run() and startServer() are not finished yet.

## 
6/11 22:20  
The protocol:  
1. client request:
DOWNLOAD<filename>  
(Client) so create fR fuction to format the request.  
2. server response:
OK<filename>SIZE<size_bytes>PORT<port_number>  
ERR<filename>NOT_FOUND
(Server) so create fR and fE function to format the response.  

if respond is lost, then the client timeouts and retransmits the DOWNLOAD request.  
(Client) so create sendAndReceive function.  
(just for framework)  

6/12 20:02  
(Server)specify run(),add findFreePort() function.  
Client has downloadFile() method which promisses that only one file is downloaded at a time for one client.  
then do Server need downloadFile() method?  

##
6/12 20:20  
port number (in the range 50000-51000) 
(Sever) I have make wrong with the BufferedReader and PrintWriter(These are for TCP). In UDP the BufferedReader and PrintWriter are not used.And socket is DatagramSocket.  
I have searched and changed run() function.  

##  
6/12 21:22  
(Client)sendAndReceive() function is finished.maybe.

##  
6/12 21:45 
(Client)client_Request()  is finished.
(Server)server_Request()  is finished.  
And static main().
downloadFile()?

##  
6/12 23:32
Server started on port 51234
Exception in thread "main" java.lang.NoClassDefFoundError: Server$ClientHandler
        at Server.startServer(Server.java:164)
        at Server.main(Server.java:199)
Caused by: java.lang.ClassNotFoundException: Server$ClientHandler
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:528)
        ... 2 more

compiled wrong.
re-compile and run again.  
 
## 
6/13 7:54
support file list
(Client)rewrited downloadFile(),client_Request(). 
And added files.txt (which contains test.txt and test2.txt)to Client file.