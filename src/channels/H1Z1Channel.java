package channels;

import display.ChatServer;
import utilities.ClientHandler;

import javax.swing.*;

public class H1Z1Channel extends Channel {

    public H1Z1Channel(ChatServer chatServer) {
        super(chatServer);
    }



    @Override
    public void addUserToChannel(ClientHandler client) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                usersInChannel.add(client);
            }
        });

    }

    @Override
    public void removeUserFromChannel(ClientHandler client) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                usersInChannel.remove(client);
            }
        });
    }

}