package de.binfalse.martin.iso2l.objects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;



/**
 * The Class Isotopes.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class Isotopes
{
	
	/** The atoms, indexed by symbol. */
	HashMap<String, Atom>	atoms;
	
	
	/**
	 * Instantiates new isotopes.
	 */
	public Isotopes ()
	{
		atoms = new HashMap<String, Atom> ();
		
		BufferedReader br = new BufferedReader (new InputStreamReader (
				new DataInputStream (new BufferedInputStream (getClass ()
						.getClassLoader ().getResourceAsStream (
								"de/binfalse/martin/iso2l/data/isotops.raw")))));
		try
		{
			String s;
			while ( (s = br.readLine ()) != null)
			{
				String[] tokens = s.split ("\\s");
				if (tokens.length < 5)
					continue;
				Atom a = new Atom (tokens[1], tokens[2]);
				for (int i = 4; i < tokens.length; i++)
				{
					String[] isotops = tokens[i].split (",");
					a.addIsotope (new Isotope (Double.parseDouble (isotops[1]), Double
							.parseDouble (isotops[0])));
				}
				atoms.put (a.getSymbol (), a);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}
	

	/**
	 * Get an atom.
	 * 
	 * @param sym
	 *          the symbol of this atom
	 * @return the atom
	 */
	public Atom getAtom (String sym)
	{
		return atoms.get (sym);
	}
	

	/**
	 * Get the atom map.
	 * 
	 * @return the atom map indexed by symbol
	 */
	public HashMap<String, Integer> getAtomMap ()
	{
		HashMap<String, Integer> map = new HashMap<String, Integer> ();
		for (String key : atoms.keySet ())
			map.put (key, 0);
		return map;
	}
}
