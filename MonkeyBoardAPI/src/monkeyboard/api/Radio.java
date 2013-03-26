package monkeyboard.api;

import java.util.HashMap;
import java.util.Map;

public class Radio {

	private static Radio instance;
	private RadioMode mode = RadioMode.DAB;
	private int currentDabIndex = 0;
	private int maxDabIndex;
	private KeyStoneAPI api = new KeyStoneAPI();
	private Map<Integer, String> stationMap = new HashMap<Integer, String>();
	
	private Radio() {}
	
	public static Radio instance() {
		if (instance == null) {
			instance = new Radio();
			if (!instance.api.findAndOpen()) {
				// could not connect to board
				instance = null;
			} else {
				instance.buildStationMap();
			}
		}
	
		return instance;
	}
	
	public void buildStationMap() {
		maxDabIndex = (int) api.getTotalPrograms();
		for (int i=0; i<maxDabIndex; i++) {
			stationMap.put(i, api.getProgramName(mode, i, false));
		}
	}
	
	public void nextStream() {
		api.nextStream();
		if (mode == RadioMode.DAB) {
			currentDabIndex = (++currentDabIndex)%maxDabIndex;
		} else {
			// TODO
		}
	}
	
	public void prevStream() {
		api.prevStream();
		if (mode == RadioMode.DAB) {
			currentDabIndex = (--currentDabIndex)%maxDabIndex;
		} else {
			// TODO
		}
	}
	
	public void volumePlus() {
		api.volumePlus();
	}
	
	public void volumeMinus() {
		api.volumeMinus();
	}
}
