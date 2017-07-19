package com.mail929.java.kbslider;

public class Buttons
{
	String buttons[];
	int used;
	
	public Buttons()
	{
		buttons = new String[4];
		used = 0;
	}
	
	public boolean isPressed(String button)
	{
		for(String pressed : buttons)
		{
			if(pressed != null)
			{
				if(pressed.equalsIgnoreCase(button))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean press(String button)
	{
		if(used == buttons.length)
		{
			return false;
		}
		
		buttons[used] = button;
		used++;
		return true;
	}
	
	public void depress(String button)
	{
		used--;
		boolean found = false;
		for(int i = 0; i < buttons.length; i++)
		{
			String pressed = buttons[i];
			if(pressed != null)
			{
				if(pressed.equalsIgnoreCase(button))
				{
					found = true;
				}
				
				if(found)
				{
					if((i + 1) == buttons.length)
					{
						buttons[i] = null;
					}
					else
					{
						buttons[i] = buttons[i + 1];
					}
				}
			}
		}
	}
	
	public String getPressed()
	{
		String pressed = "";
		for(int i = 0; i < used; i++)
		{
			if(i != 0)
			{
				pressed += "+";
			}
			pressed += buttons[i];
		}
		return pressed;
	}
}
