package network;

public class Protocol {

    public static String msg = "/ms";
    public static String whisper = "/ws";
    public static String requestUsersInGroup = "/ru";
    public static String connectionSuccesful = "/cs";
    public static String clientDisconnection = "/cd";
    public static String sendUsername = "/su";

    public static int DISCONNECTION_IDENTIFIER = -1;
    public static int REQUEST_USERS_IDENTIFIER = -900;
    public static int AUDIO_BYTES_IDENTIFIER = 0;


	/*



	=======================================================================
	AUDIO PACKET
	Audio packet will only have byte[] of audio to write to connected
	client.
	========================================================================
	STRING PACKET
	// {"packetType": PACKET_TYPE, "loginState": LOGIN_STATE};

	========================================================================
	LOGIN_STATES
	-NEED_AUTH
	-LOGGED_IN

	PACKET_TYPE
	-LOGIN
	-MESSAGE
	-DISCONNECT
	=======================================================================
	loginPacket reqs
	--------------------------
	-username
	-password
	-date/time


	messagePacket reqs
	--------------------------
	-Username
	-Channel
	-Message
	-util
	-date/time




	=======================================================================
	-Username
	-login type
	--Ip Address
	-Channel
	-packet type
	-msg
	--extra data
	--date time


	=======================================================================

	 */








}

