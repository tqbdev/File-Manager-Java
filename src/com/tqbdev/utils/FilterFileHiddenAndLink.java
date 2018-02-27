package com.tqbdev.utils;

import java.io.File;
import java.io.FileFilter;

import javax.swing.filechooser.FileSystemView;

public class FilterFileHiddenAndLink implements FileFilter {
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	
	@Override
	public boolean accept(File arg0) {
		return !arg0.isHidden() && !isShortcutFile(arg0);
	}

	public boolean isShortcutFile(File file) {
		if (fileSystemView.isLink(file))
		{
			return true;
		}
		
		try {
			if (!file.exists()) {
				return true;
			} else {
				return !file.getAbsolutePath().equals(file.getCanonicalPath());
			}
		} catch (Exception ex) {
			System.err.println(ex);
			return true;
		}
	}
}
