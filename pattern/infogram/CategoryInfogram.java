/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern.infogram;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;
import pattern.PatternResult;

/**
 * Category Infogram
 * 
 * @author Peter
 */
public class CategoryInfogram implements IInfogram {

	/**
	 * Initialize a instance of a Category Infogram.
	 */
	public CategoryInfogram() {

	}

	/**
	 * Execute calculations for category infogram.
	 * 
	 * @param csvResult
	 * @param patternResults
	 */
	public void executeCalculations(CsvFile csvResult,
			ArrayList<PatternResult> patternResults) {
		int partitions = 6;
		ArrayList<CsvColumn> columns = csvResult.getColumns();
		int maxResults = 6;
		for (CsvColumn column : columns) {
			if (maxResults < 0) {
				return;
			}
			if (column.getProperty("IsCategoryText")
					&& !column.getTextName().toLowerCase().contains("details")) {
				Map<String, Integer> groupCounts = column.getCachedCounts();
				Entry<String, Integer>[] groupItemCounts = CsvUtilities
						.extractMaximumCounts(groupCounts, column, partitions,
								false);
				if (groupItemCounts[partitions] == null
						|| groupItemCounts[partitions].getValue() < column
								.getNonBlankRows() / 1.5) {
					StringBuilder jsonOutput = new StringBuilder();
					int groupItemCounter = 0;
					for (Entry<String, Integer> groupItemCount : groupItemCounts) {
						if (groupItemCount != null) {
							jsonOutput.append(groupItemCount.getKey() + ": "
									+ groupItemCount.getValue() + "\r\n");
							groupItemCounter++;
						}
					}
					if (groupItemCounter > 1) {
						PatternResult patternResult = new PatternResult();
						patternResult.setPatternType(1);
						patternResult.setPatternTitle(column.getTextName());
						patternResult.setPatternJson(jsonOutput.toString());
						patternResults.add(patternResult);
						maxResults--;
					}
				}
			}
		}
	}
}
