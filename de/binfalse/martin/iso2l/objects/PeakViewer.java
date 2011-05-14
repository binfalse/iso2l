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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
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
		implements java.awt.event.ComponentListener, java.awt.event.MouseListener,
		java.awt.event.MouseMotionListener
{
	
	/** ext stuff. */
	private static final long				serialVersionUID	= 1L;
	
	/** The vector of peaks. */
	private Vector<Isotope>					peaks;
	
	/** name of formula. */
	private String									formulaName;
	
	/** array of peaks. */
	private double[]								msModeVals;
	
	/** msmode enabled!?. */
	private boolean									msmode;
	
	/** The resolution. */
	private double									resolution;
	
	/** The min mass. */
	private java.util.Stack<Double>	minMass;
	
	/** The max mass. */
	private java.util.Stack<Double>	maxMass;
	
	/** The mouse position. */
	private java.awt.Point					mouse;
	
	/** The init mouse position. */
	private java.awt.Point					initMouse;
	
	/** The indicator whether to draw the mouse. */
	private boolean									drawMouse;
	
	/** The indicator whether mouse moved. */
	private boolean									mousemoved;
	
	/** The cursor. */
	private java.awt.Cursor					cursor;
	
	/** The origin x. */
	private double									originX;
	
	/** The origin y. */
	private double									originY;
	
	/** max x value. */
	private double									maxX;
	
	/** max y value. */
	private double									maxY;
	
	/** start x coord. */
	private double									startX;
	
	/** start y coord. */
	private double									startY;
	
	/** end x coord. */
	private double									endX;
	
	/** end y coord. */
	private double									endY;
	
	/** stretch the peaks. */
	private boolean									stretch;
	
	
	/**
	 * Instantiates a new peak viewer.
	 */
	public PeakViewer ()
	{
		super ();
		peaks = null;
		formulaName = null;
		msmode = false;
		msModeVals = null;
		resolution = 8000;
		minMass = null;
		maxMass = null;
		this.addComponentListener (this);
		drawMouse = false;
		mouse = null;
		initMouse = null;
		this.addMouseListener (this);
		this.addMouseMotionListener (this);
		mousemoved = false;
		cursor = java.awt.Toolkit.getDefaultToolkit ().createCustomCursor (
				new java.awt.image.BufferedImage (16, 16,
						java.awt.image.BufferedImage.TYPE_INT_ARGB),
				new java.awt.Point (0, 0), "blank cursor");
		originX = 50;
		originY = getHeight () - 50;
		maxX = getWidth () - 20;
		maxY = 20;
		startX = originX + 10;
		startY = originY;
		endX = maxX - 10;
		endY = maxY + 10;
		stretch = false;
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
		mousemoved = false;
		initMouse = null;
		minMass = null;
		maxMass = null;
	}
	

	/**
	 * Set the peaks.
	 * 
	 * @param peaks
	 *          the vector of peaks we should draw later on
	 * @param stretch
	 *          the stretch
	 */
	public void drawPeaks (Vector<Isotope> peaks, boolean stretch)
	{
		this.stretch = stretch;
		if (peaks.size () < 1)
		{
			peaks = null;
			this.repaint ();
			return;
		}
		this.peaks = peaks;
		minMass = new java.util.Stack<Double> ();
		maxMass = new java.util.Stack<Double> ();
		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < peaks.size (); i++)
		{
			if (peaks.elementAt (i).mass < min)
				min = peaks.elementAt (i).mass;
			if (peaks.elementAt (i).mass > max)
				max = peaks.elementAt (i).mass;
		}
		minMass.push (Math.floor ( (min - 2) / 3.) * 3.);
		maxMass.push (Math.ceil ( (max + 2) / 3.) * 3.);
		mousemoved = false;
		initMouse = null;
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
		double max = 0, antisigma = resolution * 2.0
				* Math.sqrt (2.0 * Math.log (2));
		for (int i = 0; i < peaks.size (); i++)
		{
			Isotope p = peaks.elementAt (i);
			double sigma = p.mass / antisigma;
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
	 * Draw mouse.
	 * 
	 * @param g2
	 *          the Graphics2D object we have to speak to
	 */
	private void drawMouse (Graphics2D g2)
	{
		String text;
		Rectangle2D rect;
		Font f = new Font ("SansSerif", Font.PLAIN, 10);
		g2.setFont (f);
		FontMetrics fm = g2.getFontMetrics (f);
		
		g2.setColor (java.awt.Color.LIGHT_GRAY);
		if (drawMouse && mouse != null)
		{
			g2.drawLine (mouse.x, mouse.y - 10, mouse.x, mouse.y + 10);
			g2.drawLine (mouse.x - 10, mouse.y, mouse.x + 10, mouse.y);
			if (mouse.x >= startX && mouse.x <= endX && mouse.y >= endY
					&& mouse.y <= startY)
			{
				double maxMass = this.maxMass.peek ();
				double minMass = this.minMass.peek ();
				double mass = ( ((double) (mouse.x - startX) / (double) (endX - startX)))
						* (maxMass - minMass) + minMass;
				double intens = ((double) (mouse.y - startY))
						/ (double) (endY - startY);
				text = "" + Math.round (mass * 100.) / 100.;
				rect = fm.getStringBounds (text, g2);
				g2.drawString (text, mouse.x + 3, mouse.y - 3);
				g2.drawString ("" + Math.round (intens * 10000.) / 100. + " %",
						mouse.x + 3, mouse.y - 3 - (int) rect.getHeight ());
				g2.drawLine (mouse.x, (int) startY, mouse.x, (int) startY + 6);
				g2.drawLine ((int) originX, mouse.y, (int) originX - 6, mouse.y);
			}
			if (initMouse != null)
			{
				double maxMass = Math.ceil ( (this.maxMass.peek () + 2) / 3.) * 3.;
				double minMass = Math.floor ( (this.minMass.peek () - 2) / 3.) * 3.;
				g2.drawLine (mouse.x, initMouse.y, initMouse.x, initMouse.y);
				double mass = ( ((double) (initMouse.x - startX) / (double) (endX - startX)))
						* (maxMass - minMass) + minMass;
				double intens = ((double) (initMouse.y - startY))
						/ (double) (endY - startY);
				if (mouse.x >= initMouse.x)
				{
					text = "" + Math.round (mass * 100.) / 100.;
					rect = fm.getStringBounds (text, g2);
					g2.drawString (text, initMouse.x + 3, initMouse.y + 3
							+ (int) rect.getHeight ());
					text = "" + Math.round (intens * 10000.) / 100. + " %";
					rect = fm.getStringBounds (text, g2);
					g2.drawString (text, initMouse.x + 3, initMouse.y - 3);
				}
				else
				{
					text = "" + Math.round (mass * 100.) / 100.;
					rect = fm.getStringBounds (text, g2);
					g2.drawString (text, initMouse.x - 3 - (int) rect.getWidth (),
							initMouse.y + 3 + (int) rect.getHeight ());
					text = "" + Math.round (intens * 10000.) / 100. + " %";
					rect = fm.getStringBounds (text, g2);
					g2.drawString (text, initMouse.x - 3 - (int) rect.getWidth (),
							initMouse.y - 3);
				}
				
				g2.drawLine (mouse.x, (int) startY + 6, mouse.x, (int) endY);
				g2.drawLine (initMouse.x, (int) startY + 6, initMouse.x, (int) endY);
			}
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
		this.setBackground (Color.WHITE);
		g2.setColor (Color.WHITE);
		g2.fillRect (getX (), getY (), WIDTH, HEIGHT);
		String text;
		Rectangle2D rect;
		
		originX = 50;
		originY = getHeight () - 50;
		maxX = getWidth () - 20;
		maxY = 20;
		startX = originX + 10;
		startY = originY;
		endX = maxX - 10;
		endY = maxY + 10;
		
		g2.setColor (new Color (250, 250, 250));
		g2.fillRect ((int) originX, (int) endY, (int) (maxX - originX),
				(int) ( (startY - endY) / 4));
		g2.fillRect ((int) originX, (int) (endY + (startY - endY) / 2),
				(int) (maxX - originX), (int) ( (startY - endY) / 4));
		g2.setColor (Color.BLACK);
		g2.drawLine ((int) originX, (int) originY, (int) originX, (int) endY);
		g2.drawLine ((int) originX, (int) originY, (int) maxX, (int) originY);
		
		double maxAbundance = 1;
		double maxMass = this.maxMass.peek ();
		double minMass = this.minMass.peek ();
		int px = 0;
		double round = maxMass - minMass;
		if (round > 100)
			round = 1;
		else if (round > 10)
			round = 10;
		else if (round > 1)
			round = 100;
		else if (round > .1)
			round = 10000;
		else
			round = 100000;
		
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
		
		text = "" + Math.round (maxMass * round) / round;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) endX, (int) originY, (int) endX, (int) originY + 5);
		g2.drawString (text, (int) (endX - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + Math.round (minMass * round) / round;
		rect = fm.getStringBounds (text, g2);
		g2.drawLine ((int) startX, (int) originY, (int) startX, (int) originY + 5);
		g2.drawString (text, (int) (startX - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + Math.round ( (minMass + (maxMass - minMass) * 3. / 4.) * round)
				/ round;
		rect = fm.getStringBounds (text, g2);
		px = (int) ( (endX - startX) * 3. / 4. + startX);
		g2.drawLine (px, (int) originY, px, (int) originY + 5);
		g2.drawString (text, (int) (px - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + Math.round ( ( (minMass + maxMass) / 2.) * round) / round;
		rect = fm.getStringBounds (text, g2);
		px = (int) ( (endX - startX) / 2. + startX);
		g2.drawLine (px, (int) originY, px, (int) originY + 5);
		g2.drawString (text, (int) (px - rect.getWidth () / 2.),
				(int) (originY + 10 + rect.getHeight ()));
		
		text = "" + Math.round ( (minMass + (maxMass - minMass) / 4.) * round)
				/ round;
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
			double max = 0;
			if (stretch)
				for (int i = 1; i < msModeVals.length; i++)
				{
					if (msModeVals[i] > max)
						max = msModeVals[i];
				}
			else
				max = 1;
			
			for (int i = 1; i < msModeVals.length; i++)
			{
				g2.drawLine (i - 1 + (int) startX, (int) (startY + (endY - startY)
						* msModeVals[i - 1]), i + (int) startX,
						(int) (startY + (endY - startY) * msModeVals[i] / max));
			}
		}
		else
		{
			double max = 0;
			if (stretch)
				for (int i = 0; i < peaks.size (); i++)
				{
					if (peaks.elementAt (i).mass < minMass
							|| peaks.elementAt (i).mass > maxMass)
						continue;
					if (peaks.elementAt (i).abundance > max)
						max = peaks.elementAt (i).abundance;
				}
			else
				max = 1;
			for (int i = 0; i < peaks.size (); i++)
			{
				Isotope p = peaks.elementAt (i);
				double x = startX
						+ ( ( (p.mass - minMass) / (maxMass - minMass)) * (endX - startX));
				g2.drawLine ((int) x, (int) originY, (int) x,
						(int) (startY + (endY - startY) * p.abundance / max));
			}
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint (java.awt.Graphics)
	 */
	public void paint (Graphics g)
	{
		super.paint (g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint (RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (peaks == null)
			drawAbout (g2);
		else
			drawPeaks (g2);
		drawMouse (g2);
	}
	

	/**
	 * Full unzoom.
	 */
	public void fullUnzoom ()
	{
		if (minMass == null)
			return;
		while (minMass.size () > 1)
		{
			msModeVals = null;
			minMass.pop ();
			maxMass.pop ();
		}
		repaint ();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentResized
	 * (java.awt.event.ComponentEvent)
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
	 * following: unnecessary stuff, but we have to implement it...
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentShown
	 * (java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown (ComponentEvent e)
	{
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentHidden
	 * (java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden (ComponentEvent e)
	{
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentMoved
	 * (java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved (ComponentEvent e)
	{
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked (java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked (MouseEvent e)
	{
		if (peaks != null && e.getClickCount () == 2
				&& e.getModifiers () == java.awt.event.InputEvent.BUTTON1_MASK)
		{
			if (minMass.size () > 1)
			{
				msModeVals = null;
				minMass.pop ();
				maxMass.pop ();
			}
			repaint ();
			return;
		}
		if (peaks != null
				&& e.getModifiers () == java.awt.event.InputEvent.BUTTON3_MASK)
		{
			javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu ();
			javax.swing.JMenuItem item = new javax.swing.JMenuItem ("Full unzoom");
			item.addActionListener (new java.awt.event.ActionListener ()
			{
				
				public void actionPerformed (java.awt.event.ActionEvent e)
				{
					fullUnzoom ();
				}
			});
			menu.add (item);
			menu.show (this, e.getX (), e.getY ());
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered (java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered (MouseEvent e)
	{
		setCursor (cursor);
		drawMouse = true;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited (java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited (MouseEvent e)
	{
		setCursor (new java.awt.Cursor (java.awt.Cursor.DEFAULT_CURSOR));
		drawMouse = false;
		initMouse = null;
		repaint ();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed (java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed (MouseEvent e)
	{
		if (peaks != null && initMouse == null && mouse != null)
		{
			initMouse = getMousePosition ();
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased (java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased (MouseEvent e)
	{
		
		if (mousemoved && initMouse != null && mouse != null
				&& e.getModifiers () == java.awt.event.InputEvent.BUTTON3_MASK)
		{
			msModeVals = null;
			double maxMass = this.maxMass.peek ();
			double minMass = this.minMass.peek ();
			double min = ( ((double) (initMouse.x - startX) / (double) (endX - startX)))
					* (maxMass - minMass) + minMass;
			double max = ( ((double) (mouse.x - startX) / (double) (endX - startX)))
					* (maxMass - minMass) + minMass;
			if (min > max)
			{
				double tmp = max;
				max = min;
				min = tmp;
			}
			this.minMass.push (min);
			this.maxMass.push (max);
			mousemoved = false;
			repaint ();
		}
		initMouse = null;
		
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged
	 * (java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged (MouseEvent e)
	{
		mousemoved = true;
		drawMouse = true;
		mouse = getMousePosition ();
		repaint ();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved
	 * (java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved (MouseEvent e)
	{
		drawMouse = true;
		mouse = getMousePosition ();
		repaint ();
	}
}
