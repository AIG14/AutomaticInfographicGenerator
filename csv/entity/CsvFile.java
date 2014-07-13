/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package csv.entity;

import java.util.ArrayList;

/**
 * Represents CSV file data.
 * 
 * @author Peter
 */
public class CsvFile {
	/**
	 * Represents the title of the Csv file.
	 */
	private String title;

	/**
	 * An array list of Csv columns within the Csv file.
	 */
	private ArrayList<CsvColumn> csvColumns = new ArrayList<CsvColumn>();

	/**
	 * Initializes a new CsvFile.
	 */
	public CsvFile() {
	}

	/**
	 * Adds a Csv Column to the Csv File class.
	 * 
	 * @param csvColumn
	 *            the Csv Column to add.
	 */
	public void addColumn(CsvColumn csvColumn) {
		this.csvColumns.add(csvColumn);
	}

	/**
	 * Gets the title of the Csv.
	 * 
	 * @return the title of the Csv.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Gets all columns within the Csv.
	 * 
	 * @return the columns within the Csv.
	 */
	public ArrayList<CsvColumn> getColumns() {
		return csvColumns;
	}

	/**
	 * Sets the title of the Csv.
	 * 
	 * @param title
	 *            the title of the Csv.
	 */
	public void setTitle(String title) {
		this.title = CsvUtilities.toProper(CsvUtilities.formatName(title));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder output = new StringBuilder();
		for (CsvColumn csvColumn : csvColumns) {
			output.append(csvColumn);
		}
		return output.toString();
	}
}
