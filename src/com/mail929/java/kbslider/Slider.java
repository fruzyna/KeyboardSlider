package com.mail929.java.kbslider;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;

import arduino.Arduino;

public class Slider
{
	Plugin[] plugins;
	Plugin currentPlugin;
	
	MyArduino arduino;
	Thread sliderReader;
	
	private static Slider slider;
	
	static boolean debug = true;
	
	//Switches to the now current plugin
	public void update()
	{
		String buttons = KeyListener.getInstance().pressed;
		String application = "system";

		boolean found = false;
		for(Plugin p : plugins)
		{
			if(p.eligible(application, buttons))
			{
				currentPlugin = p;
				found = true;
				break;
			}
		}
		if(!found)
		{
			currentPlugin = new Plugin("NONE", "system", "", "", "", "", "", "");
		}
		debug("Current Plugin is now: " + currentPlugin.name + " at " + currentPlugin.getPos());
		
		setSlider(currentPlugin.getPos());
	}
	
	//Sends a new position to the slider
	public void setSlider(int pos)
	{
		arduino.serialWrite(pos + "");
	}
	
	//Listener for updates from the slider
	private class Listener implements Runnable
	{
		@Override
		public void run()
		{
			boolean running = true;
			arduino.startReader();
			while(running)
			{
				String input = arduino.serialReadNext();
				if(input.length() > 0)
				{
					if(input.charAt(0) == '@')
					{
						try {
							int position = Integer.parseInt(input.substring(1, input.indexOf('%')));
							debug("Moved to: " + position);
							currentPlugin.setPos(position);
						} catch(NumberFormatException e) {
							System.out.println("Not a number!");
						}
					}
				}
			}
		}
		
	}
	
	public Slider()
	{
		// Clear previous logging configurations.
		LogManager.getLogManager().reset();

		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		IO.getInstance().readConfig();
		plugins = IO.getInstance().getPlugins();
		
		currentPlugin = new Plugin("NONE", "system", "", "", "", "", "", "");

		arduino = new MyArduino("/dev/ttyUSB0", 9600);
		if(arduino.openConnection())
		{
			KeyListener.getInstance();
			
			update();
			
			sliderReader = new Thread(new Listener());
			sliderReader.start();
		}
	}
	
	public static void main(String[] args)
	{
		getInstance();
	}

	public static Slider getInstance()
	{
		if(slider == null)
		{
			slider = new Slider();
			new ManagementPanel();
		}
		return slider;
	}
	
	public static void debug(String output)
	{
		if(debug)
		{
			System.out.println(output);
		}
	}
}
