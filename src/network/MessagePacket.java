package network;

import com.google.gson.JsonObject;

public class MessagePacket {

    public JsonObject jsonData;

    public String loginState;
    public String packetType;
    public String username;
    public String simpleMessage;
    public String utilityMessage;
    public String channel;


    public MessagePacket(String loginState, String packetType, String username, String channel, String simpleMessage, String utilityMessage) {
        this.loginState = loginState;
        this.packetType = packetType;
        this.username = username;
        this.channel = channel;
        this.simpleMessage = simpleMessage;
        this.utilityMessage = utilityMessage;
    }

    public void buildPacket() {
        jsonData = new JsonObject();
        jsonData.addProperty("loginState", this.loginState);
        jsonData.addProperty("packetType", this.packetType);
        jsonData.addProperty("username", this.username);
        jsonData.addProperty("channel", this.channel);
        jsonData.addProperty("simpleMessage", this.simpleMessage);
        jsonData.addProperty("utilityMessage", this.utilityMessage);
    }

}
