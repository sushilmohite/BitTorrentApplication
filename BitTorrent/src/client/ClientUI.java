package client;

import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

public class ClientUI {

	private JFrame jFrame;
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
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
				// TODO: on clicking download
			}

		});
		buttons.add(download);
		
		JButton upload = new JButton("Upload");
		upload.setPreferredSize(dummy1.getPreferredSize());
		upload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: on clicking upload
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

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				//TODO: On selecting a torrent
				if(!event.getValueIsAdjusting()) {
					ListSelectionModel index = (ListSelectionModel) event.getSource();
					
					OngoingTorrent ot = listOfTorrents.get(index.getMinSelectionIndex());
					torrentSelected(ot);
				}
			}
		});
		/*
		scrollPane = table.createScrollPaneForTable( table );
		topPanel.add( scrollPane, BorderLayout.CENTER );*/
		/*final JList<OngoingTorrent> listOfTorrents = new JList<>(listModel);
		listOfTorrents.setCellRenderer(new CustomCellRenderer());
		listOfTorrents.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				//TODO: On selecting a torrent
				Email email = listOfEmails.getSelectedValue();
				if(email != null) {
					if(!email.isSeen()) {
						email.setSeen(true);
						clientReceiver.updateSeenStatus(email.getId());
					}
					setContent("" + email.getHTMLContent());
					setReceiver(email.getFrom());
					setSubject(email.getSubject());
				}
			}
		});*/
		
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

//			String[] trackerIps = ot.torrent.getTrackerIP();
			String[] trackerIps = new String[] {"2", "5", "7", "8"};
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
			for(int i = 0; i < otherClients.length; i++) {
				JPanel clientStatus = getChunkStatus(ot, otherClients[i]);
				connectionsView.add(clientStatus);
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
				Random r = new Random();
//				if(ot.isChunkDownloaded(i)) {
				if(i % 100 == 0) {
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
//				detailsView = getDetailsView(ot);
				connectionsView = getConnectionsView(ot);

				main.add(detailsView, detailsConstraints);
				main.add(connectionsView, connectionsConstraints);
				
                jFrame.validate();
                jFrame.repaint();
			}
		});
	}

	private void getTorrents() {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		for(int i = 0; i < 20; i++) {
			Torrent t = new Torrent("file" + (i + 1), 102474, 1024);
			OngoingTorrent ot = new OngoingTorrent(t, "", false);
			listOfTorrents.add(ot);
			dataModel.addRow(new String[]{
					ot.getFileName(),
					ot.getProgress(),
					ot.getFileSize(),
					ot.getNumOfConnectedPeers()
			});
		}
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
		Scanner sc = new Scanner(System.in);
		sc.next();
		Torrent t = new Torrent("New file", 102474, 10249);
		final OngoingTorrent ot = new OngoingTorrent(t, "", false);
		
		c.addTorrent(ot);
	}

}
