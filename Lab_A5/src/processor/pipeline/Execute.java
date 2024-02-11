package processor.pipeline;

import configuration.Configuration;
import generic.*;
import generic.Instruction.OperationType;
import processor.Clock;
import processor.Processor;

import java.util.Arrays;

public class Execute implements Element {
    Processor containingProcessor;
    OF_EX_LatchType OF_EX_Latch;
    EX_MA_LatchType EX_MA_Latch;
    EX_IF_LatchType EX_IF_Latch;
    IF_OF_LatchType IF_OF_Latch;
    MA_RW_LatchType MA_RW_Latch;

    public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, MA_RW_LatchType mA_RW_Latch) {
        this.containingProcessor = containingProcessor;
        this.OF_EX_Latch = oF_EX_Latch;
        this.EX_MA_Latch = eX_MA_Latch;
        this.EX_IF_Latch = eX_IF_Latch;
        this.IF_OF_Latch = iF_OF_Latch;
        this.MA_RW_Latch = mA_RW_Latch;
    }

    public void performEX() {
        //System.out.println("YessE");
        //System.out.println(EX_MA_Latch.isMA_busy());
        if (EX_MA_Latch.isMA_busy()) {
            return;
        } else if (OF_EX_Latch.isEX_enable()) {
            if (OF_EX_Latch.isEX_busy()) {
                //System.out.println(" is in EX Busy Stage");
                EX_MA_Latch.setMA_enable(false);
                return;
            }
            Instruction cmd = OF_EX_Latch.getInstruction();
            EX_MA_Latch.setInstruction(cmd);
            OperationType cmd_op = cmd.getOperationType();
            int cmd_op_opcode = Arrays.asList(OperationType.values()).indexOf(cmd_op);
            int currentPC = containingProcessor.getRegisterFile().getProgramCounter() - 1;

            int ALU_output = 0;

            if (cmd_op_opcode < 21 && cmd_op_opcode % 2 == 0) {
                int SOP1 = containingProcessor.getRegisterFile().getValue(
                        cmd.getSourceOperand1().getValue());
                int SOP2 = containingProcessor.getRegisterFile().getValue(
                        cmd.getSourceOperand2().getValue());
                if (cmd_op.toString().equals("add")) {
                    ALU_output = SOP1 + SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("sub")) {
                    ALU_output = SOP1 - SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("mul")) {
                    ALU_output = SOP1 * SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.multiplier_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("div")) {
                    ALU_output = SOP1 / SOP2;
                    int r = SOP1 % SOP2;
                    containingProcessor.getRegisterFile().setValue(31, r);
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.divider_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("and")) {
                    ALU_output = SOP1 & SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("or")) {
                    ALU_output = SOP1 | SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("xor")) {
                    ALU_output = SOP1 ^ SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("slt")) {
                    if (SOP1 < SOP2)
                        ALU_output = 1;
                    else
                        ALU_output = 0;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("sll")) {
                    ALU_output = SOP1 << SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("srl")) {
                    ALU_output = SOP1 >>> SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("sra")) {
                    ALU_output = SOP1 >> SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                }
            } else if (cmd_op_opcode < 23) {
                int con = cmd.getSourceOperand1().getValue();
                int SOP1 = containingProcessor.getRegisterFile().getValue(con);
                int SOP2 = cmd.getSourceOperand2().getValue();

                if (cmd_op.toString().equals("addi")) {
                    ALU_output = SOP1 + SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("subi")) {
                    ALU_output = SOP1 - SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("muli")) {
                    ALU_output = SOP1 * SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.multiplier_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("divi")) {
                    ALU_output = SOP1 / SOP2;
                    int r = SOP1 % SOP2;
                    containingProcessor.getRegisterFile().setValue(31, r);
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.divider_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("andi")) {
                    ALU_output = SOP1 & SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("ori")) {
                    ALU_output = SOP1 | SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("xori")) {
                    ALU_output = SOP1 ^ SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("slti")) {
                    if (SOP1 < SOP2)
                        ALU_output = 1;
                    else
                        ALU_output = 0;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("slli")) {
                    ALU_output = SOP1 << SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("srli")) {
                    ALU_output = SOP1 >>> SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("srai")) {
                    ALU_output = SOP1 >> SOP2;
                    Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this, this));
                    OF_EX_Latch.setEX_busy(true);
                    EX_MA_Latch.setMA_enable(false);
                } else if (cmd_op.toString().equals("load")) {
                    ALU_output = SOP1 + SOP2;
                }
            } else if (cmd_op_opcode == 23) {
                int SOP1 = containingProcessor.getRegisterFile().getValue(cmd.getDestinationOperand().getValue());
                int SOP2 = cmd.getSourceOperand2().getValue();
                ALU_output = SOP1 + SOP2;
            } else if (cmd_op_opcode == 24) {
				/*OperandType OPERNDTYPE = cmd.getDestinationOperand().getOperandType();
				int immediate = 0;
				if (OPERNDTYPE == OperandType.Register)
				{
					immediate = containingProcessor.getRegisterFile().getValue(
							cmd.getDestinationOperand().getValue());
				}
				else
				{
					immediate = cmd.getDestinationOperand().getValue();
				}
				ALU_output = immediate + currentPC;
				EX_IF_Latch.setIS_enable(true, ALU_output);*/
                //DONE IN OF.
            } else if (cmd_op_opcode < 29) {
                //System.out.println(cmd_op);
                int SOP1 = containingProcessor.getRegisterFile().getValue(
                        cmd.getSourceOperand1().getValue());
                int SOP2 = containingProcessor.getRegisterFile().getValue(cmd.getSourceOperand2().getValue());
                int immediate = cmd.getDestinationOperand().getValue();

                if (cmd_op.toString().equals("beq")) {
                    //SOP2=1;
                    //System.out.println(cmd.getSourceOperand1());
                    //System.out.println(cmd.getSourceOperand2());
                    //System.out.println(cmd.);
                    if (SOP1 == SOP2) {
                        //System.out.println(SOP2);
                        ALU_output = immediate + currentPC;
                        //EX_IF_Latch.setIS_enable(true, ALU_output);
                        containingProcessor.getRegisterFile().setProgramCounter(ALU_output);
                        EX_MA_Latch.setFlag(1);
                    }
                } else if (cmd_op.toString().equals("bne")) {
                    if (SOP1 != SOP2) {
                        ALU_output = immediate + currentPC;
                        //EX_IF_Latch.setIS_enable(true, ALU_output);
                        containingProcessor.getRegisterFile().setProgramCounter(ALU_output);
                        EX_MA_Latch.setFlag(1);
                    }
                } else if (cmd_op.toString().equals("blt")) {
                    if (SOP1 < SOP2) {
                        ALU_output = immediate + currentPC;
                        //EX_IF_Latch.setIS_enable(true, ALU_output);
                        containingProcessor.getRegisterFile().setProgramCounter(ALU_output);
                        EX_MA_Latch.setFlag(1);
                    }
                } else if (cmd_op.toString().equals("bgt")) {
                    if (SOP1 > SOP2) {
                        ALU_output = immediate + currentPC;
                        //EX_IF_Latch.setIS_enable(true, ALU_output);
                        containingProcessor.getRegisterFile().setProgramCounter(ALU_output);
                        EX_MA_Latch.setFlag(1);
                    }
                }

            } else if (cmd_op.toString().equals("nop")) {
                //System.out.print(" bubble ");
            }
            EX_MA_Latch.setALU_result(ALU_output);
            if (!IF_OF_Latch.isOF_enable()) {
                OF_EX_Latch.setEX_enable(false);
            }
        }
    }

    @Override
    public void handleEvent(Event e) {
        //System.out.println(" Handling Execution Complete Event(ALU) in EX Stage");
        if (EX_MA_Latch.isMA_busy()) {
            e.setEventTime(Clock.getCurrentTime() + 1);
            Simulator.getEventQueue().addEvent(e);
        } else {
            //System.out.println(" is in ex");
            EX_MA_Latch.setMA_enable(true);
            OF_EX_Latch.setEX_busy(false);
        }
    }
}
