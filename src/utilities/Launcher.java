package utilities;

import display.ChatServer;

public class Launcher {

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
