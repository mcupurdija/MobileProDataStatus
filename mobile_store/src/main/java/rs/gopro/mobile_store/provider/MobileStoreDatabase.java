package rs.gopro.mobile_store.provider;

import java.io.IOException;

import rs.gopro.mobile_store.util.AssetUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.PropertiesUtil;
import rs.gopro.mobile_store.util.SqlParserUtil;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @see SQLiteOpenHelper
 * @author aleksandar
 * 
 */

public class MobileStoreDatabase extends SQLiteOpenHelper {

	private static final String TAG = LogUtils.makeLogTag(MobileStoreDatabase.class);

	private static final String SQL_DIR = "sql";
	private static final String CREATE_FILE = "create_mobile_store.sql";
	private static final String UPGRADEFILE_PREFIX = "upgrade-";
	private static final String UPGRADEFILE_SUFFIX = ".sql";
	private static final String DATABSE_NAME = "mobile_store.db";
	private static final Integer DEFAULT_DATABSE_VERSION = 1;
	private Context context;

	public MobileStoreDatabase(Context context) {	
		super(context, DATABSE_NAME, null, PropertiesUtil.getDatabaseVersion(context.getAssets()) == null ? DEFAULT_DATABSE_VERSION : PropertiesUtil.getDatabaseVersion(context.getAssets()));
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			LogUtils.log(Log.INFO, TAG, "Create database");
			execSqlFile(CREATE_FILE, SQL_DIR, db);
		} catch (IOException exception) {
			throw new RuntimeException("Database creation failed", exception);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			LogUtils.log(Log.INFO, TAG, "Upgrade database from " + oldVersion + " to " + newVersion);
			// if exist file with prefix upgrade use it, regardless of create
			// script
			if (AssetUtil.existsFileWithPrefix(UPGRADEFILE_PREFIX, SQL_DIR, this.context.getAssets())) {
				for (String sqlFile : AssetUtil.listOfFileName(SQL_DIR, this.context.getAssets())) {
					if (sqlFile.startsWith(UPGRADEFILE_PREFIX)) {
						int fileVersion = Integer.parseInt(sqlFile.substring(UPGRADEFILE_PREFIX.length(), sqlFile.length() - UPGRADEFILE_SUFFIX.length()));
						if (fileVersion > oldVersion && fileVersion <= newVersion) {
							execSqlFile(sqlFile, SQL_DIR, db);
						}
					}
				}
			} else {
				// if upgrade file does not exist use create file
				execSqlFile(CREATE_FILE, SQL_DIR, db);
			}

		} catch (IOException exception) {
			throw new RuntimeException("Database upgrade failed", exception);
		}
	}

	protected void execSqlFile(String sqlFile, String filePath, SQLiteDatabase db) throws SQLException, IOException {
		
		String trigger = "";
		for (String sqlInstruction : SqlParserUtil.parseSqlFile(filePath + "/" + sqlFile, this.context.getAssets())) {
			if (SqlParserUtil.isScriptForTableCreation(sqlInstruction)) {
				String tableName = SqlParserUtil.getTableName(sqlInstruction);
				LogUtils.log(Log.INFO, TAG, "Table " + tableName + " is dropped");
				db.execSQL("DROP TABLE IF EXISTS " + tableName);
				LogUtils.log(Log.INFO, TAG, "Execute sql file: " + "DROP TABLE IF EXISTS " + tableName);
				db.execSQL(sqlInstruction);
				LogUtils.log(Log.INFO, TAG, "Execute sql file: " + sqlInstruction);
			} else if (SqlParserUtil.isScriptForTriggerCreation(sqlInstruction)) {
				trigger += sqlInstruction;
			} else if (sqlInstruction.contains("END;")) {
				trigger += "END;";
				db.execSQL(trigger);
				LogUtils.log(Log.INFO, TAG, "Execute sql file: " + trigger);
				trigger = "";
			} else {
				if (!trigger.equals("")) {
					trigger += sqlInstruction;
				} else {
					db.execSQL(sqlInstruction);
					LogUtils.log(Log.INFO, TAG, "Execute sql file: " + sqlInstruction);
				}
			}
		}
	}
	
	

}
