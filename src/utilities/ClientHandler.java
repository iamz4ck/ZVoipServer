package utilities;

import channels.Channel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import display.ChatServer;
import network.LoginPacket;
import network.MessagePacket;
import network.Protocol;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private ChatServer chatServer;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean isRunning = false, isUsernameSent = false;
    private String username;
    private Channel currentChannel;
    private Gson gson = new Gson();

    public ClientHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
        init();
    }

    public void run() {
        isRunning = true;

        while(isRunning) {
            int length = 0;
            try{
                length = dataInputStream.readInt();
            } catch (IOException e) {
                disconnect();
                e.printStackTrace();
            }

            if(length < 0) {
                System.out.println("length: " + length);
                length = Math.abs(length);
                byte[] incomingBytes = new byte[length];
                try {
                    dataInputStream.readFully(incomingBytes, 0, incomingBytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Creates String from socket
                String incomingJSONString = new String(incomingBytes);


                //Checks if this is first contact with user

                System.out.println("first print out" + incomingJSONString);
                if(isUsernameSent){
                    MessagePacket messagePacket = gson.fromJson(incomingJSONString, MessagePacket.class);

                    if(messagePacket.packetType.equalsIgnoreCase("UTILITY_MESSAGE")) {
                        if(messagePacket.utilityMessage.equalsIgnoreCase("CLIENT_USER_REQUEST")) {
                            MessagePacket requestedUsersPacket = new MessagePacket("LOGGED_IN", "UTILITY_MESSAGE", username, "GENERAL",
                                    "REQUESTED_USERS", getUsersInChannelList());
                            sendMessagePacketToClient(requestedUsersPacket);
                        }
                    }

                    if(messagePacket.packetType.equalsIgnoreCase("SIMPLE_MESSAGE")) {
                        sendMessagePacketToClient(messagePacket);
                    }


                    //sendMessagePacketToClient(messagePacket);
                    System.out.println("users in list:  " + getUsersInChannelList());

                }

                if(!isUsernameSent) {
                    LoginPacket loginPacket = gson.fromJson(incomingJSONString, LoginPacket.class);
                    this.username = loginPacket.username;
                    chatServer.addClientToConnectedUserList(this);
                    isUsernameSent = true;
                }

            }

			/*
			int length = 0;
			try {
				length = dataInputStream.readInt();
				//System.out.println(length + " ");
			} catch (IOException e) {
				disconnect();
				e.printStackTrace();
			}
			if(length == -1) {
				byte[] incomingBytes = new byte[Protocol.clientDisconnection.getBytes().length];
				try {
					dataInputStream.readFully(incomingBytes, 0, incomingBytes.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				disconnect();
			}
			if(length == -900) {
				//complete users connected request
				byte[] incomingBytes = new byte[Protocol.requestUsersInGroup.length()];
				try {
					dataInputStream.readFully(incomingBytes, 0, incomingBytes.length);
				} catch (IOException e) {
					disconnect();
					e.printStackTrace();
				}
				for(ClientHandler client: chatServer.getClientsConnected()) {
					int clientUsernameLength = client.getUsername().length();
					String configuredStringToSend = Protocol.requestUsersInGroup + clientUsernameLength + client.getUsername();
					byte[] configuredStringByteArray = configuredStringToSend.getBytes();
					writeStringToConnectedClients(configuredStringByteArray);
				}
			}
			if(length < -1 && !(length == -900)) {
				//handle strings
				int incomingByteLength = Math.abs(length);
				byte[] incomingStringData = new byte[incomingByteLength];
				try {
					dataInputStream.readFully(incomingStringData, 0, incomingByteLength);
				} catch (IOException e) {
					disconnect();
					e.printStackTrace();
				}
				String incomingString1 = new String(incomingStringData);
				System.out.println(incomingString1);
				if(!isUsernameSent) {
					String incomingString = new String(incomingStringData);
					this.username = incomingString.substring(3, incomingString.length());
					chatServer.addClientToConnectedUserList(this);
					String configuredStringToSend = Protocol.connectionSuccesful + username.length() + username;
					writeStringToConnectedClients(configuredStringToSend.getBytes());
					if(this.username.length() > 0) {
						isUsernameSent = true;
					}
				} else {
					writeStringToConnectedClients(incomingStringData);
				}


			}
			if(length > 0) {
				byte[] incomingBytes = new byte[length];
				try {
					dataInputStream.readFully(incomingBytes, 0, incomingBytes.length);
				} catch (IOException e) {
					disconnect();
					e.printStackTrace();
				}
				//System.out.println("sending audio bytes");
				writeAudioBytesToConnectedClients(incomingBytes);
			}
			if(isUsernameSent) {
				writeStringToConnectedClients(Protocol.requestUsersInGroup.getBytes());
			} */
        }
    }

    public String getUsersInChannelList() {
        ArrayList<ClientHandler> clientsInChannel = chatServer.getClientsConnected();
        JsonObject jsonObject = new JsonObject();
        for (ClientHandler client : clientsInChannel) {
            jsonObject.addProperty("username:", client.username);
        }
        return jsonObject.toString();
    }

    public void init() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void writeStringToConnectedClients(byte[] data) {
        chatServer.appendToServerActions(data.toString());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int dataLength = data.length;
                int intToWrite = dataLength - (dataLength * 2);
                for (ClientHandler client : chatServer.getClientsConnected()) {
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

    public void sendMessagePacketToClient(MessagePacket messagePacket) {
        messagePacket.buildPacket();
        byte[] convertedJSON = messagePacket.jsonData.toString().getBytes();
        int intToWrite = convertedJSON.length - (convertedJSON.length * 2);
        try {
            dataOutputStream.writeInt(intToWrite);
            dataOutputStream.write(convertedJSON, 0, convertedJSON.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDisconnectionToConnectedClients() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String disconnectionString = Protocol.clientDisconnection + username.length() + username;
                byte[] data = disconnectionString.getBytes();
                for(ClientHandler client: chatServer.getClientsConnected()) {
                    if(!client.getUsername().contains(username)) {
                        int dataLength = data.length;
                        int intToWrite = dataLength - (dataLength * 2);
                        try {
                            if(!client.getSocket().isClosed()) {
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
            }
        });

    }

    public void writeAudioBytesToConnectedClients(byte[] data) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (ClientHandler client : chatServer.getClientsConnected()) {
                    if (!equalsClient(client)) {
                        try {
                            client.getDataOutputStream().writeInt(data.length);
                            client.getDataOutputStream().write(data, 0, data.length);
                            //client.getDataOutputStream().flush();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }
            }
        });

    }

    public boolean equalsClient(ClientHandler client) {
        if (client.getSocket() == getSocket()) {
            return true;
        } else {
            return false;
        }
    }

    public void disconnect() {
        chatServer.appendToServerActions("User: " + username + " disconnected.");
        chatServer.removeConnectedClientFromArray(this);
        writeDisconnectionToConnectedClients();
        isRunning = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

}
