package au.com.bytecode.opencsv.bean;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Copyright 2007 Kyle Miller.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class HeaderColumnNameMappingStrategy<T> implements MappingStrategy<T> {
    protected String[] header;
    protected Map<String, Field> fieldMap = null;
    protected Class<T> type;

    public void captureHeader(CSVReader reader) throws IOException {
        header = reader.readNext();
    }

    public Field findField(int col) {
        String columnName = getColumnName(col);
        return (null != columnName && columnName.trim().length() > 0) ? findField(columnName) : null;
    }

    protected String getColumnName(int col) {
        return (null != header && col < header.length) ? header[col] : null;
    }

    protected Field findField(String name) {
        if (null == fieldMap) fieldMap = loadFieldMap(getType()); //lazy load descriptors
        return fieldMap.get(name.toUpperCase().trim());
    }

    protected boolean matches(String name, Field desc) {
        return desc.getName().equals(name.trim());
    }

	protected Map<String, Field> loadFieldMap(Class<T> cls) {
        Map<String, Field> map = new HashMap<String, Field>();

        Field[] fields;
        fields = loadFields(getType());
        for (Field field : fields) {
            map.put(field.getName().toUpperCase().trim(), field);
        }

        return map;
    }

    private Field[] loadFields(Class<T> cls) {
    	Field[] aClassFields = cls.getDeclaredFields();
        return aClassFields;
    }

    public T createBean() throws InstantiationException, IllegalAccessException {
        return type.newInstance();
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
