package monkeyboard.api;

public class UnknownRadioStationException extends RuntimeException {

	private static final long serialVersionUID = -7205731928498085813L;
	
	public UnknownRadioStationException(String station) {
		super("Radio station ["+station+"] could not be found.");
	}
}
