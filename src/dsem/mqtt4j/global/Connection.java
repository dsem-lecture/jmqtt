package dsem.mqtt4j.global;

import java.io.*;
import java.net.*;

public class Connection {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	public Connection() {
		this.socket = null;
		this.reader = null;
		this.writer = null;
	}
	
	public Connection(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.global.Connection.Connection()");
			System.out.println(e.getMessage());
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}
	
	public void setConnection(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.global.Connection.setConnection(Socket socket)");
			System.out.println(e.getMessage());
		}
	}

	public boolean connect(String ip, int port) {
		try {
			Socket socket = new Socket(ip, port);
	        System.out.println("MQTTBroker is connected.");
	        
	        this.setConnection(socket);
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.global.Connection.connect()");
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean disconnect() {
		try {
			this.reader.close();
			this.writer.close();
			this.socket.close();
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.global.Connection.disconnect()");
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}

	public boolean sendMessage(String message) {
		try {
			this.writer.println(message);
			this.writer.flush();
		}catch (Exception e) {
			System.out.println("dsem.mqtt4j.global.Connection.sendMessage()");
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public String receiveMessage() {
		StringBuffer sb = new StringBuffer();
		String line;
		String message = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
	        }
			
			message = sb.toString();
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.global.Connection.receiveMessage()");
			System.out.println(e.getMessage());
		}
		
		return message;
	}
}
