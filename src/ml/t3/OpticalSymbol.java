package ml.t3;


public class OpticalSymbol {

	public static final int SYMBOL_HEIGHT = 30;
	public static final int SYMBOL_WIDTH = 20;
	public char[][] data;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf=new StringBuffer();
		buf.append("\n");
		for(int i=0;i<SYMBOL_HEIGHT;i++)
		{
			for(int j=0;j<SYMBOL_WIDTH;j++)
				buf.append(data[i][j]);
			buf.append("\n");
		}
		return "Symbol ["+buf.toString()+"]";
	}

	public OpticalSymbol() {
		super();
		this.data = new char[SYMBOL_HEIGHT][SYMBOL_WIDTH];
	}

}
