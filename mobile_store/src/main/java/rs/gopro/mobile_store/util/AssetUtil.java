package rs.gopro.mobile_store.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.res.AssetManager;

/**
 * Util class which read files from assets folder (and nested folder)
 * 
 * @author aleksandar
 */
public class AssetUtil {

	/**
	 * Check does file exist in assets
	 * @param fileName
	 * @param filePath
	 * @param assetManager
	 * @return
	 * @throws IOException
	 */
	public static boolean exists(String fileName, String filePath, AssetManager assetManager) throws IOException {
		for (String currentFileName : assetManager.list(filePath)) {
			if (currentFileName.equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Obtain files in asset
	 * Return list of files
	 * @param path
	 * @param assetManager
	 * @return 
	 * @throws IOException
	 */
	public static String[] listOfFileName(String path, AssetManager assetManager) throws IOException {
		String[] files = assetManager.list(path);
		List<String> listOfFileName = Arrays.asList(files);
		Collections.sort(listOfFileName);
		return files;
	}
	
	
	public static Boolean existsFileWithPrefix(String fileNamePrefix, String filePath, AssetManager assetManager) throws IOException{
		for (String currentFileName : assetManager.list(filePath)) {
			if (currentFileName.startsWith(fileNamePrefix)) {
				return true;
			}
		}
		return false;
	}
}
