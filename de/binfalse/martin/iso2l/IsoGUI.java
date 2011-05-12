package de.binfalse.martin.iso2l;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.binfalse.martin.iso2l.objects.Isotope;
import de.binfalse.martin.iso2l.objects.Link;
import de.binfalse.martin.iso2l.objects.PeakViewer;



/**
 * The Class IsoGUI.
 * 
 * @author Martin Scharm
 *         visit http://binfalse.de
 */
public class IsoGUI
		extends javax.swing.JFrame
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= 1L;
	
	/** The operator. */
	private Operator					op;
	
	/** num form. */
	private NumberFormat			numForm;
	
	/** The vec of peaks. */
	private Vector<Isotope>		peaks;
	
	/**
	 * The vec of peaks to present, we'll change this object without loosing
	 * information about the real values...
	 */
	private Vector<Isotope>		presentingPeaks;
	
	/** The min abundance. */
	private double						minAbundance;
	
	
	/**
	 * Instantiates a new iso GUI.
	 */
	public IsoGUI ()
	{
		op = new Operator ();
		minAbundance = Operator.minAbundance;
		init ();
		numForm = NumberFormat.getInstance ();
		numForm.setGroupingUsed (false);
		numForm.setMaximumFractionDigits (20);
	}
	

	/**
	 * Change status.
	 * 
	 * @param status
	 *          the status message to display
	 */
	public void changeStatus (String status)
	{
		this.jLabelStatus.setText ("<html>Status: " + status + "</html>");
	}
	

	/**
	 * Change status to default status.
	 */
	public void changeStatus ()
	{
		this.jLabelStatus.setText ("Status: Let's go!");
	}
	

	/**
	 * Save the graphics as PNG image.
	 */
	private void saveImage ()
	{
		// maybe add JPG or GIF or smth else..
		JFileChooser fc = new JFileChooser (".");
		String name = jTextFieldForm.getText ();
		if (jTextFieldDispName.getText ().length () > 0)
			name = jTextFieldDispName.getText () + "-" + name;
		if (name.length () < 1)
			name = "iso2l";
		fc.setSelectedFile (new File (name
				+ "_"
				+ new java.text.SimpleDateFormat ("yyyy-MM-dd_HH-mm")
						.format (new java.util.Date ()) + ".png"));
		fc.setFileFilter (new javax.swing.filechooser.FileFilter ()
		{
			
			public boolean accept (File f)
			{
				return f.getName ().toLowerCase ().endsWith (".png")
						|| f.isDirectory ();
			}
			

			public String getDescription ()
			{
				return "PNG images (*.png)";
			}
		});
		changeStatus ("choose a file!");
		int returnVal = fc.showSaveDialog (this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			changeStatus ("Saving...");
			File imageFile = fc.getSelectedFile ();
			BufferedImage bufImage = new BufferedImage (jPanelGraph.getSize ().width,
					jPanelGraph.getSize ().height, BufferedImage.TYPE_INT_RGB);
			jPanelGraph.paint (bufImage.createGraphics ());
			try
			{
				imageFile.createNewFile ();
				javax.imageio.ImageIO.write (bufImage, "png", imageFile);
			}
			catch (Exception ex)
			{
				changeStatus ("Writing failed: <font color='red'>" + ex.getMessage ()
						+ "</font>");
			}
			changeStatus ();
		}
		else
		{
			changeStatus ("<font color='red'>exporting aborted</font>");
		}
	}
	

	/**
	 * Copy table to clipboard.
	 */
	private void copyTable ()
	{
		String s = "";
		javax.swing.table.TableModel tm = jTableIsos.getModel ();
		for (int i = 0; i < tm.getRowCount (); i++)
		{
			s += tm.getValueAt (i, 0) + "\t" + tm.getValueAt (i, 1) + "\n";
		}
		java.awt.Toolkit.getDefaultToolkit ().getSystemClipboard ().setContents (
				new java.awt.datatransfer.StringSelection (s), null);
		java.awt.Toolkit.getDefaultToolkit ().getSystemSelection ().setContents (
				new java.awt.datatransfer.StringSelection (s), null);
		changeStatus ("Copied table to clipboard!");
	}
	

	/**
	 * Calc the clusters, this is the method to start with..
	 */
	private void calc ()
	{
		presentingPeaks = null;
		String formular = jTextFieldForm.getText ();
		changeStatus ("parsing formular");
		String t = (String) jComboBoxTypeChooser.getSelectedItem ();
		int type = 0;
		if (t.startsWith ("Chemical formular"))
			type = 1;
		if (t.startsWith ("1-Letter Amino Acids"))
			type = 2;
		if (t.startsWith ("3-Letter Amino Acids"))
			type = 3;
		if (formular.length () < 1 || !op.parseFormular (formular, type, this))
		{
			changeStatus ("formular parsing failed!");
			return;
		}
		changeStatus ("calculating distribution");
		if (!op.calcDistribution (op.isos/* , jCheckBoxStretcher.isSelected () */))
		{
			changeStatus ("calculation failed!");
			return;
		}
		
		peaks = op.getPeaks ();
		if (peaks.size () < 1)
		{
			changeStatus ("found no peaks...");
			drawImage ();
			return;
		}
		postProcess ();
	}
	

	/**
	 * Draw about image.
	 */
	private void drawAbout ()
	{
		jPanelGraph.drawAbout ();
		jTableIsos.setModel (new javax.swing.table.DefaultTableModel (
				new Object[][] {}, new String[] { "Mass", "Abundance" }));
		changeStatus ();
	}
	

	/**
	 * sets the min abundance.
	 */
	private void setMinAbundance ()
	{
		String s = (String) javax.swing.JOptionPane.showInputDialog (this,
				"Minimum abundance", "Adjust Value",
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (s == null)
			return;
		double neu = -1;
		try
		{
			neu = Double.parseDouble (s);
		}
		catch (java.lang.NumberFormatException e)
		{
			javax.swing.JOptionPane.showMessageDialog (this,
					"Input is not numeric!\nUsing default value!", "Ooops",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			jLabelMinAbu.setText ("Minimum abu. " + Operator.minAbundance);
			changeStatus ("Input failed!");
			return;
		}
		if (neu >= 1 && neu < Operator.minAbundance)
			javax.swing.JOptionPane.showMessageDialog (this,
					"not a valid value... Has to be <1 && >=" + Operator.minAbundance,
					"Ooops", javax.swing.JOptionPane.ERROR_MESSAGE);
		minAbundance = neu;
		jLabelMinAbu.setText ("Minimum abu. " + neu);
		changeStatus ();
		postProcess ();
	}
	

	/**
	 * Tell the viewer to draw a new image.
	 */
	private void drawImage ()
	{
		if (presentingPeaks == null)
		{
			drawAbout ();
		}
		else
		{
			if (jCheckBoxDispName.isSelected ())
			{
				if (jTextFieldDispName.getText ().length () > 0)
					jPanelGraph.setFormulaName (jTextFieldDispName.getText ());
				else
					jPanelGraph.setFormulaName (jTextFieldForm.getText ());
			}
			else
				jPanelGraph.setFormulaName (null);
			
			jPanelGraph.setMsMode (jCheckBoxMS.isSelected ());
			
			jPanelGraph.drawPeaks (presentingPeaks);
		}
	}
	

	/**
	 * Post processing chain.
	 */
	private void postProcess ()
	{
		if (peaks == null)
			return;
		presentingPeaks = new Vector<Isotope> ();
		for (int i = 0; i < peaks.size (); i++)
			presentingPeaks.add (peaks.elementAt (i).copy ());
		
		try
		{
			calcMS ();
			tryToStretch ();
			round ();
			filterAbundance ();
			fillTable ();
			drawImage ();
			changeStatus ();
		}
		catch (ProcessingException e)
		{
			changeStatus ("<font color='red'>" + e.getMessage () + "</font>");
			drawImage ();
		}
		
	}
	

	/**
	 * Tell the viewer to show mountains instead of peaks.
	 * 
	 * @throws ProcessingException
	 *           the processing exception if something went wrong
	 */
	private void calcMS () throws ProcessingException
	{
		if (!jCheckBoxMS.isSelected ())
			return;
		
		double res = -1;
		try
		{
			res = Double.parseDouble (jTextFieldResolution.getText ());
		}
		catch (java.lang.NumberFormatException e)
		{
			throw new ProcessingException ("invalid value for resolution");
		}
		
		if (res <= 0)
			return;
		
		jPanelGraph.setMsRes (res);
	}
	

	/**
	 * Filter abundance.
	 */
	private void filterAbundance ()
	{
		for (int i = presentingPeaks.size () - 1; i >= 0; i--)
		{
			if (presentingPeaks.elementAt (i).abundance < minAbundance)
				presentingPeaks.remove (i);
		}
	}
	

	/**
	 * round the values.
	 * 
	 * @throws ProcessingException
	 *           the processing exception if something went wrong
	 */
	private void round () throws ProcessingException
	{
		double roundMass = -1;
		double roundAbun = -1;
		try
		{
			roundAbun = Double.parseDouble (jTextFieldRoundAbun.getText ());
		}
		catch (java.lang.NumberFormatException e)
		{
			throw new ProcessingException ("Round value for abu. "
					+ jTextFieldRoundAbun.getText ()
					+ " is not numeric! To suppress roundings leave it <= 0");
		}
		try
		{
			roundMass = Double.parseDouble (jTextFieldRoundMass.getText ());
		}
		catch (java.lang.NumberFormatException e)
		{
			throw new ProcessingException ("Round value for mass "
					+ jTextFieldRoundMass.getText ()
					+ " is not numeric! To suppress roundings leave it <= 0");
		}
		if (roundAbun > 1000000000)
		{
			throw new ProcessingException ("Round value for abu. "
					+ jTextFieldRoundAbun.getText () + " should be &lt; 10<sup>9</sup>");
		}
		if (roundMass > 1000000000)
		{
			throw new ProcessingException ("Round value for mass "
					+ jTextFieldRoundMass.getText () + " should be &lt; 10<sup>9</sup>");
		}
		
		if (roundMass > 0 || roundAbun > 0)
		{
			changeStatus ("rounding");
			for (int i = 0; i < presentingPeaks.size (); i++)
			{
				if (roundMass > 0)
					presentingPeaks.elementAt (i).mass = Math.round (presentingPeaks
							.elementAt (i).mass
							* roundMass)
							/ roundMass;
				if (roundAbun > 0)
				{
					presentingPeaks.elementAt (i).abundance = Math.round (presentingPeaks
							.elementAt (i).abundance
							* roundAbun)
							/ roundAbun;
					if (presentingPeaks.elementAt (i).abundance == 0)
						presentingPeaks.remove (i--);
				}
			}
			// some masses meeting?
			if (roundMass > 0)
				for (int i = 0; i < presentingPeaks.size (); i++)
					for (int j = i + 1; j < presentingPeaks.size (); j++)
						if (presentingPeaks.elementAt (i).mass == presentingPeaks
								.elementAt (j).mass)
						{
							presentingPeaks.elementAt (i).abundance += presentingPeaks
									.elementAt (j).abundance;
							presentingPeaks.remove (j--);
						}
		}
		
	}
	

	/**
	 * Fill table with isotopes.
	 */
	private void fillTable ()
	{
		String[][] model = new String[presentingPeaks.size ()][2];
		for (int i = 0; i < presentingPeaks.size (); i++)
		{
			model[i][0] = "" + numForm.format (presentingPeaks.elementAt (i).mass);
			model[i][1] = ""
					+ numForm.format (presentingPeaks.elementAt (i).abundance);
		}
		jTableIsos.setModel (new javax.swing.table.DefaultTableModel (model,
				new String[] { "Mass", "Abundance" }));
		
	}
	

	/**
	 * stretch the peaks if the user checked the box.
	 */
	private void tryToStretch ()
	{
		if (presentingPeaks == null || !jCheckBoxStretcher.isSelected ())
			return;
		
		double max = 0;
		for (int i = 0; i < presentingPeaks.size (); i++)
		{
			if (max < presentingPeaks.get (i).abundance)
				max = presentingPeaks.get (i).abundance;
		}
		for (int i = 0; i < presentingPeaks.size (); i++)
			presentingPeaks.get (i).abundance /= max;
		
		String[][] model = new String[presentingPeaks.size ()][2];
		for (int i = 0; i < presentingPeaks.size (); i++)
		{
			model[i][0] = "" + numForm.format (presentingPeaks.elementAt (i).mass);
			model[i][1] = ""
					+ numForm.format (presentingPeaks.elementAt (i).abundance);
		}
		jTableIsos.setModel (new javax.swing.table.DefaultTableModel (model,
				new String[] { "Mass", "Abundance" }));
	}
	

	/**
	 * init the window.
	 */
	private void init ()
	{
		this.setLocation (100, 100);
		this.setTitle ("iso2l - by Martin Scharm");
		
		jComboBoxTypeChooser = new javax.swing.JComboBox ();
		jTextFieldForm = new javax.swing.JTextField ();
		jButtonCalc = new javax.swing.JButton ();
		jButtonInfo = new javax.swing.JButton ();
		jLabelMinAbu = new javax.swing.JLabel ();
		jCheckBoxStretcher = new javax.swing.JCheckBox ();
		jCheckBoxDispName = new javax.swing.JCheckBox ();
		jLabelDispName = new javax.swing.JLabel ();
		jTextFieldDispName = new javax.swing.JTextField ();
		jScrollPaneTableIsos = new javax.swing.JScrollPane ();
		jTableIsos = new javax.swing.JTable ();
		jButtonCopy = new javax.swing.JButton ();
		jButtonSave = new javax.swing.JButton ();
		jLabelLink = new Link ();
		jPanelGraph = new PeakViewer ();
		jLabelRoundMass = new javax.swing.JLabel ();
		jLabelRoundAbun = new javax.swing.JLabel ();
		jTextFieldRoundMass = new javax.swing.JTextField ();
		jTextFieldRoundAbun = new javax.swing.JTextField ();
		jSeparatorStatus = new javax.swing.JSeparator ();
		jLabelStatus = new javax.swing.JLabel ();
		jCheckBoxMS = new javax.swing.JCheckBox ();
		jTextFieldResolution = new javax.swing.JTextField ();
		
		jTextFieldResolution.setText ("8000");
		jTextFieldResolution.setHorizontalAlignment (JTextField.RIGHT);
		jCheckBoxMS.setToolTipText ("Enable Mass-Spec mode");
		jCheckBoxMS.setText ("MS mode, resoltion: ");
		jCheckBoxMS.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				postProcess ();
			}
		});
		
		setDefaultCloseOperation (javax.swing.WindowConstants.EXIT_ON_CLOSE);
		
		jComboBoxTypeChooser.setModel (new javax.swing.DefaultComboBoxModel (
				new String[] { "Try to detect", "Chemical formular",
						"1-Letter Amino Acids", "3-Letter Amino Acids" }));
		
		jTextFieldForm.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent e)
			{
				calc ();
			}
		});
		
		jButtonCalc.setText ("calc");
		jButtonCalc.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				calc ();
			}
		});
		
		jButtonInfo.setText ("info");
		jButtonInfo.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				drawAbout ();
			}
		});
		
		jLabelMinAbu.setText ("Minimum abu. " + Operator.minAbundance);
		jLabelMinAbu.setToolTipText ("adjust this value");
		jLabelMinAbu.addMouseListener (new java.awt.event.MouseListener ()
		{
			
			public void mouseClicked (MouseEvent arg0)
			{
				setMinAbundance ();
			}
			

			public void mouseEntered (MouseEvent arg0)
			{
			}
			

			public void mouseExited (MouseEvent arg0)
			{
			}
			

			public void mousePressed (MouseEvent arg0)
			{
			}
			

			public void mouseReleased (MouseEvent arg0)
			{
			}
		});
		
		jCheckBoxStretcher.setText ("Stretch peaks");
		jCheckBoxStretcher.setToolTipText ("Scale peaks to 100%");
		jCheckBoxStretcher.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				postProcess ();
			}
		});
		
		jCheckBoxDispName.setText ("Display Name");
		jCheckBoxDispName
				.setToolTipText ("Write a name of this cluster to the pic");
		jCheckBoxDispName.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				drawImage ();
			}
		});
		
		jLabelDispName.setText ("Name:");
		jLabelDispName
				.setToolTipText ("Provide an alternative name of this cluster");
		
		jTextFieldDispName.getDocument ().addDocumentListener (
				new javax.swing.event.DocumentListener ()
				{
					
					public void changedUpdate (DocumentEvent arg0)
					{
						jCheckBoxDispName.setSelected (true);
						drawImage ();
					}
					

					public void insertUpdate (DocumentEvent arg0)
					{
						jCheckBoxDispName.setSelected (true);
						drawImage ();
					}
					

					public void removeUpdate (DocumentEvent arg0)
					{
						jCheckBoxDispName.setSelected (true);
						drawImage ();
					}
				});
		
		jTableIsos.setModel (new javax.swing.table.DefaultTableModel (
				new Object[][] {}, new String[] { "Mass", "Abundance" }));
		jTableIsos.setAutoCreateRowSorter (true);
		jScrollPaneTableIsos.setViewportView (jTableIsos);
		
		jButtonCopy.setText ("copy table");
		jButtonCopy.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				copyTable ();
			}
		});
		
		jButtonSave.setText ("save graphics");
		jButtonSave.addActionListener (new java.awt.event.ActionListener ()
		{
			
			public void actionPerformed (java.awt.event.ActionEvent evt)
			{
				saveImage ();
			}
		});
		
		jLabelLink.setText ("http://binfalse.de");
		jLabelLink.setURL ("http://binfalse.de");
		jLabelLink.setHorizontalAlignment (javax.swing.JLabel.RIGHT);
		
		javax.swing.GroupLayout jPanelGraphLayout = new javax.swing.GroupLayout (
				jPanelGraph);
		jPanelGraph.setLayout (jPanelGraphLayout);
		jPanelGraphLayout.setHorizontalGroup (jPanelGraphLayout
				.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
				.addGap (0, 846, Short.MAX_VALUE));
		jPanelGraphLayout.setVerticalGroup (jPanelGraphLayout.createParallelGroup (
				javax.swing.GroupLayout.Alignment.LEADING).addGap (0, 347,
				Short.MAX_VALUE));
		
		javax.swing.event.DocumentListener docl = new javax.swing.event.DocumentListener ()
		{
			
			public void changedUpdate (DocumentEvent arg0)
			{
				postProcess ();
			}
			

			public void insertUpdate (DocumentEvent arg0)
			{
				postProcess ();
			}
			

			public void removeUpdate (DocumentEvent arg0)
			{
				postProcess ();
			}
		};
		
		jLabelRoundMass.setText ("Round mass:");
		jLabelRoundAbun.setText ("Round abun.:");
		jTextFieldRoundMass.setText ("10000000");
		jTextFieldRoundAbun.setText ("10000000");
		jTextFieldRoundMass.setHorizontalAlignment (JTextField.RIGHT);
		jTextFieldRoundAbun.setHorizontalAlignment (JTextField.RIGHT);
		jTextFieldRoundAbun.getDocument ().addDocumentListener (docl);
		jTextFieldRoundMass.getDocument ().addDocumentListener (docl);
		jTextFieldResolution.getDocument ().addDocumentListener (docl);
		
		jLabelStatus.setText ("Status:");
		changeStatus ();
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout (
				getContentPane ());
		getContentPane ().setLayout (layout);
		layout
				.setHorizontalGroup (layout
						.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup (
								layout
										.createSequentialGroup ()
										.addContainerGap ()
										.addGroup (
												layout
														.createParallelGroup (
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup (
																layout.createSequentialGroup ().addComponent (
																		jSeparatorStatus,
																		javax.swing.GroupLayout.DEFAULT_SIZE, 846,
																		Short.MAX_VALUE).addContainerGap ())
														.addGroup (
																layout.createSequentialGroup ().addComponent (
																		jPanelGraph,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE).addContainerGap ())
														.addGroup (
																layout
																		.createSequentialGroup ()
																		.addGroup (
																				layout
																						.createParallelGroup (
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent (
																								jComboBoxTypeChooser,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent (jCheckBoxStretcher)
																						.addComponent (jCheckBoxDispName)
																						.addGroup (
																								layout
																										.createSequentialGroup ()
																										.addComponent (jCheckBoxMS)
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent (
																												jTextFieldResolution,
																												20, 45, 45))
																						.addGroup (
																								layout
																										.createSequentialGroup ()
																										.addGap (21, 21, 21)
																										.addComponent (
																												jLabelDispName)
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent (
																												jTextFieldDispName,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												75, 140)))
																		.addPreferredGap (
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup (
																				layout
																						.createParallelGroup (
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent (
																								jTextFieldForm,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								566, Short.MAX_VALUE)
																						.addGroup (
																								javax.swing.GroupLayout.Alignment.LEADING,
																								layout
																										.createSequentialGroup ()
																										.addGroup (
																												layout
																														.createParallelGroup (
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addGroup (
																																layout
																																		.createParallelGroup (
																																				javax.swing.GroupLayout.Alignment.LEADING,
																																				false)
																																		.addGroup (
																																				layout
																																						.createSequentialGroup ()
																																						.addComponent (
																																								jLabelRoundAbun)
																																						.addPreferredGap (
																																								javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																						.addComponent (
																																								jTextFieldRoundAbun,
																																								javax.swing.GroupLayout.DEFAULT_SIZE,
																																								70,
																																								Short.MAX_VALUE))
																																		.addGroup (
																																				layout
																																						.createSequentialGroup ()
																																						.addComponent (
																																								jLabelRoundMass)
																																						.addPreferredGap (
																																								javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																						.addComponent (
																																								jTextFieldRoundMass,
																																								javax.swing.GroupLayout.DEFAULT_SIZE,
																																								70,
																																								Short.MAX_VALUE)))
																														.addComponent (
																																jLabelMinAbu))
																										.addGap (18, 18, 18)
																										.addComponent (
																												jScrollPaneTableIsos,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												235, Short.MAX_VALUE)))
																		.addPreferredGap (
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup (
																				layout
																						.createParallelGroup (
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addComponent (
																								jLabelLink,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent (
																								jButtonSave,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addGroup (
																								layout
																										.createSequentialGroup ()
																										.addComponent (jButtonCalc)
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent (jButtonInfo))
																						.addComponent (
																								jButtonCopy,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addContainerGap ()).addGroup (
																layout.createSequentialGroup ().addComponent (
																		jLabelStatus,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)))));
		layout
				.setVerticalGroup (layout
						.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup (
								layout
										.createSequentialGroup ()
										.addContainerGap ()
										.addGroup (
												layout.createParallelGroup (
														javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent (jComboBoxTypeChooser,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent (jTextFieldForm).addComponent (
																jButtonInfo).addComponent (jButtonCalc))
										.addGroup (
												layout
														.createParallelGroup (
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup (
																layout
																		.createSequentialGroup ()
																		.addGap (4, 4, 4)
																		.addGroup (
																				layout
																						.createParallelGroup (
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup (
																								layout
																										.createSequentialGroup ()
																										.addComponent (jLabelMinAbu)
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addGroup (
																												layout
																														.createParallelGroup (
																																javax.swing.GroupLayout.Alignment.BASELINE)
																														.addComponent (
																																jLabelRoundAbun)
																														.addComponent (
																																jTextFieldRoundAbun,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE)
																														.addComponent (
																																jCheckBoxDispName))
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addGroup (
																												layout
																														.createParallelGroup (
																																javax.swing.GroupLayout.Alignment.BASELINE)
																														.addComponent (
																																jLabelRoundMass)
																														.addComponent (
																																jTextFieldRoundMass,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE)
																														.addComponent (
																																jLabelDispName)
																														.addComponent (
																																jTextFieldDispName,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE))
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addGroup (
																												layout
																														.createParallelGroup (
																																javax.swing.GroupLayout.Alignment.BASELINE)
																														.addComponent (
																																jCheckBoxMS)
																														.addComponent (
																																jTextFieldResolution,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE)))
																						.addGroup (
																								layout
																										.createSequentialGroup ()
																										.addComponent (jButtonCopy)
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent (jButtonSave)
																										.addPreferredGap (
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent (jLabelLink))))
														.addComponent (jScrollPaneTableIsos,
																javax.swing.GroupLayout.PREFERRED_SIZE, 104,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent (jCheckBoxStretcher))
										.addPreferredGap (
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent (jPanelGraph, 220,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addPreferredGap (
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent (jSeparatorStatus,
												javax.swing.GroupLayout.PREFERRED_SIZE, 2,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap (
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent (jLabelStatus).addContainerGap (10, 10)));
		
		pack ();
	}
	
	/**
	 * The ProcessingException.
	 * 
	 * used to take care of processing pathways
	 */
	private class ProcessingException
			extends Exception
	{
		
		/** ext stuff */
		private static final long	serialVersionUID	= 1339810279736153946L;
		
		
		/**
		 * Instantiates a new processing exception.
		 * 
		 * @param message
		 *          the error message
		 */
		public ProcessingException (String message)
		{
			super (message);
		}
	}
	
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *          the arguments
	 */
	public static void main (String[] args)
	{
		java.awt.EventQueue.invokeLater (new Runnable ()
		{
			
			public void run ()
			{
				new IsoGUI ().setVisible (true);
			}
		});
	}
	
	private javax.swing.JButton			jButtonCalc;
	private javax.swing.JButton			jButtonCopy;
	private javax.swing.JButton			jButtonInfo;
	private javax.swing.JButton			jButtonSave;
	private javax.swing.JCheckBox		jCheckBoxDispName;
	private javax.swing.JCheckBox		jCheckBoxMS;
	private javax.swing.JCheckBox		jCheckBoxStretcher;
	private javax.swing.JComboBox		jComboBoxTypeChooser;
	private javax.swing.JLabel			jLabelDispName;
	private javax.swing.JLabel			jLabelMinAbu;
	private javax.swing.JLabel			jLabelRoundMass;
	private javax.swing.JLabel			jLabelRoundAbun;
	private javax.swing.JLabel			jLabelStatus;
	private javax.swing.JScrollPane	jScrollPaneTableIsos;
	private javax.swing.JSeparator	jSeparatorStatus;
	private javax.swing.JTable			jTableIsos;
	private javax.swing.JTextField	jTextFieldDispName;
	private javax.swing.JTextField	jTextFieldForm;
	private javax.swing.JTextField	jTextFieldResolution;
	private javax.swing.JTextField	jTextFieldRoundMass;
	private javax.swing.JTextField	jTextFieldRoundAbun;
	private Link										jLabelLink;
	private PeakViewer							jPanelGraph;
}
