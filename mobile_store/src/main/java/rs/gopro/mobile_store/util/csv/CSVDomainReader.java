package rs.gopro.mobile_store.util.csv;

import java.io.Reader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.Domain;

public class CSVDomainReader {
	
	private static final String TAG = "CSVReader";
	
	private static final char SEPARATOR = ';';
	private static final char QUOTE = '"';
	/**
	 * Parses csv and store it in domain class. Only applicable for {@link Domain} classes.
	 * @param reader
	 * @param type Needs to pass type, to enable get instance.
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws CSVParseException 
	 */
	public static <T extends Domain> List<T> parse(Reader reader, Class<T> type) throws CSVParseException {
		try {
			// get strategy from domain using reflection
			ColumnPositionMappingStrategy<T> strat = new ColumnPositionMappingStrategy<T>();
			strat.setType(type);
			strat.setColumnMapping(type.newInstance().getCSVMappingStrategy());
			// parse csv and bind to bean
			
			CSVReader csvReader = new CSVReader(reader, SEPARATOR, QUOTE, 1);
			
			CsvToBean<T> csv = new CsvToBean<T>();
			
			List<T> result = csv.parse(strat, csvReader);
			return result;
		} catch (InstantiationException e) {
			LogUtils.LOGE(TAG, "Error during parsing csv!", e);
			throw new CSVParseException(e);
		} catch (IllegalAccessException e) {
			LogUtils.LOGE(TAG, "Error during parsing csv!", e);
			throw new CSVParseException(e);
		} catch (Exception e) {
			LogUtils.LOGE(TAG, "Error during parsing csv!", e);
			throw new CSVParseException(e);
		}
	}

}
