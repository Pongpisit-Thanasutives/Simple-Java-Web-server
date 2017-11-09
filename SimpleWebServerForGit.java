import java.io.*;
import java.net.*;
import java.util.*;
// telnet localhost 8080
// PUT / C:/Users/Pongpisit/Desktop/index.html index.html
// The above command will change the content in index.html to
// <!DOCTYPE html><html><head><title>NOT YOUR SITE ANYMORE</title></head><body>YOU ARE HACKED.</body></html> 
public class SimpleWebServer {
  /* Run the HTTP server on this TCP port. */
  private static final int PORT = 8080;
  /* The socket used to process incoming connections
  from web clients */
  private static ServerSocket dServerSocket;
  private static String logString = "";
  private static String fileDestination = "C:" + File.separator + "SimpleWebServer";
  public SimpleWebServer () throws Exception {
    dServerSocket = new ServerSocket (PORT);
  }
  public void run() throws Exception {
    while (true) {
    /* wait for a connection from a client */
    Socket s = dServerSocket.accept();
    String addr = s.getRemoteSocketAddress().toString();
    this.logString=addr;
    this.logString+="/";
    /* then process the client's request */
    processRequest(s);
    }
  }
/* Reads the HTTP request from the client, and
responds with the file the user requested or
a HTTP error code. */
  public void processRequest(Socket s) throws Exception {
  /* used to read data from the client */
    BufferedReader br = new BufferedReader (new InputStreamReader (s.getInputStream()));
  /* used to write data to the client */
  OutputStreamWriter osw = new OutputStreamWriter (s.getOutputStream());
  /* read the HTTP request from the client */
  String request = br.readLine();
  String command = null;
  String pathname = null;
  String filepath = null;
  String filename = null;
  /* parse the HTTP request */
  StringTokenizer st = new StringTokenizer (request, " ");
  command = st.nextToken();
  pathname = st.nextToken();
  if (command.equals("GET")) {
    /* if the request is a GET
    try to respond with the file
    the user is requesting */
    serveFile (osw,pathname);
  }
  else {
    if (command.equals("PUT")) {
        filepath = st.nextToken();
        filename = st.nextToken();
        // System.out.println("CALL STORE FILE");
        storeFile (pathname,filepath,filename);
      }else{
        osw.write ("HTTP/1.0 501 Not Implemented\n\n");
      }
  }
  /* close the connection to the client */
  osw.close();
  }
  public void serveFile (OutputStreamWriter osw,String pathname) throws Exception {
    boolean isGetBigFile=false;
    BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt", true));
    FileReader fr=null;
    int c=-1;
    StringBuffer sb = new StringBuffer();
    /* remove the initial slash at the beginning
    of the pathname in the request */
    if (pathname.charAt(0)=='/'){
      pathname=pathname.substring(1);
      if (pathname.equals("getBigFile")){
        isGetBigFile=true;
      }
    }
    /* if there was no filename specified by the
    client, serve the "index.html" file */
    if (pathname.equals(""))pathname="index.html";
    /* try to open file specified by pathname */
    if (!isGetBigFile)
    {
      try {
           fr = new FileReader (pathname);
           c = fr.read();
          }
          catch (Exception e) {
            /* if the file is not found,return the
            appropriate HTTP response code */
            osw.write ("HTTP/1.0 404 Not Found\n\n");
            this.logString+="HTTP/1.0 404 Not Found/";
            bw.newLine();
            bw.write(this.logString);
            bw.flush();
            return;
          }
    }else{
      osw.write ("HTTP/1.0 403 Permission Denied\n\n");
      this.logString+="HTTP/1.0 403 Permission Denied/";
      bw.newLine();
      bw.write(this.logString);
      bw.flush();
      return;
    }
    /* if the requested file can be successfully opened
    and read, then return an OK response code and
    send the contents of the file */
    osw.write ("HTTP/1.0 200 OK\n\n");
    this.logString+="HTTP/1.0 200 OK/";
    while (c != -1) {
      sb.append((char)c);
      c = fr.read();
    }
  osw.write (sb.toString());
  bw.newLine();
  bw.write(this.logString);
  bw.flush();
  // return;
  }
  public void storeFile(String pathname,String filepath,String filename) throws Exception{
    // System.out.println("STORE FILE");
    OutputStream out = null;
    InputStream filecontent = null;
    File file = new File(filepath); 
    try {
        // System.out.println("BEFORE OUT");
        out = new FileOutputStream(new File("C:" + File.separator + "SimpleWebServer" + File.separator + filename));
        // System.out.println("AFTER OUT");
        filecontent = new FileInputStream(file);
        int read = 0;
        final byte[] bytes = new byte[1024];
        while ((read = filecontent.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
    } catch (FileNotFoundException fne) {
        System.out.println("FileNotFoundException");
    } finally {
        if (out != null) {
            out.close();
        }
        if (filecontent != null) {
            filecontent.close();
        }
      }
  }
 /* This method is called when the program is run from
 the command line. */
  public static void main (String argv[]) throws Exception {
/* Create a SimpleWebServer object, and run it */
    SimpleWebServer sws = new SimpleWebServer();
    /* Log file is not created yet, Use the code below. */
    // String path = "C:" + File.separator + "SimpleWebServer" + File.separator + "log.txt";
    // // Use relative path for Unix systems
    // File f = new File(path);
    // f.getParentFile().mkdirs(); 
    // f.createNewFile();
    sws.run();
 }
} 