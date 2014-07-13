/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.headerprocessor.*;

/**
 * Csv Processor.
 * 
 * @author Peter
 */
public class CsvProcessor {

	/**
	 * MD5 checksum string.
	 */
	private String md5checksum = null;

	/**
	 * Csv Processor.
	 */
	public CsvProcessor() {

	}

	/**
	 * Get a MD5 string of the file contents.
	 * 
	 * @return String the MD5 checksum of the file contents.
	 */
	public String getMD5Checksum() {
		return this.md5checksum;
	}

	/**
	 * Process Csv file.
	 * 
	 * @param filename
	 *            string the filename of the CSV data to read.
	 * @return A CsvFile populated with the CSV data contents read by the
	 *         provided filename.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public CsvFile Process(String filename) throws FileNotFoundException,
			IOException {
		CsvFile csvContent = new CsvFile();
		File file = new File(filename);
		char seperater = ',';
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				file))) {
			StringBuilder inputBuffer = new StringBuilder();
			String line = bufferedReader.readLine();
			int ignoreLines = 0;
			boolean firstLines = true;
			while (line != null) {
				inputBuffer.append(line);
				inputBuffer.append(System.lineSeparator());
				if (line.indexOf(seperater) == -1) {
					if (firstLines) {
						ignoreLines++;
					}
				} else {
					firstLines = false;
				}
				line = bufferedReader.readLine();
			}
			MessageDigest messageDigest;
			try {
				messageDigest = MessageDigest.getInstance("MD5");

				messageDigest.update(inputBuffer.toString().getBytes());
				byte[] digest = messageDigest.digest();
				StringBuffer md5checksumTmp = new StringBuffer();
				for (byte b : digest) {
					md5checksumTmp.append(String.format("%02x", b & 0xff));
				}
				md5checksum = md5checksumTmp.toString();
			} catch (NoSuchAlgorithmException e) {
			}

			inputBuffer.append("\n");
			ArrayList<CsvColumn> csvColumns = new ArrayList<CsvColumn>();

			// Process CSV data.
			String rawCsvData = inputBuffer.toString();
			int numberOfCharacters = rawCsvData.length();
			int columnCounter = 0;
			boolean currentlyEscaped = false;
			boolean passedHeaderRow = false;
			boolean rowUpdated = false;
			StringBuilder valueBuffer = new StringBuilder();
			for (int characterCounter = 0; characterCounter < numberOfCharacters; characterCounter++) {
				char currentCharacter = rawCsvData.charAt(characterCounter);
				char nextCharacter = characterCounter + 1 < numberOfCharacters ? rawCsvData
						.charAt(characterCounter + 1) : '\0';
				if (currentCharacter == seperater && !currentlyEscaped) {
					if (passedHeaderRow) {
						if (columnCounter < csvColumns.size()) {
							csvColumns.get(columnCounter).addData(
									valueBuffer.toString());
						}
					} else {
						csvColumns.add(new CsvColumn(valueBuffer.toString()));
					}
					valueBuffer.setLength(0);
					columnCounter++;
					rowUpdated = true;
				} else if (currentCharacter == '"' && nextCharacter != '"') {
					currentlyEscaped = !currentlyEscaped;
				} else if ((currentCharacter == '\r' || currentCharacter == '\n')
						&& !currentlyEscaped) {
					if (ignoreLines > 0) {
						if (currentCharacter == '\n') {
							ignoreLines--;
						}
					} else {
						if (rowUpdated) {
							if (passedHeaderRow) {
								if (columnCounter < csvColumns.size()) {
									csvColumns.get(columnCounter).addData(
											valueBuffer.toString());
								}
								for (int remainingHeaders = columnCounter + 1; remainingHeaders < csvColumns
										.size(); remainingHeaders++) {
									csvColumns.get(columnCounter).addData("");
								}
							} else {
								csvColumns.add(new CsvColumn(valueBuffer
										.toString()));
								passedHeaderRow = true;
							}
							valueBuffer.setLength(0);
						}
						columnCounter = 0;
						rowUpdated = false;
					}
				} else {
					if (currentCharacter == '\r' || currentCharacter == '\n') {
						currentCharacter = ' ';
					}
					valueBuffer.append(currentCharacter);
					rowUpdated = true;

					if (currentCharacter == '"' && nextCharacter == '"') {
						characterCounter++;
					}
				}
			}

			for (CsvColumn csvColumn : csvColumns) {
				csvContent.addColumn(csvColumn);
			}
		}

		new DataTypeColumnProcessor().setProperty(csvContent);
		csvContent.setTitle(file.getName());
		return csvContent;
	}
}
