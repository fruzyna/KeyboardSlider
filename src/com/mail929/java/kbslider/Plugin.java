package com.mail929.java.kbslider;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
	
	int currPos;
	
	public Plugin(String name, String application, String buttons, String inType, String inData, String outType, String outData, String outFilter)
	{
		this.name = name;
		this.application = application;
		this.buttons = buttons;
		this.inType = inType;
		this.inData = inData;
		this.outType = outType;
		this.outData = outData;
		this.outFilter = outFilter;
		
		currPos = 50;
	}
	
	//gets position that slider should be at
	public int getPos()
	{
		if(outType.equals("bash"))
		{
			try {
				System.out.println(outFilter);
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
		currPos = pos;
	}
	//determines if this could be the current plugin
	public boolean eligible(String currApp, String pressed)
	{
		if(currApp.equals(application))
		{
			String[] buttonsArray = buttons.split("\\+");
			String[] pressedArray = pressed.split("\\+");
			if(buttonsArray.length != pressedArray.length)
			{
				return false;
			}
			for(String button : buttonsArray)
			{
				boolean found = false;
				for(String press : pressedArray)
				{
					if(press.equals(button))
					{
						found = true;
					}
				}
				if(!found)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
