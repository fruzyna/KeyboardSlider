package com.mail929.java.kbslider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IO
{
	static IO instance;
	
	String pluginDir;
	
	public static IO getInstance()
	{
		if(instance == null)
		{
			instance = new IO();
		}
		return instance;
	}
	
	public void readConfig()
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("slider.cfg")));
			
			String line = "";
			while((line = br.readLine()) != null)
			{
				String[] parts = line.split(":");
				if(parts[0].equals("pluginDir"))
				{
					pluginDir = parts[1];
				}
			}
			
			br.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Plugin[] getPlugins()
	{
		File dir = new File(pluginDir);
		File[] files = dir.listFiles();
		Plugin[] plugins = new Plugin[files.length];
		for(int i = 0; i < files.length; i++)
		{
			plugins[i] = makePlugin(files[i]);
		}
		return plugins;
	}
	
	public Plugin makePlugin(File file)
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String name = "", application = "", inType = "NONE", outType = "NONE", inData = "", outData = "", outFilter = "", buttons = "";
			
			String line = "";
			while((line = br.readLine()) != null)
			{
				String[] parts = line.split(":");
				if(parts[0].equals("name"))
				{
					name = parts[1];
				}
				else if(parts[0].equals("application"))
				{
					application = parts[1];
				}
				else if(parts[0].equals("inType"))
				{
					inType = parts[1];
				}
				else if(parts[0].equals("outType"))
				{
					outType = parts[1];
				}
				else if(parts[0].equals("inData"))
				{
					inData = parts[1];
				}
				else if(parts[0].equals("outData"))
				{
					outData = parts[1];
				}
				else if(parts[0].equals("buttons"))
				{
					buttons = parts[1];
				}
				else if(parts[0].equals("outFilter"))
				{
					outFilter = parts[1];
				}
				else
				{
					outFilter += line;
				}
			}
			return new Plugin(name, application, buttons, inType, inData, outType, outData, outFilter);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
