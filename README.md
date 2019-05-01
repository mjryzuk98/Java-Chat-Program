# Java-Chat-Program
A basic Java online chat program

This was my final project for IT 114 - Advanced Programming for Information Technology at NJIT.

# To use: 

Compile ChatServer.java, ChatFrame.java, and DataObject.java using the javac command in cmd or terminal. Make sure you have JRE and JDK installed on your computer. 
ChatFrame will throw a warning when compiling, but it's okay.

The server needs to have at least ChatServer and DataObject compiled, and the client needs to have at least ChatFrame and DataObejct compiled.

Then, run ChatServer and ChatFrame in separate command prompt windows, using the java command.

If you want to communicate on different networks make sure port 3000 is open on your router. 

If you're just using this on a local network it's not necessary to open ports.

If you want to use this program on other computers on the network, make sure you modify line 99 of ChatFrame.java by
replacing the IP address with the server's IP address, then save and compile. The IP address already entered is the local loopback address, 
which is meant to be used if you're running ChatFrame and ChatServer on the same machine.

Use the server's local IP if you're connecting on a local network, or the public IP if you're connecting on separate networks.
