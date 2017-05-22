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
package de.binfalse.martin.iso2l;

import java.awt.Component;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.binfalse.martin.iso2l.objects.AminoAcid;
import de.binfalse.martin.iso2l.objects.AminoAcids;
import de.binfalse.martin.iso2l.objects.Atom;
import de.binfalse.martin.iso2l.objects.Isotope;
import de.binfalse.martin.iso2l.objects.Isotopes;
import de.binfalse.martin.iso2l.objects.MolecularParser;



/**
 * The Operator, brain of iso2l.
 * 
 * @author Martin Scharm visit http://binfalse.de
 */
public class Operator
{
	
	/** The formula composition map. */
	private HashMap<String, Integer>	compositionMap;
	
	/** The peak distribution. */
	private HashMap<Double, Double>		peakDistribution;
	
	/** The isotopes. */
	public Isotopes										isos;
	
	/** The amino acids. */
	public AminoAcids									aas;
	
	/** The min abundance. */
	public final static double				minAbundance	= 0.000000001;
	
	
	/**
	 * Instantiates a new operator.
	 */
	public Operator ()
	{
		isos = new Isotopes ();
		aas = new AminoAcids ();
		peakDistribution = null;
		compositionMap = null;
	}
	

	/**
	 * Parses the formula.
	 * 
	 * @param formula
	 *          the formula
	 * @param type
	 *          the type
	 * @param c
	 *          the c
	 * @return true, if successful
	 */
	public boolean parseFormular (String formula, int type, Component c)
	{
		// molecular
		if (type == 1)
			return parseFormula (formula);
		else if (type == 2)
		{
			// one letter code
			formula = formulaFromOneLetter (formula);
			if (formula == null)
				return false;
			// peps come w/o water
			return parseFormula (formula + "H2O");
		}
		else if (type == 3)
		{
			// three letter code
			formula = formulaFromThreeLetter (formula);
			if (formula == null)
				return false;
			// peps come w/o water
			return parseFormula (formula + "H2O");
		}
		else
		{
			// detect
			// if one of ()0123456789 -> molecular
			if (stringContainsOneOf (formula, "()0123456789"))
				return parseFormular (formula, 1, c);
			// if two joint lowercase letters -> three letter code
			Matcher m = Pattern.compile ("[a-z]{2}").matcher (formula);
			if (m.find ())
				return parseFormular (formula, 3, c);
			// if not two joint lowercase letters but at least one lowercase in
			// word -> molecular
			if (!formula.toUpperCase ().equals (formula))
				return parseFormular (formula, 1, c);
			// we have only upper case letters... but can't be sure... failed...
			javax.swing.JOptionPane
					.showMessageDialog (
							c,
							"Not able to decide which type of formular...\nPlease choose on your own!",
							"Ooops", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
	

	/**
	 * does a string contain one of ... ?
	 * 
	 * @param s
	 *          the string
	 * @param find
	 *          the elements to find
	 * @return true, if we found any element
	 */
	private boolean stringContainsOneOf (String s, String find)
	{
		for (int i = 0; i < find.length (); i++)
			if (s.indexOf (find.charAt (i)) >= 0)
				return true;
		return false;
	}
	

	/**
	 * Formula from one letter code.
	 * 
	 * @param formula
	 *          the formula
	 * @return the elemental string
	 */
	private String formulaFromOneLetter (String formula)
	{
		String form = "";
		HashMap<String, AminoAcid> ones = aas.getOneLetterMap ();
		for (int i = 0; i < formula.length (); i++)
		{
			if (ones.get ("" + formula.charAt (i)) == null)
				return null;
			form += ones.get ("" + formula.charAt (i)).formula;
		}
		return form;
	}
	

	/**
	 * Formula from three letter code.
	 * 
	 * @param formula
	 *          the formula
	 * @return the elemental string
	 */
	private String formulaFromThreeLetter (String formula)
	{
		if (formula.length () % 3 != 0)
			return null;
		String form = "";
		HashMap<String, AminoAcid> threes = aas.getThreeLetterMap ();
		for (int i = 0; i < formula.length (); i += 3)
		{
			if (threes.get (formula.substring (i, i + 3)) == null)
				return null;
			form += threes.get (formula.substring (i, i + 3)).formula;
		}
		return form;
	}
	

	/**
	 * Parses the formula.
	 * 
	 * @param formular
	 *          the formula
	 * @return true, if successful
	 */
	private boolean parseFormula (String formula)
	{
		MolecularParser mp = new MolecularParser ();
		compositionMap = mp.parseFormula (formula, isos);
		if (compositionMap != null && compositionMap.size () > 0)
			return true;
		return false;
	}
	

	/**
	 * ggT via steinscher algorithmus
	 * 
	 * @param a
	 *          first integer
	 * @param b
	 *          second integer
	 * @return greatest common divisor
	 */
	public int ggT (int a, int b)
	{
		if (a == 0)
			return b;
		
		int k = 0, t = 0;
		
		while ( (a & 1) == 0 && (b & 1) == 0)
		{
			a /= 2;
			b /= 2;
			k++;
		}
		
		if ( (a & 1) != 0)
			t = -b;
		else
			t = a;
		
		while (t != 0)
		{
			while ( (t & 1) == 0)
				t /= 2;
			if (t > 0)
				a = t;
			else
				b = -t;
			t = a - b;
		}
		return a * (1 << k);
	}
	

	/**
	 * Calc distribution of this composition map.
	 * 
	 * @param isos
	 *          the isotopes
	 * @return true, if successful
	 */
	public boolean calcDistribution (Isotopes isos)
	{
		if (compositionMap == null || compositionMap.size () < 1)
			return false;
		
		peakDistribution = null;
		
		for (Map.Entry<String, Integer> entry : compositionMap.entrySet ())
		{
			Atom a = isos.getAtom (entry.getKey ());
			int anz = entry.getValue ();
			
			//System.out.println (a.getSymbol () + " -> " + anz);
			
			HashMap<Double, Double> peaks = peaksOfAtom (anz, a);
			
			if (peakDistribution == null)
				peakDistribution = peaks;
			else
			{
				HashMap<Double, Double> allPeaksNew = new HashMap<Double, Double> ();
				for (Map.Entry<Double, Double> allPeak : peakDistribution.entrySet ())
				{
					for (Map.Entry<Double, Double> e : peaks.entrySet ())
					{
						if (allPeaksNew.get (allPeak.getKey () + e.getKey ()) != null)
						{
							allPeaksNew.put (allPeak.getKey () + e.getKey (), allPeaksNew
									.get (allPeak.getKey () + e.getKey ())
									+ allPeak.getValue () * e.getValue ());
						}
						else
						{
							allPeaksNew.put (allPeak.getKey () + e.getKey (), allPeak
									.getValue ()
									* e.getValue ());
						}
					}
				}
				peakDistribution = allPeaksNew;
			}
			
			// delete very small stuff
			HashMap<Double, Double> allPeaksNew = new HashMap<Double, Double> ();
			for (Map.Entry<Double, Double> e : peakDistribution.entrySet ())
			{
				if (Double.isInfinite (e.getValue ()) || Double.isNaN (e.getValue ()))
					continue;
				if (e.getValue () > minAbundance)
					allPeaksNew.put (e.getKey (), e.getValue ());
			}
			peakDistribution = allPeaksNew;
			
		}
		return true;
	}
	

	/**
	 * Peaks of a single atom.
	 * 
	 * @param num
	 *          number of atoms
	 * @param a
	 *          the atom we are processing
	 * @return the hash map of masses => abundances
	 */
	private HashMap<Double, Double> peaksOfAtom (int num, Atom a)
	{
		return peaksOfAtom (new int[a.getIsotopes ().size ()], num, 0, a);
	}
	

	/**
	 * Peaks of a single atom.
	 * 
	 * @param vec
	 *          holds distribution of isotopes. |vec| = number of possible
	 *          isotopes. Intended to be zero (new int [size]) on access..
	 * @param num
	 *          number of atoms
	 * @param element
	 *          which element comes next
	 * @param a
	 *          the atom we are processing
	 * @return the hash map of masses => abundances
	 */
	private HashMap<Double, Double> peaksOfAtom (int[] vec, int num, int element,
			Atom a)
	{
		if (vec.length > element)
		{
			int s = 0;
			for (int i = 0; i < element; i++)
				s += vec[i];
			if (element < vec.length - 1)
			{
				HashMap<Double, Double> map = new HashMap<Double, Double> ();
				for (int i = num - s; i >= 0; i--)
				{
					int[] copy = (int[]) vec.clone ();
					copy[element] = i;
					HashMap<Double, Double> sub = peaksOfAtom (copy, num, element + 1, a);
					for (Map.Entry<Double, Double> entry : sub.entrySet ())
					{
						if (map.get (entry.getKey ()) != null)
							map.put (entry.getKey (), map.get (entry.getKey ())
									+ entry.getValue ());
						else
							map.put (entry.getKey (), entry.getValue ());
					}
				}
				return map;
			}
			else
			{
				vec[element] = num - s;
				return peaksOfAtom (vec, num, element + 1, a);
			}
		}
		else
		{
			HashMap<Double, Double> map = new HashMap<Double, Double> ();
			
			BigInteger binomial = BigInteger.ONE;
			
			double mass = 0, abundance = 1;
			for (int i = 0; i < vec.length; i++)
			{
				binomial = binomial.multiply (factorial (vec[i]));
				
				mass += a.getIsotopes ().elementAt (i).mass * vec[i];
				abundance *= Math
						.pow (a.getIsotopes ().elementAt (i).abundance, vec[i]);
			}
			binomial = factorial (num).divide (binomial);
			
			abundance *= binomial.doubleValue ();
			if (Double.isNaN (abundance) || Double.isInfinite (abundance))
				return map;
			
			map.put (mass, abundance);
			return map;
		}
	}
	

	/**
	 * Factorial.
	 * 
	 * @param n
	 *          the integer
	 * @return the BigInteger representation of the factorial of n
	 */
	private BigInteger factorial (int n)
	{
		BigInteger bi = BigInteger.ONE;
		for (int i = 2; i <= n; i++)
		{
			bi = bi.multiply (BigInteger.valueOf (i));
		}
		return bi;
	}
	

	/**
	 * Returns the peak distribution.
	 * 
	 * @return the peak distribution
	 */
	public HashMap<Double, Double> getPeakDistribution ()
	{
		return peakDistribution;
	}
	

	/**
	 * Get the peaks.
	 * 
	 * @return the peaks
	 */
	public Vector<Isotope> getPeaks ()
	{
		Vector<Isotope> peaks = new Vector<Isotope> ();
		for (Map.Entry<Double, Double> e : peakDistribution.entrySet ())
		{
			peaks.add (new Isotope (e.getKey (), e.getValue ()));
		}
		for (int i = 0; i < peaks.size (); i++)
		{
			for (int j = 0; j < i; j++)
			{
				if (peaks.get (i).abundance > peaks.get (j).abundance)
				{
					Isotope is = peaks.get (i);
					peaks.setElementAt (peaks.get (j), i);
					peaks.setElementAt (is, j);
				}
			}
		}
		return peaks;
	}
	
}
