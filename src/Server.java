import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //Listens to clients waiting to connect
    //When client tries to connect, it will create a new thread.

    private final ServerSocket serverSocket;
    // Responsible for listening to incoming connections / clients
    // Creates a socket to connect to them.

    //Responsible for keeping the server running.
    public void startServer() {

        try {

            while (!serverSocket.isClosed()) {

                //Blocking method / Will be haulted here until client connects
                //When client does connect, will return a socket object
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");

                //Is responsible for communicating the client and implements the interface runnable.
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        } catch(IOException e) {
            closeServerSocket();
        }

    }

    //If an error occurs we want to shut down our server socket.
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException{

        ServerSocket serverSocket = new ServerSocket(1234);

        //Server Object takes a server socket
        Server server = new Server(serverSocket);

        //Keeps our server constantly running.
        server.startServer();

    }

}
