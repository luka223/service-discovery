package main;

import connection.ReceiveConnection;
import etcdApi.EtcdApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test
{
    public static void main(String[] args)
    {
        EtcdApi etcdApi;
        String serviceName;
        String hostname;
        int port;

        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter service name: ");
            serviceName = bufferedReader.readLine();

            System.out.print("Enter hostname: ");
            hostname = bufferedReader.readLine();

            System.out.print("Enter port number: ");
            port = Integer.parseInt(bufferedReader.readLine());

            new ReceiveConnection(port).start();
            // if exception is not thrown, service has started successfully
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            return;
        }
        catch (NumberFormatException e)
        {
            System.err.println("Port must be a number");
            return;
        }

        etcdApi = new EtcdApi(serviceName, hostname, port);
        etcdApi.keepAlive();
    }
}
