package rs.gopro.mobile_store.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import rs.gopro.mobile_store.provider.MobileStoreDatabase;

import android.content.res.AssetManager;
import android.util.Log;

public class PropertiesUtil {

	private static final String TAG = LogUtils.makeLogTag(PropertiesUtil.class);

	
	
	
	public static Integer getDatabaseVersion(AssetManager assetManager){
		return getIntegerProperty("DATABASE_VERSION", assetManager);
	}
	

	/**
	 * Returns boolean property
	 * 
	 * @param propertyName
	 * @return
	 */
	public static boolean getBooleanProperty(String propertyName, AssetManager assetManager) {
		Properties properties = initPropertiesConfiguration(assetManager);
		Boolean value = null;
		if (properties.containsKey(propertyName)) {
			value = Boolean.valueOf(properties.getProperty(propertyName));
		} else {
			LogUtils.log(Log.WARN, TAG, "Property not found in mobile_store.properties:" + propertyName);
		}
		return value;
	}

	/**
	 * Return property
	 * 
	 * @param
	 * @return
	 * @throws ConfigurationException
	 */
	public static String getStringProperty(String propertyName, AssetManager assetManager) {
		Properties properties = initPropertiesConfiguration(assetManager);
		String value = properties.getProperty(propertyName);
		if (value == null) {
			LogUtils.log(Log.WARN, TAG, "Property not found in mobile_store.properties:" + propertyName);
		}
		return value;
	}

	public static Integer getIntegerProperty(String propertyName, AssetManager assetManager) {
		try {
			Properties properties = initPropertiesConfiguration(assetManager);
			Integer value = null;
			if (properties.containsKey(propertyName)) {
				value = Integer.parseInt(properties.getProperty(propertyName));
			}
			return value;
		} catch (NumberFormatException e) {
			LogUtils.log(Log.WARN,TAG, "Number format exception");
		}
		LogUtils.log(Log.WARN,TAG, "Property not found in mobile_store.properties:" + propertyName);
		return null;
	}

	/**
	 * @return
	 * @throws ConfigurationException
	 */
	private static Properties initPropertiesConfiguration(AssetManager assetManager) {
		Properties properties = null;
		try {
			assetManager.open("config/mobile_store.properties");
			InputStream inputStream = assetManager.open("config/mobile_store.properties");
			properties = new Properties();
			properties.load(inputStream);
			LogUtils.log(Log.INFO, TAG, "The mobile_store properties are now loaded");
		} catch (IOException e) {
			LogUtils.log(Log.INFO, TAG, "Failed to open mobile_store property file");
		}
		return properties;
	}

}
