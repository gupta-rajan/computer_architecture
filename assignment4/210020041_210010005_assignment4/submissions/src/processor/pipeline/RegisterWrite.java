package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;

	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType ifOfLatchType)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = ifOfLatchType;
	}

	public void performRW()
	{
		Instruction cmd = null;
		try{
			cmd = MA_RW_Latch.getInstruction();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		if(cmd!=null)
		{

			int alu_output = MA_RW_Latch.getALU_Output();
			OperationType operationType = cmd.getOperationType();

			switch(operationType)
			{
				case store:
				case jmp:
				case beq:
				case bne:
				case blt:
				case bgt:
				case nop:
					break;
				case load:
					int load_output = MA_RW_Latch.getLoad_Output();
					int DOP = cmd.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(DOP, load_output);
					break;
				case end:
					containingProcessor.getRegisterFile().setProgramCounter(containingProcessor.getRegisterFile().getFreezedprogramCounter());
					Simulator.setSimulationComplete(true);
					break;
				default:
					DOP = cmd.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(DOP, alu_output);
					break;
			}

			//MA_RW_Latch.setRW_enable(false);
			//IF_EnableLatch.setIF_enable(true);
		}
	}

}
