package rs.gopro.mobile_store.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileReaderUtil {

	public static String fileToString(String path) {

		try {
			return FileUtils.readFileToString(new File(path), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String numberOfLines(String path) {
		
		try {
			return String.valueOf(FileUtils.readLines(new File(path), "UTF-8").size() - 1);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}