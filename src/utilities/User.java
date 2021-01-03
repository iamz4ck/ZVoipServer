package utilities;

import com.google.gson.JsonObject;

public class User {

    private String loginState;
    private String username;
    private String channel;
    private String dateTime;
    private JsonObject jsonData = new JsonObject();

    public User(String loginState, String username, String channel, String dateTime) {
        this.loginState = loginState;
        this.username = username;
        this.channel = channel;
        this.dateTime = dateTime;
    }

}
