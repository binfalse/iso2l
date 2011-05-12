package de.binfalse.martin.iso2l.objects;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class Isotope
{
	public double mass;
	public double abundance;
	public Isotope (double mass, double abundance)
	{
		this.mass = mass;
		this.abundance = abundance;
	}
	public Isotope copy ()
	{
		return new Isotope (mass, abundance);
	}
}
