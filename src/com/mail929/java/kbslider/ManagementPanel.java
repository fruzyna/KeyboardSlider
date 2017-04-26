package com.mail929.java.kbslider;
import java.awt.Dimension;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ManagementPanel extends JFrame
{
	PluginPanel plugin;
	SettingsPanel settings;
	String[] plugins;
	
	public ManagementPanel()
	{
		super("Keyboard Slider Management Panel");

		plugins = new String[Slider.getInstance().plugins.length];
		for(int i = 0; i < plugins.length; i++)
		{
			plugins[i] = Slider.getInstance().plugins[i].name;
		}
		
		plugin = new PluginPanel(plugins);
		settings = new SettingsPanel();
		
		setLayout(new GridLayout(1,0));
		
		add(settings);
		add(plugin);
		
		setSize(500,500);
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
			listScroller.setPreferredSize(new Dimension(250, 80));
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
			if(e.getSource().equals(up))
			{
			}
			else if(e.getSource().equals(down))
			{
				
			}
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
			port = new JLabel("Slider Port: " + Slider.getInstance().arduino.getPortDescription());
			add(port);

			plugin = new JLabel("Current Plugin: " + Slider.getInstance().currentPlugin.name);
			add(plugin);
			
			app = new JLabel("Plugin App: " + Slider.getInstance().currentPlugin.application);
			add(app);
			
			type = new JLabel("Plugin Type: " + Slider.getInstance().currentPlugin.inType + ", " + Slider.getInstance().currentPlugin.outType);
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
