package network;

public class DisconnectPacket {

    private String loginState = "NEED_AUTH",
            packetType = "";

    public DisconnectPacket(String loginState, String packetType) {
        this.loginState = loginState;
        this.packetType = packetType;
    }





    public String buildPacket() {




        return "";
    }

    public String getLoginState() {
        return this.loginState;
    }

    public String getPacketType() {
        return this.packetType;
    }


}
