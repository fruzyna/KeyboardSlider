package com.mail929.java.kbslider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;

public class Slider
{
	Plugin[] plugins;
	Plugin currentPlugin;
	
	MyArduino arduino;
	Thread sliderReader;
	Thread appListener;
	Thread systemListener;
	
	private static Slider slider;
	
	static boolean debug = true;
	
	//Switches to the now current plugin
	public void update()
	{
		Buttons pressed = KeyListener.getInstance().pressed;
		String application = "system";

		try {
			Process proc = Runtime.getRuntime().exec(new String[]{"bash", "-c", "xdotool getwindowfocus getwindowname"});
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			application = br.readLine();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Slider.debug("Combo: " + pressed.getPressed());
		
		boolean found = false;
		for(Plugin p : plugins)
		{
			if(p.eligible(application, pressed))
			{
				currentPlugin = p;
				currentPlugin.init();
				found = true;
				break;
			}
		}
		if(!found)
		{
			currentPlugin = new Plugin("NONE", "system", "", "", "", "", "", "", true);
		}
		debug("Current Plugin is now: " + currentPlugin.name + " at " + currentPlugin.getPos());
		
		setSlider(currentPlugin.getPos());
	}
	
	//Sends a new position to the slider
	public void setSlider(int pos)
	{
		System.out.println("Moving to " + pos);
		currentPlugin.currPos = pos;
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
					System.out.println(input);
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
	
	private class SystemListener implements Runnable
	{
		@Override
		public void run()
		{
			boolean running = true;
			int last = -1;
			while(running)
			{
				int current = currentPlugin.getPos();
				if(current != currentPlugin.currPos)
				{
					last = current;
					if(last == current)
					{
						setSlider(current);
					}
				}
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	//Listener for updates from the slider
	private class AppListener implements Runnable
	{
		@Override
		public void run()
		{
			boolean running = true;
			String currApp = "";
			while(running)
			{
				try	{
					Process proc = Runtime.getRuntime().exec(new String[]{"bash", "-c", "xdotool getwindowfocus getwindowname"});
					BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					String app = "";
					if(!(app = br.readLine()).equals(currApp))
					{
						System.out.println("New application: " + app);
						currApp = app;
						update();
					}
					br.close();
				} catch (IOException e)	{
					e.printStackTrace();
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
		
		currentPlugin = new Plugin("NONE", "system", "", "", "", "", "", "", true);

		arduino = new MyArduino("/dev/ttyUSB0", 9600);
		if(arduino.openConnection())
		{
			KeyListener.getInstance();
			
			update();
			
			appListener = new Thread(new AppListener());
			appListener.start();
			
			sliderReader = new Thread(new Listener());
			sliderReader.start();
			
			systemListener = new Thread(new SystemListener());
			systemListener.start();
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
