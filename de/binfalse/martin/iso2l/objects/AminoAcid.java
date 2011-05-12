package de.binfalse.martin.iso2l.objects;

/**
 * The Class for AminoAcid.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class AminoAcid
{
	
	/** The name of this aa. */
	public String	name;
	
	/** The three letter code. */
	public String	threeLetter;
	
	/** The one letter code. */
	public String	oneLetter;
	
	/** The formula. */
	public String	formula;
	
	
	/**
	 * Instantiates a new amino acid.
	 * 
	 * @param name
	 *          the name
	 * @param threeLetter
	 *          the three letter code
	 * @param oneLetter
	 *          the one letter code
	 * @param formula
	 *          the formula
	 */
	public AminoAcid (String name, String threeLetter, String oneLetter,
			String formula)
	{
		this.oneLetter = oneLetter;
		this.threeLetter = threeLetter;
		this.name = name;
		this.formula = formula;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return name + " [" + threeLetter + "-" + oneLetter + "-" + formula + "]";
	}
}
