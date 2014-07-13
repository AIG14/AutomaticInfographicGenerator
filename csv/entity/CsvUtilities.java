/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package csv.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Various Csv Utility functions.
 * 
 * @author Peter
 * 
 */
public class CsvUtilities {

	/**
	 * The ISO date format string.
	 */
	private static String isoDateStringFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * Gets an int representation of a string value.
	 * 
	 * @param value
	 *            the input string value.
	 * @return the int representation of the string value.
	 */
	public static int toInt(String value) {
		try {
			if (value != null) {
				return Integer.parseInt(value);
			}
		} catch (Exception ex) {
		}
		return 0;
	}

	/**
	 * Gets the float value of the input string value.
	 * 
	 * @param value
	 *            the string value
	 * @return a float value of the input string value.
	 */
	public static float toFloat(String value) {
		try {
			value = value.replaceAll("[\\s+ \\$]", "");
			if (value != null) {
				return Float.parseFloat(value);
			}
		} catch (Exception ex) {
		}
		return 0;
	}

	/**
	 * Gets the double value of the input string value.
	 * 
	 * @param value
	 *            the string value
	 * @return a double value of the input string value.
	 */
	public static double toDouble(String value) {
		try {
			value = value.replaceAll("[\\s+ \\$]", "");
			if (value != null) {
				return Double.parseDouble(value);
			}
		} catch (Exception ex) {
		}
		return 0;
	}

	/**
	 * Gets the date value of the input string value.
	 * 
	 * @param value
	 *            the string value
	 * @return a date value of the input string value.
	 */
	public static Date toDate(String value) {
		try {
			if (value != null) {
				SimpleDateFormat df = new SimpleDateFormat(isoDateStringFormat);
				return df.parse(value);
			}
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Gets a ISO representation of a date within a string, when provided the
	 * format of the original string.
	 * 
	 * @param format
	 *            the format of the original string
	 * @param value
	 *            the date time value within the string.
	 * @return an ISO representation of the string.
	 */
	public static String toISODate(String format, String value) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date date = sdf.parse(value);
			sdf.applyPattern(isoDateStringFormat);
			return sdf.format(date);
		} catch (ParseException e) {
		}
		return null;
	}

	public static String toDisplayDate(Date date) {
		DateFormat df = new SimpleDateFormat("EEEEE, d MMMM y");
		return df.format(date);
	}

	public static String toISODate(Date date) {
		DateFormat df = new SimpleDateFormat(isoDateStringFormat);
		return df.format(date);
	}

	/**
	 * Formats name, for example changes "ExampleId_A" to "Example Id".
	 * 
	 * @param original
	 *            the original string
	 * @return a formatted string that has camel case removed, along with the
	 *         replacement of underscore characters with spaces.
	 */
	public static String formatName(String original) {
		StringBuilder output = new StringBuilder();
		int originalLength = original.length();
                boolean convertToLowerCase = originalLength > 3 && original.toUpperCase().equals(original);
		char previousChar = '\0';
		for (int characterCounter = 0; characterCounter < originalLength; characterCounter++) {
			char currentChar = original.charAt(characterCounter);
			if (currentChar == '_' || currentChar == '-') {
				currentChar = ' ';
			}

			if (currentChar == ' ') {
				if (previousChar != ' ') {
					output.append(' ');
				}
			} else {
				if (currentChar >= 'A' && currentChar <= 'Z') {
					if (previousChar >= 'a' && previousChar <= 'z') {
						output.append(' ');
					}
				}
				if (currentChar != '\r' && currentChar != '\n') {
                                    char tmpChar = currentChar;
                                    if (convertToLowerCase && previousChar != ' ' && currentChar >= 'A' && currentChar <= 'Z') {
                                       tmpChar = Character.toLowerCase(currentChar);
                                    }
					output.append(tmpChar);
				}
			}
			previousChar = currentChar;
		}
		return output.toString();
	}

	/**
	 * Get the extract maximum counts.
	 * 
	 * @param input
	 *            the input data
	 * @param column
	 *            the column the data is associated with
	 * @param limitMaximumCounts
	 *            the number of items to limit returned
	 * @return a calculated set of maximums for the provided data.
	 */
	@SuppressWarnings("unchecked")
	public static Entry<String, Integer>[] extractMaximumCounts(
			Map<String, Integer> input, CsvColumn column,
			int limitMaximumCounts, boolean all) {
		Entry<String, Integer>[] output = (Entry<String, Integer>[]) new Entry[limitMaximumCounts + 1];
		for (Entry<String, Integer> entry : input.entrySet()) {
			if (!entry.getKey().equalsIgnoreCase("Other")) {
				boolean itemFound = false;
				for (int outputCounter = 0; outputCounter < limitMaximumCounts
						&& !itemFound; outputCounter++) {
					if (all
							|| entry.getValue() > ((double) column
									.getNonBlankRows()) / 20) {
						if (output[outputCounter] == null
								|| entry.getValue() > output[outputCounter]
										.getValue()) {
							// Move all items required right.
							for (int moveRightCounter = limitMaximumCounts - 1; moveRightCounter > outputCounter; moveRightCounter--) {
								output[moveRightCounter] = output[moveRightCounter - 1];
							}
							itemFound = true;
							output[outputCounter] = entry;
						}
					}
				}
			}
		}

		// Add other option field.
		int nonOther = 0;
		for (int outputCounter = 0; outputCounter < limitMaximumCounts; outputCounter++) {
			if (output[outputCounter] != null) {
				nonOther += output[outputCounter].getValue();
			}
		}
		if (column.getNonBlankRows() > nonOther) {
			// Force create of Map.Entity record by pushing into a temporary
			// HashMap.
			HashMap<String, Integer> tmp = new HashMap<String, Integer>();
			tmp.put("Other", column.getNonBlankRows() - nonOther);
			output[limitMaximumCounts] = (Entry<String, Integer>) tmp
					.entrySet().toArray()[0];
		}

		return output;
	}

	/**
	 * Gets the proper format for a string by capitalized the first letter, and
	 * removing all commas and brackets. Example: Converts string "example," to
	 * "Example"
	 * 
	 * @param input
	 *            the input string.
	 * @return the string formatted to proper with the first character
	 *         capitalized and following commas removed.
	 */
	public static String toProper(String input) {
		String output = input;
		if (input.length() > 1) {
			input = input.replaceAll("([,()\\.]|csv|xlsx|xls)", "");
			output = Character.toUpperCase(input.charAt(0))
					+ input.substring(1);
		}
		return output;
	}

	/**
	 * Adds the provided number of days to the input date. Note: days can be
	 * negative which will subtract the number of days from the date.
	 * 
	 * @param date
	 *            the input date
	 * @param days
	 *            the number of days to add to the date.
	 * @return the date with the number of days added, or subtracted if days is
	 *         negative.
	 */
	public static Date addDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * Gets the day of week where: Sunday = 0 Monday = 1 Tuesday = 2 .. Saturday
	 * = 6
	 * 
	 * @param date
	 *            The date.
	 * @return The day of the week starting from Sunday = 0.
	 */
	public static int getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK) - 1;
	}
}
