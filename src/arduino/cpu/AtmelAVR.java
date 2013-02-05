package arduino.cpu;

public class AtmelAVR {
	
	//////////////////////////////////// Definitions \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	//Status Register Flags
	public final byte C = 0x0;
	public final byte Z = 0x1;
	public final byte N = 0x2;
	public final byte V = 0x3;
	public final byte S = 0x4;
	public final byte H = 0x5;
	public final byte T = 0x6;
	public final byte I = 0x7;

	//offsets
	public final byte sreg = 0x5f; // status register

	// execution variables
	protected int program_counter;
	protected int instruction_register;	
	protected int current_instruction_id;
	protected int stack_pointer;
	
	
	//////////////////////////////////// Instruction Data \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static int getInstructionId(int instr) {

		for (int i = 0; i < Instructions.length; i++) {
			if ((instr & Instructions[i].bitmask) == Instructions[i].code) 
				return Instructions[i].id;
		}

		return -1;
	}
	
	public int getInstructionParameter(char ch) {
		return decodeInstructionParameter(instruction_register, Instructions[current_instruction_id].formatString, ch);
	}
	
	public int getNoOfWords(int instr) {
		return Instructions[getInstructionId(instr)].words;
	}
	
	//Fatma valla cok ozur dilerim tam senin kodu save edilmemis ekrandan cut paste ederken
	//bilgisayar gitti senin kod da gitti mecbur oturup bastan yazdim beni affet

	public static int decodeInstructionParameter(int instr, String formatString, char ch) {

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

	protected static final Instruction[] Instructions = new Instruction[] {

		new Instruction(0, "adc", 0x1C00, 0xFC00, "0001 11rd dddd rrrr", 1),
		new Instruction(1, "add", 0x0C00, 0xFC00, "0000 11rd dddd rrrr", 1),
		new Instruction(2, "adiw", 0x9600, 0xFF00, "1001 0110 KKdd KKKK", 2),
		new Instruction(3, "and", 0x2000, 0xFC00, "0010 00rd dddd rrrr", 1),
		new Instruction(4, "andi", 0x7000, 0xF000, "0111 KKKK dddd KKKK", 1),
		new Instruction(5, "asr", 0x9405, 0xFE0F, "1001 010d dddd 0101", 1),
		new Instruction(6, "bclr", 0x9488, 0xFF8F, "1001 0100 1sss 1000", 1),
		new Instruction(7, "bld", 0xF800, 0xFE08, "1111 100d dddd 0bbb", 1),
		new Instruction(8, "brbc", 0xF400, 0xFC00, "1111 01kk kkkk ksss", 1),
		new Instruction(9, "brbs", 0xF000, 0xFC00, "1111 00kk kkkk ksss", 1),
		new Instruction(10, "brcc", 0xF400, 0xFC07, "1111 01kk kkkk k000", 1),
		new Instruction(11, "brcs", 0xF000, 0xFC07, "1111 00kk kkkk k000", 1),
		new Instruction(12, "break", 0x9598, 0xFFFF, "1001 0101 1001 1000", 1),
		new Instruction(13, "breq", 0xF001, 0xFC07, "1111 00kk kkkk k001", 1),
		new Instruction(14, "brge", 0xF404, 0xFC07, "1111 01kk kkkk k100", 1),
		new Instruction(15, "brhc", 0xF405, 0xFC07, "1111 01kk kkkk k101", 1),
		new Instruction(16, "brhs", 0xF005, 0xFC07, "1111 00kk kkkk k101", 1),
		new Instruction(17, "brid", 0xF407, 0xFC07, "1111 01kk kkkk k111", 1),
		new Instruction(18, "brie", 0xF007, 0xFC07, "1111 00kk kkkk k111", 1),
		new Instruction(19, "brlo", 0xF000, 0xFC07, "1111 00kk kkkk k000", 1),
		new Instruction(20, "brlt", 0xF004, 0xFC07, "1111 00kk kkkk k100", 1),
		new Instruction(21, "brmi", 0xF002, 0xFC07, "1111 00kk kkkk k010", 1),
		new Instruction(22, "brne", 0xF401, 0xFC07, "1111 01kk kkkk k001", 1),
		new Instruction(23, "brpl", 0xF402, 0xFC07, "1111 01kk kkkk k010", 1),
		new Instruction(24, "brsh", 0xF400, 0xFC07, "1111 01kk kkkk k000", 1),
		new Instruction(25, "brtc", 0xF406, 0xFC07, "1111 01kk kkkk k110", 1),
		new Instruction(26, "brts", 0xF006, 0xFC07, "1111 00kk kkkk k110", 1),
		new Instruction(27, "brvc", 0xF403, 0xFC07, "1111 01kk kkkk k011", 1),
		new Instruction(28, "brvs", 0xF003, 0xFC07, "1111 00kk kkkk k011", 1),
		new Instruction(29, "bset", 0x9408, 0xFF8F, "1001 0100 0sss 1000", 1),
		new Instruction(30, "bst", 0xFA00, 0xFE08, "1111 101d dddd 0bbb", 1),
		new Instruction(31, "call", 0x940E, 0xFE0E, "1001 010k kkkk 111k", 2),
		new Instruction(32, "cbi", 0x9800, 0xFF00, "1001 1000 AAAA Abbb", 1),
		new Instruction(33, "cbr", 0x7000, 0xF000, "0111 KKKK dddd KKKK", 1),
		new Instruction(34, "clc", 0x9488, 0xFFFF, "1001 0100 1000 1000", 1),
		new Instruction(35, "clh", 0x94D8, 0xFFFF, "1001 0100 1101 1000", 1),
		new Instruction(36, "cli", 0x94F8, 0xFFFF, "1001 0100 1111 1000", 1),
		new Instruction(37, "cln", 0x94A8, 0xFFFF, "1001 0100 1010 1000", 1),
		//new Instruction(38, "clr", 0x2400, 0xFC00, "0010 01dd dddd dddd", 1),
		new Instruction(-1, "skip clr", 0xFFFF, 0xFFFF, "", 1),
		new Instruction(39, "cls", 0x94C8, 0xFFFF, "1001 0100 1100 1000", 1),
		new Instruction(40, "clt", 0x94E8, 0xFFFF, "1001 0100 1110 1000", 1),
		new Instruction(41, "clv", 0x94B8, 0xFFFF, "1001 0100 1011 1000", 1),
		new Instruction(42, "clz", 0x9498, 0xFFFF, "1001 0100 1001 1000", 1),
		new Instruction(43, "com", 0x9400, 0xFE0F, "1001 010d dddd 0000", 1),
		new Instruction(44, "cp", 0x1400, 0xFC00, "0001 01rd dddd rrrr", 1),
		new Instruction(45, "cpc", 0x0400, 0xFC00, "0000 01rd dddd rrrr", 1),
		new Instruction(46, "cpi", 0x3000, 0xF000, "0011 KKKK dddd KKKK", 1),
		new Instruction(47, "cpse", 0x1000, 0xFC00, "0001 00rd dddd rrrr", 1),
		new Instruction(48, "dec", 0x940A, 0xFE0F, "1001 010d dddd 1010", 1),
		new Instruction(49, "des", 0x940B, 0xFF0F, "1001 0100 KKKK 1011", 1),
		new Instruction(50, "eicall", 0x9519, 0xFFFF, "1001 0101 0001 1001", 1),
		new Instruction(51, "eijmp", 0x9419, 0xFFFF, "1001 0100 0001 1001", 1),
		new Instruction(52, "elpm1", 0x95D8, 0xFFFF, "1001 0101 1101 1000", 1),
		new Instruction(53, "elpm2", 0x9006, 0xFE0F, "1001 000d dddd 0110", 1),
		new Instruction(54, "elpm3", 0x9007, 0xFE0F, "1001 000d dddd 0111", 1),
		new Instruction(55, "eor", 0x2400, 0xFC00, "0010 01rd dddd rrrr", 1),
		new Instruction(56, "fmul", 0x0308, 0xFF88, "0000 0011 0ddd 1rrr", 1),
		new Instruction(57, "fmuls", 0x0380, 0xFF88, "0000 0011 1ddd 0rrr", 1),
		new Instruction(58, "fmulsu", 0x0388, 0xFF88, "0000 0011 1ddd 1rrr", 1),
		new Instruction(59, "icall", 0x9509, 0xFFFF, "1001 0101 0000 1001", 1),
		new Instruction(60, "ijmp", 0x9409, 0xFFFF, "1001 0100 0000 1001", 1),
		new Instruction(61, "in", 0xB000, 0xF800, "1011 0AAd dddd AAAA", 1),
		new Instruction(62, "inc", 0x9403, 0xFE0F, "1001 010d dddd 0011", 1),
		new Instruction(63, "jmp", 0x940C, 0xFE0E, "1001 010k kkkk 110k", 2),
		new Instruction(64, "lac", 0x9206, 0xFE0F, "1001 001r rrrr 0110", 1),
		new Instruction(65, "las", 0x9205, 0xFE0F, "1001 001r rrrr 0101", 1),
		new Instruction(66, "lat", 0x9207, 0xFE0F, "1001 001r rrrr 0111", 1),
		new Instruction(67, "ldx1", 0x900C, 0xFE0F, "1001 000d dddd 1100", 1),
		new Instruction(68, "ldx2", 0x900D, 0xFE0F, "1001 000d dddd 1101", 1),
		new Instruction(69, "ldx3", 0x900E, 0xFE0F, "1001 000d dddd 1110", 1),
		new Instruction(70, "ldy1", 0x8008, 0xFE0F, "1000 000d dddd 1000", 1),
		new Instruction(71, "ldy2", 0x9009, 0xFE0F, "1001 000d dddd 1001", 1),
		new Instruction(72, "ldy3", 0x900A, 0xFE0F, "1001 000d dddd 1010", 1),
		new Instruction(73, "ldy4", 0x8008, 0xD208, "10q0 qq0d dddd 1qqq", 1),
		new Instruction(74, "ldz1", 0x8000, 0xFE0F, "1000 000d dddd 0000", 1),
		new Instruction(75, "ldz2", 0x9001, 0xFE0F, "1001 000d dddd 0001", 1),
		new Instruction(76, "ldz3", 0x9002, 0xFE0F, "1001 000d dddd 0010", 1),
		new Instruction(77, "ldz4", 0x8000, 0xD208, "10q0 qq0d dddd 0qqq", 1),
		new Instruction(78, "ldi", 0xE000, 0xF000, "1110 KKKK dddd KKKK", 1),
		new Instruction(79, "lds", 0x9000, 0xFC0F, "1001 000d dddd 0000", 2),
		new Instruction(80, "lds16", 0xA000, 0xF800, "1010 0kkk dddd kkkk", 1),
		new Instruction(81, "lpm1", 0x95C8, 0xFFFF, "1001 0101 1100 1000", 1),
		new Instruction(82, "lpm2", 0x9004, 0xFE0F, "1001 000d dddd 0100", 1),
		new Instruction(83, "lpm3", 0x9005, 0xFE0F, "1001 000d dddd 0101", 1),
		new Instruction(84, "lsl", 0x0C00, 0xFC00, "0000 11dd dddd dddd", 1),
		new Instruction(85, "lsr", 0x9406, 0xFE0F, "1001 010d dddd 0110", 1),
		new Instruction(86, "mov", 0x2C00, 0xFC00, "0010 11rd dddd rrrr", 1),
		new Instruction(87, "movw", 0x0100, 0xFF00, "0000 0001 dddd rrrr", 1),
		new Instruction(88, "mul", 0x9C00, 0xFC00, "1001 11rd dddd rrrr", 1),
		new Instruction(89, "muls", 0x0200, 0xFF00, "0000 0010 dddd rrrr", 1),
		new Instruction(90, "mulsu", 0x0300, 0xFF88, "0000 0011 0ddd 0rrr", 1),
		new Instruction(91, "neg", 0x9401, 0xFE0F, "1001 010d dddd 0001", 1),
		new Instruction(92, "nop", 0x0000, 0xFFFF, "0000 0000 0000 0000", 1),
		new Instruction(93, "or", 0x2800, 0xFC00, "0010 10rd dddd rrrr", 1),
		new Instruction(94, "ori", 0x6000, 0xF000, "0110 KKKK dddd KKKK", 1),
		new Instruction(95, "out", 0xB800, 0xF800, "1011 1AAr rrrr AAAA", 1),
		new Instruction(96, "pop", 0x900F, 0xFE0F, "1001 000d dddd 1111", 1),
		new Instruction(97, "push", 0x920F, 0xFE0F, "1001 001d dddd 1111", 1),
		new Instruction(98, "rcall", 0xD000, 0xF000, "1101 kkkk kkkk kkkk", 1),
		new Instruction(99, "ret", 0x9508, 0xFFFF, "1001 0101 0000 1000", 1),
		new Instruction(100, "reti", 0x9518, 0xFFFF, "1001 0101 0001 1000", 1),
		new Instruction(101, "rjmp", 0xC000, 0xF000, "1100 kkkk kkkk kkkk", 1),
		new Instruction(102, "rol", 0x1C00, 0xFC00, "0001 11dd dddd dddd", 1),
		new Instruction(103, "ror", 0x9407, 0xFE0F, "1001 010d dddd 0111", 1),
		new Instruction(104, "sbc", 0x0800, 0xFC00, "0000 10rd dddd rrrr", 1),
		new Instruction(105, "sbci", 0x4000, 0xF000, "0100 KKKK dddd KKKK", 1),
		new Instruction(106, "sbi", 0x9A00, 0xFF00, "1001 1010 AAAA Abbb", 1),
		new Instruction(107, "sbic", 0x9900, 0xFF00, "1001 1001 AAAA Abbb", 1),
		new Instruction(108, "sbis", 0x9B00, 0xFF00, "1001 1011 AAAA Abbb", 1),
		new Instruction(109, "sbiw", 0x9700, 0xFF00, "1001 0111 KKdd KKKK", 1),
		new Instruction(110, "sbr", 0x6000, 0xF000, "0110 KKKK dddd KKKK", 1),
		new Instruction(111, "sbrc", 0xFC00, 0xFE08, "1111 110r rrrr 0bbb", 1),
		new Instruction(112, "sbrs", 0xFE00, 0xFE08, "1111 111r rrrr 0bbb", 1),
		new Instruction(113, "sec", 0x9408, 0xFFFF, "1001 0100 0000 1000", 1),
		new Instruction(114, "seh", 0x9458, 0xFFFF, "1001 0100 0101 1000", 1),
		new Instruction(115, "sei", 0x9478, 0xFFFF, "1001 0100 0111 1000", 1),
		new Instruction(116, "sen", 0x9428, 0xFFFF, "1001 0100 0010 1000", 1),
		new Instruction(117, "ser", 0xEF0F, 0xFF0F, "1110 1111 dddd 1111", 1),
		new Instruction(118, "ses", 0x9448, 0xFFFF, "1001 0100 0100 1000", 1),
		new Instruction(119, "set", 0x9468, 0xFFFF, "1001 0100 0110 1000", 1),
		new Instruction(120, "sev", 0x9438, 0xFFFF, "1001 0100 0011 1000", 1),
		new Instruction(121, "sez", 0x9418, 0xFFFF, "1001 0100 0001 1000", 1),
		new Instruction(122, "sleep", 0x9588, 0xFFFF, "1001 0101 1000 1000", 1),
		new Instruction(123, "spm", 0x95E8, 0xFFFF, "1001 0101 1110 1000", 1),
		new Instruction(126, "stx1", 0x920C, 0xFE0F, "1001 001r rrrr 1100", 1),
		new Instruction(127, "stx2", 0x920D, 0xFE0F, "1001 001r rrrr 1101", 1),
		new Instruction(128, "stx3", 0x920E, 0xFE0F, "1001 001r rrrr 1110", 1),
		new Instruction(129, "sty1", 0x9208, 0xFE0F, "1000 001r rrrr 1000", 1),
		new Instruction(130, "sty2", 0x9209, 0xFE0F, "1001 001r rrrr 1001", 1),
		new Instruction(131, "sty3", 0x900A, 0xFE0F, "1001 001r rrrr 1010", 1),
		new Instruction(132, "sty4", 0x8208, 0xD208, "10q0 qq1r rrrr 1qqq", 1),
		new Instruction(133, "stz1", 0x8200, 0xFE0F, "1000 001r rrrr 0000", 1),
		new Instruction(134, "stz2", 0x9201, 0xFE0F, "1001 001r rrrr 0001", 1),
		new Instruction(135, "stz3", 0x9202, 0xFE0F, "1001 001r rrrr 0010", 1),
		new Instruction(136, "stz4", 0x8200, 0xD208, "10q0 qq1r rrrr 0qqq", 1),
		new Instruction(137, "sts", 0x9200, 0xFE0F, "1001 001d dddd 0000", 2),
		new Instruction(138, "sts16", 0xA800, 0xF800, "1010 1kkk dddd kkkk", 1),
		new Instruction(139, "sub", 0x1800, 0xFC00, "0001 10rd dddd rrrr", 1),
		new Instruction(140, "subi", 0x5000, 0xF000, "0101 KKKK dddd KKKK", 1),
		new Instruction(141, "swap", 0x9402, 0xFE0F, "1001 010d dddd 0010", 1),
		new Instruction(142, "tst", 0x2000, 0xFC00, "0010 00dd dddd dddd", 1),
		new Instruction(143, "wdr", 0x95A8, 0xFFFF, "1001 0101 1010 1000", 1),
		new Instruction(145, "xch", 0x9204, 0xFE0F, "1001 001r rrrr 0100", 1)

	};



}
