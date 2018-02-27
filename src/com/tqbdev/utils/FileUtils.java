package com.tqbdev.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.filechooser.FileSystemView;

public class FileUtils {
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	public static String genInfoDrive(File file) {
		String info = null;
		if (fileSystemView.isDrive(file)) {
			info = "";
			info += ConvertVolume.sizeToString(file.getFreeSpace());
			info += " of ";
			info += ConvertVolume.sizeToString(file.getTotalSpace());
			info += " free";
		}
		return info;
	}

	public static String genInfoListFile(File[] lstFiles) {
		String info = null;

		int totalFolder = 0;
		int totalFile = 0;
		for (File file : lstFiles) {
			if (file.isDirectory()) {
				totalFolder++;
			} else if (file.isFile()) {
				totalFile++;
			}
		}

		info = Integer.toString(totalFile);
		info += " file(s), ";
		info += Integer.toString(totalFolder);
		info += " dir(s)";

		return info;
	}

	public static File getDrive(File cur) {
		while (cur != null && !fileSystemView.isDrive(cur)) {
			cur = cur.getParentFile();
		}

		return cur;
	}

	public static boolean checkZipFile(File fileFrom) {
		try {
			ZipFile zipFile = new ZipFile(fileFrom);
			zipFile.close();
		} catch (ZipException e1) {
			return false;
		} catch (IOException e1) {
			return false;
		}

		return true;
	}

	// Get list of all files recursively by iterating through sub directories
	public static List<File> listFile(List<File> listFiles, File inputDirectory) throws IOException {

		File[] allFiles = inputDirectory.listFiles();
		for (File file : allFiles) {
			if (file.isDirectory()) {
				listFile(listFiles, file);
			} else {
				listFiles.add(file);
			}
		}
		return listFiles;
	}
}