package de.binfalse.martin.iso2l.objects;

import java.util.Vector;



/**
 * The Class Atom.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class Atom
{
	
	/** The name. */
	private String					name;
	
	/** The symbol. */
	private String					symbol;
	
	/** The vec of isotopes. */
	private Vector<Isotope>	isotopes;
	
	
	/**
	 * Instantiates a new atom.
	 * 
	 * @param name
	 *          the name
	 * @param symbol
	 *          the symbol
	 */
	public Atom (String name, String symbol)
	{
		this.name = name;
		this.symbol = symbol;
		isotopes = new Vector<Isotope> ();
	}
	

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	public String getName ()
	{
		return name;
	}
	

	/**
	 * Get the symbol.
	 * 
	 * @return the symbol
	 */
	public String getSymbol ()
	{
		return symbol;
	}
	

	/**
	 * Adds an isotope.
	 * 
	 * @param i
	 *          the isotope
	 */
	public void addIsotope (Isotope i)
	{
		isotopes.add (i);
	}
	

	/**
	 * Get the isotopes.
	 * 
	 * @return the vector of isotopes
	 */
	public Vector<Isotope> getIsotopes ()
	{
		return isotopes;
	}
}
