package com.tqbdev.components;

import com.tqbdev.utils.*;
import java.io.File;
import java.sql.Date;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5317925726290479605L;
	private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
        "",
        "Name",
        "Size",
        "Last Modified",
        "Type"
    };

    public FileTableModel() {
        this(new File[0]);
    }

    public FileTableModel(File[] files) {
        this.files = files;
    }

    public Object getValueAt(int row, int column) {
        File file = files[row];
        switch (column) {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            case 2:
            	if (file.isFile()) {
            		return ConvertVolume.sizeToString(file.length());
            	} else {
            		return "<DIR>";
            	}
            case 3:
                return file.lastModified();
            case 4:
            	return fileSystemView.getSystemTypeDescription(file);
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 2:
                return String.class;
            case 3:
                return Date.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}
