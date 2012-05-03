/*
 * Invatare Automata 
 * Tema 3
 * 
 * Stefan-Dobrin Cosmin
 * 342C4
 */
package ml.t3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Main {

	private static int SYMBOLS_PER_WORD;
	private static int SYMBOL_COUNT;

	private static final Logger log = Logger.getLogger(Main.class);

	public static OpticalSymbol[] readInputFile(String filename) throws FileNotFoundException {
		log.info("Reading input file: " + filename+" ...");
		Scanner in = new Scanner(new File(filename));

		OpticalSymbol[] symbols = new OpticalSymbol[SYMBOLS_PER_WORD];
		// Read data
		for (int s = 0; s < SYMBOLS_PER_WORD; s++) {
			symbols[s] = new OpticalSymbol();
		}

		// Read a new line
		for (int i = 0; i < OpticalSymbol.SYMBOL_HEIGHT; i++) {
			String line = in.nextLine();
			log.debug(line);
			// Read a new chunk of symbol
			for (int s = 0; s < SYMBOLS_PER_WORD; s++)
				for (int j = 0; j < OpticalSymbol.SYMBOL_WIDTH; j++)
					if (line.charAt(s * OpticalSymbol.SYMBOL_WIDTH + j) == '1')
						symbols[s].data[i * OpticalSymbol.SYMBOL_WIDTH + j] = 1;
					else if (line.charAt(s * OpticalSymbol.SYMBOL_WIDTH + j) == '0')
						symbols[s].data[i * OpticalSymbol.SYMBOL_WIDTH + j] = 0;
					else {
						log.fatal("Illegal character in input data: " + line);
						symbols[s].data[i * OpticalSymbol.SYMBOL_WIDTH + j] = 2;
					}
		}

		log.debug("Symbols: " + Arrays.toString(symbols));
		log.info("Input data read complete!");

		return symbols;
	}

	public static OpticalSymbol[] readConfigFile(String filename) throws FileNotFoundException {
		log.info("Reading config file: " + filename+" ...");
		Scanner in = new Scanner(new File(filename));
		SYMBOLS_PER_WORD = in.nextInt();
		SYMBOL_COUNT = in.nextInt();
		log.debug("Config data: [" + SYMBOLS_PER_WORD + " symbols/word] & [" + SYMBOL_COUNT + " symbols].");

		OpticalSymbol[] symbols = new OpticalSymbol[SYMBOL_COUNT];
		// Read data
		in.nextLine();
		for (int s = 0; s < SYMBOL_COUNT; s++) {
			OpticalSymbol sy = new OpticalSymbol();

			// Read a new symbol
			in.nextLine();
			for (int i = 0; i < OpticalSymbol.SYMBOL_HEIGHT; i++) {
				// Read a new line
				String line = in.nextLine();
				for (int j = 0; j < OpticalSymbol.SYMBOL_WIDTH; j++)
					if (line.charAt(j) == '1')
						sy.data[i * OpticalSymbol.SYMBOL_WIDTH + j] = 1;
					else if (line.charAt(j) == '0')
						sy.data[i * OpticalSymbol.SYMBOL_WIDTH + j] = 0;
					else {
						log.fatal("Illegal character in input data: " + line);
						sy.data[i * OpticalSymbol.SYMBOL_WIDTH + j] = 2;
					}
			}

			symbols[s] = sy;
		}

		log.debug("Symbols: " + Arrays.toString(symbols));
		log.info("Configuration data read complete!");

		return symbols;
	}

	/**
	 * Configure logger.
	 */
	private static void configureLogger() {
		PatternLayout patternLayout = new PatternLayout("%-3r [%-5p] %c - %m%n");
		ConsoleAppender appender = new ConsoleAppender(patternLayout);
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.ERROR);
	}

	public static void main(String args[]) throws IOException {
		configureLogger();
		OpticalSymbol input[];
		OpticalSymbol alphabet[];

		if (args.length > 2) {
			log.fatal("Illegal number of parameters: <executable> <input> <config>");
			return;
		}

		// Read the configuration file
		if (args.length >= 2) {
			alphabet=Main.readConfigFile(args[1]);
		} else {
			alphabet=Main.readConfigFile("config.txt");
		}

		// Read the input file
		if (args.length >= 1) {
			input=Main.readInputFile(args[0]);
		} else {
			input=Main.readInputFile("intrare.txt");
		}
		
		//Train the neural network
		NeuralNetwork net=new NeuralNetwork();		
		net.trainNetwork(alphabet);
		
		//Use the network	
		DisplaySymbol ds[]=new DisplaySymbol[SYMBOLS_PER_WORD];
		String outS="";
		for(int i=0;i<SYMBOLS_PER_WORD;i++)
		{
			ds[i]=net.processSymbol(input[i]);
			outS+=ds[i].toString();
		}
		
		//Write the results to console
		System.out.println(outS);
		for(int i=0;i<SYMBOLS_PER_WORD;i++)
			System.out.println(ds[i].printSymbol());
		
		//Write the results to file
		BufferedWriter out=new BufferedWriter(new FileWriter("iesire.txt"));
		out.write(outS);
		out.close();
		
		
	}
}
