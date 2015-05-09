package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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

		JPanel main = new JPanel();
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
		
		JPanel detailsView = new JPanel();
		detailsView.setLayout(new GridBagLayout());
		detailsView.setBorder(BorderFactory.createTitledBorder(border, "Details"));
		detailsView.setMinimumSize(detailsView.getPreferredSize());
		detailsView.setMaximumSize(detailsView.getPreferredSize());
		detailsView.setPreferredSize(detailsView.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 1;
		constraints.weighty = 55;

		main.add(detailsView, constraints);

		/*
		// Add From line
		receiverlbl = new JLabel(FROM_STR);
		receiverlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		receiverlbl.setBackground(Color.GREEN);
		receiverlbl.setPreferredSize(new Dimension(80, 20));
		receiverlbl.setMinimumSize(receiverlbl.getPreferredSize());
		receiverlbl.setMaximumSize(receiverlbl.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 5;
		constraints.weighty = 2;
		detailsView.add(receiverlbl, constraints);

		receiver = new JLabel("");
		receiver.setPreferredSize(new Dimension(400, 20));
		receiver.setMinimumSize(receiver.getPreferredSize());
		receiver.setMaximumSize(receiver.getPreferredSize());
		receiver.setBackground(Color.GREEN);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 95;
		constraints.weighty = 2;
		detailsView.add(receiver, constraints);

		// Add Subject line
		subjectlbl = new JLabel(SUBJECT_STR);
		subjectlbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		subjectlbl.setBackground(Color.RED);
		subjectlbl.setPreferredSize(new Dimension(80, 20));
		subjectlbl.setMinimumSize(subjectlbl.getPreferredSize());
		subjectlbl.setMaximumSize(subjectlbl.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 5;
		constraints.weighty = 2;
		detailsView.add(subjectlbl, constraints);

		subject = new JLabel("");
		subject.setPreferredSize(new Dimension(400, 20));
		subject.setMinimumSize(subject.getPreferredSize());
		subject.setMaximumSize(subject.getPreferredSize());
		subject.setBackground(Color.RED);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 95;
		constraints.weighty = 2;
		detailsView.add(subject, constraints);		

		// Add content
		content = new JLabel();
		content.setHorizontalAlignment(SwingConstants.LEFT);
		content.setVerticalAlignment(SwingConstants.TOP);
		content.setPreferredSize(content.getPreferredSize());
		content.setMinimumSize(content.getPreferredSize());
		content.setMaximumSize(content.getPreferredSize());
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		//		constraints.weightx = 90;
		constraints.weighty = 96;
		JScrollPane contentPane = new JScrollPane();
		contentPane.setViewportView(content);
		detailsView.add(contentPane, constraints);
*/
		return main;
	}

	
	protected void torrentSelected(OngoingTorrent ot) {
		System.out.println(ot);
	}

	private void getTorrents() {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		for(int i = 0; i < 20; i++) {
			Torrent t = new Torrent("file" + (i + 1), 10240, 1024);
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
		new ClientUI(args[0]);
	}

}
