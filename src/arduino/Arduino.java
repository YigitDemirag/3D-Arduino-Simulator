package arduino;

import arduino.cpu.ATMega328;


public class Arduino {
	
	int[] pins = new int[32]; // kac pin var hatirlamiyorum
	
	//byte[] Eeprom = new byte[1024];
	
	
	ATMega328 cpu = new ATMega328();
	
	public void run() {
		
		//for(int i = 0; i < 30; i++) {
		
		if (cpu.hasInstructions()) cpu.execute();
		
		//}
		
		
		/*
		for (int i = 0; i < cpu.noOfLoadedInstructions(); i++) {
			cpu.execute();
		}
		*/
		
	}

	public String getCpuRegisters() {
		String s = "";
		for (int i = 0; i < 32; i++) {
			s += "R" + i + ":\t" + cpu.sram[i] + "\n";
		}
		return s;
	}
	

}
