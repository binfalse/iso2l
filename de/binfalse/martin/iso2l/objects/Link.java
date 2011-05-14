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
import java.awt.Cursor;
import java.awt.event.MouseEvent;



/**
 * The Link representation of a JLabel.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class Link
		extends javax.swing.JLabel
		implements java.awt.event.MouseListener
{
	
	/** ext stuff */
	private static final long	serialVersionUID	= 1L;
	
	/** The url for the link. */
	private String						url;
	
	
	/**
	 * Instantiates a new link.
	 */
	public Link ()
	{
		super ();
		init ("");
	}
	

	/**
	 * Instantiates a new link.
	 * 
	 * @param image
	 *          the icon
	 */
	public Link (javax.swing.Icon image)
	{
		super (image);
		init ("");
	}
	

	/**
	 * Instantiates a new link.
	 * 
	 * @param image
	 *          the icon
	 * @param horizontalAlignment
	 *          the horizontal alignment of the label
	 */
	public Link (javax.swing.Icon image, int horizontalAlignment)
	{
		super (image, horizontalAlignment);
		init ("");
	}
	

	/**
	 * Instantiates a new link.
	 * 
	 * @param text
	 *          the label text
	 */
	public Link (String text)
	{
		super (text);
		init (text);
	}
	

	/**
	 * Instantiates a new link.
	 * 
	 * @param text
	 *          the label text
	 * @param icon
	 *          the icon
	 * @param horizontalAlignment
	 *          the horizontal alignment of the label
	 */
	public Link (String text, javax.swing.Icon icon, int horizontalAlignment)
	{
		super (text, icon, horizontalAlignment);
		init (text);
	}
	

	/**
	 * Sets the URL.
	 * 
	 * @param url
	 *          the new URL
	 */
	public void setURL (String url)
	{
		this.url = url;
		this.setToolTipText ("Open " + url + " in your browser");
	}
	

	/**
	 * Initializes the Link.
	 * 
	 * @param url
	 *          the URL
	 */
	private void init (String url)
	{
		setURL (url);
		this.addMouseListener (this);
		this.setForeground (Color.BLUE);
	}
	

	/**
	 * Instantiates a new link.
	 * 
	 * @param text
	 *          the label text
	 * @param horizontalAlignment
	 *          the horizontal alignment of the label
	 */
	public Link (String text, int horizontalAlignment)
	{
		super (text, horizontalAlignment);
		init (text);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked (MouseEvent arg0)
	{
		browse ();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered (MouseEvent arg0)
	{
		setCursor (new Cursor (Cursor.HAND_CURSOR));
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited (MouseEvent arg0)
	{
		setCursor (new Cursor (Cursor.DEFAULT_CURSOR));
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed (MouseEvent arg0)
	{
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased (MouseEvent arg0)
	{
	}
	

	/**
	 * Browse to the link using a browser.
	 */
	private void browse ()
	{
		if (java.awt.Desktop.isDesktopSupported ())
		{
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop ();
			if (desktop.isSupported (java.awt.Desktop.Action.BROWSE))
			{
				try
				{
					desktop.browse (new java.net.URI (url));
					return;
				}
				catch (java.io.IOException e)
				{
					e.printStackTrace ();
				}
				catch (java.net.URISyntaxException e)
				{
					e.printStackTrace ();
				}
			}
		}
		
		String osName = System.getProperty ("os.name");
		try
		{
			if (osName.startsWith ("Windows"))
			{
				Runtime.getRuntime ().exec (
						"rundll32 url.dll,FileProtocolHandler " + url);
			}
			else if (osName.startsWith ("Mac OS"))
			{
				Class<?> fileMgr = Class.forName ("com.apple.eio.FileManager");
				java.lang.reflect.Method openURL = fileMgr.getDeclaredMethod (
						"openURL", new Class[] { String.class });
				openURL.invoke (null, new Object[] { url });
			}
			else
			{
				// check for $BROWSER
				java.util.Map<String, String> env = System.getenv ();
				if (env.get ("BROWSER") != null)
				{
					Runtime.getRuntime ().exec (env.get ("BROWSER") + " " + url);
					return;
				}
				
				// check for common browsers
				String[] browsers = { "firefox", "iceweasel", "chrome", "opera",
						"konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime ().exec (
							new String[] { "which", browsers[count] }).waitFor () == 0)
					{
						browser = browsers[count];
						break;
					}
				if (browser == null)
					throw new RuntimeException ("couldn't find any browser...");
				else
					Runtime.getRuntime ().exec (new String[] { browser, url });
			}
		}
		catch (Exception e)
		{
			javax.swing.JOptionPane.showMessageDialog (null,
					"couldn't find a webbrowser to use...\nPlease browser for yourself:\n"
							+ url);
		}
	}
}
