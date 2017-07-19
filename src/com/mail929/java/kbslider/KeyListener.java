package com.mail929.java.kbslider;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener
{
	private static KeyListener kl;
	
	Buttons pressed;
	
	public KeyListener()
	{
		pressed = new Buttons();
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
		if(e.getKeyCode() == NativeKeyEvent.VC_CONTROL)
		{
			pressed.press("ctrl");
			Slider.getInstance().update();
		}
		else if(e.getKeyCode() == NativeKeyEvent.VC_ALT)
		{
			pressed.press("alt");
			Slider.getInstance().update();
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		if(e.getKeyCode() == NativeKeyEvent.VC_CONTROL)
		{
			pressed.depress("ctrl");
			Slider.getInstance().update();
		}
		else if(e.getKeyCode() == NativeKeyEvent.VC_ALT)
		{
			pressed.depress("alt");
			Slider.getInstance().update();
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e){}

}
