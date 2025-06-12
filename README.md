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