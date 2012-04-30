package ml.t3;

/**
 * A DisplaySymbol that contains 14 values, corresponding to the 14 display positions on
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
public class DisplaySymbol {

	/** The values. */
	public char[] values;

	/**
	 * Instantiates a new display symbol.
	 */
	public DisplaySymbol() {
		super();
		this.values = new char[14];
	}

	/**
	 * Prints the symbol.
	 *
	 * @return the string
	 */
	public String printSymbol() {
		StringBuffer out = new StringBuffer();

		// Line 1
		if (values[0] == 1)
			out.append(" --- ");
		else
			out.append("     ");
		out.append("\n");

		// Line 2
		if (values[1] == 1)
			out.append("|");
		else
			out.append(" ");

		if (values[2] == 1)
			out.append("\\");
		else
			out.append(" ");

		if (values[3] == 1)
			out.append("|");
		else
			out.append(" ");

		if (values[4] == 1)
			out.append("/");
		else
			out.append(" ");

		if (values[5] == 1)
			out.append("|");
		else
			out.append(" ");
		out.append("\n");

		// Line 3
		if(values[6]==1)
			out.append(" -");
		else
			out.append("  ");
		if(values[7]==1)
			out.append(" - ");
		else
			out.append("   ");
		out.append("\n");
		
		//Line 4
		if(values[8]==1)
			out.append("|");
		else
			out.append(" ");
		if(values[9]==1)
			out.append("/");
		else
			out.append(" ");
		if(values[10]==1)
			out.append("|");
		else
			out.append(" ");
		if(values[11]==1)
			out.append("\\");
		else
			out.append(" ");
		if(values[12]==1)
			out.append("|");
		else
			out.append(" ");
		out.append("\n");
		
		//Line 5
		if(values[13]==1)
			out.append(" --- ");
		else
			out.append("     ");
		

		return out.toString();
	}

}
