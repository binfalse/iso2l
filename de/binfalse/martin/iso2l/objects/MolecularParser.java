package de.binfalse.martin.iso2l.objects;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class MolecularParser
{
	public HashMap<String, Integer> praseFormular (String formular, Isotopes iso)
	{
		if (!formular.matches ("^[A-Za-z0-9()]*$")) return null;
		int openBraces = 0;
		for (int i = 0; i < formular.length (); i++)
		{
			if (formular.charAt (i) == '(') openBraces++;
			if (formular.charAt (i) == ')')
			{
				openBraces--;
				if (openBraces < 0) return null;
			}
		}
		if (openBraces != 0) return null;
		
		HashMap<String, Integer> map = praseFormular (formular, iso, 1);
		if (map == null) return null;
		
		Vector<String> keys = new Vector <String> ();
		for (String key: map.keySet ())
			if (map.get (key) < 1) keys.add (key);
		for (int i = 0; i < keys.size (); i++)
			map.remove (keys.elementAt (i));
		return map;
	}
	private HashMap<String, Integer> praseFormular (String formular, Isotopes iso, int multiplier)
	{
		HashMap<String, Integer> map;
		if (formular.indexOf ("(") >= 0)
		{
			int start = formular.indexOf ("(");
			int end = formular.lastIndexOf (")");
			if (end < 0) return null;
			int endNum = end + 1;
			while (endNum < formular.length () && isNumeric (formular.charAt (endNum))) endNum++;
			endNum--;
			int mult = 0;
			if (endNum == end) mult = 1;
			else mult = Integer.parseInt (formular.substring (end + 1, endNum + 1));
			HashMap<String, Integer> sub = praseFormular (formular.substring (start + 1, end), iso, mult);
			if (sub == null) return sub;
			map = sub;
			String tmp = "";
			if (start - 1 >= 0) tmp = formular.substring (0, start);
			if (endNum + 1 < formular.length ()) tmp += formular.substring (endNum + 1);
			formular = tmp;
		}
		else map = iso.getAtomMap ();

		for (String key: map.keySet ())
			map.put (key, map.get (key) * multiplier);
		
		Pattern p = Pattern.compile("^([A-Z][a-z]?)(\\d*)");
		Matcher m = p.matcher(formular);
		while (m.find())
		{
			if (map.get (m.group (1)) == null) return null;
			int anz = m.group (2).length () > 0 ? Integer.parseInt (m.group (2)) * multiplier : multiplier;
			map.put (m.group (1), map.get (m.group (1)) + anz);
			formular = formular.substring (m.group (0).length ());
			m = p.matcher(formular);
		}
		if (formular.length () > 0) return null;
		
		return map;
	}
	
	private boolean isNumeric (char n)
	{
		if (n <= '9' && n >= '0') return true;
		return false;
	}
}
