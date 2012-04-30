package ml.t3;

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
	private Perceptron outputLayer[]=new Perceptron[14];
	
	/**
	 * Inits the output layer.
	 */
	private void initOutputLayer(){
		this.outputLayer=new Perceptron[14];
		
		for(int i=0;i<14;i++)
			outputLayer[i]=new Perceptron(SymbolsVals.vals[i], SymbolsVals.thetas[i]);
	}
	
	/**
	 * Instantiates a new neural network.
	 */
	public NeuralNetwork() {
		super();
		initOutputLayer();
	}
	
	/**
	 * Converts a given Optical Symbol to a Display Symbol. The processing done by the output layer.
	 *
	 * @param symb the symbol
	 * @return the display symbol
	 */
	public DisplaySymbol convertToDisplaySymbol(OpticalSymbol symb)
	{
		DisplaySymbol s=new DisplaySymbol();
		
		for(int i=0;i<14;i++)
			s.values[i]=outputLayer[i].processData(symb.data);
		
		return s;		
	}
	
	
}
