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

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * The MolecularParser.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class MolecularParser
{
	
	/**
	 * Parse formula.
	 * 
	 * @param formula
	 *          the formula
	 * @param iso
	 *          the isotopes
	 * @return the elemental composition represented as hash map
	 */
	public HashMap<String, Integer> parseFormula (String formula, Isotopes iso)
	{
		if (!formula.matches ("^[A-Za-z0-9()]*$"))
			return null;
		int openBraces = 0;
		for (int i = 0; i < formula.length (); i++)
		{
			if (formula.charAt (i) == '(')
				openBraces++;
			if (formula.charAt (i) == ')')
			{
				openBraces--;
				if (openBraces < 0)
					return null;
			}
		}
		if (openBraces != 0)
			return null;
		
		HashMap<String, Integer> map = parseFormula (formula, iso, 1);
		if (map == null)
			return null;
		
		Vector<String> keys = new Vector<String> ();
		for (String key : map.keySet ())
			if (map.get (key) < 1)
				keys.add (key);
		for (int i = 0; i < keys.size (); i++)
			map.remove (keys.elementAt (i));
		return map;
	}
	

	/**
	 * Parse formula.
	 * 
	 * @param formula
	 *          the formula
	 * @param iso
	 *          the isotopes
	 * @param multiplier
	 *          the multiplier
	 * @return the elemental composition represented as hash map
	 */
	private HashMap<String, Integer> parseFormula (String formula, Isotopes iso,
			int multiplier)
	{
		HashMap<String, Integer> map;
		if (formula.indexOf ("(") >= 0)
		{
			int start = formula.indexOf ("(");
			int end = formula.lastIndexOf (")");
			if (end < 0)
				return null;
			int endNum = end + 1;
			while (endNum < formula.length () && isNumeric (formula.charAt (endNum)))
				endNum++;
			endNum--;
			int mult = 0;
			if (endNum == end)
				mult = 1;
			else
				mult = Integer.parseInt (formula.substring (end + 1, endNum + 1));
			HashMap<String, Integer> sub = parseFormula (formula.substring (
					start + 1, end), iso, mult);
			if (sub == null)
				return sub;
			map = sub;
			String tmp = "";
			if (start - 1 >= 0)
				tmp = formula.substring (0, start);
			if (endNum + 1 < formula.length ())
				tmp += formula.substring (endNum + 1);
			formula = tmp;
		}
		else
			map = iso.getAtomMap ();
		
		for (String key : map.keySet ())
			map.put (key, map.get (key) * multiplier);
		
		Pattern p = Pattern.compile ("^([A-Z][a-z]?)(\\d*)");
		Matcher m = p.matcher (formula);
		while (m.find ())
		{
			if (map.get (m.group (1)) == null)
				return null;
			int anz = m.group (2).length () > 0 ? Integer.parseInt (m.group (2))
					* multiplier : multiplier;
			map.put (m.group (1), map.get (m.group (1)) + anz);
			formula = formula.substring (m.group (0).length ());
			m = p.matcher (formula);
		}
		if (formula.length () > 0)
			return null;
		
		return map;
	}
	

	/**
	 * Checks if a character is numeric.
	 * 
	 * @param n
	 *          the character
	 * @return true, if is numeric
	 */
	private boolean isNumeric (char n)
	{
		if (n <= '9' && n >= '0')
			return true;
		return false;
	}
}
