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
 * Top 10 Lister Infogram.
 * 
 * @author Peter
 */
public class Top10ListerInfogram implements IInfogram {

	/**
	 * Initialize a instance of a Top 10 Lister Infogram.
	 */
	public Top10ListerInfogram() {

	}

	/**
	 * Execute calculations for the top 10 lister infogram.
	 * 
	 * @param csvResult
	 * @param patternResults
	 */
	public void executeCalculations(CsvFile csvResult,
			ArrayList<PatternResult> patternResults) {
		int limitMaximumCounts = 10;
		ArrayList<CsvColumn> columns = csvResult.getColumns();
		int columnSize = columns.size();
		DecimalFormat decimalFormat = new DecimalFormat("#");
		int maxResults = 6;
		for (CsvColumn column : columns) {
			if (column.getProperty("IsPrimary")
					&& column.getNonBlankRows() > 10) {
				// Search for number columns, and display the top 10 and bottom
				// 10 results.

				ArrayList<String> primaryKeyValues = column.getData();
				for (int columnCompareCounter = 0; columnCompareCounter < columnSize; columnCompareCounter++) {
					CsvColumn columnCompare = columns.get(columnCompareCounter);
					String columnCompareText = columnCompare.getTextName();
					if (columnCompareText != column.getTextName()
							&& !columnCompareText.toLowerCase().contains(
									"details")
							&& columnCompare.getProperty("IsSummable")
							&& !columnCompare.getProperty("IsMonthHeader")) {
						if (maxResults < 0) {
							return;
						}
						Double[] maxValues = new Double[limitMaximumCounts];
						String[] maxLabels = new String[limitMaximumCounts];
						Double[] minValues = new Double[limitMaximumCounts];
						String[] minLabels = new String[limitMaximumCounts];
						ArrayList<String> dataValues = columnCompare.getData();
						boolean itemFound = false;
						for (int dataValueRawCounter = 0; dataValueRawCounter < dataValues
								.size(); dataValueRawCounter++) {
							String dataValueRaw = dataValues
									.get(dataValueRawCounter);
							double dataValue = CsvUtilities
									.toDouble(dataValueRaw);
							// Find maximum values.
							itemFound = false;
							for (int outputCounter = 0; outputCounter < limitMaximumCounts
									&& !itemFound; outputCounter++) {
								if (maxValues[outputCounter] == null
										|| dataValue > maxValues[outputCounter]) {
									// Move all items required right.
									for (int moveRightCounter = limitMaximumCounts - 1; moveRightCounter > outputCounter; moveRightCounter--) {
										maxValues[moveRightCounter] = maxValues[moveRightCounter - 1];
										maxLabels[moveRightCounter] = maxLabels[moveRightCounter - 1];
									}
									itemFound = true;
									maxValues[outputCounter] = dataValue;
									maxLabels[outputCounter] = primaryKeyValues
											.get(dataValueRawCounter);
								}
							}

							// Find minimum values.
							itemFound = false;
							for (int outputCounter = 0; outputCounter < limitMaximumCounts
									&& !itemFound; outputCounter++) {
								if (minValues[outputCounter] == null
										|| dataValue < minValues[outputCounter]) {

									// Move all items required right.
									for (int moveRightCounter = limitMaximumCounts - 1; moveRightCounter > outputCounter; moveRightCounter--) {
										minValues[moveRightCounter] = minValues[moveRightCounter - 1];
										minLabels[moveRightCounter] = minLabels[moveRightCounter - 1];
									}
									itemFound = true;
									minValues[outputCounter] = dataValue;
									minLabels[outputCounter] = primaryKeyValues
											.get(dataValueRawCounter);
								}
							}
						}

						// Render highest values.
						StringBuilder jsonOutput = new StringBuilder();
						for (int outputCounter = 0; outputCounter < limitMaximumCounts; outputCounter++) {
							if (maxValues[outputCounter] != null) {
								jsonOutput
										.append(String
												.format("%s:%s\r\n",
														maxLabels[outputCounter],
														decimalFormat
																.format(maxValues[outputCounter])));
							}
						}
						PatternResult patternResult = new PatternResult();
						patternResult.setPatternType(5);
						patternResult.setPatternTitle("Top 10 highest "
								+ columnCompare.getTextName() + " by " + column.getTextName());
						patternResult.setPatternJson(jsonOutput.toString());
						patternResults.add(patternResult);
						maxResults--;

						// Render lowest values.
						boolean nonZeroInMin = false;
						for (int outputCounter = 0; outputCounter < limitMaximumCounts
								&& !nonZeroInMin; outputCounter++) {
							if (minValues[outputCounter] != null) {
								if (minValues[outputCounter] != 0) {
									nonZeroInMin = true;
								}
							}
						}

						if (nonZeroInMin) {
							jsonOutput = new StringBuilder();
							for (int outputCounter = 0; outputCounter < limitMaximumCounts; outputCounter++) {
								if (minValues[outputCounter] != null) {
									jsonOutput
											.append(String
													.format("%s:%s\r\n",
															minLabels[outputCounter],
															decimalFormat
																	.format(minValues[outputCounter])));
								}
							}

							patternResult = new PatternResult();
							patternResult.setPatternType(5);
							patternResult.setPatternTitle("Top 10 lowest "
									+ columnCompare.getTextName() + " by "+column.getTextName());
							patternResult.setPatternJson(jsonOutput.toString());
							patternResults.add(patternResult);
							maxResults--;
						}
					}
				}
			}
		}
	}
}
