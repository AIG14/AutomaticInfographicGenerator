/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package csv.headerprocessor;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;

/**
 * Data Type Column Processor.
 * 
 * @author Peter
 */
public class DataTypeColumnProcessor {
	/**
	 * The pattern representing a numeric data type.
	 */
	// TODO Handle negatives.
	private Pattern numericPattern = Pattern
			.compile("\\$?\\-?[\\s0-9]+\\.?[\\s0-9]*");

	/**
	 * The pattern representing a date data type.
	 */
	private Pattern datePattern = Pattern
			.compile("^([0-9]+/[0-9]+/[0-9]{4}|[0-9]{4}-[0-9]{2}-[0-9]{2}.*?)");

	/**
	 * The pattern representing a US/AUS date format data type.
	 */
	private Pattern dateConvertPattern = Pattern
			.compile("^[0-9]+/[0-9]+/[0-9]{4}");

	/**
	 * The pattern representing a date data 2 type.
	 */
	private Pattern dateConvertPattern2 = Pattern
			.compile("^[0-9]{1,2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-[0-9]{2}");

	/**
	 * The pattern representing a non ISO date format data type.
	 */
	private Pattern dateConvertPattern3 = Pattern
			.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:.*?");

	/**
	 * The pattern representing a summable heading.
	 */
	private Pattern summablePattern = Pattern
			.compile(
					".*(Id|Code|Post|Zip|Rate|phone|fax|mobile|per|age|expect|average|current|avg|key|year|date|section|ratio|latitu|long|median|detail).*",
					Pattern.CASE_INSENSITIVE);

	/**
	 * The pattern representing a descriptive text data type.
	 */
	private Pattern desctiptiveTextPattern = Pattern.compile(".* .* .* .*");

	/**
	 * The pattern representing a GUID data type.
	 */
	private Pattern guidPattern = Pattern
			.compile("\\{?[A-Z0-9]{8}\\-[A-Z0-9]{4}\\-[A-Z0-9]{4}\\-[A-Z0-9]{4}\\-[A-Z0-9]{12}\\}?");

	/**
	 * The pattern representing a month data type.
	 */
	private Pattern monthPattern = Pattern.compile(
			".*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * The matcher representing a numeric data type.
	 */
	private Matcher numericMatcher = numericPattern.matcher("");

	/**
	 * The matcher representing a date data type.
	 */
	private Matcher dateMatcher = datePattern.matcher("");

	/**
	 * The matcher representing a US/AUS formatted date data type.
	 */
	private Matcher dateConvertMatcher = dateConvertPattern.matcher("");

	/**
	 * The matcher representing a date data type.
	 */
	private Matcher dateConvertMatcher2 = dateConvertPattern2.matcher("");

	/**
	 * The matcher representing a date data type.
	 */
	private Matcher dateConvertMatcher3 = dateConvertPattern3.matcher("");

	/**
	 * The matcher representing a summable header data type.
	 */
	private Matcher summableMatcher = summablePattern.matcher("");

	/**
	 * The matcher representing a descriptive text data type.
	 */
	private Matcher descriptiveTextMatcher = desctiptiveTextPattern.matcher("");

	/**
	 * The matcher representing a GUID data type.
	 */
	private Matcher guidMatcher = guidPattern.matcher("");

	/**
	 * The matcher representing a Month data type.
	 */
	private Matcher monthMatcher = monthPattern.matcher("");

	/**
	 * The threshold margin for determining if a header is of a certain data
	 * type.
	 */
	private float thresholdMargin = 0.88f;

	/**
	 * The threshold for determining if a column set is an option group.
	 */
	private float thresholdOptionGroup = 2;

	/**
	 * Set Is Integer property.
	 * 
	 * @param csvContent
	 *            The Csv resulted data.
	 */
	public void setProperty(CsvFile csvContent) {
		for (CsvColumn column : csvContent.getColumns()) {
			ArrayList<String> values = column.getData();
			int isNumericCounter = 0;
			int isDateCounter = 0;
			int isDescriptiveTextCounter = 0;
			int isGuidCounter = 0;
			int nonBlankRowCounter = 0;
			int usDateFormat = 0;
			int ausDateFormat = 0;
			int valueCounterLimit = values.size();
			for (int valueCounter = 0; valueCounter < valueCounterLimit; valueCounter++) {
				String value = values.get(valueCounter);
				// Cell based properties.
				if (value != null) {
					numericMatcher.reset(value);
					if (numericMatcher.matches()) {
						// Step 1: Check if numeric.
						isNumericCounter++;
					} else {
						dateMatcher.reset(value);
						dateConvertMatcher.reset(value);
						boolean dateMatcherMatches = dateMatcher.matches();
						if (dateMatcherMatches) {
							// Step 2: Check if date, and if so check if in US
							// date.
							isDateCounter++;
							if (dateConvertMatcher
									.matches()) {
								String[] params = value.split("/");
								if (params.length == 3) {
									if (CsvUtilities.toInt(params[0]) > 12) {
										ausDateFormat++;
									} else if (CsvUtilities.toInt(params[1]) > 12) {
										usDateFormat++;
									}
								}
							}
						} else {
							// Step 3: Check for GUID.
							guidMatcher.reset(value);
							if (guidMatcher.matches()) {
								isGuidCounter++;
							} else {
								// Step 4: Check is Descriptive Text.
								descriptiveTextMatcher.reset(value);
								if (descriptiveTextMatcher.matches()) {
									isDescriptiveTextCounter++;
								}
							}
						}
					}
					nonBlankRowCounter++;
				}
			}

			// Column based properties.
			boolean isNumeric = nonBlankRowCounter != 0
					&& (isNumericCounter / (double) nonBlankRowCounter) > thresholdMargin;
			boolean isDate = nonBlankRowCounter != 0
					&& (isDateCounter / (double) nonBlankRowCounter) > thresholdMargin;
			boolean isDescriptiveText = nonBlankRowCounter != 0
					&& (isDescriptiveTextCounter / (double) nonBlankRowCounter) > 0.5;
			boolean isGuid = nonBlankRowCounter != 0
					&& (isGuidCounter / (double) nonBlankRowCounter) > thresholdMargin;
			boolean isCategoryText = false;
			boolean isPrimary = false;
			boolean isSumable = false;
			boolean isMonthHeader = false;

			if (!isDate) {
				Map<String, Integer> groupCounts = column.getCachedCounts();
				if (!isNumeric
						&& groupCounts.size() < values.size()
								/ thresholdOptionGroup) {
					isCategoryText = true;
				}

				if (groupCounts.size() == values.size()) {
					isPrimary = true;
				}
			} else {
				// Convert date patterns to ISO.
				for (int valueCounter = 0; valueCounter < values.size(); valueCounter++) {
					String value = values.get(valueCounter);
					if (value != null) {
						dateConvertMatcher.reset(value);
						dateConvertMatcher2.reset(value);
						dateConvertMatcher3.reset(value);
						if (dateConvertMatcher.matches()) {
							if (usDateFormat > ausDateFormat) {
								values.set(valueCounter, CsvUtilities
										.toISODate("MM/dd/yyyy", value));
							} else {
								values.set(valueCounter, CsvUtilities
										.toISODate("dd/MM/yyyy", value));
							}
						} else if (dateConvertMatcher2.matches()) {
							values.set(valueCounter,
									CsvUtilities.toISODate("dd-MMM-yy", value));
						} else if (dateConvertMatcher3.matches()) {
							values.set(
									valueCounter,
									CsvUtilities.toISODate("yyyy-MM-dd",
											value.substring(0, 10)));
						}
					}
				}
			}

			if (isNumeric) {
				summableMatcher.reset(column.getTextName());
				isSumable = !summableMatcher.matches();
			}

			monthMatcher.reset(column.getTextName());
			isMonthHeader = monthMatcher.matches();

			column.setNonBlankRows(nonBlankRowCounter);
			column.setProperty("IsNumeric", isNumeric);
			column.setProperty("IsDate", isDate);
			column.setProperty("IsDescriptiveText", isDescriptiveText);
			column.setProperty("IsGuid", isGuid);
			column.setProperty("IsCategoryText", isCategoryText);
			column.setProperty("IsPrimary", isPrimary);
			column.setProperty("IsSummable", isSumable);
			column.setProperty("IsMonthHeader", isMonthHeader);

			// Todo: Inject code to automatically update column data when int.
		}
	}
}
