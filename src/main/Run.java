package main;

public class Run {
	
	public static void main(String[] args) {
		
		WorkspaceGUI wg = new WorkspaceGUI();
		
	}
	
	/*
	public static int getParameter(int instr, String formatString, char ch) {
		
		String bitmask = ""; String parameter = "";
		String instruction = Integer.toBinaryString(instr);
		
		for (int i = instruction.length(); i < 16; i++) 
			instruction = "0" + instruction;
		
		for (int i = 0; i< 16; i++) 	
			bitmask += (formatString.replace(" ", "").charAt(i) == ch) ? "1" : "0";
		
		for (int i = 15; i > -1; i--) {
			if (bitmask.charAt(i) == '1') parameter = instruction.charAt(i) + parameter;	
		}
		
		return Integer.parseInt(parameter, 2);
	}
	
	*/

}
