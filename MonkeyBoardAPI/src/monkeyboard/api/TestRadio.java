package monkeyboard.api;

import java.util.Scanner;

public class TestRadio {

	private static class LineReader extends Thread {
		String line;
		boolean terminated = false;
		public void run() {
			Scanner scanner = new Scanner(System.in);
			while (!terminated) 
				setLine(scanner.next());
			scanner.close();
		}

		synchronized String getLine() {
			String copy = line;
			line = null;
			return copy;
		}

		synchronized void setLine(String line) {
			this.line = line;
		}
	}
	
	private static void waitForInput() throws InterruptedException {
		String cmd = reader.getLine();
		if (cmd != null) {
			int prg = -1;
			try {
				prg = Integer.valueOf(cmd);
			} catch (Exception e){}
			if (prg > -1) {
				kapi.playStream(RadioMode.DAB, prg);
				dabProg = prg;
				System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, dabProg, false));
				Thread.sleep(2000);
			} else if ("n".equals(cmd)) {
				kapi.nextStream();
				System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, ++dabProg%totalPro, false));
				Thread.sleep(2000);
			} else if ("p".equals(cmd)) {
				kapi.prevStream();
				System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, ++dabProg%totalPro, false));
				Thread.sleep(2000);
			} else if ("+".equals(cmd)) {
				kapi.volumePlus();
			} else if ("-".equals(cmd)) {
				kapi.volumeMinus();
			} else if ("m".equals(cmd)) {
				kapi.volumeMute();
			}
		}
	}
	
	static LineReader reader = new LineReader();
	static KeyStoneAPI kapi = new KeyStoneAPI();
	static long totalPro; 
	static int dabProg;
	public static void main(String[] args) throws InterruptedException {
		reader.start();
		System.out.println("Fuck off");
		byte volume = 15;
		if (kapi.findAndOpen(true)) {
			System.out.println("Port open - retuning...");
			//kapi.dabAutoSearch((byte)0, (byte)40);
			totalPro = (int)kapi.getTotalPrograms();
//			while (totalPro < 100) {
//				Thread.sleep(1000);
//				totalPro = (int)kapi.getTotalPrograms();
//				System.out.println("Playstate "+kapi.getPlayStatus());
//				int j = 0;
//				for (int i=0; i<totalPro; i++) {
//					System.out.println("Found - ["+(j++)+"]"+kapi.getProgramName(RadioMode.DAB, i, false));
//				}
//			}
			dabProg = 0;
			System.out.println("Found ["+totalPro+"] programmes...");
			int j = 0;
			for (int i=0; i<totalPro; i++) {
				System.out.println("Found - ["+(j++)+"]"+kapi.getProgramName(RadioMode.DAB, i, false));
			}
			kapi.setVolume(volume);
			kapi.setStereoMode(true);
			if (kapi.playStream(RadioMode.DAB, dabProg)) {
				
				System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, dabProg, false));
				while (true) {
					PlayStatus stat = kapi.getPlayStatus();
					if (stat == PlayStatus.PLAYING) {
						String s = kapi.getProgramText();
						if (s != null) {
							System.out.println("Text - "+s);
							System.out.println("Signal - "+kapi.getSignalStrength());
						}
						waitForInput();
					} else if (stat == PlayStatus.STOP) {
						System.out.println("Playstate "+stat);
						waitForInput();
					} else {
						System.out.println("Playstate "+stat);
					}
				}
			}
		}
	}
}
