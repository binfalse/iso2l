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



// TODO: Auto-generated Javadoc
/**
 * The Operator, brain of iso2l.
 * 
 * @author Martin Scharm visit http://binfalse.de
 */
public class Operator
{
	
	/** The composition map. */
	private HashMap<String, Integer>	compositionMap;
	
	/** The peak distribution. */
	private HashMap<Double, Double>		peakDistribution;
	
	/** The isos. */
	public Isotopes										isos;
	
	/** The prots. */
	public AminoAcids									prots;
	
	/** The min abundance. */
	public static double							minAbundance	= 0.000000001;
	
	
	/**
	 * Instantiates a new operator.
	 */
	public Operator ()
	{
		isos = new Isotopes ();
		prots = new AminoAcids ();
		peakDistribution = null;
		compositionMap = null;
	}
	

	/**
	 * Parses the formular.
	 * 
	 * @param formular
	 *          the formular
	 * @param type
	 *          the type
	 * @param c
	 *          the c
	 * @return true, if successful
	 */
	public boolean parseFormular (String formular, int type, Component c)
	{
		// molecular
		if (type == 1)
			return parseFormular (formular);
		else if (type == 2)
		{
			// one letter code
			formular = formularFromOneLetter (formular);
			if (formular == null)
				return false;
			return parseFormular (formular);
		}
		else if (type == 3)
		{
			// three leter code
			formular = formularFromThreeLetter (formular);
			if (formular == null)
				return false;
			return parseFormular (formular);
		}
		else
		{
			// detect
			// if one of ()0123456789 -> molecular
			if (stringContainsOneOf (formular, "()0123456789"))
				return parseFormular (formular, 1, c);
			// if two joint lowercase letters -> three letter code
			Matcher m = Pattern.compile ("[a-z]{2}").matcher (formular);
			if (m.find ())
				return parseFormular (formular, 3, c);
			// if not two joint lowercase letters but at least one lowercase in
			// word -> molecular
			if (!formular.toUpperCase ().equals (formular))
				return parseFormular (formular, 1, c);
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
	 * String contains one of.
	 * 
	 * @param s
	 *          the s
	 * @param find
	 *          the find
	 * @return true, if successful
	 */
	private boolean stringContainsOneOf (String s, String find)
	{
		for (int i = 0; i < find.length (); i++)
			if (s.indexOf (find.charAt (i)) >= 0)
				return true;
		return false;
	}
	

	/**
	 * Formular from one letter.
	 * 
	 * @param formular
	 *          the formular
	 * @return the string
	 */
	private String formularFromOneLetter (String formular)
	{
		String form = "";
		HashMap<String, AminoAcid> ones = prots.getOneLetterMap ();
		for (int i = 0; i < formular.length (); i++)
		{
			if (ones.get ("" + formular.charAt (i)) == null)
				return null;
			form += ones.get ("" + formular.charAt (i)).formular;
		}
		return form;
	}
	

	/**
	 * Formular from three letter.
	 * 
	 * @param formular
	 *          the formular
	 * @return the string
	 */
	private String formularFromThreeLetter (String formular)
	{
		if (formular.length () % 3 != 0)
			return null;
		String form = "";
		HashMap<String, AminoAcid> threes = prots.getThreeLetterMap ();
		for (int i = 0; i < formular.length (); i += 3)
		{
			if (threes.get (formular.substring (i, i + 3)) == null)
				return null;
			form += threes.get (formular.substring (i, i + 3)).formular;
		}
		return form;
	}
	

	/**
	 * Parses the formular.
	 * 
	 * @param formular
	 *          the formular
	 * @return true, if successful
	 */
	private boolean parseFormular (String formular)
	{
		MolecularParser mp = new MolecularParser ();
		compositionMap = mp.praseFormular (formular, isos);
		if (compositionMap != null && compositionMap.size () > 0)
			return true;
		return false;
	}
	

	// steinscher algorithmus
	/**
	 * Gg t.
	 * 
	 * @param a
	 *          the a
	 * @param b
	 *          the b
	 * @return the int
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
	 * Calc distribution.
	 * 
	 * @param isos
	 *          the isos
	 * @param scale
	 *          the scale
	 * @return true, if successful
	 */
	public boolean calcDistribution (Isotopes isos)
	{
		if (compositionMap == null || compositionMap.size () < 1)
			return false;
		
		peakDistribution = null;
		boolean scaled = false;
		
		for (Map.Entry<String, Integer> entry : compositionMap.entrySet ())
		{
			Atom a = isos.getAtom (entry.getKey ());
			int anz = entry.getValue ();
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
			
			/*double max = 0;
			for (Map.Entry<Double, Double> e : peakDistribution.entrySet ())
			{
				if (e.getValue () > max)
					max = e.getValue ();
			}
			if (max < 0.01) // scale it or we'll loose peaks
			{
				scaled = true;
				for (Map.Entry<Double, Double> e : peakDistribution.entrySet ())
				{
					peakDistribution.put (e.getKey (), e.getValue () / max);
				}
			}*/
			
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
		
		/*double max = 0;
		HashMap<Double, Double> allPeaksNew = new HashMap<Double, Double> ();
		for (Map.Entry<Double, Double> e : peakDistribution.entrySet ())
		{
			if (Double.isInfinite (e.getValue ()) || Double.isNaN (e.getValue ()))
				continue;
			if (e.getValue () > minAbundance)
			{
				allPeaksNew.put (e.getKey (), e.getValue ());
				if (e.getValue () > max)
					max = e.getValue ();
			}
		}
		peakDistribution = allPeaksNew;
		// do we have to scale? if all peaks too small, or we scaled previously
		// or if user wishes
		if (max < 0.01)
		{
			for (Map.Entry<Double, Double> e : peakDistribution.entrySet ())
			{
				peakDistribution.put (e.getKey (), e.getValue () / max);
			}
		}*/
		// TODO: stretching in main prog...
		return true;
	}
	

	/**
	 * Peaks of atom.
	 * 
	 * @param sum
	 *          number of atoms
	 * @param a
	 *          the atom we are processing
	 * @return the hash map of masses => abundances
	 */
	private HashMap<Double, Double> peaksOfAtom (int sum, Atom a)
	{
		return peaksOfAtom (new int[a.getIsotopes ().size ()], sum, 0, a);
	}
	

	/**
	 * Peaks of atom.
	 * 
	 * @param vec
	 *          holds distribution of isotopes. |vec| = number of possible
	 *          isotopes. Intended to be zero (new int [size]) on access..
	 * @param sum
	 *          number of atoms
	 * @param element
	 *          which element comes next
	 * @param a
	 *          the atom we are processing
	 * @return the hash map of masses => abundances
	 */
	private HashMap<Double, Double> peaksOfAtom (int[] vec, int sum, int element,
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
				for (int i = sum - s; i >= 0; i--)
				{
					int[] copy = (int[]) vec.clone ();
					copy[element] = i;
					HashMap<Double, Double> sub = peaksOfAtom (copy, sum, element + 1, a);
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
				vec[element] = sum - s;
				return peaksOfAtom (vec, sum, element + 1, a);
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
			binomial = factorial (sum).divide (binomial);
			
			abundance *= binomial.doubleValue ();
			// if (binomial.doubleValue() - mult != 0)
			// System.out.println(binomial.doubleValue() + " -vs- " + mult);
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
			/*peaks.add (new Isotope (
					Math.round (e.getKey () * 10000000000.) / 10000000000., Math.round (e
							.getValue () * 10000000000.) / 10000000000.));*/
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
