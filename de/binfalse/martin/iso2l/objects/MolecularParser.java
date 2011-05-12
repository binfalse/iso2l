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
	private HashMap<String, Integer> parseFormula (String formula,
			Isotopes iso, int multiplier)
	{
		HashMap<String, Integer> map;
		if (formula.indexOf ("(") >= 0)
		{
			int start = formula.indexOf ("(");
			int end = formula.lastIndexOf (")");
			if (end < 0)
				return null;
			int endNum = end + 1;
			while (endNum < formula.length ()
					&& isNumeric (formula.charAt (endNum)))
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
