package ml.t3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Level;
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

	/** The output layer. */
	private Perceptron outputLayer[] = new Perceptron[14];

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(NeuralNetwork.class);

	/** The Constant T_SIZE. */
	public static final int T_SIZE = OpticalSymbol.SYMBOL_HEIGHT * OpticalSymbol.SYMBOL_WIDTH;

	/** The neuron weights. */
	double w[][] = new double[T_SIZE][T_SIZE];

	/**
	 * Inits the output layer.
	 */
	private void initOutputLayer() {
		this.outputLayer = new Perceptron[14];

		for (int i = 0; i < 14; i++)
			outputLayer[i] = new Perceptron(SymbolsVals.vals[i], SymbolsVals.thetas[i]);
		log.info("Neural network output layer init complete!");
	}

	/**
	 * Instantiates a new neural network.
	 */
	public NeuralNetwork() {
		super();
		initOutputLayer();
		log.setLevel(Level.DEBUG);
	}

	/**
	 * Converts a given Optical Symbol to a Display Symbol. The processing done by the output layer.
	 * 
	 * @param symb the symbol
	 * @return the display symbol
	 */
	public DisplaySymbol convertToDisplaySymbol(OpticalSymbol symb) {
		DisplaySymbol s = new DisplaySymbol();

		for (int i = 0; i < 14; i++)
			s.values[i] = outputLayer[i].processData(symb.data);

		return s;
	}

	/**
	 * Trains the Hopfield Network using a given OpticalSymbol Set - the alphabet.
	 * 
	 * @param trainData the train data
	 */
	public void trainNetwork(OpticalSymbol trainData[]) {
		log.info("Training Hopfield Network on the alphabet...");
		for (int i = 0; i < T_SIZE; i++)
			for (int j = 0; j < T_SIZE; j++) {

				if (i != j) {
					w[i][j] = 0.0d;
					for (int s = 0; s < trainData.length; s++)
						w[i][j] += trainData[s].data[i] * trainData[s].data[j];
					w[i][j] /= T_SIZE;
				} else
					w[i][j] = 0.0d;
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
	public OpticalSymbol classifySymbol(OpticalSymbol input) {
		int oldValue = 0;
		int neuron;
		// Modifiable neurons - neurons which are not yet stable in the current epoch
		ArrayList<Integer> unstabilizedNeurons = new ArrayList<Integer>();
		// Initialize the unstable neurons
		unstabilizedNeurons.clear();
		for (int i = 0; i < T_SIZE; i++)
			unstabilizedNeurons.add(i);
		//Make a copy of the neuron
		OpticalSymbol x=new OpticalSymbol();
		x.data=Arrays.copyOf(input.data, input.data.length);
		
		log.info("Classifying input symbol using Hopfield Network...");
		log.debug("Input symbol: "+x);
		
		// Until the network has stabilized
		while (!unstabilizedNeurons.isEmpty()) {
			// Pick an unstabilized neuron
			neuron = unstabilizedNeurons.get(new Random().nextInt(unstabilizedNeurons.size()));
			oldValue = x.data[neuron];

			// Compute the new neuron output
			double val = 0;
			for (int j = 0; j < T_SIZE; j++)
				val += w[neuron][j] * x.data[j];
			if (val > 0)
				x.data[neuron] = 1;
			else
				x.data[neuron] = 0;

			// If the value is not modified, choose another neuron
			if (x.data[neuron] == oldValue)
				unstabilizedNeurons.remove((Integer) neuron);
			else {
				unstabilizedNeurons.clear();
				for (int i = 0; i < T_SIZE; i++)
					unstabilizedNeurons.add(i);
			}
		}
		log.info("Classification complete.");
		log.debug("Classified as: "+x);
		
		return x;
	}

}
