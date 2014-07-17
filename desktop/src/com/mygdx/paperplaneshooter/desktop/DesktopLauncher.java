package com.mygdx.paperplaneshooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.paperplaneshooter.PaperplaneShooter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Paperplane Shooter";
		config.width = 800;
		config.height = 640;
		new LwjglApplication(new PaperplaneShooter(), config);
		
	}
}
