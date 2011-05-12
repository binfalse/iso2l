package de.binfalse.martin.iso2l.objects;

/**
 * The Class Isotope.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class Isotope
{
	
	/** The mass. */
	public double	mass;
	
	/** The abundance. */
	public double	abundance;
	
	
	/**
	 * Instantiates a new isotope.
	 * 
	 * @param mass
	 *          the mass
	 * @param abundance
	 *          the abundance
	 */
	public Isotope (double mass, double abundance)
	{
		this.mass = mass;
		this.abundance = abundance;
	}
	

	/**
	 * Copy this isotope.
	 * 
	 * @return the copy
	 */
	public Isotope copy ()
	{
		return new Isotope (mass, abundance);
	}
}
