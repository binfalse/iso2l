/*
 * iso2l - calculate the theoretical isotopic distribution of a compound
 * Copyright (C) 2011 Martin Scharm
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
