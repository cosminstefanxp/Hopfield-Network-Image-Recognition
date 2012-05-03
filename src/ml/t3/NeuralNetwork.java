/*
 * Invatare Automata 
 * Tema 3
 * 
 * Stefan-Dobrin Cosmin
 * 342C4
 */
package ml.t3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * A NeuralNetwork that is composed of a Hopfield Neural Network and an output Layer.<br/>
 * The Hopfield Network takes Optical Symbols of SYMBOL_HEIGHT x SYMBOL_WIDTH characters and outputs
 * the original, non-altered and non-noisy Optical Symbol of SYMBOL_HEIGHT x SYMBOL_WIDTH.<br/>
 * The output layer takes an original non-altered and non-noisy Optical character of size
 * SYMBOL_HEIGHT x SYMBOL_WIDTH and outputs 14 values, corresponding to the 14 display positions on
 * the LCD. The order of display elements it like this:<br/>
 * 
 * <pre>
 *   1
 * 23456
 *  7 8
 * 9ABCD
 *   E
 * </pre>
 */
public class NeuralNetwork {

	private static final double GAP_VALUE = 100;

	/** The output layer. */
	private SimpleNeuron outputLayer[] = new SimpleNeuron[14];

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(NeuralNetwork.class);

	/** The Constant T_SIZE. */
	public static final int NEURON_COUNT = OpticalSymbol.SYMBOL_HEIGHT * OpticalSymbol.SYMBOL_WIDTH;

	/**
	 * The neuron weights. w[i][j] - the weight for the i'th neuron and its j'th input ~ the input
	 * from the jth neuron to the ith neuron.
	 */
	double w[][] = new double[NEURON_COUNT][NEURON_COUNT];

	/**
	 * Inits the output layer.
	 */
	private void initOutputLayer() {
		this.outputLayer = new SimpleNeuron[14];

		for (int i = 0; i < 14; i++)
			outputLayer[i] = new SimpleNeuron(SymbolsVals.vals[i], SymbolsVals.thetas[i]);
		log.info("Neural network output layer init complete!");
	}

	/**
	 * Instantiates a new neural network.
	 */
	public NeuralNetwork() {
		super();
		initOutputLayer();
		// log.setLevel(Level.DEBUG);
	}

	/**
	 * Gets the neuron output.
	 * 
	 * @param n the n
	 * @param x the x
	 * @return the neuron output
	 */
	private double getNeuronOutput(int n, OpticalSymbol x) {
		// Compute the new neuron output
		double val = 0;
		for (int j = 0; j < NEURON_COUNT; j++)
			val += w[n][j] * x.data[j];
		return val;
	}

	/**
	 * Converts a given Optical Symbol to a Display Symbol. The processing done by the output layer.
	 * 
	 * @param symb the symbol
	 * @return the display symbol
	 */
	private DisplaySymbol convertToDisplaySymbol(OpticalSymbol symb) {
		DisplaySymbol s = new DisplaySymbol();

		for (int i = 0; i < 14; i++)
			s.values[i] = outputLayer[i].processData(symb.data);

		return s;
	}

	/**
	 * Trains the Hopfield Network using a given OpticalSymbol Set - the alphabet. The algorithm
	 * uses Widrow-Hoff rule for learning. It takes every input template and every neuron and
	 * updates its weights so that the output is the proper one. <br/>
	 * It only updates the weights if there are inputs for the neurons and tries to make a gap
	 * between the "classes", not comparing to 0, but to a different value.
	 * 
	 * @see http://homepages.gold.ac.uk/nikolaev/311htn.htm
	 * @param trainData the train data
	 */
	public void trainNetwork(OpticalSymbol trainData[]) {
		log.info("Training Hopfield Network on the alphabet...");

		// Initialize the weights
		Random rand = new Random();
		for (int i = 0; i < NEURON_COUNT; i++)
			for (int j = 0; j < NEURON_COUNT; j++)
				w[i][j] = 0.45 + rand.nextFloat() / 10;

		int epoch = 1;
		// Repeat until convergence
		boolean allDone = false;
		while (!allDone) {
			allDone = true;
			log.info("Epoch " + epoch++);

			// For every input example
			for (int ind = 0; ind < trainData.length; ind++) {
				log.debug("Processing training example " + ind);

				OpticalSymbol in = trainData[ind];

				// Compute number of non-null pixels
				int nonNullPixels = 0;
				for (int i = 0; i < in.data.length; i++)
					if (in.data[i] == 1)
						nonNullPixels++;

				// For every neuron
				for (int i = 0; i < NEURON_COUNT; i++) {
					// Compute the output value
					double val = 0;
					for (int j = 0; j < NEURON_COUNT; j++)
						val += w[i][j] * in.data[j];

					// If the ouput of the neuron is not ok, adjust the weights
					// and force a bigger span between "classes"
					Double variation = null;
					if (val >= -GAP_VALUE && in.data[i] == 0)
						variation = -(GAP_VALUE + 0.1 + val) / (double) nonNullPixels;
					if (val < GAP_VALUE && in.data[i] == 1)
						variation = (GAP_VALUE + 0.1 - val) / (double) nonNullPixels;

					if (variation != null) {
						allDone = false;
						for (int j1 = 0; j1 < NEURON_COUNT; j1++)
							if (in.data[j1] == 1)
								w[i][j1] += variation;
					}

				}

			}

		}
		log.info("Training complete.");
	}

	/**
	 * Classifies an Optical Symbol and converts it to one of the original symbols from the
	 * alphabet. This is the Hopfield Network classification.
	 * 
	 * @param input the input
	 * @return the optical symbol that the input resembles the most, from the alphabet
	 */
	private OpticalSymbol classifySymbol(OpticalSymbol input) {
		int oldValue = 0;
		int neuron;
		// Modifiable neurons - neurons which are not yet stable in the current epoch
		ArrayList<Integer> unstabilizedNeurons = new ArrayList<Integer>();
		// Initialize the unstable neurons
		unstabilizedNeurons.clear();
		for (int i = 0; i < NEURON_COUNT; i++)
			unstabilizedNeurons.add(i);
		// Make a copy of the neuron
		OpticalSymbol x = new OpticalSymbol();
		x.data = Arrays.copyOf(input.data, input.data.length);

		log.info("Classifying input symbol using Hopfield Network...");
		log.debug("Input symbol: " + x);

		// Until the network has stabilized
		while (!unstabilizedNeurons.isEmpty()) {
			// Pick an unstabilized neuron
			neuron = unstabilizedNeurons.get(new Random().nextInt(unstabilizedNeurons.size()));
			oldValue = x.data[neuron];

			// Compute the new neuron output
			double val = getNeuronOutput(neuron, x);
			if (val > 0)
				x.data[neuron] = 1;
			else
				x.data[neuron] = 0;

			// If the value is not modified, choose another neuron
			if (x.data[neuron] == oldValue)
				unstabilizedNeurons.remove((Integer) neuron);
			else {
				unstabilizedNeurons.clear();
				for (int i = 0; i < NEURON_COUNT; i++)
					unstabilizedNeurons.add(i);
			}
		}
		log.info("Classification complete.");
		log.debug("Classified as: " + x);

		return x;
	}
	
	public DisplaySymbol processSymbol(OpticalSymbol symbol)
	{
		//The Hopfield Network Processing
		OpticalSymbol classified=classifySymbol(symbol);
		//The output layer processing
		return convertToDisplaySymbol(classified);
	}

}
