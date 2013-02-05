package main;

import javax.swing.*;

import arduino.Arduino;
import java.awt.*;
import java.awt.event.*;

public class WorkspaceGUI extends JFrame {
	
	JPanel jp = new JPanel();
	JButton b = new JButton("Execute Next Instruction");
	JTextArea jta_registers = new JTextArea("Registers [SRAM offsets 0x00 - 0x20]");
	JTextArea jta_instruction = new JTextArea("Instruction Data");
	GridBagLayout gb = new GridBagLayout();
	GridBagConstraints gc = new GridBagConstraints();
	
	Arduino a = new Arduino();
	
	public WorkspaceGUI() {
		init();
	}
	
	public void init() {
		
		this.setSize(700,700);
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jp.setLayout(gb);
		//button
		b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				a.run();
				jta_registers.setText(a.getCpuRegisters());
				jta_instruction.setText("Instruction Register: \nPProgram Counter: \nInstruction: \n");
			}
			
		});
		
		gc.anchor = GridBagConstraints.NORTH;
		gc.insets = new Insets(5,5,5,5);
		gc.gridx = 0;
		gc.gridy = 0;
		jp.add(b, gc);
		gc.gridy = 1;
		jp.add(jta_registers, gc);
		gc.gridx=1;
		jp.add(jta_instruction, gc);
		
		this.add(jp, BorderLayout.NORTH);
		this.setVisible(true);

	}
	
	      
	
}
