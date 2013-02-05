package arduino.cpu;

public final class Instruction {

	int id;
	String name;
	int code;
	int bitmask;
	String formatString;
	int words;
	//char param1, param2;

	public Instruction( int id, String name, int code, int bitmask,
			String formatString, int words)//, char param1, char param2)
	{
		this.name = name;
		this.id = id;
		this.code = code;
		this.bitmask = bitmask;
		this.formatString = formatString;
		this.words = words;
		//this.param1 = param1;
		//this.param2 = param2;
	}
}

