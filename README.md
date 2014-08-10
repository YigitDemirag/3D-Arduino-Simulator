3D-Arduino-Simulator
====================

I created 3D Arduino Simulator and make it open-source as a final project of my Algorithms and Programming II class in my freshman. I was playing with Arduino but could not find a simulator where I can upload my compiled hex file and watch simulation on PC.Also I was eager to learn how a CPU works, moreover, how I could write an emulator.

Arduino board includes an 8 bit Atmel AVR CPU, 14 I/O pins supporting serial communication and 6 analog inputs.The first aim was to being able to program digital outputs with compiled hex file created by Arduino IDE. 

Atmel ATmega328 has 131 instructions, 32 general purpose I/O registers, a 2 KB SRAM, 1KB EEPROM.Writing a program on Arduino IDE, gives a hex file to upload Arduino.Therefore simulator should be able decode this Intel hex binary, and process each single instruction that includes byte count, address, record type, data and checksum information.Hence, the general simulation strategy is :

- Fetch, decode and evaluate instructions in the CPU core
- Update internal CPU modules for changes in the IO registers
- Check occurrences of CPU interrupts and manage appropriately
- Update values of CPU pins, Arduino pins and 3D view respectively. 

I write flash module with 64kb instruction and 32kb program memory which reads the hex file, an AtmelAVR module to mask instructions, and it also has registers and defined operands.I also added ATmega328 object that keeps and update program counter, 8 register status flags, and update CPU clock accordingly.I defined only 13 instructions as it is enough when using for, while or if statements to control digital output pins.At the end of the project, simulator can demonstrate the states of digital inputs in 3D view with given compiled hex file.
