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
