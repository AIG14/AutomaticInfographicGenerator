/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern.infogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;
import pattern.PatternResult;

/**
 * Histogram Infogram.
 * 
 * @author Peter
 */
public class HistogramInfogram implements IInfogram {

	/**
	 * Initialize a instance of a Histogram Infogram.
	 */
	public HistogramInfogram() {

	}

	/**
	 * Execute calculations for histogram infogram.
	 * 
	 * @param csvResult
	 * @param patternResults
	 */
	public void executeCalculations(CsvFile csvResult,
			ArrayList<PatternResult> patternResults) {
		ArrayList<CsvColumn> columns = csvResult.getColumns();
		int maxResults = 6;
		for (CsvColumn column : columns) {
			if (maxResults < 0) {
				return;
			}
			if (column.getProperty("IsNumeric")
					&& column.getProperty("IsSummable")
					&& !column.getProperty("IsMonthHeader")) {
				Float min = null;
				Float max = null;
				ArrayList<String> values = column.getData();
				for (String value : values) {
					if (value != null) {
						Float dataValue = CsvUtilities.toFloat(value);
						if (min == null || dataValue < min) {
							min = dataValue;
						}
						if (max == null || dataValue > max) {
							max = dataValue;
						}
					}
				}

				long increment = (int) Math.ceil(Math
						.max((max - min) / 10.0, 1));
				long factor = (int) Math.pow(10.0,
						Math.floor(Math.log10((double) increment)));
				increment = (increment / factor) * factor;
				if (increment < 1000000) {
					Map<Long, Integer> histogramCounts = new HashMap<Long, Integer>();
					ArrayList<Long> keys = new ArrayList<Long>();
					for (String value : values) {
						if (value != null) {
							long dataValue = ((long) (CsvUtilities
									.toFloat(value) / increment)) * increment;
							int histogramCounter = 1;
							if (histogramCounts.containsKey(dataValue)) {
								histogramCounter = histogramCounts
										.get(dataValue) + 1;
							} else {
								keys.add(dataValue);
							}
							histogramCounts.put(dataValue, histogramCounter);
						}
					}

					if (histogramCounts.size() > 1
							|| !histogramCounts.containsKey(0)) {
						StringBuilder jsonOutput = new StringBuilder();
						Collections.sort(keys, new Comparator<Long>() {
							@Override
							public int compare(Long int1, Long int2) {
								return int1.compareTo(int2);
							}
						});

						for (Long key : keys) {
							String label = key
									+ (increment != 1 ? " to "
											+ (key + increment) : "");
							jsonOutput.append(label + ": "
									+ histogramCounts.get(key) + "\r\n");
						}

						PatternResult patternResult = new PatternResult();
						patternResult.setPatternType(3);
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
