package processor.pipeline;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Statistics;
import processor.Processor;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OperandFetch {
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	public OperandFetch(Processor containingProcessor,
						IF_OF_LatchType iF_OF_Latch,
						OF_EX_LatchType oF_EX_Latch,
						EX_MA_LatchType eX_MA_Latch,
						MA_RW_LatchType mA_RW_Latch,
						IF_EnableLatchType if_enableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch=if_enableLatch;
	}

	public static char opposite(char c)
	{
		return (c == '0') ? '1' : '0';
	}
	public boolean CheckConflict(Instruction inst,Instruction toCheck){
		if(toCheck!=null &&
				!toCheck.getOperationType().toString().equals("nop")
				&& !toCheck.getOperationType().toString().equals("end")
				&& !inst.getOperationType().toString().equals("nop")
				&& !inst.getOperationType().toString().equals("end")
				&& !inst.getOperationType().toString().equals("jmp")
				&& toCheck.getDestinationOperand()!=null
				//&& inst.getSourceOperand1()!=null
				//&& inst.getSourceOperand2()!=null
				&& ((toCheck.getDestinationOperand().getOperandType() == OperandType.Register && inst.getSourceOperand1().getOperandType() == OperandType.Register && toCheck.getDestinationOperand().getValue() == inst.getSourceOperand1().getValue()) ||
				(toCheck.getDestinationOperand().getOperandType() == OperandType.Register && inst.getSourceOperand2().getOperandType() == OperandType.Register && toCheck.getDestinationOperand().getValue() == inst.getSourceOperand2().getValue()))
				){
			///System.out.println("!!!");
			return true;
		}
		else if(toCheck!=null &&
				!toCheck.getOperationType().toString().equals("nop")
				&& !toCheck.getOperationType().toString().equals("end")
				&& !inst.getOperationType().toString().equals("nop")
				&& !inst.getOperationType().toString().equals("end")
				&& !inst.getOperationType().toString().equals("jmp")
				&& toCheck.getDestinationOperand()!=null
				//&& inst.getSourceOperand1()!=null
				//&& inst.getSourceOperand2()!=null
				&& (toCheck.getDestinationOperand().getValue()== inst.getSourceOperand1().getValue() ||
				toCheck.getDestinationOperand().getValue()== inst.getSourceOperand2().getValue()) &&
				(inst.getOperationType().toString().equals("beq") ||
				inst.getOperationType().toString().equals("bne") ||
				inst.getOperationType().toString().equals("blt")  ||
				inst.getOperationType().toString().equals("bgt"))
		){
//			System.out.println("KARANA");
			return true;
		}
		else {
			return false;
		}

	}
	public static String twosComplement(String binary)
	{
		String twos = "", ones = "";
		for (int i = 0; i < binary.length(); i++)
		{
			ones += opposite(binary.charAt(i));
		}

		StringBuilder builder = new StringBuilder(ones);
		boolean flag = false;
		for (int i = ones.length() - 1; i > 0; i--)
		{
			if (ones.charAt(i) == '1')
			{
				builder.setCharAt(i, '0');
			}
			else
			{
				builder.setCharAt(i, '1');
				flag = true;
				break;
			}
		}
		if (!flag)
		{
			builder.append("1", 0, 7);
		}
		twos = builder.toString();
		return twos;
	}

	public void performOF()
	{
		int inst=999999999;
		try {
			inst = IF_OF_Latch.getInstruction();

		}
		catch (Exception e){
			e.printStackTrace();
		}

		if(inst!=999999999)
		{
			OperationType[] operationType = OperationType.values();
			String instb = Integer.toBinaryString(inst);
			if(instb.length()!=32){
				int limm = instb.length();
				String lRepeated = "";
				if ((32 - limm) != 0) {
					String s = "0";
					int q = 32 - limm;
					lRepeated = IntStream.range(0, q).mapToObj(i -> s).collect(Collectors.joining(""));
				}
				instb = lRepeated+instb;
			}
			String opcode = instb.substring(0, 5);
			int opcodei = Integer.parseInt(opcode, 2);
			OperationType operation = operationType[opcodei];
			Instruction instn = new Instruction();
			Operand rs1;
			int reg_no;
			Operand rs2;
			Operand rd;
			String cons;
			int cons_val;
			if(operation.toString().equals("add")
					|| operation.toString().equals("sub")
					|| operation.toString().equals("mul")
					|| operation.toString().equals("div")
					|| operation.toString().equals("and")
					|| operation.toString().equals("or")
					|| operation.toString().equals("xor")
					|| operation.toString().equals("slt")
					|| operation.toString().equals("sll")
					|| operation.toString().equals("srl")
					|| operation.toString().equals("sra") ){
				rs1 = new Operand();
				rs1.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(5, 10), 2);
				rs1.setValue(reg_no);

				rs2 = new Operand();
				rs2.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(10, 15), 2);
				rs2.setValue(reg_no);

				rd = new Operand();
				rd.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(15, 20), 2);
				rd.setValue(reg_no);

				instn.setOperationType(operationType[opcodei]);
				instn.setSourceOperand1(rs1);
				instn.setSourceOperand2(rs2);
				instn.setDestinationOperand(rd);
			}
			else if(operation.toString().equals("end")){
				instn.setOperationType(operationType[opcodei]);
			}
			else if(operation.toString().equals("jmp")){
				Operand op = new Operand();
				cons = instb.substring(10, 32);
				cons_val = Integer.parseInt(cons, 2);
				if (cons.charAt(0) == '1')
				{
					cons = twosComplement(cons);
					cons_val = Integer.parseInt(cons, 2) * -1;
				}
				if (cons_val != 0)
				{
					op.setOperandType(OperandType.Immediate);
					op.setValue(cons_val);
				}
				else
				{
					reg_no = Integer.parseInt(instb.substring(5, 10), 2);
					op.setOperandType(OperandType.Register);
					op.setValue(reg_no);
				}

				instn.setOperationType(operationType[opcodei]);
				instn.setDestinationOperand(op);
				OperandType OPERNDTYPE = instn.getDestinationOperand().getOperandType();
				int immediate = 0;
				if (OPERNDTYPE == OperandType.Register)
				{
					immediate = containingProcessor.getRegisterFile().getValue(
							instn.getDestinationOperand().getValue());
				}
				else
				{
					immediate = instn.getDestinationOperand().getValue();
				}
				int cPC=containingProcessor.getRegisterFile().programCounter -1;
				int output = immediate + cPC;
				containingProcessor.getRegisterFile().setProgramCounter(output);
			}
			else if(operation.toString().equals("beq")
					|| operation.toString().equals("bne")
					|| operation.toString().equals("blt")
					|| operation.toString().equals("bgt")){
				rs1 = new Operand();
				rs1.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(5, 10), 2);
				rs1.setValue(reg_no);

				// destination register
				rs2 = new Operand();
				rs2.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(10, 15), 2);
				rs2.setValue(reg_no);

				// Immediate value
				rd = new Operand();
				rd.setOperandType(OperandType.Immediate);
				cons = instb.substring(15, 32);
				cons_val = Integer.parseInt(cons, 2);
				if (cons.charAt(0) == '1')
				{
					cons = twosComplement(cons);
					cons_val = Integer.parseInt(cons, 2) * -1;
				}
				rd.setValue(cons_val);

				instn.setOperationType(operationType[opcodei]);
				instn.setSourceOperand1(rs1);
				instn.setSourceOperand2(rs2);
				instn.setDestinationOperand(rd);
			}
			else {
				// Source register 1
				rs1 = new Operand();
				rs1.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(5, 10), 2);
				rs1.setValue(reg_no);

				// Destination register
				rd = new Operand();
				rd.setOperandType(OperandType.Register);
				reg_no = Integer.parseInt(instb.substring(10, 15), 2);
				rd.setValue(reg_no);

				// Immediate values
				rs2 = new Operand();
				rs2.setOperandType(OperandType.Immediate);
				cons = instb.substring(15, 32);
				cons_val = Integer.parseInt(cons, 2);
				if (cons.charAt(0) == '1')
				{
					cons = twosComplement(cons);
					cons_val = Integer.parseInt(cons, 2) * -1;
				}
				rs2.setValue(cons_val);

				instn.setOperationType(operationType[opcodei]);
				instn.setSourceOperand1(rs1);
				instn.setSourceOperand2(rs2);
				instn.setDestinationOperand(rd);
			}
			//System.out.println(instn.getOperationType()+" "+instn.getSourceOperand1()+" "+instn.getSourceOperand2()+" "+instn.getDestinationOperand());

			Instruction nop = new Instruction();
			nop.setOperationType(OperationType.nop);
			//String instqb = Integer.toBinaryString(nop);
			//System.out.println("Here is Operands "+instn.getSourceOperand1().getValue()+" "+instn.getSourceOperand2().getValue());
			//EX_MA_LatchType EX_MA_Latch;
			boolean notConflict=false;
			//System.out.println(CheckConflict(instn, OF_EX_Latch.instruction));
			//System.out.println(CheckConflict(instn,EX_MA_Latch.instruction));
			//System.out.println(CheckConflict(instn,MA_RW_Latch.instruction));
			//int tk=0;
//			int freeze = 0;
			while(true){

				//tk=tk+1;
				//System.out.println("LOOP "+tk);
				//System.out.println(EX_MA_Latch.getFlag());
				if(EX_MA_Latch.getFlag()==1){
					OF_EX_Latch.setInstruction(nop);
					//System.out.println("WRONG BRANCH DISCARDING "+instn.getOperationType()+" "+instn.getSourceOperand1());
					containingProcessor.getRegisterFile().setProgramCounter(containingProcessor.getRegisterFile().getProgramCounter()-1);
					containingProcessor.setWrong_input(containingProcessor.getWrong_input()+1);
					EX_MA_Latch.setFlag(0);
					Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() - 1);
					//System.out.println("@");
					break;
				}
				else if(CheckConflict(instn, OF_EX_Latch.instruction)
						&& OF_EX_Latch.instruction.getOperationType() != OperationType.nop){
					//System.out.println("CONFLICT1 -- "+instn.getOperationType()+" and "+OF_EX_Latch.instruction.getOperationType());
					IF_EnableLatch.setIF_enable(false);
					OF_EX_Latch.setInstruction(nop);
					//containingProcessor.getRegisterFile().setProgramCounter(containingProcessor.getRegisterFile().getProgramCounter()-1);
					containingProcessor.setStalls(containingProcessor.getStalls()+1);
					Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() - 1);
					break;
				}
				else if(CheckConflict(instn,EX_MA_Latch.instruction)
						&& EX_MA_Latch.instruction.getOperationType() != OperationType.nop){
					//System.out.println("CONFLICT2 -- "+instn.getOperationType()+" and "+EX_MA_Latch.instruction.getOperationType());
					IF_EnableLatch.setIF_enable(false);
					OF_EX_Latch.setInstruction(nop);
					//containingProcessor.getRegisterFile().setProgramCounter(containingProcessor.getRegisterFile().getProgramCounter()-1);
					containingProcessor.setStalls(containingProcessor.getStalls()+1);
					Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() - 1);
					//System.out.println("$");
					break;
				}
				else if(CheckConflict(instn,MA_RW_Latch.instruction)
						&& MA_RW_Latch.instruction.getOperationType() != OperationType.nop){
					//System.out.println("CONFLICT3 -- "+instn.getOperationType()+" and "+MA_RW_Latch.instruction.getOperationType());
					IF_EnableLatch.setIF_enable(false);
					OF_EX_Latch.setInstruction(nop);
					//containingProcessor.getRegisterFile().setProgramCounter(containingProcessor.getRegisterFile().getProgramCounter()-1);
					containingProcessor.setStalls(containingProcessor.getStalls()+1);
					Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() - 1);
					//System.out.println("^");
					break;
				}
				else if(instn.getOperationType()==OperationType.end){
					containingProcessor.getRegisterFile().setFreezed(true);
					containingProcessor.getRegisterFile().setFreezedprogramCounter(containingProcessor.getRegisterFile().getProgramCounter());
					containingProcessor.setFreezed_stalls(containingProcessor.getStalls());
					containingProcessor.setFreezed_wrong_input(containingProcessor.getWrong_input());
					Statistics.setFreezednumberOfInstructions(Statistics.getNumberOfInstructions());
				}
				notConflict = true;
				break;

			}
			if(notConflict && IF_OF_Latch.isOF_enable()){
				//System.out.println("No conglict");
				IF_EnableLatch.setIF_enable(true);

				OF_EX_Latch.setInstruction(instn);
				//IF_OF_Latch.setOF_enable(false);
				//OF_EX_Latch.setEX_enable(true);
			}
		}
	}

}
