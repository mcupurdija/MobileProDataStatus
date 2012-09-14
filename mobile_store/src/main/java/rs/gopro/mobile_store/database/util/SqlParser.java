package rs.gopro.mobile_store.database.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;

/**
 * Parse SQL script file loaded from assets folder/subfolder. Return clear sql
 * statement, prepared for execution
 * 
 * @author aleksandar
 * 
 */
public class SqlParser {

	/**
	 * Generate list of sql statements
	 * 
	 * @param sqlFile
	 *            - sql file for parsing
	 * @param assetManager
	 * @return
	 * @throws IOException
	 */
	public static List<String> parseSqlFile(String sqlFile, AssetManager assetManager) throws IOException {
		List<String> sqlStatements = null;
		InputStream inputStream = assetManager.open(sqlFile);
		try {
			sqlStatements = parseSqlFile(inputStream);
		} finally {
			inputStream.close();
		}
		return sqlStatements;
	}

	/**
	 * Generate list of sql statements
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static List<String> parseSqlFile(InputStream inputStream) throws IOException {
		String script = removeComments(inputStream);
		return splitSqlScript(script, ';');
	}

	/**
	 * Clean up script from comments
	 * 
	 * @param inputStream
	 * @return String - script without comments
	 * @throws IOException
	 */
	private static String removeComments(InputStream inputStream) throws IOException {
		StringBuilder sqlScritptWithoutComments = new StringBuilder();
		InputStreamReader isReader = new InputStreamReader(inputStream);
		try {
			BufferedReader buffReader = new BufferedReader(isReader);
			try {
				String line;
				String multiLineComment = null;
				while ((line = buffReader.readLine()) != null) {
					line = line.trim();

					if (multiLineComment == null) {
						if (line.startsWith("/*")) {
							if (!line.endsWith("}")) {
								multiLineComment = "/*";
							}
						} else if (line.startsWith("{")) {
							if (!line.endsWith("}")) {
								multiLineComment = "{";
							}
						} else if (!line.startsWith("--") && !line.equals("")) {
							sqlScritptWithoutComments.append(line);
						}
					} else if (multiLineComment.equals("/*")) {
						if (line.endsWith("*/")) {
							multiLineComment = null;
						}
					} else if (multiLineComment.equals("{")) {
						if (line.endsWith("}")) {
							multiLineComment = null;
						}
					}
				}
			} finally {
				buffReader.close();
			}

		} finally {
			isReader.close();
		}
		return sqlScritptWithoutComments.toString();
	}

	/**
	 * Split script by delimiter value
	 * 
	 * @param script
	 * @param delim
	 *            - expected value is semicolon
	 * @return
	 */
	private static List<String> splitSqlScript(String script, char delim) {
		List<String> statements = new ArrayList<String>();
		StringBuilder stringBuffer = new StringBuilder();
		boolean inLiteral = false;
		char[] content = script.toCharArray();
		for (int i = 0; i < script.length(); i++) {
			if (content[i] == '\'') {
				inLiteral = !inLiteral;
			}
			if (content[i] == delim && !inLiteral) {
				if (stringBuffer.length() > 0) {
					statements.add(stringBuffer.toString().trim());
					stringBuffer = new StringBuilder();
				}
			} else {
				stringBuffer.append(content[i]);
			}
		}
		if (stringBuffer.length() > 0) {
			statements.add(stringBuffer.toString().trim());
		}
		return statements;
	}

	/**
	 * Obtain table name in create scripts
	 * 
	 * @param sqlStatement
	 * @return
	 */
	public static String getTableName(String sqlStatement) {
		String tableName = null;
		// CREATE TABLE
		char scriptQuote = '`';
		sqlStatement = sqlStatement.replace(scriptQuote, ' ');
		String[] splitSql = sqlStatement.split("\\s+");
		tableName = splitSql[2];
		return tableName;
	}

	/**
	 * investigate  whether the script to create table
	 * @param sqlStatement
	 * @return
	 */
	public static boolean isScriptForTableCreation(String sqlStatement) {
		String create = "CREATE";
		char scriptQuote = '`';
		sqlStatement = sqlStatement.replace(scriptQuote, ' ');
		String[] splitSql = sqlStatement.split("\\s+");
		if (create.equalsIgnoreCase(splitSql[0])) {
			return true;
		}
		return false;
	}
}
