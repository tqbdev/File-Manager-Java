package com.tqbdev.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class DriveItemEditor extends BasicComboBoxEditor {
	private JPanel panel = new JPanel();
	private JLabel labelItem = new JLabel();
	private JLabel labelName = new JLabel();
	private String selectedValue;
	private FileSystemView fsv = FileSystemView.getFileSystemView();

	public DriveItemEditor() {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(2, 5, 2, 0);

		labelItem.setOpaque(false);
		labelItem.setHorizontalAlignment(JLabel.LEFT);
		labelItem.setForeground(Color.BLACK);
		panel.add(labelItem, constraints);
		
		labelName.setOpaque(false);
		labelName.setHorizontalAlignment(JLabel.RIGHT);
		labelName.setForeground(Color.BLACK);
		labelName.setSize(new Dimension(50, 50));
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(2, 0, 2, 5);
		panel.add(labelName, constraints);
		
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createMatteBorder(1,1,1,0, Color.BLACK));
	}

	public Component getEditorComponent() {
		return this.panel;
	}

	public Object getItem() {
		return this.selectedValue;
	}

	public void setItem(Object item) {
		if (item == null) {
			return;
		}

		try {
			File driveItem = (File) item;
			selectedValue = driveItem.getPath();
			labelItem.setText(selectedValue);
			labelItem.setIcon(fsv.getSystemIcon(driveItem));
			// set drive name
			labelName.setText(fsv.getSystemDisplayName(driveItem));
		} catch (Exception ex) {

		}
	}
}
