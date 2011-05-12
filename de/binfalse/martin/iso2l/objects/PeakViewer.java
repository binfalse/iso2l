package de.binfalse.martin.iso2l.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.Vector;



/**
 * The Class PeakViewer.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class PeakViewer
		extends javax.swing.JPanel
		implements ComponentListener
{
	
	/** ext stuff. */
	private static final long	serialVersionUID	= 1L;
	
	/** The vector of peaks. */
	private Vector<Isotope>		peaks;
	
	/** The min mass. */
	private double						minMass;
	
	/** The max mass. */
	private double						maxMass;
	
	/** name of formula. */
	private String						formulaName;
	
	/** array of peaks. */
	private double[]					msModeVals;
	
	/** msmode enabled!?. */
	private boolean						msmode;
	
	/** The resolution. */
	private double						resolution;
	
	
	/**
	 * Instantiates a new peak viewer.
	 */
	public PeakViewer ()
	{
		super ();
		peaks = null;
		minMass = Double.POSITIVE_INFINITY;
		maxMass = Double.NEGATIVE_INFINITY;
		formulaName = null;
		msmode = false;
		msModeVals = null;
		resolution = 8000;
		this.addComponentListener (this);
	}
	

	/**
	 * Sets the MS resolution.
	 * 
	 * @param resolution
	 *          the new MS res
	 */
	public void setMsRes (double resolution)
	{
		this.resolution = resolution;
		msModeVals = null;
	}
	

	/**
	 * Switch the MS mode on or off.
	 * 
	 * @param msmode
	 *          the new MS state
	 */
	public void setMsMode (boolean msmode)
	{
		this.msmode = msmode;
		msModeVals = null;
	}
	

	/**
	 * Sets the formula name.
	 * 
	 * @param name
	 *          the new formula name
	 */
	public void setFormulaName (String name)
	{
		this.formulaName = name;
	}
	

	/**
	 * Draw about.
	 */
	public void drawAbout ()
	{
		this.setBackground (Color.WHITE);
		peaks = null;
		this.repaint ();
	}
	

	/**
	 * Set the peaks.
	 * 
	 * @param peaks
	 *          the vector of peaks we should draw later on
	 */
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
			if (peaks.elementAt (i).mass < minMass)
				minMass = peaks.elementAt (i).mass;
			if (peaks.elementAt (i).mass > maxMass)
				maxMass = peaks.elementAt (i).mass;
		}
		this.repaint ();
	}
	

	/**
	 * Draw about.
	 * 
	 * @param g2
	 *          the Graphics2D we should speak to
	 */
	private void drawAbout (Graphics2D g2)
	{
		this.setBackground (Color.WHITE);
		g2.setColor (Color.WHITE);
		g2.fillRect (getX (), getY (), WIDTH, HEIGHT);
		Font f = new Font ("Serif", Font.BOLD, 75);
		g2.setFont (f);
		FontMetrics fm = g2.getFontMetrics (f);
		
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
		fm = g2.getFontMetrics (f);
		
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
	

	/**
	 * Gauss - just some probs.
	 * 
	 * @param x
	 *          the value (w/o mean)
	 * @param sigma
	 *          the variance
	 * @return prob @ x
	 */
	private double gauss (double x, double sigma)
	{
		return Math.exp (-x * x / (2 * sigma * sigma))
				/ (Math.sqrt (2 * Math.PI) * sigma);
	}
	

	/**
	 * Calc graph in MS mode.
	 * 
	 * @param length
	 *          the length of the graph
	 * @param minMass
	 *          the min mass
	 * @param maxMass
	 *          the max mass
	 */
	private void calcMsMode (int length, double minMass, double maxMass)
	{
		msModeVals = new double[length];
		double max = 0;
		for (int i = 0; i < peaks.size (); i++)
		{
			Isotope p = peaks.elementAt (i);
			double fwhm = p.mass / resolution;
			double sigma = fwhm / (2.0 * Math.sqrt (2.0 * Math.log (2)));
			for (double x = 0; x < length; x++)
			{
				double mass = x * (maxMass - minMass) / length + minMass;
				msModeVals[(int) x] += p.abundance * gauss (mass - p.mass, sigma);
				if (msModeVals[(int) x] > max)
					max = msModeVals[(int) x];
			}
		}
		for (int x = 0; x < length; x++)
		{
			msModeVals[(int) x] /= max;
		}
	}
	

	/**
	 * Draw peaks.
	 * 
	 * @param g2
	 *          the Graphics2D object we have to speak to
	 */
	private void drawPeaks (Graphics2D g2)
	{
		// TODO: zoom
		// TODO: more labels at bottom
		this.setBackground (Color.WHITE);
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
		g2.fillRect ((int) originX, (int) endY, (int) (maxX - originX),
				(int) ( (startY - endY) / 4));
		g2.fillRect ((int) originX, (int) (endY + (startY - endY) / 2),
				(int) (maxX - originX), (int) ( (startY - endY) / 4));
		g2.setColor (Color.BLACK);
		g2.drawLine ((int) originX, (int) originY, (int) originX, (int) endY);
		g2.drawLine ((int) originX, (int) originY, (int) maxX, (int) originY);
		
		double maxAbundance = 1;
		double maxMass = Math.ceil ( (this.maxMass + 2) / 3.) * 3.;
		double minMass = Math.floor ( (this.minMass - 2) / 3.) * 3.;
		int px = 0;
		
		Font f = new Font ("SansSerif", Font.PLAIN, 10);
		g2.setFont (f);
		FontMetrics fm = g2.getFontMetrics (f);
		
		text = "" + (int) (maxAbundance * 100.) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) endY, (int) originX - 5, (int) endY);
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()),
				(int) (endY + rect.getHeight () / 2.));
		
		text = "" + (int) ( (maxAbundance * 3. / 4.) * 100.) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) (endY + (startY - endY) / 4.),
				(int) originX - 5, (int) (endY + (startY - endY) / 4.));
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY
				+ (startY - endY) / 4. + rect.getHeight () / 2.));
		
		text = "" + (int) ( (maxAbundance / 2.) * 100.) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) (endY + (startY - endY) / 2.),
				(int) originX - 5, (int) (endY + (startY - endY) / 2.));
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY
				+ (startY - endY) / 2. + rect.getHeight () / 2.));
		
		text = "" + (int) ( (maxAbundance / 4.) * 100.) + "%";
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) originX, (int) (endY + (startY - endY) * 3. / 4.),
				(int) originX - 5, (int) (endY + (startY - endY) * 3. / 4.));
		g2.drawString (text, (int) (originX - 10 - rect.getWidth ()), (int) (endY
				+ (startY - endY) * 3. / 4. + rect.getHeight () / 2.));
		
		text = "" + maxMass;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) endX, (int) originY, (int) endX, (int) originY + 5);
		g2.drawString (text, (int) (endX - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + minMass;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) startX, (int) originY, (int) startX, (int) originY + 5);
		g2.drawString (text, (int) (startX - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + (minMass + (maxMass - minMass) * 3. / 4.);
		rect = fm.getStringBounds (text, g2);
		px = (int) ( (endX - startX) * 3. / 4. + startX);
		g2.drawLine (px, (int) originY, px, (int) originY + 5);
		g2.drawString (text, (int) (px - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + (minMass + maxMass) / 2.;
		rect = fm.getStringBounds (text, g2);
		px = (int) ( (endX - startX) / 2. + startX);
		g2.drawLine (px, (int) originY, px, (int) originY + 5);
		g2.drawString (text, (int) (px - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + (minMass + (maxMass - minMass) / 4.);
		rect = fm.getStringBounds (text, g2);
		px = (int) ( (endX - startX) / 4. + startX);
		g2.drawLine (px, (int) originY, px, (int) originY + 5);
		g2.drawString (text, (int) (px - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		if (formulaName != null)
		{
			f = new Font ("SansSerif", Font.PLAIN, 12);
			g2.setFont (f);
			fm = g2.getFontMetrics (f);
			text = formulaName; // sub numbers ?
			rect = fm.getStringBounds (text, g2);
			g2.drawString (text, (int) (endX - rect.getWidth ()), (int) (10 + rect
					.getHeight ()));
		}
		
		if (msmode && resolution > 0)
		{
			if (msModeVals == null)
				calcMsMode ((int) (endX - startX), minMass, maxMass);
			for (int i = 1; i < msModeVals.length; i++)
			{
				g2.drawLine (i - 1 + (int) startX, (int) (startY + (endY - startY)
						* msModeVals[i - 1]), i + (int) startX,
						(int) (startY + (endY - startY) * msModeVals[i]));
			}
		}
		else
			for (int i = 0; i < peaks.size (); i++)
			{
				Isotope p = peaks.elementAt (i);
				double x = startX
						+ ( ( (p.mass - minMass) / (maxMass - minMass)) * (endX - startX));
				g2.drawLine ((int) x, (int) originY, (int) x,
						(int) (startY + (endY - startY) * p.abundance));
			}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint (Graphics g)
	{
		super.paint (g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint (RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		
		if (peaks == null)
			drawAbout (g2);
		else
			drawPeaks (g2);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentResized (ComponentEvent e)
	{
		msModeVals = null;
		// if we immediately repaint msModeVals istn't null. so we have to wait some
		// time to let msModeVals becoming null, otherwise it will not be updated
		try
		{
			Thread.sleep (10);
		}
		catch (InterruptedException ex)
		{
		}
		this.repaint ();
	}
	

	/*
	 * 
	 * following: unnecessary stuff, but we have to implement it...
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentShown (ComponentEvent e)
	{
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentHidden (ComponentEvent e)
	{
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentMoved (ComponentEvent e)
	{
	}
}
