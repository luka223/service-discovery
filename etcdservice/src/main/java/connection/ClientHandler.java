package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler extends Thread
{
    private BufferedReader bufferedReader;

    public ClientHandler(Socket socket) throws IOException
    {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                String data = bufferedReader.readLine();

                if (data.equals("\0"))
                {
                    System.out.println("Ending connection...");
                    break;
                }

                System.out.println("Received data: " + data);
            }
            catch (IOException e)
            {
                System.err.println("Error while receiving data. Message: " + e.getMessage());
            }

        }


    }
}
