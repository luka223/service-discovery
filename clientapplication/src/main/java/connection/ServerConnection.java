package connection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ServerConnection
{
    private Socket socket;
    private PrintStream output;

    public ServerConnection(String hostname, int port) throws IOException
    {
        socket = new Socket(hostname, port);

        output = new PrintStream(socket.getOutputStream());
    }

    public void sendMessage(String message)
    {
        try
        {
            output.println(message);
        }
        catch (Exception e)
        {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }

}
