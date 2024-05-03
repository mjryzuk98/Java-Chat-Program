//Mitchell Ryzuk
//IT114 Final Project Spring 2019
//ChatServer.java, class ChatHandler

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer{  
	public static void main(String[] args ){  
		ArrayList<ChatHandler>handlers = new ArrayList<ChatHandler>();
		try{  
			ServerSocket s = new ServerSocket(3000);
			for(;;){
				Socket incoming = s.accept();
				new ChatHandler(incoming, handlers).start();
			}
		}catch (Exception e){  
			System.out.println(e);
		} 
	} 
}	  
	  
class ChatHandler extends Thread{
	DataObject myObject = null;
	private Socket incoming;
	ArrayList<ChatHandler>handlers;
	ObjectInputStream in;
	ObjectOutputStream out;
	String username;
	
	public ChatHandler(Socket i, ArrayList<ChatHandler>h){
		incoming = i;
		handlers = h;
		handlers.add(this);
	}
	public void run(){
		try{
			in = new incoming.getInputStream(); //removed due to Snyk vulnerabiliy
			out = new incoming.getOutputStream(); //removed due to Snyk vuln

			boolean done = false;
			
			while (!done){  
			
				DataObject objIn = (DataObject)in.readObject();
				
				if (objIn == null){
					done = true;
				}
				else{	
				
					//If this is the first time the user is connecting
					if (objIn.isFirstConnected()) {
						//set username in the handler
						username = objIn.getMessage().trim(); 
						
						//writes out username to everyone else in chat
						for (ChatHandler h : handlers) {
							h.out.writeObject(objIn); 
						}
						
						//getting the rest of the usernames for the newly connected user
						for (int i = 0; i < handlers.size() - 1; i++) {
							DataObject temp = new DataObject();
							temp.setFirstConnected();
							temp.setMessage(handlers.get(i).username);
							this.out.writeObject(temp); //only writes to this chat instance
						}
					}
					
					//if the user has already connected
					else {
						//if user leaves by closing window or pressing disconnect
						if (objIn.getMessage().trim().equals("ByEEE")){	
							done = true;
							objIn.setMessage(username); //sets incoming message to the leaving user's name
							
							//broadcasts username with the true boolean value to indicate that the user is leaving
							for (ChatHandler h : handlers) {
								h.out.writeObject(objIn); 
							}
						}
						//for normal messages
						else{ 
							//if it's a private message
							if (objIn.isPrivate()) { 
								for (ChatHandler h : handlers) { //loop through connected users
									//checking if it's the intended user
									if ((h.username).equals(objIn.getPrivateUser())) { 
										h.out.writeObject(objIn);
									}
								}
								this.out.writeObject(objIn); //writes out message to the sender so they see the message
							}
							//broadcast normal message to everyone
							else {
								for(ChatHandler h : handlers){
									h.out.writeObject(objIn);
								}
							}
						}
					}
				}
			}
			incoming.close();
		}catch (Exception e){  
			System.out.println(e);
		}finally{
			handlers.remove(this);
		} 
	} 
}
