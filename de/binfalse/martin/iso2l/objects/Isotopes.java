package de.binfalse.martin.iso2l.objects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class Isotopes
{
	HashMap<String, Atom> atoms;
	public Isotopes ()
	{ 
		atoms = new HashMap<String, Atom> ();
		
		BufferedReader br = new BufferedReader (new InputStreamReader (new DataInputStream(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream ("de/binfalse/martin/iso2l/data/isotops.raw")))));
		try
		{
			String s;
			while ((s = br.readLine ()) != null)
			{
			  String [] tokens = s.split ("\\s");
			  if (tokens.length < 5) continue;
			  Atom a = new Atom (tokens[1], tokens[2]);
			  for (int i = 4; i < tokens.length; i++)
			  {
			  	String [] isotops = tokens[i].split (",");
			  	a.addIsotope (new Isotope (Double.parseDouble (isotops[1]), Double.parseDouble (isotops[0])));
			  	if (a.getSymbol().equals("C"))
			  		System.out.println(isotops[0]);
			  }
			  atoms.put (a.getSymbol (), a);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Atom getAtom (String sym)
	{
		return atoms.get (sym);
	}
	
	public HashMap<String, Integer> getAtomMap ()
	{
		HashMap<String, Integer> map = new HashMap<String, Integer> ();
		for (String key: atoms.keySet ())
			map.put (key, 0);
		return map;
	}
}
