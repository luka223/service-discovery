package gui;

import connection.ServerConnection;
import etcdApi.EtcdApi;
import io.etcd.jetcd.KeyValue;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindow extends JFrame
{
    private final int width = 200;
    private final int height = 270;

    private EtcdApi etcdApi;
    private boolean connected = false;
    private ServerConnection serverConnection;

    private JComboBox<String> jComboBox;
    private JButton buttonConnect;
    private JTextField messageTextField;
    private JButton buttonSend;

    public MainWindow()
    {
        connectToEtcd();

        setPreferredSize(new Dimension(width, height));
        setLayout(null);

        initializeLoginGui();
        refreshServices();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToEtcd()
    {
        try
        {
            etcdApi = new EtcdApi();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error with connecting to etcd service", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void initializeLoginGui()
    {
        JLabel label1 = new JLabel("Select service");
        label1.setBounds(10, 10, 150, 30);

        jComboBox = new JComboBox<>();
        jComboBox.setBounds(10, 40, 150, 30);

        buttonConnect = new JButton("Connect");
        buttonConnect.setBounds(10, 70, 150, 30);
        buttonConnect.addActionListener(e ->
        {
            if (connected == false)
                connectToService();
            else
                disconnectFromService();

            buttonSend.setEnabled(connected);
            messageTextField.setEnabled(connected);
        });

        JLabel label2 = new JLabel("Message");
        label2.setBounds(10, 120, 150, 30);

        messageTextField = new JTextField();
        messageTextField.setBounds(10, 150, 150, 30);
        messageTextField.setEnabled(false);

        buttonSend = new JButton("Send message");
        buttonSend.setEnabled(false);
        buttonSend.setBounds(10, 180, 150, 30);
        buttonSend.addActionListener(e ->
        {
            sendMessage();
        });

        getContentPane().add(jComboBox);
        getContentPane().add(buttonConnect);
        getContentPane().add(messageTextField);
        getContentPane().add(buttonSend);
        getContentPane().add(label1);
        getContentPane().add(label2);
    }

    private void connectToService()
    {
        String hostname;
        int port;

        String selectedItem = (String)jComboBox.getSelectedItem();

        if (selectedItem == null)
        {
            JOptionPane.showMessageDialog(this, "You must select a service", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try
        {

            String address = etcdApi.getValue(selectedItem);
            hostname = address.split(":")[0];
            port = Integer.parseInt(address.split(":")[1]);

            serverConnection = new ServerConnection(hostname, port);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error with connecting to service", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        connected = true;
        buttonConnect.setText("Disconnect");
    }

    private void disconnectFromService()
    {
        serverConnection.sendMessage("\0");

        connected = false;
        buttonConnect.setText("Connect");
    }

    private void sendMessage()
    {
        String message = messageTextField.getText();
        serverConnection.sendMessage(message);

        messageTextField.setText("");
    }

    private void refreshServices()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                jComboBox.removeAllItems();

                List<String> list = etcdApi.getAvailableServices();
                for (String service : list)
                    jComboBox.addItem(service);
            }

        }, 0, 5000);
    }
}
