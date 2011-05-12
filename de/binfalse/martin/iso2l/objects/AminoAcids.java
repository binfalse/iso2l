package de.binfalse.martin.iso2l.objects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class AminoAcids
{
	private Vector<AminoAcid> prots;
	
	public AminoAcids ()
	{
		prots = new Vector<AminoAcid> ();
		BufferedReader br = new BufferedReader (new InputStreamReader (new DataInputStream(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream ("de/binfalse/martin/iso2l/data/aminoacids.raw")))));
		try
		{
			String s;
			while ((s = br.readLine ()) != null)
			{
			  String [] tokens = s.split ("\\s");
			  if (tokens.length < 4) continue;
			  prots.add (new AminoAcid (tokens[0], tokens[1], tokens[2], tokens[3]));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public HashMap<String, AminoAcid> getOneLetterMap ()
	{
		HashMap<String, AminoAcid> map = new HashMap<String, AminoAcid> ();
		for (int i = 0; i < prots.size (); i++)
			map.put (prots.elementAt (i).oneLetter, prots.elementAt (i));
		return map;
	}

	public HashMap<String, AminoAcid> getThreeLetterMap ()
	{
		HashMap<String, AminoAcid> map = new HashMap<String, AminoAcid> ();
		for (int i = 0; i < prots.size (); i++)
			map.put (prots.elementAt (i).threeLetter, prots.elementAt (i));
		return map;
	}
}
