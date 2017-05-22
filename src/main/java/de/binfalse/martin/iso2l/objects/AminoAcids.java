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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Vector;



/**
 * The container for AminoAcids.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class AminoAcids
{
	
	/** The amino acids. */
	private Vector<AminoAcid>	aas;
	
	
	/**
	 * Instantiates a new amino acid container.
	 */
	public AminoAcids ()
	{
		aas = new Vector<AminoAcid> ();
		BufferedReader br = new BufferedReader (new InputStreamReader (Isotopes.class.getResourceAsStream ("/aminoacids.raw")));
		try
		{
			String s;
			while ( (s = br.readLine ()) != null)
			{
				String[] tokens = s.split ("\\s");
				if (tokens.length < 4)
					continue;
				aas.add (new AminoAcid (tokens[0], tokens[1], tokens[2], tokens[3]));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}
	

	/**
	 * Get an one letter map.
	 * 
	 * @return the one letter map
	 */
	public HashMap<String, AminoAcid> getOneLetterMap ()
	{
		HashMap<String, AminoAcid> map = new HashMap<String, AminoAcid> ();
		for (int i = 0; i < aas.size (); i++)
			map.put (aas.elementAt (i).oneLetter, aas.elementAt (i));
		return map;
	}
	

	/**
	 * Get a three letter map.
	 * 
	 * @return the three letter map
	 */
	public HashMap<String, AminoAcid> getThreeLetterMap ()
	{
		HashMap<String, AminoAcid> map = new HashMap<String, AminoAcid> ();
		for (int i = 0; i < aas.size (); i++)
			map.put (aas.elementAt (i).threeLetter, aas.elementAt (i));
		return map;
	}
}
