package rs.gopro.mobile_store.database;

import java.io.IOException;

import rs.gopro.mobile_store.database.util.SqlParser;
import rs.gopro.mobile_store.util.AssetUtil;
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

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

	private static final String SQL_DIR = "sql";

	private static final String CREATE_FILE = "create_db_script.sql";

	private static final String UPGRADEFILE_PREFIX = "upgrade-";

	private static final String UPGRADEFILE_SUFFIX = ".sql";

	private static final String DATABSE_NAME = "mobile_store.db";

	private Context context;

	public SQLiteDatabaseHelper(Context context, int version) {
		super(context, DATABSE_NAME, null, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			Log.i(this.getClass().getName(), "Create database");

			execSqlFile(CREATE_FILE, db);
		} catch (IOException exception) {
			throw new RuntimeException("Database creation failed", exception);
		}
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			Log.i(this.getClass().getName(), "Upgrade database from " + oldVersion + " to " + newVersion);
			for (String sqlFile : AssetUtil.listOfFileName(SQL_DIR, this.context.getAssets())) {
				if (sqlFile.startsWith(UPGRADEFILE_PREFIX)) {
					int fileVersion = Integer.parseInt(sqlFile.substring(UPGRADEFILE_PREFIX.length(), sqlFile.length() - UPGRADEFILE_SUFFIX.length()));
					if (fileVersion > oldVersion && fileVersion <= newVersion) {
						execSqlFile(sqlFile, db);
					}
				}
			}
		} catch (IOException exception) {
			throw new RuntimeException("Database upgrade failed", exception);
		}
	}

	protected void execSqlFile(String sqlFile, SQLiteDatabase db) throws SQLException, IOException {
		Log.i(this.getClass().getName(), "Exec sql file: " + sqlFile);
		for (String sqlInstruction : SqlParser.parseSqlFile(SQL_DIR + "/" + sqlFile, this.context.getAssets())) {
			Log.i(this.getClass().getName(), "Sql statement: " + sqlInstruction);
			if(SqlParser.isScriptForTableCreation(sqlInstruction)){
			String tableName =	SqlParser.getTableName(sqlInstruction);
			Log.i(this.getClass().getName(),"Table "+tableName+" is dropped");
			db.execSQL("DROP TABLE IF EXISTS "+tableName);
			}
			db.execSQL(sqlInstruction);
		}
	}
}
