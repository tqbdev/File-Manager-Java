package com.tqbdev.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

public class DriveItemRenderer extends JPanel implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 413399228728443235L;
	private JLabel labelItem = new JLabel();
	private JLabel labelName = new JLabel();
	private FileSystemView fsv = FileSystemView.getFileSystemView();

	public DriveItemRenderer() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		//constraints.weighty = 1.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(2, 5, 2, 0);

		labelItem.setOpaque(true);
		labelItem.setHorizontalAlignment(JLabel.LEFT);

		add(labelItem, constraints);
		
		labelName.setOpaque(true);
		labelName.setHorizontalAlignment(JLabel.RIGHT);
		labelName.setSize(new Dimension(50, 50));
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(2, 0, 2, 5);
		
		add(labelName, constraints);
		
		setBackground(Color.WHITE);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		File driveItem = (File) value;

		// set drive path
		labelItem.setText(driveItem.getPath());

		// set drive icon
		labelItem.setIcon(fsv.getSystemIcon(driveItem));

		// set drive name
		labelName.setText(fsv.getSystemDisplayName(driveItem));
		
		if (isSelected) {
			labelItem.setBackground(Color.BLUE);
			labelItem.setForeground(Color.WHITE);
			
			labelName.setBackground(Color.BLUE);
			labelName.setForeground(Color.WHITE);
		} else {
			labelItem.setForeground(Color.BLACK);
			labelItem.setBackground(Color.WHITE);
			
			labelName.setBackground(Color.WHITE);
			labelName.setForeground(Color.BLACK);
		}

		return this;
	}
}
