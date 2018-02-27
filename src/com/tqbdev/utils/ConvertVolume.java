package com.tqbdev.utils;

public class ConvertVolume {
	public static String sizeToString(long size) {
    	int sizeType = 0;

		while (size >= 1048576)
		{
			size /= 1024;
			sizeType++;
		}

		long nRight;

		if (size >= 1024)
		{
			//Lấy một chữ số sau thập phân của nSize chứa trong nRight
			nRight = size % 1024;

			while (nRight > 99)
				nRight /= 10;

			size /= 1024;
			sizeType++;
		}
		else
		{
			nRight = 0;
		}

		String result = Long.toString(size);
		result += ".";
		result += Long.toString(nRight);

		switch (sizeType)
		{
		case 0:
			result += " bytes";
			break;
		case 1:
			result += " KB";
			break;
		case 2:
			result += " MB";
			break;
		case 3:
			result += " GB";
			break;
		case 4:
			result += " TB";
			break;
		}

		return result;
    }
	
	public static double stringToSize(String strSize) {
		long unit = 1;
		
		double num = -1.0;
		try {
			char ch = strSize.charAt(strSize.length() - 2);

			switch (ch) {
			case 'e':
				unit = 1;
				break;
			case 'K':
				unit = 1024;
				break;
			case 'M':
				unit = 1024 * 1024;
				break;
			case 'G':
				unit = 1024 * 1024 * 1024;
				break;
			case 'T':
				unit = 1024 * 1024 * 1024 * 1024;
				break;
			}

			if (unit == 1) {
				num = Double.parseDouble(strSize.substring(0, strSize.length() - 6));
			} else {
				num = Double.parseDouble(strSize.substring(0, strSize.length() - 3));
			}

			num = num * unit;
		} catch (Exception ex) {
			num = -1.0;
		}
		
		return num;
	}
}
