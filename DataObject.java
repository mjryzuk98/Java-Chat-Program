//Mitchell Ryzuk
//IT114 Final Project Spring 2019
//DataObject.java

import java.io.*;
import java.util.*;

public class DataObject implements Serializable{

	private String message;
	private boolean firstConnected;
	private boolean disconnecting;
	private boolean isPrivate;
	private String privateUser;

	DataObject(){
		message = "";
	}
	
	//returns message
	public String getMessage(){
		return message;
	}

	//tells if the user is first connecting to the server
	public boolean isFirstConnected() {
		return firstConnected;
	}
	
	//tells if the user is disconnecting from the server
	public boolean isDisconnecting() {
		return disconnecting;
	}
	
	//sets if user will be disconnecting from server
	public void disconnect() {
		disconnecting = true;
	}
	
	//sets message
	public void setMessage(String inMessage){
		message = inMessage;
	}

	//sets if the user is first connecting to the server
	public void setFirstConnected() {
		firstConnected = true;
	}

	//sets whether or not this outgoing message is private
	public void setPrivate() { 
		isPrivate = true;
	}

	//tells if outgoing message is private
	public boolean isPrivate() {
		return isPrivate;
	}

	//sets the target username of the private message
	public void setPrivateUser(String user) { 
		privateUser = user;
	}

	//gets the username of the intended user for a private message
	public String getPrivateUser() {
		return privateUser;
	}
}
