package game2D;

import java.io.*;
import javax.sound.sampled.*;

public class Sound extends Thread {

	String filename;	// The name of the file to play
	boolean finished;	// A flag showing that the thread has finished
	boolean loop;       // To see if a sound will loop or not
	Clip clip;			// The clip is what plays the sound

	public Sound(String fname, boolean looped) {
		filename = fname;
		loop = looped;
	}

	/**
	 * run will play the actual sound but you should not call it directly.
	 * You need to call the 'start' method of your sound object (inherited
	 * from Thread, you do not need to declare your own). 'run' will
	 * eventually be called by 'start' when it has been scheduled by
	 * the process scheduler.
	 */
	public void run() {
		while (loop == true) {
			playSound();
		}
		if (finished == true)
		{
			//System.out.println("this is the end");
		}
		else if (loop == false) {
			playSound();
			//System.out.println("jump sound");
		}
	}

	/**
	 * This plays the actual sound and uses a filter
	 */
	public void playSound()
	{
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat	format = stream.getFormat();
			FadeFilterStream filtered = new FadeFilterStream(stream);
			AudioInputStream f = new AudioInputStream(filtered,format,stream.getFrameLength());			
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(f);
			clip.start();
			Thread.sleep(100);
			while (clip.isRunning()) {  }
			clip.close();
		}
		catch (Exception e) {	}

	}

	/**
	 * This stops the sound thread while playing a sound. It is mainly used for
	 * the background music
	 */
	public void stopSound() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
			loop = false;
			finished = true;
		}
	}
}