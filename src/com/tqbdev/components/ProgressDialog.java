package com.tqbdev.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class ProgressDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587637773910024873L;
	private JProgressBar dpb;
	private JLabel text;
	private JButton btnCancel;
	private boolean isCanceled = false;
	
	public ProgressDialog(JFrame parent, String title)  {
		super(parent, title, true);
		getRootPane().setBorder(new EmptyBorder(5, 5, 5, 5));
		
		text = new JLabel();
		text.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		dpb = new JProgressBar(0, 100);
		
		JButton btn = new JButton("Do In Background!");
		btn.setBorder(new EmptyBorder(5, 5, 5, 5));
		btn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBorder(new EmptyBorder(5, 5, 5, 5));
		btnCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				isCanceled = true;
			}
		});
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(text);
		content.add(dpb);
		content.add(btn);
		content.add(btnCancel);
		
		add(BorderLayout.CENTER, content);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(400, 100);
		setLocationRelativeTo(parent);
	}
	
	public void hideCancelButton() {
		btnCancel.setVisible(false);
		this.revalidate();
	}
	
	public void setValueProgress(int value) {
		dpb.setValue(value);
	}
	
	public void setText(String str) {
		text.setText(str);
		pack();
	}
	
	public void setIndeterminate(boolean value) {
		dpb.setIndeterminate(value);
	}
	
	public boolean isCanceled() {
		return isCanceled;
	}
}
