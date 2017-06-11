package com.mail929.java.kbslider;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ManagementPanel extends JFrame
{
	PluginPanel plugin;
	SettingsPanel settings;
	String[] names;
	
	public ManagementPanel()
	{
		super("Keyboard Slider Management Panel");

		names = new String[Slider.getInstance().plugins.length];
		for(int i = 0; i < names.length; i++)
		{
			names[i] = Slider.getInstance().plugins[i].name;
		}
		
		plugin = new PluginPanel(names);
		settings = new SettingsPanel();
		
		setLayout(new GridLayout(1,0));
		
		add(settings);
		add(plugin);
		
		setSize(400,250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public class PluginPanel extends JPanel implements ActionListener
	{
		JList list;
		JButton up;
		JButton down;
		
		public PluginPanel(String[] plugins)
		{
			list = new JList(plugins);
			list.setLayoutOrientation(JList.VERTICAL);
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			JScrollPane listScroller = new JScrollPane(list);
			add(listScroller);
			
			up = new JButton("Shift Up");
			up.addActionListener(this);
			add(up);
			
			down = new JButton("Shift Down");
			down.addActionListener(this);
			add(down);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int selected = list.getSelectedIndex();
			Plugin plugins[] = Slider.getInstance().plugins;
			if(e.getSource().equals(up))
			{
				Plugin sp = plugins[selected];
				plugins[selected] = plugins[selected - 1];
				plugins[selected - 1] = sp;
			}
			else if(e.getSource().equals(down))
			{
				Plugin sp = plugins[selected];
				plugins[selected] = plugins[selected + 1];
				plugins[selected + 1] = sp;
			}
			Slider.getInstance().plugins = plugins;
			for(int i = 0; i < names.length; i++)
			{
				names[i] = plugins[i].name;
			}
			list.setListData(names);
		}
	}
	
	public class SettingsPanel extends JPanel
	{
		JLabel port;
		JLabel plugin;
		JLabel state;
		JLabel type;
		JLabel app;
		
		public SettingsPanel()
		{
			setLayout(new GridLayout(5,0));
			port = new JLabel("Slider Port: " + Slider.getInstance().arduino.getPortDescription());
			add(port);

			plugin = new JLabel("Current Plugin: " + Slider.getInstance().currentPlugin.name);
			add(plugin);
			
			app = new JLabel("Plugin App: " + Slider.getInstance().currentPlugin.application);
			add(app);
			
			type = new JLabel("Plugin Types: " + Slider.getInstance().currentPlugin.inType + ", " + Slider.getInstance().currentPlugin.outType);
			add(type);
			
			state = new JLabel("Plugin Position: " + Slider.getInstance().currentPlugin.currPos);
			add(state);
			
			(new Thread() {
				@Override
				public void run()
				{
					boolean running = true;
					while(running)
					{
						plugin.setText("Current Plugin: " + Slider.getInstance().currentPlugin.name);
						state.setText("Plugin Position: " + Slider.getInstance().currentPlugin.currPos);
						app.setText("Plugin App: " + Slider.getInstance().currentPlugin.application);
						type.setText("Plugin Type: " + Slider.getInstance().currentPlugin.inType + ", " + Slider.getInstance().currentPlugin.outType);
						try	{
							Thread.sleep(50);
						} catch (InterruptedException e){
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
}
