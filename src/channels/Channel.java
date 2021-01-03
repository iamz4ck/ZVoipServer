package channels;

import display.ChatServer;
import utilities.ClientHandler;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Channel {

    protected ChatServer chatServer;
    protected ArrayList<ClientHandler> usersInChannel;

    public Channel(ChatServer chatServer) {
        this.chatServer = chatServer;
        init();
    }

    public void init() {
        usersInChannel = new ArrayList<ClientHandler>();
    }

    public void writeStringToConnectedClients(byte[] data) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int dataLength = data.length;
                int intToWrite = dataLength - (dataLength * 2);
                for (ClientHandler client : usersInChannel) {
                    try {
                        if (!client.getSocket().isClosed()) {
                            client.getDataOutputStream().writeInt(intToWrite);
                            client.getDataOutputStream().write(data, 0, data.length);
                            client.getDataOutputStream().flush();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }
            }
        });
    }

    public void writeAudioBytesToConnectedClients(byte[] data, ClientHandler clientSendingData) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (ClientHandler client : usersInChannel) {
                    if (!equalsClient(client, clientSendingData)) {
                        try {
                            client.getDataOutputStream().writeInt(data.length);
                            client.getDataOutputStream().write(data, 0, data.length);
                            client.getDataOutputStream().flush();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }
            }
        });
    }

    public boolean equalsClient(ClientHandler client, ClientHandler clientSendingData) {
        if (client.getSocket() == clientSendingData.getSocket()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ClientHandler> getClientsInChannel() {
        return usersInChannel;
    }

    public abstract void addUserToChannel(ClientHandler client);

    public abstract void removeUserFromChannel(ClientHandler client);


}
