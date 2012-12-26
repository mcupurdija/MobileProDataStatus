package rs.gopro.mobile_store.util;


import rs.gopro.mobile_store.BuildConfig;
import android.util.Log;

public class LogUtils {
	private static final String LOG_PREFIX = "mobile_store_";
	private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
	private static final int MAX_LOG_TAG_LENGTH = 23;

	public static String makeLogTag(String str) {
		if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
			return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
		}

		return LOG_PREFIX + str;
	}
	
	public static String makeLogTag(Class cls) {
		return makeLogTag(cls.getSimpleName());
	}
	
	public static void log(int logLevel, String tag, String message) {
		switch (logLevel) {
		case Log.DEBUG:
			if (Log.isLoggable(tag, Log.DEBUG)) {
				Log.d(tag, message);
			}
			break;
		case Log.ERROR:
			Log.e(tag, message);
			break;
		case Log.INFO:
			Log.i(tag, message);
			break;
		case Log.VERBOSE:
			if (Log.isLoggable(tag, Log.VERBOSE)) {
				Log.v(tag, message);
			}
			break;
		case Log.WARN:
			Log.w(tag, message);
			break;
		default:
			Log.i(tag, message);
			break;
		}

	}
	
	
	public static void log(int logLevel, String tag, String message, Throwable throwable) {
		switch (logLevel) {
		case Log.DEBUG:
			if (Log.isLoggable(tag, Log.DEBUG)) {
				Log.d(tag, message, throwable);
			}
			break;
		case Log.ERROR:
			Log.e(tag, message, throwable);
			break;
		case Log.INFO:
			Log.i(tag, message, throwable);
			break;
		case Log.VERBOSE:
			if ( Log.isLoggable(tag, Log.VERBOSE)) {
				Log.v(tag, message, throwable);
			}
			break;
		case Log.WARN:
			Log.w(tag, message, throwable);
			break;
		default:
			Log.i(tag, message, throwable);
			break;
		}

	}
	
	

	public static void logDebugLevel(final String tag, String message) {
		if (Log.isLoggable(tag, Log.DEBUG)) {
			Log.d(tag, message);
		}
	}

	public static void logDebugLevel(final String tag, String message, Throwable cause) {
		if (Log.isLoggable(tag, Log.DEBUG)) {
			Log.d(tag, message, cause);
		}
	}

	public static void logVerboseLevel(final String tag, String message) {
		if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, message);
		}
	}

	public static void logVerboseLevel(final String tag, String message, Throwable cause) {
		if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, message, cause);
		}
	}

	public static void logInfoLevel(final String tag, String message) {
		Log.i(tag, message);
	}

	public static void logInfoLevel(final String tag, String message, Throwable cause) {
		Log.i(tag, message, cause);
	}

	public static void logWarnLevel(final String tag, String message) {
		Log.w(tag, message);
	}

	public static void logWarnLevel(final String tag, String message, Throwable cause) {
		Log.w(tag, message, cause);
	}

	public static void logErrorLevel(final String tag, String message) {
		Log.e(tag, message);
	}

	public static void logErrorLevel(final String tag, String message, Throwable cause) {
		Log.e(tag, message, cause);
	}
	
	public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    public static void LOGV(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void LOGV(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    public static void LOGI(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
    }

    public static void LOGW(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
    }

    public static void LOGE(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
    }

    private LogUtils() {
    }
}
