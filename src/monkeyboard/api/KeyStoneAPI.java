package monkeyboard.api;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class KeyStoneAPI {

	/*
	 * Stereo = 1
	 * Mono = 0
	 * Volume 0 - 16
	 * Stream mod DAB = 0 FM = 1
	 * Name mode abrev = 0 full = 1
	 * 
	 * 0 = <Prg Type N/A  >
		1 = News
		2 = Current Affairs
		3 = Information
		4 = Sport
		5 = Education
		6 = Drama
		7 = Arts
		8 = Science
		9 = Talk
		10 = Pop Music	11 = Rock Music
		12 = Easy Listening
		13 = Light Classical
		14 = Classical Music
		15 = Other Music
		16 = Weather
		17 = Finance
		18 = Children's
		19 = Factual
		20 = Religion 	
		21 = Phone In
		22 = Travel
		23 = Leisure
		24 = Jazz and Blues
		25 = Country Music
		26 = National Music
		27 = Oldies Music
		28 = Folk Music
		29 = Documentary
		30 = <Undefined>
		31 = <Undefined> 
	 * 
	 */
	private interface LibKeyStoneAPI extends Library {

		final byte STEREO_MODE_STEREO = 1;
		final byte STEREO_MODE_MONO = 0;
		final byte STREAM_MODE_DAB = 0;
		final byte STREAM_MODE_FM = 1;
		final byte NAME_MODE_ABRV = 0;
		final byte NAME_MODE_FULL = 1;
		final byte PLAYING = 0;
		final byte SEARCHING = 1;
		final byte TUNING = 2;
		final byte STOP = 3;
		final byte SORTING = 4;
		final byte RECONFIGURING = 5;
		
		long CommVersion();
		boolean OpenRadioPort(String port, boolean usehardmute);
		boolean HardResetRadio();
		boolean IsSysReady();
		boolean CloseRadioPort();
		boolean SetVolume(byte volume);
		boolean PlayStream(byte mode, long channel);
		boolean StopStream();

		byte VolumePlus();
		byte VolumeMinus();
		void VolumeMute();
		byte GetVolume();
		byte GetPlayMode();
		byte GetPlayStatus();
		long GetTotalProgram();
		boolean NextStream();
		boolean PrevStream();
		long GetPlayIndex();
		byte GetSignalStrength(IntByReference biterror);
		byte GetProgramType(byte mode, long dabIndex);
		byte GetProgramText(Pointer programText);
		boolean GetProgramName(byte mode, long dabIndex, byte namemode, Pointer programName);
		long GetPreset(byte mode, byte presetindex);
		boolean SetPreset(byte mode, byte presetindex, long channel);
		boolean DABAutoSearch(byte startindex, byte endindex);
		boolean GetEnsembleName(long dabIndex, byte namemode, Pointer programName);
		int GetDataRate();
		boolean SetStereoMode(byte mode);
		byte GetFrequency();
		byte GetStereoMode();
		byte GetStereo();
		boolean ClearDatabase();

		boolean SetBBEEQ(byte BBEOn, byte EQMode, byte BBELo, byte BBEHi, byte BBECFreq, byte BBEMachFreq, byte BBEMachGain, byte BBEMachQ, byte BBESurr, byte BBEMp, byte BBEHpF, byte BBEHiMode );
		boolean GetBBEEQ(String BBEOn, String EQMode, String BBELo, String BBEHi, String BBECFreq, String BBEMachFreq, String BBEMachGain, String BBEMachQ, String BBESurr, String BBEMp, String BBEHpF, String BBEHiMode);
		boolean SetHeadroom(byte headroom);
		byte GetHeadroom();
		byte GetApplicationType(long dabIndex);
		boolean MotQuery();
		//void GetImage(WStringImageFileName);
		//void MotReset(MotMode enMode);
		byte GetDABSignalQuality();

		void HardMute();
		void HardUnMute();
		/*boolean ReadSerialBytes (HANDLE serialhandle,  String buffer, DWORD noofbytestoread, DWORD *bytesreadreturn, DWORD exitFD);
		boolean WriteSerialBytes(HANDLE serialhandle, LPCVOID lpBuffer, DWORD nNumberOfBytesToWrite, LPDWORD lpNumberOfBytesWritten, LPOVERLAPPED lpOverlapped);
		boolean GoodHeader( char* input, DWORD dwBytes);*/
	}

	private final LibKeyStoneAPI api;
	private final Pointer RDSString = new Memory(1024);

	public KeyStoneAPI() {
		api = (LibKeyStoneAPI) Native.loadLibrary("keystonecomm", LibKeyStoneAPI.class);
	}
	
	public boolean findAndOpen() {
		String serial = "/dev/ttyACM";
		int i = 0;
		boolean open = false;
		do {
			open = openRadioPort(serial+(i++));
		} while (!open && i < 16);
		return open;
	}

	/**
	 * Connects to the keystone port on the given serial port
	 * @param portName - the name of the serial port.
	 * @return True if succesfull.
	 */
	public boolean openRadioPort(String portName) {
		return api.OpenRadioPort(portName, true);
	}

	public boolean closeRadioPort() {
		return api.CloseRadioPort();
	}
	
	public boolean playStream(RadioMode mode, long channel) {
		byte bmode = LibKeyStoneAPI.STREAM_MODE_DAB;
		if (mode == RadioMode.FM) {
			bmode = LibKeyStoneAPI.STREAM_MODE_FM;
		}
		return api.PlayStream(bmode, channel);
	}
	
	public boolean stopStream() {
		return api.StopStream();
	}
	
	public boolean setVolume(byte volume) {
		return api.SetVolume(volume);
	}
	
	public byte volumePlus() {
		return api.VolumePlus();
	}
	
	public byte volumeMinus() {
		return api.VolumeMinus();
	}
	
	public void volumeMute() {
		api.VolumeMute();
	}
	
	public byte getVolume() {
		return api.GetVolume();
	}
	
	public RadioMode getPlayMode() {
		byte bmode = api.GetPlayMode();
		if (bmode == LibKeyStoneAPI.STREAM_MODE_DAB) {
			return RadioMode.DAB;
		} else if (bmode == LibKeyStoneAPI.STREAM_MODE_FM) {
			return RadioMode.FM;
		} else {
			throw new RuntimeException();
		}
	}
	
	public PlayStatus getPlayStatus() {
		byte bstate = api.GetPlayStatus();
		if (bstate == LibKeyStoneAPI.PLAYING) {
			return PlayStatus.PLAYING;
		} else if (bstate == LibKeyStoneAPI.SEARCHING) {
			return PlayStatus.SEARCHING;
		} else if (bstate == LibKeyStoneAPI.TUNING) {
			return PlayStatus.TUNING;
		} else if (bstate == LibKeyStoneAPI.STOP) {
			return PlayStatus.STOP;
		} else if (bstate == LibKeyStoneAPI.SORTING) {
			return PlayStatus.SORTING;
		} else if (bstate == LibKeyStoneAPI.RECONFIGURING) {
			return PlayStatus.RECONFIGURING;
		} else {
			System.out.println("Doggey status - "+bstate);
		}
		return null;
	}
	
	public long getTotalPrograms() {
		return api.GetTotalProgram();
	}
	
	public boolean nextStream() {
		return api.NextStream();
	}
	
	public boolean prevStream() {
		return api.PrevStream();
	}
	
	public long getPlayIndex() {
		return api.GetPlayIndex();
	}
	
	public byte getSignalStrength() {
		return api.GetSignalStrength(new IntByReference());
	}
	public byte getProgramType(RadioMode mode, long dabIndex) {
		byte bmode = LibKeyStoneAPI.STREAM_MODE_DAB;
		if (mode == RadioMode.FM) {
			bmode = LibKeyStoneAPI.STREAM_MODE_FM;
		}
		return api.GetProgramType(bmode, dabIndex);
	}
	
	public String getProgramText() {
		if (api.GetProgramText(RDSString) == 0) {
			return RDSString.getString(0, true);
		}
		return null;
	}
	
	public String getProgramName(RadioMode mode, long dabIndex, boolean abrev) {
		byte bmode = LibKeyStoneAPI.STREAM_MODE_DAB;
		if (mode == RadioMode.FM) {
			bmode = LibKeyStoneAPI.STREAM_MODE_FM;
		}
		byte namemode = abrev ? LibKeyStoneAPI.NAME_MODE_ABRV : LibKeyStoneAPI.NAME_MODE_FULL;
		Pointer p = new Memory(1024);
		if (api.GetProgramName(bmode, dabIndex, namemode, p)) {
			return p.getString(0, true);
		}
		return null;
	}
	
	public boolean setStereoMode(boolean stereo) {
		byte mode = LibKeyStoneAPI.STEREO_MODE_MONO;
		if (stereo) {
			mode = LibKeyStoneAPI.STEREO_MODE_STEREO;
		}
		return api.SetStereoMode(mode);
	}
	/*
	long GetPreset(byte mode, byte presetindex);
	boolean SetPreset(byte mode, byte presetindex, long channel);
	boolean DABAutoSearch(byte startindex, byte endindex);
	boolean GetEnsembleName(long dabIndex, byte namemode, Pointer programName);
	int GetDataRate();
	byte GetFrequency();
	byte GetStereoMode();
	byte GetStereo();
	boolean ClearDatabase();
	*/

}
