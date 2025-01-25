import java.io.*;
import java.util.ArrayList;
import java.net.Socket;

public class ClientHandler implements Runnable{
    //Instances will be executed by a seperate thread.

    //Keep track of all the clients.
    //Can loop through the arraylist and broadcast through all the clients.
    //Static to belong to class
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    //establish a connection between the client and server
    private Socket socket;

    //To be able to read messages
    private BufferedReader bufferedReader;

    //To be able to send messages
    private BufferedWriter bufferedWriter;

    //Used to represent each Client
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;

            //Charater Stream           //Byte Stream
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );
            this.bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);

            //Send message that this user has joined.
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    //Everything inside is run on a seperate thread.
    @Override
    public void run() {
        String messageFromClient;

        //While we are connected to a client
        while(socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){

        //For each clientHandlers in our arraylist {Do something}
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    //"I am done sending over data, no need to wait for me"
                    clientHandler.bufferedWriter.newLine();

                    //Will not fill the buffer so we are sending over the information before the buffer is full.
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

    }

    public void removeClientHandler() {

        //If the user has left the chat, we no longer want to broadcast a message to them.
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            //No need to close inputStreamReader or OutputStreamWriter since it is inside both BufferedReader and BufferedWriter
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if( bufferedWriter != null) {
                bufferedWriter.close();
            }

            //Closing a socket will close its input and output stream.
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
