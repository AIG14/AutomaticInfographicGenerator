/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern.infogram;

import java.text.DecimalFormat;
import java.util.ArrayList;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;
import pattern.PatternResult;

/**
 * Total Infogram.
 * 
 * @author Peter
 */
public class TotalInfogram implements IInfogram {

	/**
	 * Initialize a instance of a Total Infogram.
	 */
	public TotalInfogram() {

	}

	/**
	 * Execute calculations for total infogram.
	 * 
	 * @param csvResult
	 * @param patternResults
	 */
	public void executeCalculations(CsvFile csvResult,
			ArrayList<PatternResult> patternResults) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		ArrayList<CsvColumn> columns = csvResult.getColumns();
		int maxResults = 6;
		for (CsvColumn column : columns) {
			if (maxResults < 0) {
				return;
			}
			if (column.getProperty("IsSummable")
					&& !column.getProperty("IsMonthHeader")) {
				// Find summable total columns.
				double total = 0;
				ArrayList<String> dataValues = column.getData();
				for (String dataValueRaw : dataValues) {
					total += CsvUtilities.toDouble(dataValueRaw);
				}

				// TODO: Clean this code!
				String output = ((int) total) + "";
				if (total > 1000000000000L) {
					output = decimalFormat.format(total / 1000000000000L)
							+ " trillion";
				} else if (total > 1000000000L) {
					output = decimalFormat.format(total / 1000000000L)
							+ " billion";
				} else if (total > 1000000L) {
					output = decimalFormat.format(total / 1000000L)
							+ " million";
				} else if (total > 1000L) {
					output = decimalFormat.format(total / 1000L) + " thousand";
				} else if (total > 100L) {
					output = decimalFormat.format(total / 100L) + " hundred";
				}

				StringBuilder jsonOutput = new StringBuilder();
				jsonOutput.append(output + "\r\n");
				PatternResult patternResult = new PatternResult();
				patternResult.setPatternType(6);
				patternResult.setPatternTitle(column.getTextName());
				patternResult.setPatternJson(jsonOutput.toString());
				patternResults.add(patternResult);
				maxResults--;
			}
		}
	}
}
