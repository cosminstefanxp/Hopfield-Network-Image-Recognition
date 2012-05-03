/*
 * Invatare Automata 
 * Tema 3
 * 
 * Stefan-Dobrin Cosmin
 * 342C4
 */
package ml.t3;

/**
 * The Class Perceptron.
 */
public class SimpleNeuron {

	/** The w. */
	public double w[];

	/** The theta. */
	public double theta;

	/**
	 * Instantiates a new simple neuron.
	 * 
	 * @param inputSize the input size
	 * @param theta the theta
	 */
	public SimpleNeuron(int inputSize, double theta) {
		super();
		w = new double[inputSize];
		this.theta = theta;
	}

	/**
	 * Instantiates a new simple neuron.
	 * 
	 * @param w the w values
	 * @param theta the theta
	 */
	public SimpleNeuron(double[] w, double theta) {
		super();
		this.w = w;
		this.theta = theta;
	}

	/**
	 * Process data.
	 * 
	 * @param input the input
	 * @return the output of the neuron
	 */
	public char processData(char[] input) {
		double s = 0;

		assert (input.length == w.length);
		for (int i = 0; i < w.length; i++)
			s += w[i] * input[i];

		if (s > theta)
			return 1;
		else
			return 0;

	}

}
