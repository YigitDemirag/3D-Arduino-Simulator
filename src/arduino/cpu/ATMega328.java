package arduino.cpu;

import arduino.BinaryFunctions;

public class ATMega328 extends AtmelAVR {

	//Variables
	public byte[] sram;
	Flash flash;

	public ATMega328 () {
		sram = new byte[32 + 64]; //register file + i/o space
		flash = new Flash();		
	}

	//Execution
	public void execute() {

		//System.out.println("[cpu.execute()] instruction: " + Integer.toHexString(this.flash.get16bitsFromInstructionMemory(this.program_counter)));

		//this.instruction_register = 0xffff & (this.flash.i_mem[this.program_counter] << 8 | this.flash.i_mem[this.program_counter+1]);

		this.instruction_register = this.flash.get16bitsFromInstructionMemory(this.program_counter);

		System.out.println("[cpu execute()] Instruction Register Dump: " + Integer.toHexString(instruction_register));


		this.current_instruction_id = getInstructionId(this.instruction_register);

		System.out.print("[cpu.execute()] PC: 0x" + Integer.toHexString(this.program_counter) + " InstID: " + this.current_instruction_id);

		if (this.current_instruction_id != -1) System.out.println(" Instruction: " + Instructions[this.current_instruction_id].name);

		/*
		System.out.print("[cpu.execute()] PC: " + this.program_counter + " InstID: " + this.current_instruction_id);

		if (this.current_instruction_id != -1)
			System.out.print(" InstName: " + Instructions[this.current_instruction_id].name + "\n");
		 */

		runInstruction();
		System.out.println("[cpu.execute()] executed runInstruction()\n");

	}

	public boolean hasInstructions() {
		return this.program_counter <= this.flash.i_count*2 ? true : false;
	}

	public int noOfLoadedInstructions() {
		return flash.i_count;
	}


	//Internal Operations
	private void setFlag(byte flag, boolean value) {
		this.sram[this.sreg] = BinaryFunctions.setBitOfByte(this.sram[this.sreg], flag, value);
	}

	private boolean getFlagValue(byte bit) {
		return BinaryFunctions.getBitOfByte(this.sram[this.sreg], bit);
	}

	private void incrementProgramCounter(int k) {
		this.program_counter += k*2;
	}


	////////////////////////////////////////// instructions \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	private void adc_command() {

		//parameters			
		int r = getInstructionParameter('r');	// 0 <= r <= 31
		int d = getInstructionParameter('d');	// 0 <= d <= 31

		boolean Rd3 = BinaryFunctions.getBitOfByte(this.sram[d], 3); //before operation
		boolean Rd7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);
		boolean Rr3 = BinaryFunctions.getBitOfByte(this.sram[r], 3);
		boolean Rr7 = BinaryFunctions.getBitOfByte(this.sram[r], 7);

		//operation
		this.sram[d] = (byte) (this.sram[r] + this.sram[d]);
		if (getFlagValue(C)) this.sram[d]++;

		boolean R3 = BinaryFunctions.getBitOfByte(this.sram[d], 3); //after operation
		boolean R7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);

		//flags
		setFlag(C, (Rd7 & Rr7 | Rr7 & !R7 | !R7 & Rd7));
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, R7);
		setFlag(V, (Rd7 & Rr7 & !R7 | !Rd7 & !Rr7 & R7));
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );
		setFlag(H, (Rd3 & Rr3 | Rr3 & !R3 | !R3 & Rd3 ));

		//program counter
		incrementProgramCounter(1);
	}

	private void add_command() {

		//parameters
		int r = getInstructionParameter('r');	// 0 <= r <= 31
		int d = getInstructionParameter('d');	// 0 <= d <= 31

		boolean Rd3 = BinaryFunctions.getBitOfByte(this.sram[d], 3); //before operation
		boolean Rd7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);
		boolean Rr3 = BinaryFunctions.getBitOfByte(this.sram[r], 3);
		boolean Rr7 = BinaryFunctions.getBitOfByte(this.sram[r], 7);

		//operation
		this.sram[d] = (byte) (this.sram[r] + this.sram[d]);

		boolean R3 = BinaryFunctions.getBitOfByte(this.sram[d], 3); //after operation
		boolean R7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);

		//flags
		setFlag(C, (Rd7 & Rr7 | Rr7 & !R7 | !R7 & Rd7));
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, R7);
		setFlag(V, (Rd7 & Rr7 & !R7 | !Rd7 & !Rr7 & R7));
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );
		setFlag(H, (Rd3 & Rr3 | Rr3 & !R3 | !R3 & Rd3 ));

		//program counter
		incrementProgramCounter(1);
	}

	private void adiw_command() {

		//parameters
		int K = getInstructionParameter('K');			// 0 <= K <= 64
		int d = 24 + 2*getInstructionParameter('d');	// 0 <= d <= 3; d = {24,26,28,30}

		boolean Rdh7 = BinaryFunctions.getBitOfByte(this.sram[d+1], 7);

		//operation
		int result = ((this.sram[d+1] << 8) | (this.sram[d])) + K;
		this.sram[d] = (byte) (result & 0xff); 
		this.sram[d+1] = (byte) (result & 0xff00 >> 8);

		boolean R15 = BinaryFunctions.getBitOfByte(this.sram[d+1], 7);

		//flags
		setFlag(C, !R15 & Rdh7);
		setFlag(Z, (this.sram[d] | this.sram[d+1]) == 0 ? true : false);
		setFlag(N, R15);
		setFlag(V, (!Rdh7 & R15));
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );

		//program counter
		incrementProgramCounter(1);
	}

	private void and_command() {

		//parameters
		int r = getInstructionParameter('r');	// 0 <= r <= 31
		int d = getInstructionParameter('d');	// 0 <= d <= 31

		//operation
		this.sram[d] = (byte) (this.sram[d] & this.sram[r]);

		//flags
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, (BinaryFunctions.getBitOfByte(this.sram[d], 7)));
		setFlag(V, false);
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );

		System.out.println("[and_command()] [ d r ] " + d + " " + r);

		//program counter
		incrementProgramCounter(1);
	}

	private void andi_command() { 

		//parameters	
		int K = getInstructionParameter('K');		// 0 <= K <= 255
		int d = 16 + getInstructionParameter('d');	// 0 <= d <= 31

		//operation
		this.sram[d] = (byte) (this.sram[d] & K);

		//flags
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, (BinaryFunctions.getBitOfByte(this.sram[d], 7)));
		setFlag(V, false);
		setFlag(S, getFlagValue(N) ^ getFlagValue(V) );

		//program counter
		incrementProgramCounter(1);
	}

	private void asr_command() { 


	}

	private void bclr_command() { //Complete

		//parameters
		byte s = (byte) this.getInstructionParameter('s');	// 0 <= s <= 7

		//operation
		setFlag(s, false);

		//program counter
		incrementProgramCounter(1);
	}

	private void bld_command() { 

	}

	private void brbc_command() { //complete

		//parameters
		int k = this.getInstructionParameter('k'); // -64 <= k <= 63
		if (k >= 128) k = ~k & 0x3f;
		byte s = (byte) this.getInstructionParameter('s');  // 0 <= s <= 7

		//operation
		if(!getFlagValue(s))
			this.incrementProgramCounter(k+1);
		else
			this.incrementProgramCounter(1);

	}

	private void brbs_command() { //complete

		//parameters
		int k = this.getInstructionParameter('k'); // -64 <= k <= 63
		if (k >= 128) k = ~k & 0x3f;
		byte s = (byte) this.getInstructionParameter('s');  // 0 <= s <= 7

		//operation
		if(getFlagValue(s))
			this.incrementProgramCounter(k+1);
		else
			this.incrementProgramCounter(1);

	}

	private void brcc_command() {

	}
	private void brcs_command() {

	}
	private void break_command() {

	}
	private void breq_command() {

	}
	private void brge_command() {

	}
	private void brhc_command() {

	}
	private void brhs_command() {

	}
	private void brid_command() {

	}
	private void brie_command() {

	}
	private void brlo_command() {

	}
	private void brlt_command() {

	}
	private void brmi_command() {

	}
	private void brne_command() {

	}
	private void brpl_command() {

	}
	private void brsh_command() {

	}
	private void brtc_command() {

	}
	private void brts_command() {

	}
	private void brvc_command() {

	}
	private void brvs_command() {

	}
	private void bset_command() {

	}
	private void bst_command() {

	}
	private void call_command() {

	}
	private void cbi_command() {

	}
	private void cbr_command() {

	}
	private void clc_command() {

	}
	private void clh_command() {

	}
	private void cli_command() {

	}
	private void cln_command() {

	}
	private void clr_command() {

	}
	private void cls_command() {

	}
	private void clt_command() {

	}
	private void clv_command() {

	}
	private void clz_command() {

	}
	private void com_command() {

	}
	private void cp_command() {

	}
	private void cpc_command() {

		incrementProgramCounter(1);

	}


	private void cpi_command() { // NOT COMPLETED !!!
		//parameters
		int d = 16 + this.getInstructionParameter('d'); // 16 <= d <=31
		int K = this.getInstructionParameter('K');      // 0 <= K <= 255

		//before operation
		boolean Rd3 = BinaryFunctions.getBitOfByte(this.sram[d], 3);
		boolean Rd7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);
		boolean K7 = BinaryFunctions.getBitFromInt(K,7);
		boolean K3 = BinaryFunctions.getBitFromInt(K,3);
		
		
		//operation
		int result = this.sram[d] - K;
		
		//after operation
		boolean R3 = BinaryFunctions.getBitOfByte(this.sram[d], 3);
		boolean R7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);
		
		//flags
		setFlag(C, (Math.abs(K) > Math.abs(this.sram[d]) ? true : false));
		setFlag(Z, (result == 0) ? true : false);
		setFlag(N, BinaryFunctions.getBitOfByte(this.sram[d], 7));
		setFlag(V, Rd7 & !K7 & R7 | !Rd7 & K7 & R7);
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );
		setFlag(H, !Rd3 & K3 | K3 & R3 | R3 & !Rd3);

		//program counter
		incrementProgramCounter(1);

	}
	private void cpse_command() {

	}
	private void dec_command() {

	}
	private void des_command() {

	}
	private void eicall_command() {

	}
	private void eijmp_command() {

	}
	private void elpm1_command() {

	}
	private void elpm2_command() {

	}
	private void elpm3_command() {

	}

	private void eor_command() { //Complete

		//parameters
		int r = getInstructionParameter('r');	// 0 <= r <= 31
		int d = getInstructionParameter('d');	// 0 <= d <= 31

		//operation
		this.sram[d] = (byte) (this.sram[d] ^ this.sram[r]);

		//flags
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, (BinaryFunctions.getBitOfByte(this.sram[d], 7)));
		setFlag(V, false);
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );

		//program counter
		incrementProgramCounter(1);	
	}

	private void fmul_command() {

	}
	private void fmuls_command() {

	}
	private void fmulsu_command() {

	}
	private void icall_command() {

	}
	private void ijmp_command() {

	}
	private void in_command() {

	}
	private void inc_command() {

	}

	private void jmp_command() { //Complete

		// parameters
		//int k = ( (this.getInstructionParameter('k') << 16)
		//		| flash.i_mem[this.program_counter + 1] ) << 1;

		int k = ((this.getInstructionParameter('k') << 16) | this.flash.get16bitsFromInstructionMemory(this.program_counter+0x2)) << 1;

		//program counter
		System.out.println("[jmp_command()] value of k: 0x" + Integer.toHexString(k));
		this.program_counter = k;
	}

	private void lac_command() {

	}
	private void las_command() {

	}
	private void lat_command() {

	}
	private void ldx1_command() {

	}
	private void ldx2_command() {

	}
	private void ldx3_command() {

	}
	private void ldy1_command() {

	}
	private void ldy2_command() {

	}
	private void ldy3_command() {

	}
	private void ldy4_command() {

	}
	private void ldz1_command() {

	}
	private void ldz2_command() {

	}
	private void ldz3_command() {

	}
	private void ldz4_command() {

	}
	private void ldi_command() {

		//parameters
		int d = 16 + this.getInstructionParameter('d');
		int K = this.getInstructionParameter('K');

		//operation
		this.sram[d] = (byte) K;

		//program counter
		incrementProgramCounter(1);

	}
	private void lds_command() {

	}
	private void lds16_command() {

	}
	private void lpm1_command() {



	}
	private void lpm2_command() {

	}
	private void lpm3_command() {

	}
	private void lsl_command() {

	}
	private void lsr_command() {

	}
	private void mov_command() {

	}
	private void movw_command() {

	}
	private void mul_command() {

	}
	private void muls_command() {

	}
	private void mulsu_command() {

	}
	private void neg_command() {

	}
	private void nop_command() {

	}

	private void or_command() {

		//parameters
		int d = getInstructionParameter('d');	// 0 <= d <= 31
		int r = getInstructionParameter('r');	// 0 <= r <= 31

		//operation
		this.sram[d] = (byte) (this.sram[d] | this.sram[r]);

		//flags
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, (BinaryFunctions.getBitOfByte(this.sram[d], 7)));
		setFlag(V, false);
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );

		//program counter
		incrementProgramCounter(1);

	}


	private void ori_command() {

		//parameters
		int d = 16 + getInstructionParameter('d');	// 16 <= d <= 31
		int K = getInstructionParameter('K');		// 0 <= d <= 255

		//operation
		this.sram[d] = (byte) (this.sram[d] | K);

		//flags
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, (BinaryFunctions.getBitOfByte(this.sram[d], 7)));
		setFlag(V, false);
		setFlag(S, getFlagValue(N) ^ getFlagValue(Z) );

		//program counter
		incrementProgramCounter(1);

	}

	private void out_command() {

		//parameters
		int A = this.getInstructionParameter('A'); // 0 <= A <= 63
		int r = this.getInstructionParameter('r'); // 0 <= r <= 31

		//instruction
		this.sram[A+0x20] = this.sram[r];

		//program counter
		incrementProgramCounter(1);

	}
	private void pop_command() {

	}
	private void push_command() {

	}
	private void rcall_command() {

	}
	private void ret_command() {

	}
	private void reti_command() {

	}

	private void rjmp_command() {

		//parameters
		int k = this.getInstructionParameter('k');	// -2*1024 <= k <= 2*1024
		if (k >= 2*1024) k = ~k & 0x7ff; 			// 2's complement

		//program counter
		this.program_counter += k+1;
	}

	private void rol_command() {

	}
	private void ror_command() {

	}
	private void sbc_command() {

	}
	private void sbci_command() {

	}
	private void sbi_command() {

	}
	private void sbic_command() {

	}
	private void sbis_command() {

	}
	private void sbiw_command() {

	}
	private void sbr_command() {

	}
	private void sbrc_command() {

	}
	private void sbrs_command() {

		//parameters
		int r = this.getInstructionParameter('r');	// 0 <= r <= 31
		int b = this.getInstructionParameter('b');	// 0 <= b <= 7 

		//operation
		if (BinaryFunctions.getBitOfByte(this.sram[r], b) == true) {
			incrementProgramCounter(this.getNoOfWords(this.flash.get16bitsFromInstructionMemory(this.program_counter+2)));	
		}

		//program counter
		incrementProgramCounter(1);
	}
	private void sec_command() {

	}
	private void seh_command() {

	}
	private void sei_command() {

	}
	private void sen_command() {

	}
	private void ser_command() {

	}
	private void ses_command() {

	}
	private void set_command() {

	}
	private void sev_command() {

	}
	private void sez_command() {

	}
	private void sleep_command() {

	}
	private void spm_command() {

	}
	private void stx1_command() {

	}
	private void stx2_command() {

	}
	private void stx3_command() {

	}
	private void sty1_command() {

	}
	private void sty2_command() {

	}
	private void sty3_command() {

	}
	private void sty4_command() {

	}
	private void stz1_command() {

	}
	private void stz2_command() {

	}
	private void stz3_command() {

	}
	private void stz4_command() {

	}
	private void sts_command() {

	}
	private void sts16_command() {

	}
	private void sub_command() {

	}
	private void subi_command() {
		//parameters
		int K = this.getInstructionParameter('K'); // 0 <= K <= 255
		int d = 16 + this.getInstructionParameter('d'); // 16 <= d <= 31
		//Getting Registers Before Operation
		boolean Rd3 = BinaryFunctions.getBitOfByte(this.sram[d], 3);
		boolean Rd7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);
		boolean K7 = BinaryFunctions.getBitFromInt(K,7);
		boolean K3 = BinaryFunctions.getBitFromInt(K,3);
		//Operation
		this.sram[d] = (byte) (this.sram[d] - K);
		//After Operation

		//int result = this.sram[d];

		boolean R3 = BinaryFunctions.getBitOfByte(this.sram[d], 3); 
		boolean R7 = BinaryFunctions.getBitOfByte(this.sram[d], 7);
		//boolean R1 = BinaryFunctions.getBitOfByte(this.sram[d], 7);

		//Flags
		setFlag(C, (Math.abs(K) > Math.abs(this.sram[d]) ? true : false) );
		setFlag(Z, this.sram[d] == 0 ? true : false);
		setFlag(N, (BinaryFunctions.getBitOfByte(this.sram[d], 7)));
		setFlag(V, Rd7 & !K7 & !R7 | !Rd7 & K7 & R7);
		setFlag(S, getFlagValue(N) ^ getFlagValue(V) );
		setFlag(H, !Rd3 & K3 | R3 | R3 & !Rd3);

		//Program Counter
		incrementProgramCounter(1);

	}

	private void swap_command() {

	}
	private void tst_command() {

	}
	private void wdr_command() {

	}
	private void xch_command() {

	}


	///////////////////////////////////////////////////////////////////////////////////////////////////

	public void runInstruction() {

		switch (this.current_instruction_id) {
		case -1: 
			System.out.println("\n[!] Unknown instruction: 0x" 
					+ Integer.toHexString(this.instruction_register));
			incrementProgramCounter(1);
			break;

		case 0: adc_command(); break;
		case 1: add_command(); break;
		case 2: adiw_command(); break;
		case 3: and_command(); break;
		case 4: andi_command(); break;
		case 5: asr_command(); break;
		case 6: bclr_command(); break;
		case 7: bld_command(); break;
		case 8: brbc_command(); break;
		case 9: brbs_command(); break;
		case 10: brcc_command(); break;
		case 11: brcs_command(); break;
		case 12: break_command(); break;
		case 13: breq_command(); break;
		case 14: brge_command(); break;
		case 15: brhc_command(); break;
		case 16: brhs_command(); break;
		case 17: brid_command(); break;
		case 18: brie_command(); break;
		case 19: brlo_command(); break;
		case 20: brlt_command(); break;
		case 21: brmi_command(); break;
		case 22: brne_command(); break;
		case 23: brpl_command(); break;
		case 24: brsh_command(); break;
		case 25: brtc_command(); break;
		case 26: brts_command(); break;
		case 27: brvc_command(); break;
		case 28: brvs_command(); break;
		case 29: bset_command(); break;
		case 30: bst_command(); break;
		case 31: call_command(); break;
		case 32: cbi_command(); break;
		case 33: cbr_command(); break;
		case 34: clc_command(); break;
		case 35: clh_command(); break;
		case 36: cli_command(); break;
		case 37: cln_command(); break;
		case 38: clr_command(); break;
		case 39: cls_command(); break;
		case 40: clt_command(); break;
		case 41: clv_command(); break;
		case 42: clz_command(); break;
		case 43: com_command(); break;
		case 44: cp_command(); break;
		case 45: cpc_command(); break;
		case 46: cpi_command(); break;
		case 47: cpse_command(); break;
		case 48: dec_command(); break;
		case 49: des_command(); break;
		case 50: eicall_command(); break;
		case 51: eijmp_command(); break;
		case 52: elpm1_command(); break;
		case 53: elpm2_command(); break;
		case 54: elpm3_command(); break;
		case 55: eor_command(); break;
		case 56: fmul_command(); break;
		case 57: fmuls_command(); break;
		case 58: fmulsu_command(); break;
		case 59: icall_command(); break;
		case 60: ijmp_command(); break;
		case 61: in_command(); break;
		case 62: inc_command(); break;
		case 63: jmp_command(); break;
		case 64: lac_command(); break;
		case 65: las_command(); break;
		case 66: lat_command(); break;
		case 67: ldx1_command(); break;
		case 68: ldx2_command(); break;
		case 69: ldx3_command(); break;
		case 70: ldy1_command(); break;
		case 71: ldy2_command(); break;
		case 72: ldy3_command(); break;
		case 73: ldy4_command(); break;
		case 74: ldz1_command(); break;
		case 75: ldz2_command(); break;
		case 76: ldz3_command(); break;
		case 77: ldz4_command(); break;
		case 78: ldi_command(); break;
		case 79: lds_command(); break;
		case 80: lds16_command(); break;
		case 81: lpm1_command(); break;
		case 82: lpm2_command(); break;
		case 83: lpm3_command(); break;
		case 84: lsl_command(); break;
		case 85: lsr_command(); break;
		case 86: mov_command(); break;
		case 87: movw_command(); break;
		case 88: mul_command(); break;
		case 89: muls_command(); break;
		case 90: mulsu_command(); break;
		case 91: neg_command(); break;
		case 92: nop_command(); break;
		case 93: or_command(); break;
		case 94: ori_command(); break;
		case 95: out_command(); break;
		case 96: pop_command(); break;
		case 97: push_command(); break;
		case 98: rcall_command(); break;
		case 99: ret_command(); break;
		case 100: reti_command(); break;
		case 101: rjmp_command(); break;
		case 102: rol_command(); break;
		case 103: ror_command(); break;
		case 104: sbc_command(); break;
		case 105: sbci_command(); break;
		case 106: sbi_command(); break;
		case 107: sbic_command(); break;
		case 108: sbis_command(); break;
		case 109: sbiw_command(); break;
		case 110: sbr_command(); break;
		case 111: sbrc_command(); break;
		case 112: sbrs_command(); break;
		case 113: sec_command(); break;
		case 114: seh_command(); break;
		case 115: sei_command(); break;
		case 116: sen_command(); break;
		case 117: ser_command(); break;
		case 118: ses_command(); break;
		case 119: set_command(); break;
		case 120: sev_command(); break;
		case 121: sez_command(); break;
		case 122: sleep_command(); break;
		case 123: spm_command(); break;
		case 126: stx1_command(); break;
		case 127: stx2_command(); break;
		case 128: stx3_command(); break;
		case 129: sty1_command(); break;
		case 130: sty2_command(); break;
		case 131: sty3_command(); break;
		case 132: sty4_command(); break;
		case 133: stz1_command(); break;
		case 134: stz2_command(); break;
		case 135: stz3_command(); break;
		case 136: stz4_command(); break;
		case 137: sts_command(); break;
		case 138: sts16_command(); break;
		case 139: sub_command(); break;
		case 140: subi_command(); break;
		case 141: swap_command(); break;
		case 142: tst_command(); break;
		case 143: wdr_command(); break;
		case 145: xch_command(); break;

		}
	}


}
