package de.binfalse.martin.iso2l.objects;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class AminoAcid
{
	public String name;
	public String threeLetter;
	public String oneLetter;
	public String formular;
	
	public AminoAcid (String name, String threeLetter, String oneLetter, String formular)
	{
		this.oneLetter = oneLetter;
		this.threeLetter = threeLetter;
		this.name = name;
		this.formular = formular;
	}
	
	public String toString ()
	{
		return name + " [" + threeLetter + "-" + oneLetter + "-" + formular + "]";
	}
}
