package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

import BitTorrentWebService.Torrent;

public class ClientUI {

	private JFrame jFrame;
	private JDialog jd;
	private ArrayList<OngoingTorrent> listOfTorrents;
	private JTable table;
	private String clientName;
	
	private JPanel main;
	private JScrollPane detailsView;
	private JScrollPane connectionsView;
	
	public ClientUI(String clientName) {
		setUIFont();
		this.clientName = clientName;
		this.listOfTorrents = new ArrayList<OngoingTorrent>();
		jFrame = new JFrame("BitTorrent");
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(jFrame, 
		            "Are you sure you want to close this window?", "Exit", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	saveTorrents();
		            System.exit(0);
		        }
		    }
		});
		
		Component content = createComponents();
		jFrame.getContentPane().add(content);
		jFrame.pack();
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
	}
	
	private Component createComponents() {

		String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
//		String lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
//		String lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
//		String lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {

		}

		main = new JPanel();
		main.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		Border border = BorderFactory.createEtchedBorder();
		main.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Add hello label
		JPanel helloLabels = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1;
		constraints.weighty = 2;
		main.add(helloLabels, constraints); 

		JLabel helloMessage = new JLabel("Welcome, " + clientName + "!");
		helloMessage.setFont(new Font(Font.SERIF, Font.BOLD, 18));
		helloLabels.add(helloMessage);

		// Add buttons
		JPanel buttons = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1;
		constraints.weighty = 2;

		JButton dummy1 = new JButton("XXXXXXXXXXXX");
		JButton download = new JButton("Download");
		download.setPreferredSize(dummy1.getPreferredSize());
		download.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createDownload();
			}

		});
		buttons.add(download);
		
		JButton upload = new JButton("Upload");
		upload.setPreferredSize(dummy1.getPreferredSize());
		upload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createUpload();
			}

		});
		buttons.add(upload);

		main.add(buttons, constraints);

		JScrollPane listPane = new JScrollPane();

		listPane.setBorder(BorderFactory.createTitledBorder(border, "Ongoing Tasks"));
		listPane.setMinimumSize(listPane.getPreferredSize());
		listPane.setMaximumSize(listPane.getPreferredSize());
		listPane.setPreferredSize(listPane.getPreferredSize());

		
		// list of torrents
		table = new JTable(new DefaultTableModel(Utility.COLUMNS, 0));

		// Configure some of JTable's paramters
		table.setShowHorizontalLines(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(700);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		
		// Change the selection colour
		table.setSelectionForeground(Color.WHITE);
		table.setSelectionBackground(Color.BLUE);

		// Add the torrents
		getTorrents();
//		getStaticTorrents();

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(!event.getValueIsAdjusting()) {
					ListSelectionModel index = (ListSelectionModel) event.getSource();
					
					OngoingTorrent ot = listOfTorrents.get(index.getMinSelectionIndex());
					torrentSelected(ot);
				}
			}
		});
		
		// Add the table to a scrolling pane
		listPane.setViewportView(table);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 10);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1;
		constraints.weighty = 41;

		main.add(listPane, constraints);
		
		// Add details view
		detailsView = getDetailsView(null);
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 20;
		constraints.weighty = 55;

		main.add(detailsView, constraints);
		
		// Add connections view
		connectionsView = getConnectionsView(null);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 80;
		constraints.weighty = 55;

		main.add(connectionsView, constraints);
		
		return main;
	}

	protected void createDownload() {
		JFileChooser jfc = new JFileChooser();
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Torrent Files (*.torrent)";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.getName().endsWith(".torrent")) {
					return true;
				} else {
					return false;
				}
			}
		});
		jfc.showOpenDialog(null);
		
		File torrentFile = jfc.getSelectedFile();
		
		Torrent t = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(torrentFile));
			t = (Torrent) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			JOptionPane.showMessageDialog(null, "Invalid file! Please use proper *.torrent file");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if(t != null) {
			OngoingTorrent ot = new OngoingTorrent(t, "", false);
			addTorrent(ot);
			startDownload(ot);
		}
	}

	protected void createUpload() {
		JPanel main = new JPanel();
		Dimension d = new Dimension(600, 100);
		main.setPreferredSize(d);
		main.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		main.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Add Filename
		JLabel fileNameLbl = new JLabel("File: ");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 15;
		constraints.weighty = 2;
		main.add(fileNameLbl, constraints);

		// Add label
		final JTextField filename = new JTextField();
		filename.setEditable(false);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 75;
		constraints.weighty = 2;
		main.add(filename, constraints);

		// Add Browse button
		JButton browse = new JButton("Browse");
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 10;
		constraints.weighty = 2;
		main.add(browse, constraints);

		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(new File(System.getProperty("user.dir")));
				jfc.setAcceptAllFileFilterUsed(true);
				jfc.showOpenDialog(null);
				
				File f = jfc.getSelectedFile();
				filename.setText(f.getAbsolutePath());
				
			}
		});
		
		// Add chunkLbl
		JLabel chunkLbl = new JLabel("Number of chunks: ");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 15;
		constraints.weighty = 2;
		main.add(chunkLbl, constraints);

		// Add chunks
		final JTextField chunks = new JTextField();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 85;
		constraints.weighty = 2;
		main.add(chunks, constraints);
		
		// Add Upload button
		JButton upload = new JButton("Upload");
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 10;
		constraints.weighty = 2;
		main.add(upload, constraints);
		
		upload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "";
				long fileSize = new File(filename.getText()).length();
				int numOfChunks = 1;
				try {
					if(fileSize == 0) {
						message = "Please select a file";
					} else {
						if(!chunks.getText().matches("^[0-9]+$")) {
							message = "Please input numbers only";
							throw new Exception();
						};
						numOfChunks = Integer.parseInt(chunks.getText());
						int chunksize = (int)(fileSize / numOfChunks);
						int lastChunkSize = (int)((fileSize % numOfChunks) + chunksize);
						System.out.println(fileSize + " = " + chunksize + " * " + (numOfChunks - 1) + " + " + lastChunkSize);
						if(lastChunkSize > Utility.MAX_CHUNK_SIZE) {
							message = "Please use a higher number of chunks";
						}
					}
				} catch(NumberFormatException ex) {
					message = "Please use a number less than " + Integer.MAX_VALUE;
				} catch (Exception e1) {
					
				}
				
				if(message.equals("")) {
					jd.dispose();
					jd.setVisible(false);
					String filenameStr = filename.getText();

					filenameStr = filenameStr.substring(filenameStr.lastIndexOf(File.separatorChar) + 1, filenameStr.length());
					Torrent t = initUpload(filenameStr, FileHandler.getHash(filenameStr), fileSize, numOfChunks);
					if(t != null) {
//						OngoingTorrent ot = new OngoingTorrent(t, filenameStr.substring(0, filenameStr.lastIndexOf(".")), true);
						OngoingTorrent ot = new OngoingTorrent(t, "", true);
						addTorrent(ot);
					}
				} else {
					JOptionPane.showMessageDialog(null, message);
				}
			}
		});
		
		jd = new JDialog(jFrame, "Upload a file", Dialog.ModalityType.APPLICATION_MODAL);
		jd.getContentPane().add(main);
		jd.pack();
		jd.setLocationRelativeTo(null);
		jd.setVisible(true);

	}
	
	protected Torrent initUpload(String filenameStr, String hash, long fileSize, int numOfChunks) {
        String line = null;
		try {
			// Call Restful service and get Torrent
			System.out.println("Sending file details to web service");
			String url = "http://" + Utility.WEB_SERVICE_IP + ":8080/BitTorrentWebService/webresources/resource?clientIP=" + InetAddress.getLocalHost().getHostAddress() + "&fileName=" + filenameStr + "&fileHash=" + hash + "&fileSize=" + fileSize + "&numberOfChunks=" + numOfChunks;
			System.out.println(url);
			URL torrentUrl = new URL(url);
        
			HttpURLConnection connection = (HttpURLConnection) torrentUrl.openConnection();
			connection.setRequestMethod("PUT");
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			line = in.readLine();
			System.out.println("Received response: " + line);
			in.close();
		} catch(ConnectException e) {
			System.out.println("WebService is down!");
		} catch (IOException e) {	
		}
		
        if(line != null) {
        	System.out.println("Got the torrent file");
        	return Torrent.decode(line);
        } else {
        	System.out.println("Could not connect to webservice");
        	return null;
        }
	}

	private JScrollPane getDetailsTableView(OngoingTorrent ot) {
		Border border = BorderFactory.createEtchedBorder();
		JScrollPane listPane = new JScrollPane();

		listPane.setBorder(BorderFactory.createTitledBorder(border, "Details"));
		listPane.setMinimumSize(listPane.getPreferredSize());
		listPane.setMaximumSize(listPane.getPreferredSize());
		listPane.setPreferredSize(listPane.getPreferredSize());
		
		JPanel detailsView = new JPanel();
		detailsView.setLayout(new GridLayout(0, 1));
		detailsView.setMinimumSize(detailsView.getPreferredSize());
		detailsView.setMaximumSize(detailsView.getPreferredSize());
		detailsView.setPreferredSize(detailsView.getPreferredSize());
		
		DefaultTableModel dataModel = new DefaultTableModel(new String[] {"Attribute", "Value"}, 0);
		if(ot != null) {
			dataModel.addRow(new String[] {"FileName", ot.getFileName()});
			dataModel.addRow(new String[] {"Size", ot.getFileSize()});
			dataModel.addRow(new String[] {"Progress", ot.getProgress()});
			dataModel.addRow(new String[] {"# of Peers", ot.getNumOfConnectedPeers()});

			String[] trackerIps = ot.getTrackers();
//			String[] trackerIps = new String[] {"2", "5", "7", "8"};
			dataModel.addRow(new String[] {"Tracker IP(s)", trackerIps[0]});
			for(int i = 1; i < trackerIps.length; i++) {
				dataModel.addRow(new String[] {"", trackerIps[i]});
			}

			dataModel.addRow(new String[] {"# of Chunks", "" + ot.torrent.getNumberOfChunks()});

			dataModel.addRow(new String[] {"Chunk size", "" + ot.torrent.getChunkSize()});
		}
		
		JTable table = new JTable(dataModel);

		// Configure some of JTable's paramters
		table.setShowHorizontalLines(false);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
//		table.setBackground(jFrame.getBackground());
		table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.setRowHeight((int)(table.getRowHeight() * 1.5));
		table.setFont(new Font(Font.SERIF, 0, table.getFont().getSize() + 2));
		
		detailsView.add(table);
		
		listPane.setViewportView(table);
		return listPane;
	}
	
	private JScrollPane getDetailsView(OngoingTorrent ot) {
		Border border = BorderFactory.createEtchedBorder();
		JScrollPane listPane = new JScrollPane();

		listPane.setBorder(BorderFactory.createTitledBorder(border, "Details"));
		listPane.setMinimumSize(listPane.getPreferredSize());
		listPane.setMaximumSize(listPane.getPreferredSize());
		listPane.setPreferredSize(listPane.getPreferredSize());
		
		JPanel detailsView = new JPanel();
		detailsView.setLayout(new GridLayout(0, 2));
		detailsView.setMinimumSize(detailsView.getPreferredSize());
		detailsView.setMaximumSize(detailsView.getPreferredSize());
		detailsView.setPreferredSize(detailsView.getPreferredSize());
		detailsView.setFont(new Font(Font.SERIF, 0, detailsView.getFont().getSize() + 2));
		
		if(ot != null) {
			detailsView.add(new JLabel("Filename"));
			detailsView.add(new JLabel(ot.getFileName()));
			
			detailsView.add(new JLabel("Size"));
			detailsView.add(new JLabel(ot.getFileSize()));
			
			detailsView.add(new JLabel("Progress"));
			detailsView.add(new JLabel(ot.getProgress()));
			
			detailsView.add(new JLabel("# of Peers"));
			detailsView.add(new JLabel(ot.getNumOfConnectedPeers()));
			
			detailsView.add(new JLabel("Tracker IP(s)"));
//			String[] trackerIps = ot.torrent.getTrackerIP();
			String[] trackerIps = new String[] {"2", "5", "7", "8"};
			detailsView.add(new JLabel(trackerIps[0]));
			for(int i = 1; i < trackerIps.length; i++) {
				detailsView.add(new JLabel(""));
				detailsView.add(new JLabel(trackerIps[i]));
			}
			
			detailsView.add(new JLabel("# of Chunks"));
			detailsView.add(new JLabel("" + ot.torrent.getNumberOfChunks()));
			
			detailsView.add(new JLabel("Chunk size"));
			detailsView.add(new JLabel("" + ot.torrent.getChunkSize()));
			
		}
		listPane.setViewportView(detailsView);
		return listPane;
	}
	
	private JScrollPane getConnectionsView(OngoingTorrent ot) {
		Border border = BorderFactory.createEtchedBorder();
		JScrollPane listPane = new JScrollPane();

		listPane.setBorder(BorderFactory.createTitledBorder(border, "Connections"));
		listPane.setMinimumSize(listPane.getPreferredSize());
		listPane.setMaximumSize(listPane.getPreferredSize());
		listPane.setPreferredSize(listPane.getPreferredSize());
		
		JPanel connectionsView = new JPanel();

		connectionsView.setLayout(new BoxLayout(connectionsView, BoxLayout.Y_AXIS));
		connectionsView.setMinimumSize(connectionsView.getPreferredSize());
		connectionsView.setMaximumSize(connectionsView.getPreferredSize());
		connectionsView.setPreferredSize(connectionsView.getPreferredSize());
		connectionsView.setAlignmentX(Component.LEFT_ALIGNMENT);
		connectionsView.setAlignmentY(Component.LEFT_ALIGNMENT);
		if(ot != null) {
			JPanel myStatus = getChunkStatus(ot, null);
			connectionsView.add(myStatus);
			String[] otherClients = ot.getConnectedClients();
			if(otherClients != null) {
				for(int i = 0; i < otherClients.length; i++) {
					JPanel clientStatus = getChunkStatus(ot, otherClients[i]);
					connectionsView.add(clientStatus);
				}
			}
		}
		
		listPane.setViewportView(connectionsView);
		return listPane;
	}
	
	private JPanel getChunkStatus(OngoingTorrent ot, String client) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel dummyLabel = new JLabel("XXXXXXXXXXXXXXXXXX");
		JLabel name;
		if(client == null) {
			name = new JLabel("My Status: ");
		} else {
			name = new JLabel(client + ": ");
		}
		name.setMinimumSize(dummyLabel.getPreferredSize());
		name.setMaximumSize(dummyLabel.getPreferredSize());
		name.setPreferredSize(dummyLabel.getPreferredSize());
		
		panel.add(name);
		
		JPanel chunks = new JPanel(new FlowLayout());
		Dimension d = new Dimension((int)dummyLabel.getPreferredSize().getWidth() * 5, (int)dummyLabel.getPreferredSize().getHeight() * 2);
		chunks.setMinimumSize(d);
		chunks.setMaximumSize(d);
		chunks.setPreferredSize(d);
		for(int i = 0; i < ot.torrent.getNumberOfChunks(); i++) {
			JPanel chunk = new JPanel();
//			chunk.setMinimumSize(chunk.getPreferredSize());
//			chunk.setMaximumSize(chunk.getPreferredSize());
//			chunk.setPreferredSize(chunk.getPreferredSize());
			if(client == null) {
				if(ot.getChunkStatus(i) != -1) {
//				if(i % 100 == 0) {
					chunk.setBackground(Color.BLACK);
				} else {
					chunk.setBackground(Color.WHITE);
				}
			} else {
				if(client.equals(ot.getChunkStatus(i))) {
					chunk.setBackground(Color.BLACK);
				} else {
					chunk.setBackground(Color.WHITE);
				}
			}
			chunks.add(chunk);
		}
		panel.add(chunks);

		
		return panel;
	}
	
	protected void torrentSelected(final OngoingTorrent ot) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GridBagLayout layout = (GridBagLayout) main.getLayout();
				GridBagConstraints detailsConstraints = layout.getConstraints(detailsView);
				GridBagConstraints connectionsConstraints = layout.getConstraints(connectionsView);
				
				main.remove(detailsView);
				main.remove(connectionsView);

				detailsView = getDetailsTableView(ot);
				connectionsView = getConnectionsView(ot);

				main.add(detailsView, detailsConstraints);
				main.add(connectionsView, connectionsConstraints);
				
                jFrame.validate();
                jFrame.repaint();
			}
		});
	}

	public void updateUI(final String filename, final String sender, final int chunkNumber) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				OngoingTorrent ot = null;
				/*for(OngoingTorrent temp : listOfTorrents) {
					if(temp.getFileName().equals(filename)) {
						ot = temp;
					}
				}*/
				int index = -1;
				for(int i = 0; i < listOfTorrents.size(); i++) {
					if(listOfTorrents.get(i).getFileName().equals(filename)) {
						index = i;
						ot = listOfTorrents.get(i);
					}
				}
				
				if(ot != null) {
					DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
					
					int row = index;
					
	                listOfTorrents.get(index).setDownloaded(chunkNumber, sender);
	
					dataModel.setValueAt(ot.getProgress(), row, 1);
					dataModel.setValueAt(ot.getNumOfConnectedPeers(), row, 3);
					
	                jFrame.validate();
	                jFrame.repaint();

	                
	                /*if(!ot.isCompletelyDownloaded()) {
	                	downloadNextChunk(ot, sender);
	                }*/
				}
			}
		});
        System.out.println("Updating UI.. sending next request");
	}

	public void updateDetailsAndConnectionView(final OngoingTorrent ot) {
		if(table.getSelectedRow() == listOfTorrents.indexOf(ot)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					GridBagLayout layout = (GridBagLayout) main.getLayout();
					GridBagConstraints detailsConstraints = layout.getConstraints(detailsView);
					GridBagConstraints connectionsConstraints = layout.getConstraints(connectionsView);
					
					main.remove(detailsView);
					main.remove(connectionsView);

					detailsView = getDetailsTableView(ot);
					connectionsView = getConnectionsView(ot);

					main.add(detailsView, detailsConstraints);
					main.add(connectionsView, connectionsConstraints);
					
	                jFrame.validate();
	                jFrame.repaint();
				}
			});
		}
	}

	private void saveTorrents() {
		// Save to directory
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Utility.ONGOING_TORRENTS_FILE));
			oos.writeObject(listOfTorrents);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getTorrents() {
		// Load from directory
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Utility.ONGOING_TORRENTS_FILE));
			listOfTorrents = (ArrayList<OngoingTorrent>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			System.out.println("No ongoing torrents found.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		for(OngoingTorrent ot : listOfTorrents) {
			dataModel.addRow(new String[]{
					ot.getFileName(),
					ot.getProgress(),
					ot.getFileSize(),
					ot.getNumOfConnectedPeers()
			});
			if(!ot.isCompletelyDownloaded()) {
				startDownload(ot);
			}
		}
	}
	
	private void getStaticTorrents() {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		Random r = new Random();
		for(int i = 0; i < 20; i++) {
			long filesize = (long) (Utility.MAX_CHUNK_SIZE * (1 + r.nextDouble()));
			int chunksize = (int) Utility.MB;
//			chunksize = chunksize < 0 ? chunksize * -1 : chunksize;
			Torrent t = new Torrent("File #" + i, filesize, chunksize);
			OngoingTorrent ot = new OngoingTorrent(t, "", false);
			listOfTorrents.add(ot);
			dataModel.addRow(new String[]{
					ot.getFileName(),
					ot.getProgress(),
					ot.getFileSize(),
					ot.getNumOfConnectedPeers()
			});
		}
		long filesize = (long) (Utility.MAX_CHUNK_SIZE * (1 + r.nextDouble()));
		int chunksize = (int) Utility.MB;
//		chunksize = chunksize < 0 ? chunksize * -1 : chunksize;
		Torrent t = new Torrent("New File", filesize, chunksize);
		// Save to directory
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + File.separatorChar + "test.torrent"));
			oos.writeObject(t);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startDownload(OngoingTorrent ot) {
		// Connect to tracker and extract otherClient IPs
		String[] trackers = ot.getTrackers();
		boolean foundTracker = false;
		String request = "2" + " " + ot.getFileHash();
		for (int i = 0; i < trackers.length && !foundTracker; i++) {
			try {
				Socket socket = new Socket(trackers[i], Utility.TRACKER_PORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				ObjectInputStream br = new ObjectInputStream(socket.getInputStream());
				
				out.println(request);
				List<String> clients = (List<String>) br.readObject();
				
				// set clients
				ot.setOtherClients(clients.toArray(new String[0]));
				System.out.println(clients);
				
				// call clients
				for (String client : clients) {
					downloadNextChunk(ot, client);
				}
				
				br.close();
				out.close();
				socket.close();
				foundTracker = true;
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	protected void downloadNextChunk(OngoingTorrent ot, String sender) {
		// Download next chunk
		// reset timer
		new ClientRequestor(ot, sender).start();
	}

	public void addTorrent(final OngoingTorrent ot) {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		int index = listOfTorrents.size();
		listOfTorrents.add(ot);
		dataModel.addRow(new String[]{
				ot.getFileName(),
				ot.getProgress(),
				ot.getFileSize(),
				ot.getNumOfConnectedPeers()
		});
		selectRow(index);
	}
	
	public void selectRow(int index) {
		table.setRowSelectionInterval(index, index);
		
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport)table.getParent();

        Rectangle rect = table.getCellRect(index, 0, true);

        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);

        table.scrollRectToVisible(rect);
    }
	
	/*public boolean isCompletelyDownloaded(String filename) {
		OngoingTorrent ot = null;
		for(OngoingTorrent temp : listOfTorrents) {
			if(temp.getFileName().equals(filename)) {
				ot = temp;
			}
		}
		
		if(ot != null) {
			return ot.isCompletelyDownloaded();
		} else {
			System.out.println("Ongoing torrent not found!");
			return false;
		}
	}
	
	public int getChunkStatus(String filename, int chunkNumber) {
		OngoingTorrent ot = null;
		for(OngoingTorrent temp : listOfTorrents) {
			if(temp.getFileName().equals(filename)) {
				ot = temp;
			}
		}
		
		if(ot != null) {
			return ot.getChunkStatus(chunkNumber);
		} else {
			System.out.println("Ongoing torrent not found!");
			return -1;
		}
	}*/

    public static void setUIFont() {
    	FontUIResource f = new FontUIResource(new Font(Font.SERIF, 0, 20));
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource orig = (FontUIResource) value;
                Font font = new Font(f.getFontName(), orig.getStyle(), orig.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }
    
	public static void main(String[] args) {
		ClientUI c = new ClientUI(args[0]);
		new ClientListener(c).start();
	}

}
