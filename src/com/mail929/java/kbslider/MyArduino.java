package com.mail929.java.kbslider;

import java.io.PrintWriter;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

public class MyArduino {
	private SerialPort comPort;
	private String portDescription;
	private int baud_rate;
	
	public MyArduino() {
		//empty constructor if port undecided
	}
	public MyArduino(String portDescription) {
		//make sure to set baud rate after
		this.portDescription = portDescription;
		comPort = SerialPort.getCommPort(this.portDescription);
	}
	
	public MyArduino(String portDescription, int baud_rate) {
		//preferred constructor
		this.portDescription = portDescription;
		comPort = SerialPort.getCommPort(this.portDescription);
		this.baud_rate = baud_rate;
		comPort.setBaudRate(this.baud_rate);
	}
	
	
	
	public boolean openConnection(){
		if(comPort.openPort()){
			try {Thread.sleep(100);} catch(Exception e){}
			return true;
		}
		else {
			//AlertBox alert = new AlertBox(new Dimension(400,100),"Error Connecting", "Try Another port");
			//alert.display();
			return false;
		}
	}
	
	public void closeConnection() {
		comPort.closePort();
	}
	
	public void setPortDescription(String portDescription){
		this.portDescription = portDescription;
		comPort = SerialPort.getCommPort(this.portDescription);
	}
	public void setBaudRate(int baud_rate){
		this.baud_rate = baud_rate;
		comPort.setBaudRate(this.baud_rate);
	}
	
	public String getPortDescription(){
		return portDescription;
	}
	
	public SerialPort getSerialPort(){
		return comPort;
	}
	

	public String serialRead(){
		//will be an infinite loop if incoming data is not bound
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		String out="";
		Scanner in = new Scanner(comPort.getInputStream());
		try
		{
		   while(in.hasNext())
		   {
			   out += (in.next()+"\n");
		   }
		   	in.close();
		} catch (Exception e) { e.printStackTrace(); }
		return out;
	}
	
	String line = "";
	String lastRead = "";
	
	public void startReader()
	{
		(new Thread() {
			  public void run() {
				  boolean running = true;
				  while(running)
				  {
						//in case of unlimited incoming data, set a limit for number of readings
						comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
						Scanner in = new Scanner(comPort.getInputStream());
						try
						{
							while((line = in.nextLine()) != null);
						   	in.close();
						} catch (Exception e) { e.printStackTrace(); }
				  }
			  }
		}).start();
	}
	
	public String serialReadNext() {
		while(line.equals(lastRead))
		{
			try{
				Thread.sleep(10);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		lastRead = line;
		return line;
	}
	
	public String serialRead(int limit){
		//in case of unlimited incoming data, set a limit for number of readings
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		String out="";
		int count=0;
		Scanner in = new Scanner(comPort.getInputStream());
		try
		{
		   while(in.hasNext()&&count<=limit){
		      out += (in.next()+"\n");
		      count++;
		   }
		   	in.close();
		} catch (Exception e) { e.printStackTrace(); }
		return out;
	}
	
	public void serialWrite(String s){
		//writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try{Thread.sleep(5);} catch(Exception e){}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.print(s);
		pout.flush();
		
	}
	public void serialWrite(String s,int noOfChars, int delay){
		//writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try{Thread.sleep(5);} catch(Exception e){}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		for(int i=0;i<s.length();i+=noOfChars){
			pout.write(s.substring(i,i+noOfChars));
			pout.flush();
			System.out.println(s.substring(i,i+noOfChars));
			try{Thread.sleep(delay);}catch(Exception e){}
		}
		pout.write(noOfChars);
		pout.flush();
		
	}
	
	public void serialWrite(char c){
		//writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try{Thread.sleep(5);} catch(Exception e){}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());pout.write(c);
		pout.flush();
	}
	
	public void serialWrite(char c, int delay){
		//writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try{Thread.sleep(5);} catch(Exception e){}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());pout.write(c);
		pout.flush();
		try{Thread.sleep(delay);}catch(Exception e){}
	}
}
