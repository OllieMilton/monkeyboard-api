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

	public static void main(String[] args) {
		KeyStoneAPI kapi = new KeyStoneAPI();
		System.out.println("Fuck off");
		byte volume = 15;
		if (kapi.findAndOpen()) {
			System.out.println("Port open");
			int totalPro = (int)kapi.getTotalPrograms();
			int dabProg = 48;
			System.out.println("Found ["+totalPro+"] programmes...");
			for (int i=0; i<totalPro; i++) {
				System.out.println("Found - "+kapi.getProgramName(RadioMode.DAB, i, false));
			}
			kapi.setVolume(volume);
			kapi.setStereoMode(true);
			kapi.playStream(RadioMode.DAB, dabProg);

			LineReader reader = new LineReader();
			reader.start();
			System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, dabProg, false));
			while (true) {
				PlayStatus stat = kapi.getPlayStatus();
				if (stat == PlayStatus.PLAYING) {
					String s = kapi.getProgramText();
					if (s != null) {
						System.out.println("Text - "+s);
						System.out.println("Signal - "+kapi.getSignalStrength());
					}
					String cmd = reader.getLine();
					if (cmd != null) {
						if ("n".equals(cmd)) {
							kapi.nextStream();
							System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, ++dabProg%totalPro, false));
						} else if ("p".equals(cmd)) {
							kapi.prevStream();
							System.out.println("Now playing - "+kapi.getProgramName(RadioMode.DAB, --dabProg%totalPro, false));
						} else if ("+".equals(cmd)) {
							kapi.volumePlus();
						} else if ("-".equals(cmd)) {
							kapi.volumeMinus();
						} else if ("m".equals(cmd)) {
							kapi.volumeMute();
						}
					}
				}
			}
		}
	}
}
