package de.binfalse.martin.iso2l.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * @author Martin Scharm
 * visit http://binfalse.de
 */
public class PeakViewer extends javax.swing.JPanel 
{
	private static final long serialVersionUID = 1L;
	private Vector<Isotope> peaks;
	private double minMass, maxMass, maxAbu;
	private String name;
	
	public PeakViewer ()
	{
		super ();
		peaks = null;
		minMass = Double.POSITIVE_INFINITY;
		maxMass = Double.NEGATIVE_INFINITY;
		maxAbu = 1;
		name = null;
	}
	public void setName (String name)
	{
		this.name = name;
	}
	public void drawAbout ()
	{
		this.setBackground(Color.WHITE);
		peaks = null;
		this.repaint ();
	}
	public void drawPeaks (Vector<Isotope> peaks)
	{
		if (peaks.size () < 1)
		{
			peaks = null;
			this.repaint ();
			return;
		}
		this.peaks = peaks;
		minMass = Double.POSITIVE_INFINITY;
		maxMass = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < peaks.size (); i++)
		{
			if (peaks.elementAt (i).mass < minMass) minMass = peaks.elementAt (i).mass;
			if (peaks.elementAt (i).mass > maxMass) maxMass = peaks.elementAt (i).mass;
			//if (peaks.elementAt (i).probability > maxAbu) maxAbu = peaks.elementAt (i).probability;
		}
		this.repaint ();
	}
	
	private void drawAbout (Graphics2D g2)
	{
		this.setBackground(Color.WHITE);
		g2.setColor (Color.WHITE);
		g2.fillRect (getX (), getY (), WIDTH, HEIGHT);
		Font f = new Font ("Serif", Font.BOLD, 75);
		g2.setFont (f);
		FontMetrics fm = g2.getFontMetrics(f);
		
		String text = "";
		
		
		g2.setColor (Color.BLUE);
		text = "iso2l";
		Rectangle2D rect = fm.getStringBounds (text, g2);
		int right = (int) (rect.getWidth () + 100);
		int bottom = (int) (rect.getHeight () + 30);
		g2.drawString (text, 100, (int) (rect.getHeight () + 30));
		
		g2.setColor (Color.GRAY);
		f = new Font ("SansSerif", Font.PLAIN, 16);
		g2.setFont (f);
		fm = g2.getFontMetrics(f);
		
		text = "calc isotopic distributions";
		rect = fm.getStringBounds (text, g2);
		bottom += rect.getHeight () + 20;
		g2.drawString (text, (int) (right - rect.getWidth ()), bottom);
		text = "author: Martin Scharm";
		rect = fm.getStringBounds (text, g2);
		bottom += rect.getHeight () + 8;
		g2.drawString (text, (int) (right - rect.getWidth ()), bottom);
		text = "visit http://binfalse.de";
		rect = fm.getStringBounds (text, g2);
		bottom += rect.getHeight () + 8;
		g2.drawString (text, (int) (right - rect.getWidth ()), bottom);
	}
	private void drawPeaks (Graphics2D g2)
	{
		this.setBackground(Color.WHITE);
		g2.setColor (Color.WHITE);
		g2.fillRect (getX (), getY (), WIDTH, HEIGHT);
		String text;
		Rectangle2D rect;
		
		double originX = 50;
		double originY = getHeight () - 50;
		double maxX = getWidth () - 20;
		double maxY = 20;
		double startX = originX + 10;
		double startY = originY;
		double endX = maxX - 10;
		double endY = maxY + 10;
		
		
		g2.setColor (new Color (250, 250, 250));
		g2.fillRect ((int) originX, (int) endY, (int) (maxX - originX), (int) ((startY - endY) / 4));
		g2.fillRect ((int) originX, (int) (endY + (startY - endY) / 2), (int) (maxX - originX), (int) ((startY - endY) / 4));
		g2.setColor (Color.BLACK);
		g2.drawLine ((int) originX, (int) originY, (int) originX, (int) endY);
		g2.drawLine ((int) originX, (int) originY, (int) maxX, (int) originY);
		
		
		double maxAbundance = this.maxAbu;//Math.round (.5 + this.maxAbu);
		double maxMass = Math.round (.5 + (this.maxMass / 10)) * 10;
		double minMass = Math.floor ((this.minMass / 10)) * 10;
		
		Font f = new Font("SansSerif", Font.PLAIN, 10);
		g2.setFont (f);
		FontMetrics fm = g2.getFontMetrics(f);
		
		text = "" + (int) (maxAbundance * 100) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) endY, (int) originX - 5, (int) endY);
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY + rect.getHeight () / 2));
		
		text = "" + (int) ((maxAbundance * 3 / 4) * 100) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) (endY + (startY - endY) / 4), (int) originX - 5, (int) (endY + (startY - endY) / 4));
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY + (startY - endY) / 4 + rect.getHeight () / 2));
		
		text = "" + (int) ((maxAbundance / 2) * 100) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) (endY + (startY - endY) / 2), (int) originX - 5, (int) (endY + (startY - endY) / 2));
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY + (startY - endY) / 2 + rect.getHeight () / 2));
		
		text = "" + (int) ((maxAbundance / 4) * 100) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) (endY + (startY - endY) * 3 / 4), (int) originX - 5, (int) (endY + (startY - endY) * 3 / 4));
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY + (startY - endY) * 3 / 4 + rect.getHeight () / 2));
		

		
		text = "" + maxMass;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) endX, (int) originY, (int) endX, (int) originY + 5);
		g2.drawString (text, (int) (endX - rect.getWidth () / 2), (int) (originY + 10 + rect.getHeight ()));
		
		text = "" + minMass;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) startX, (int) originY, (int) startX, (int) originY + 5);
		g2.drawString (text, (int) (startX - rect.getWidth () / 2), (int) (originY + 10 + rect.getHeight ()));
		
		text = "" + (minMass + (maxMass - minMass) * 3 / 4);
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) ((startX + endX) * 3 / 4), (int) originY, (int) ((startX + endX) * 3 / 4), (int) originY + 5);
		g2.drawString (text, (int) (((startX + endX) * 3 / 4) - rect.getWidth () / 2), (int) (originY + 10 + rect.getHeight ()));
		
		text = "" + (minMass + maxMass) / 2;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) ((startX + endX) / 2), (int) originY, (int) ((startX + endX) / 2), (int) originY + 5);
		g2.drawString (text, (int) (((startX + endX) / 2) - rect.getWidth () / 2), (int) (originY + 10 + rect.getHeight ()));
		
		text = "" + (minMass + (maxMass - minMass) / 4);
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) ((startX + endX) / 4), (int) originY, (int) ((startX + endX) / 4), (int) originY + 5);
		g2.drawString (text, (int) (((startX + endX) / 4) - rect.getWidth () / 2), (int) (originY + 10 + rect.getHeight ()));
		
		
		if (name != null)
		{
			f = new Font ("SansSerif", Font.PLAIN, 12);
			g2.setFont (f);
			fm = g2.getFontMetrics(f);
			text = name; // sub numbers
			rect = fm.getStringBounds (text, g2);
			g2.drawString (text, (int) (endX - rect.getWidth ()), (int) (10 + rect.getHeight ()));
		}
		
		for (int i = 0; i < peaks.size (); i++)
		{
			Isotope p = peaks.elementAt (i);
			double x = startX + (((p.mass - minMass)/(maxMass - minMass)) * (endX - startX));
			g2.drawLine ((int) x, (int) originY, (int) x, (int) (startY + (endY - startY) * p.abundance / maxAbundance));
		}
	}
	
	public void paint(Graphics g)
	{
		super.paint (g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		if (peaks == null) drawAbout (g2);
		else drawPeaks (g2);
	}
	
}
