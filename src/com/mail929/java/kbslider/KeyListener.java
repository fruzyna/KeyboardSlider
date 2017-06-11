package com.mail929.java.kbslider;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener
{
	private static KeyListener kl;
	
	String pressed = "";
	
	public KeyListener()
	{
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(this);
	}
	
	public static KeyListener getInstance()
	{
		if(kl == null)
		{
			kl = new KeyListener();
		}
		return kl;
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		if(pressed != "")
		{
			pressed += "+";
		}
		if(e.getKeyCode() == NativeKeyEvent.VC_CONTROL)
		{
			pressed += "ctrl";
			Slider.getInstance().update();
		}
		else if(e.getKeyCode() == NativeKeyEvent.VC_ALT)
		{
			pressed += "alt";
			Slider.getInstance().update();
		}
		
		if(pressed.charAt(0) == '+')
		{
			pressed = pressed.substring(1);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		if(e.getKeyCode() == NativeKeyEvent.VC_CONTROL)
		{
			pressed = remove(pressed, "ctrl");
			Slider.getInstance().update();
		}
		else if(e.getKeyCode() == NativeKeyEvent.VC_ALT)
		{
			pressed = remove(pressed, "alt");
			Slider.getInstance().update();
		}
	}
	
	public String remove(String string, String seq)
	{
		if(string.charAt(0) == '+')
		{
			string = string.substring(1);
		}
		string = string.replace(seq + "+", "");
		string = string.replace("+" + seq, "");
		string = string.replace(seq, "");
		return string;
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e){}

}
