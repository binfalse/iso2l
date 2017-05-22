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
