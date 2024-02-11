package generic;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;

import generic.Operand.OperandType;
import generic.Instruction.OperationType;

import javax.print.DocFlavor;
import javax.print.attribute.HashPrintJobAttributeSet;


public class Simulator {
		
	static FileInputStream inputcodeStream = null;
	static HashMap<String,String> OpcodeTable = new HashMap<>();
	
	public static void setupSimulation(String assemblyProgramFile)
	{	
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();

		OpcodeTable.put("add", "00000");
		OpcodeTable.put("sub", "00010");
		OpcodeTable.put("mul", "00100");
		OpcodeTable.put("div", "00110");
		OpcodeTable.put("and", "01000");
		OpcodeTable.put("or", "01010");
		OpcodeTable.put("xor", "01100");
		OpcodeTable.put("slt", "01110");
		OpcodeTable.put("sll", "10000");
		OpcodeTable.put("srl", "10010");
		OpcodeTable.put("sra", "10100");

		OpcodeTable.put("addi", "00001");
		OpcodeTable.put("subi", "00011");
		OpcodeTable.put("muli", "00101");
		OpcodeTable.put("divi", "00111");
		OpcodeTable.put("andi", "01001");
		OpcodeTable.put("ori", "01011");
		OpcodeTable.put("xori", "01101");
		OpcodeTable.put("slti", "01111");
		OpcodeTable.put("slli", "10001");
		OpcodeTable.put("srli", "10011");
		OpcodeTable.put("srai", "10101");
		OpcodeTable.put("load", "10110");
		OpcodeTable.put("store", "10111");
		OpcodeTable.put("beq", "11001");
		OpcodeTable.put("bne", "11010");
		OpcodeTable.put("blt", "11011");
		OpcodeTable.put("bgt", "11100");
		OpcodeTable.put("jmp", "11000");
		OpcodeTable.put("end", "11101");
	}
	
	public static void assemble(String objectProgramFile) throws IOException {
		//TODO your assembler code
		//1. open the objectProgramFile in binary mode
		File f = new File(objectProgramFile);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		//2. write the firstCodeAddress to the file
		byte[] addressCode = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
		bos.write(addressCode);

		//3. write the data to the file
		for(int i=0;i<ParsedProgram.data.size();i++){
			byte[] data_items = ByteBuffer.allocate(4).putInt(ParsedProgram.data.get(i)).array();
			bos.write(data_items);
		}

		//4. assemble one instruction at a time, and write to the file

		for(int i=0;i<ParsedProgram.code.size();i++){


			Instruction current_ins = ParsedProgram.code.get(i);

			String full_ins = "";
			OperationType operation = current_ins.getOperationType();
			// System.out.println(operation.name());
			// System.out.println(OpcodeTable.get(operation.name()));
			full_ins += OpcodeTable.get(operation.name());

			switch (operation){
				//R3-types
				case add:
				case sub:
				case mul:
				case div:
				case and:
				case or:
				case xor:
				case slt:
				case sll:
				case srl:
				case sra:

				{
					String rs1 = Integer.toBinaryString(current_ins.getSourceOperand1().getValue());
					rs1 = String.format("%5s", rs1).replaceAll(" ", "0");
					full_ins+=rs1;

					String rs2 = Integer.toBinaryString(current_ins.getSourceOperand2().getValue());
					rs2 = String.format("%5s", rs2).replaceAll(" ", "0");
					full_ins+=rs2;

					String rsd = Integer.toBinaryString(current_ins.getDestinationOperand().getValue());
					rsd = String.format("%5s", rsd).replaceAll(" ", "0");
					full_ins+=rsd;

					full_ins+="000000000000";

					break;
				}

				//R2I-types
				case addi:
				case subi:
				case muli:
				case divi:
				case andi:
				case ori:
				case xori:
				case slti:
				case slli:
				case srli:
				case srai:
				case load:
				case store:
				{
					String rs1 = Integer.toBinaryString(current_ins.getSourceOperand1().getValue());
					rs1 = String.format("%5s", rs1).replaceAll(" ", "0");
					full_ins+=rs1;

					String rd = Integer.toBinaryString(current_ins.getDestinationOperand().getValue());
					rd = String.format("%5s", rd).replaceAll(" ", "0");
					full_ins+=rd;

					if(current_ins.getSourceOperand2().getOperandType() == OperandType.Label){

						int relAddress = ParsedProgram.symtab.get(current_ins.getSourceOperand2().getLabelValue());
						String imm = Integer.toBinaryString(relAddress);

						// if(relAddress>=0){
						imm = String.format("%17s", imm).replaceAll(" ", "0");
						
						full_ins+=imm;
					}
					else {
						String imm = Integer.toBinaryString(current_ins.getSourceOperand2().getValue());
						imm = String.format("%17s", imm).replaceAll(" ", "0");
						full_ins+=imm;
					}
					break;
				}

				case beq:
				case bne:
				case blt:
				case bgt:
				{
					String rs1 = Integer.toBinaryString(current_ins.getSourceOperand1().getValue());
					rs1 = String.format("%5s", rs1).replaceAll(" ", "0");
					full_ins+=rs1;

					String rd = Integer.toBinaryString(current_ins.getSourceOperand2().getValue());
					rd = String.format("%5s", rd).replaceAll(" ", "0");
					full_ins+=rd;

					if(current_ins.getDestinationOperand().getOperandType() == OperandType.Label){

						int relAddress = ParsedProgram.symtab.get(current_ins.getDestinationOperand().getLabelValue())-current_ins.getProgramCounter();
						String imm = Integer.toBinaryString(relAddress);

						if(relAddress>=0){
							imm = String.format("%17s", imm).replaceAll(" ", "0");
						}
						else{
							imm = imm.substring(15);
						}
						full_ins+=imm;
					}
					else {
						String imm = Integer.toBinaryString(current_ins.getDestinationOperand().getValue()-current_ins.getProgramCounter());
						imm = String.format("%17s", imm).replaceAll(" ", "0");
						full_ins+=imm;
					}
					break;
				}
//				R2I-Type
				case jmp:
				{
					full_ins += "00000";

					if(current_ins.getDestinationOperand().getOperandType() == OperandType.Label){

						int relAddress = ParsedProgram.symtab.get(current_ins.getDestinationOperand().getLabelValue())-current_ins.getProgramCounter();
						String imm = Integer.toBinaryString(relAddress);

						if(relAddress>=0){
							imm = String.format("%22s", imm).replaceAll(" ", "0");
						}
						else{
							imm = imm.substring(10);
						}
						full_ins+=imm;
					}
					else {
						int relAddress = current_ins.getDestinationOperand().getValue()-current_ins.getProgramCounter();
						String imm = Integer.toBinaryString(relAddress);
						if(relAddress>=0){
							imm = String.format("%22s", imm).replaceAll(" ", "0");
						}
						else{
							imm = imm.substring(10);
						}
						full_ins+=imm;
					}

					break;
				}
				case end:
				{
					full_ins+="000000000000000000000000000";
					break;
				}
			}
			int out = (int) Long.parseLong(full_ins, 2);
			bos.write(ByteBuffer.allocate(4).putInt(out).array());
		}


		//5. close the file
		bos.close();
	}
	
}
