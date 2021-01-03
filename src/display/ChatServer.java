package display;

import channels.Channel;
import channels.LobbyChannel;
import utilities.ClientHandler;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer extends Thread {

    private ArrayList<ClientHandler> connectedClients;
    private JTextArea serverActionsTextArea;
    private JScrollPane serverActionScrollPane;
    private JFrame frame;
    private ClientHandler clientHandler;
    private ServerSocket serverSocket;
    private int port = 5550;
    private ExecutorService executorService;
    private ArrayList<Channel> channels;
    private LobbyChannel lobbyChannel;

    public ChatServer() {
        init();
        initDisplay();
    }

    public void run() {
        while(true) {
            try {
                clientHandler = new ClientHandler(this, serverSocket.accept());
                executorService.execute(clientHandler);
                frame.toFront();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



    public void init() {
        connectedClients = new ArrayList<ClientHandler>();
        executorService = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //channels = new ArrayList<Channel>();
        //lobbyChannel = new LobbyChannel(this);
        //channels.add(lobbyChannel);
    }



    public void initDisplay() {
        serverActionsTextArea = new JTextArea(8, 30);
        serverActionsTextArea.setEditable(false);
        serverActionScrollPane = new JScrollPane(serverActionsTextArea);
        frame = new JFrame("Server");
        frame.add(serverActionScrollPane);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
        frame.validate();
    }

    public void appendToServerActions(String stringToAppend) {
        serverActionsTextArea.append(stringToAppend + getNewLine());
        serverActionsTextArea.setCaretPosition(serverActionsTextArea.getDocument().getLength());
    }

    public ArrayList<ClientHandler> getClientsConnected() {
        return connectedClients;
    }

    public void addClientToConnectedUserList(ClientHandler client) {
        appendToServerActions(client.getUsername() + " has connected.");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                connectedClients.add(client);
            }
        });

    }

    public void removeConnectedClientFromArray(ClientHandler client) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                connectedClients.remove(client);
            }
        });
    }

    public String getNewLine() {
        return System.getProperty("line.separator");
    }

	/*public void addClientToLobbyChannel(ClientHandler client) {
		lobbyChannel.addUserToChannel(client);
	}

	public void removeClientFromLobbyChannel(ClientHandler client) {
		lobbyChannel.removeUserFromChannel(client);
	} */
}

