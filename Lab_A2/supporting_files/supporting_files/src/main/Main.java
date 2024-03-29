package main;
import configuration.Configuration;
import generic.Misc;
import generic.Statistics;
import generic.Simulator;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		if(args.length != 2)
		{
			Misc.printErrorAndExit("usage: java -jar <path-to-jar-file> <path-to-assembly-program> <path-to-object-file>\n");
		}

		Simulator.setupSimulation(args[0]);
		Simulator.assemble(args[1]);
	}

}
