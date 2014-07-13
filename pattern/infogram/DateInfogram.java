/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern.infogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;
import pattern.PatternResult;

/**
 * Date Infogram
 * 
 * @author Peter
 */
public class DateInfogram implements IInfogram {

	/**
	 * Initialize a instance of a Date Infogram.
	 */
	public DateInfogram() {

	}

	/**
	 * Execute calculations for date infogram.
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
			if (column.getProperty("IsDate")) {
				boolean itemUnique = true;
				Date max = null;
				Date min = null;
				ArrayList<String> values = column.getData();
				ArrayList<String> keysYear = new ArrayList<String>();
				HashMap<String, Integer> countsDay = new HashMap<String, Integer>();
				HashMap<String, Integer> countsYear = new HashMap<String, Integer>();
				int[] countsDayOfWeek = new int[] { 0, 0, 0, 0, 0, 0, 0 };
				for (String item : values) {
					if (item != null) {
						// Day based calculations.
						Date dataValue = CsvUtilities.toDate(item);
						item = item.substring(0, Math.min(item.length(), 10));
						int counter = 1;
						if (countsDay.containsKey(item)) {
							counter = countsDay.get(item) + 1;
							if (itemUnique) {
								itemUnique = false;
							}
						} else {
							if (dataValue != null) {
								if (max == null || dataValue.compareTo(max) > 0) {
									max = dataValue;
								}
								if (min == null || dataValue.compareTo(min) < 0) {
									min = dataValue;
								}
							}
						}
						countsDay.put(item, counter);

						// Year based calculations.
						item = item.substring(0, Math.min(item.length(), 4));
						counter = 1;
						if (countsYear.containsKey(item)) {
							counter = countsYear.get(item) + 1;
						} else {
							keysYear.add(item);
						}
						countsYear.put(item, counter);

						// Day of week calculation.
						if (dataValue != null) {
							counter = countsDayOfWeek[CsvUtilities
									.getDayOfWeek(dataValue)] + 1;
							countsDayOfWeek[CsvUtilities
									.getDayOfWeek(dataValue)] = counter;
						}
					}
				}
				if (itemUnique) {
					// items are unique so graph against other dates...
				} else {
					// just graph counts

					Collections.sort(keysYear, new Comparator<String>() {
						@Override
						public int compare(String str1, String str2) {
							return str1.compareTo(str2);
						}
					});

					// Show for year.
					if (keysYear.size() > 1) {
						StringBuilder jsonOutput = new StringBuilder();
						for (String keyYear : keysYear) {
							jsonOutput.append(keyYear + ": "
									+ countsYear.get(keyYear) + "\r\n");
						}

						PatternResult patternResult = new PatternResult();
						patternResult.setPatternType(4);
						patternResult.setPatternTitle("Count of "
								+ column.getTextName() + " (By&nbsp;year)");
						patternResult.setPatternJson(jsonOutput.toString());
						patternResults.add(patternResult);
						maxResults--;
					}

					// Show for day.
					if (countsDay.size() > 1) {
						StringBuilder jsonOutput = new StringBuilder();
						if (max != null) {
							Date endDate = CsvUtilities.addDays(max, -364);
							if (endDate.compareTo(min) < 0) {
								endDate = min;
							}

							for (Date setDate = endDate; setDate.compareTo(max) <= 0; setDate = CsvUtilities
									.addDays(setDate, 1)) {
								String dateLabel = CsvUtilities
										.toISODate(setDate);
								String dateLabelDisplay = CsvUtilities
										.toDisplayDate(setDate);
								dateLabel = dateLabel.substring(0,
										Math.min(dateLabel.length(), 10));
								jsonOutput
										.append(dateLabelDisplay
												+ ": "
												+ (countsDay
														.containsKey(dateLabel) ? countsDay
														.get(dateLabel) : 0)
												+ "\r\n");
							}
						}

						PatternResult patternResult = new PatternResult();
						patternResult.setPatternType(4);
						patternResult.setPatternTitle("Count of "
								+ column.getTextName());
						patternResult.setPatternJson(jsonOutput.toString());
						patternResults.add(patternResult);
						maxResults--;
					}

					// Show for day of week.
					StringBuilder jsonOutput = new StringBuilder();
					jsonOutput.append("Sunday: " + countsDayOfWeek[0] + "\r\n");
					jsonOutput.append("Monday: " + countsDayOfWeek[1] + "\r\n");
					jsonOutput
							.append("Tuesday: " + countsDayOfWeek[2] + "\r\n");
					jsonOutput.append("Wednesday: " + countsDayOfWeek[3]
							+ "\r\n");
					jsonOutput.append("Thursday: " + countsDayOfWeek[4]
							+ "\r\n");
					jsonOutput.append("Friday: " + countsDayOfWeek[5] + "\r\n");
					jsonOutput.append("Saturday: " + countsDayOfWeek[6]
							+ "\r\n");

					PatternResult patternResult = new PatternResult();
					patternResult.setPatternType(4);
					patternResult
							.setPatternTitle("Count of " + column.getTextName()
									+ " (Day&nbsp;of&nbsp;week)");
					patternResult.setPatternJson(jsonOutput.toString());
					patternResults.add(patternResult);
				}
			}
		}
	}
}
