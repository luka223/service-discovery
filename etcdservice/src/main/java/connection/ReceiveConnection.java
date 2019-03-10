package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveConnection extends Thread
{
    ServerSocket serverSocket;

    public ReceiveConnection(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
            catch (IOException e)
            {
                System.err.println("Error while accepting socket. Message: " + e.getMessage());
            }
        }
    }
}
