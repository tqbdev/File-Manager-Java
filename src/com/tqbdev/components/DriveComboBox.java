package com.tqbdev.components;

import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class DriveComboBox extends JComboBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6430220997861597907L;
	private DefaultComboBoxModel model;
    
    public DriveComboBox() {
        model = new DefaultComboBoxModel();
        setModel(model);
        setRenderer(new DriveItemRenderer());
        setEditor(new DriveItemEditor());
    }
     
    public void addItems(File[] items) {
        for (File anItem : items) {
            model.addElement(anItem);
        }
    }
}
