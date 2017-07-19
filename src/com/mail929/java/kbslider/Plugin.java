package com.mail929.java.kbslider;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.KeyStroke;

public class Plugin
{
	String name;
	String application;
	String buttons;
	
	String inType;
	String inData;
	
	String outType;
	String outData;
	String outFilter;
	
	boolean reset;
	
	int currPos;
	
	public Plugin(String name, String application, String buttons, String inType, String inData, String outType, String outData, String outFilter, boolean reset)
	{
		this.name = name;
		this.application = application;
		this.buttons = buttons;
		this.inType = inType;
		this.inData = inData;
		this.outType = outType;
		this.outData = outData;
		this.outFilter = outFilter;
		this.reset= reset;
		
		currPos = 50;
		currPos = getPos();
	}
	
	public void init()
	{
		if(reset)
		{
			currPos = 50;
		}
		else
		{
			currPos = getPos();
		}
	}
	
	//gets position that slider should be at
	public int getPos()
	{
		if(outType.equals("bash"))
		{
			try {
				int length = 0;
				if(outFilter.indexOf("[%]") > 0)
				{
					String[] parts = outFilter.split("[%]");
					length = parts[0].length();
				}
				Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", outData});
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String string = "";
				String line = "";
				for(string = ""; (line = br.readLine()) != null; string += line);
				string = string.substring(length);
				string = string.substring(0, string.indexOf(outFilter.charAt(length+3)));
				return Integer.parseInt(string);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return currPos;
	}
	
	//move the slider to a position
	public void setPos(int pos)
	{
		if(inType.equals("bash"))
		{
			try {
				String command = inData.replace("[%]", "" + pos);
				Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(inType.equals("scroll"))
		{
			try {
				Robot r = new Robot();
				r.mouseWheel(currPos - pos);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		else if(inType.equals("press"))
		{
			String parts[] = inData.split(",");
			int net = pos - currPos;
			if(parts[0].equals("left") && net < 0)
			{
				try
				{
					Robot r = new Robot();
					for(int i = 0; i < -net; i++)
					{
						r.keyPress(getKeyCode(parts[1]));
					}
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
			else if(parts[2].equals("right") && net > 0)
			{
				try
				{
					Robot r = new Robot();
					for(int i = 0; i < net; i++)
					{
						r.keyPress(getKeyCode(parts[3]));
					}
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		}
		currPos = pos;
	}
	
	public int getKeyCode(String input)
	{
		if(input.equals("up"))
		{
			return KeyEvent.VK_UP;
		}
		else if(input.equals("down"))
		{
			return KeyEvent.VK_DOWN;
		}
		else if(input.equals("left"))
		{
			return KeyEvent.VK_LEFT;
		}
		else if(input.equals("right"))
		{
			return KeyEvent.VK_RIGHT;
		}
		else
		{
			KeyStroke ks = KeyStroke.getKeyStroke(input.charAt(0), 0);
			return ks.getKeyCode();
		}
	}
	
	//determines if this could be the current plugin
	public boolean eligible(String currApp, Buttons pressed)
	{
		if(application.equals("system") || currApp.contains(application))
		{
			String[] buttonsArray = buttons.split("\\+");
			if(buttonsArray.length != pressed.used)
			{
				return false;
			}
			for(String button : buttonsArray)
			{
				if(!pressed.isPressed(button))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
