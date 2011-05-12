package de.binfalse.martin.iso2l.objects;

import java.util.Vector;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class Atom
{
	private String name;
	private String symbol;
	private Vector<Isotope> isotopes;
	
	public Atom (String name, String symbol)
	{
		this.name = name;
		this.symbol = symbol;
		isotopes = new Vector<Isotope> ();
	}
	
	public String getName ()
	{
		return name;
	}
	
	public String getSymbol ()
	{
		return symbol;
	}
	
	public void addIsotope (Isotope i)
	{
		isotopes.add (i);
	}
	
	public Vector<Isotope> getIsotopes ()
	{
		return isotopes;
	}
}
