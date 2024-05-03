//Mitchell Ryzuk
//IT114 Final Project Spring 2019
//ChatFrame.java, class ChatPanel

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import spade.core.AuthSSLSocketFactory; //added due to cleartext vuln

public class ChatFrame extends Frame{
	public ChatFrame(){
		setSize(500, 500);
		setTitle("Chat Program");
		ChatPanel p = new ChatPanel();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				p.disconnect(); 						//calls disconnect method in the Panel 
				System.exit(0);							//to ensure proper disconnection from server
			}
		});
		add(p, BorderLayout.CENTER);
		setVisible(true);
	}
	public static void main(String[] args){
		new ChatFrame();
	}
}

class ChatPanel extends Panel implements ActionListener, ItemListener, Runnable{  
	TextArea ta;
	TextField tf;
	Button connect, disconnect;
	Thread thread;
	Socket s;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	DataObject d1,d2;
	String username;
	String pmUser; //private message username
	java.awt.List list;
	boolean connected; 
	boolean firstConnected; //if the incoming user is first connecting
	boolean isPrivate; //if the message being sent is private
	
	public ChatPanel(){
			setLayout(new BorderLayout());

			tf = new TextField();
			tf.addActionListener(this);

			ta = new TextArea();
			ta.append("Welcome to the Chat Program.\n");
			ta.append("Enter a username above BEFORE connecting!\n\n");

			add(tf, BorderLayout.NORTH);

			Panel p1 = new Panel();
			p1.setLayout(new BorderLayout());
			p1.add(ta, BorderLayout.CENTER);

			list = new java.awt.List(0,true);
			p1.add(list, BorderLayout.WEST);
			add(p1, BorderLayout.CENTER);
			list.addItemListener(this);

			connect = new Button("Connect");
			connect.addActionListener(this);
			disconnect = new Button("Disconnect");
			disconnect.setEnabled(false);
			disconnect.addActionListener(this);

			Panel p2 = new Panel();
			p2.add(connect);
			p2.add(disconnect);
			add(p2, BorderLayout.SOUTH);
		
		
	}
	public void actionPerformed(ActionEvent ae){
		//Connect user
		if(ae.getSource() == connect){
			if(!connected){
				//if no username was entered in the TextField
				if (tf.getText().equals("")) { 
					ta.append("Please enter a username above before connecting.\n");
				}
				
				else {
					username = tf.getText();
					tf.setText("");
					Component c = getParent(); 					//getting reference to Frame class
					Frame f = (Frame)c; 					  	//turn it into a Frame
					f.setTitle(username + " - Chat Program");	//putting username in title

					try{
						firstConnected = true;
						
						//these 3 lines are added due to Snyk vulnerabilities.
						InetSocketAddress sockaddr = new InetSocketAddress("localhost", 3000);
						oos = AuthSSLSocketFactory.getSocket(remoteSocket, sockaddr, "DAWOOD_READ_FROM_CONFIG");
						oos = AuthSSLSocketFactory.getSocket(remoteSocket, sockaddr, "DAWOOD_READ_FROM_CONFIG");

						//s = new Socket("127.0.0.1", 3000);
						oos = s.getOutputStream(); //removed object declaration due to vulnerability
						ois = s.getInputStream(); //removed object declaration due to vulnerability
						
						thread = new Thread(this);
						thread.start();
						
						connected = true;
						
						connect.setEnabled(false);
						disconnect.setEnabled(true);
						
						//broadcasting username info
						d1 = new DataObject();
						d1.setMessage(username);
						d1.setFirstConnected();
						oos.writeObject(d1);

					}catch(UnknownHostException uhe){
						System.out.println(uhe.getMessage());
					}catch(IOException ioe){
						System.out.println(ioe.getMessage());
					}
				}
			}
		}
		
		//if the Disconnect button was pressed
		else if(ae.getSource() == disconnect){
			disconnect(); //calls my disconnect method			
		}
		
		//if the user is sending a message through the TextField
		else if(ae.getSource() == tf){
			if(connected){
				try{
					d1 = new DataObject();
					String temp = tf.getText();
					
					//If this is a private message
					if (isPrivate) {
						d1.setPrivate(); 							//tell DataObject this is a private message
						
						d1.setPrivateUser(pmUser); 					//sends target username to DataObject
						
						d1.setMessage(username + " (PM): " + temp);	//appends sending user with PM designation to message
					}
					else {
						d1.setMessage(username + ": " + temp); 		//appends username with message
					}
					oos.writeObject(d1);						//send message out
					tf.setText("");
				}catch(IOException ioe){
					System.out.println(ioe.getMessage());
				}		
			}
		}
	}
	
	//method that disconnects user from the chat
	//activated by Disconnect button or closing the Frame
	public void disconnect() {
		if (connected) {
			d1 = new DataObject();
			d1.setMessage("ByEEE"); 	//disconnecting trigger message
			d1.disconnect(); 			//sets DataObject test value to true
			
			//send trigger message to server
			try{
				oos.writeObject(d1);
			}
			catch (IOException ioe) {
				System.out.println(ioe);
			}
			
			//enables connect button and disables disconnect button
			disconnect.setEnabled(false);
			connect.setEnabled(true);

			username = "";
			Component c = getParent();			//gets reference to parent Frame object to change title
			Frame f = (Frame) c;
			f.setTitle("Chat Program"); 		//resetting title of frame
			list.removeAll(); 					//remove all values from user list
			ta.append("\nDisconnected\n");
			
			connected = false;
			thread.stop(); 						//stops the running thread
		}
	}

	public void itemStateChanged(ItemEvent ie) {
		if (ie.getStateChange() == ie.SELECTED) {
			if (!list.getSelectedItem().equals(username)) {
				isPrivate = true;
				pmUser = list.getSelectedItem();
			}

		}
		if (ie.getStateChange() == ie.DESELECTED) {
			isPrivate = false;
			pmUser = "";
		}	
	}
	
	//thread that runs while user is connected
	//checks for new messages
	public void run(){
		while(connected){
			try{
				d2 = (DataObject)ois.readObject();					//reads object from server
				String temp = d2.getMessage();						//converts to String
				
				//testing if this is the first broadcast from the given user, user is connecting into server
				if (d2.isFirstConnected()) { 
					list.add(temp); 								//adds username to the side list, the entire message should be the username
					ta.append(temp + " has connected!\n");			//broadcasts new user to chat
				}
				
				//testing if the user is disconnecting
				else if (d2.isDisconnecting()) { 
					list.remove((String)temp); 						//removes username from the chat list
					ta.append(temp + " has disconnected!\n");
				}
				
				//normal messages
				else {
					ta.append(temp + "\n");
				}	

			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}catch(ClassNotFoundException cnfe){
				System.out.println(cnfe.getMessage());
			}
		}
	}
}
