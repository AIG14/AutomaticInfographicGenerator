/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package csv.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Csv Column.
 *
 * @author Peter
 */
public class CsvColumn {
	/**
	 * Holds the Csv Column name.
	 */
	private String columnName = "";

	/**
	 * Hold a populated map of Csv Column properties.
	 */
	private Map<String, Boolean> properties;

	/**
	 * Holds a map of cached counts for the Csv Column.
	 */
	private Map<String, Integer> cachedCounts = null;

	/**
	 * Holds all column data.
	 */
	ArrayList<String> columnData = new ArrayList<String>();

	/**
	 * Holds all long representative data data.
	 */
	long[] columnDataLong;

	/**
	 * Holds the number of non blank rows record in the column.
	 */
	private int nonBlankRows = 0;

	/**
	 * Creates a new CsvColumn class.
	 * 
	 * @param columnName
	 */
	public CsvColumn(String columnName) {
		this.columnName = CsvUtilities.toProper(
				CsvUtilities.formatName(columnName)).trim();
		this.properties = new HashMap<String, Boolean>();
	}

	/**
	 * Returns the grouping counts for the data within the Csv column.
	 * 
	 * @return a map populated with item counts.
	 */
	public Map<String, Integer> getCachedCounts() {
		if (this.cachedCounts == null) {
			this.cachedCounts = new HashMap<String, Integer>();
			for (String item : columnData) {
				if (item != null) {
					int counter = 1;
					if (this.cachedCounts.containsKey(item)) {
						counter = this.cachedCounts.get(item) + 1;
					}
					this.cachedCounts.put(item, counter);
				}
			}
		}
		return this.cachedCounts;
	}

	/**
	 * Adds data to a Csv column.
	 * 
	 * @param value
	 *            the value to add to the Csv Column.
	 */
	public void addData(String value) {
		value = value.replaceAll("\\<[^>]*>", "").trim();
		if (value.length() == 0 || value.equalsIgnoreCase("NULL")) {
			value = null;
		} else if (value.equalsIgnoreCase("TRUE")) {
			value = "Yes";
		} else if (value.equalsIgnoreCase("FALSE")) {
			value = "No";
		}
		columnData.add(value);
	}

	/**
	 * Get all column data recorded.
	 * 
	 * @return an array with column data.
	 */
	public ArrayList<String> getData() {
		return columnData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("=" + columnName + "=\r\n");
		for (String data : columnData) {
			output.append(":" + data + ":\r\n");
		}
		return output.toString();
	}

	/**
	 * Gets the text name of the Csv column.
	 * 
	 * @return the text name of the csv column.
	 */
	public String getTextName() {
		return columnName;
	}

	/**
	 * Sets a property value with a provided key.
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 */
	public void setProperty(String key, boolean value) {
		properties.put(key, new Boolean(value));
	}

	/**
	 * Gets a property value for a provided key.
	 * 
	 * @param key
	 * @return The value associated with the provided key.
	 */
	public boolean getProperty(String key) {
		if (this.properties.containsKey(key)) {
			return this.properties.get(key);
		}
		return false;
	}

	/**
	 * Gets the number of non blank rows.
	 * 
	 * @return the number of non blank rows.
	 */
	public int getNonBlankRows() {
		return this.nonBlankRows;
	}

	/**
	 * Sets the number of non blank rows.
	 * 
	 * @param nonBlankRows
	 *            the number of non blank rows.
	 */
	public void setNonBlankRows(int nonBlankRows) {
		this.nonBlankRows = nonBlankRows;
	}

	/**
	 * Converts the column to an long data type. Note: this operation cannot be
	 * undone.
	 */
	public void convertToLongColumn() {
		this.columnDataLong = new long[columnData.size()];
		// [TODO] Copy data.
		this.columnData = null;
	}
}
