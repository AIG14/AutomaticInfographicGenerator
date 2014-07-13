/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern;

import java.util.ArrayList;

import csv.entity.CsvFile;
import pattern.infogram.CategoryInfogram;
import pattern.infogram.DateInfogram;
import pattern.infogram.HistogramInfogram;
import pattern.infogram.IInfogram;
import pattern.infogram.TagInfogram;
import pattern.infogram.Top10ListerInfogram;
import pattern.infogram.TotalInfogram;

/**
 * Pattern Finder
 * 
 * @author Peter
 * 
 */
public class PatternFinder {
	CsvFile csvResult;

	/**
	 * Initializes a new Pattern Finder with the Csv file result.
	 * 
	 * @param csvResult
	 */
	public PatternFinder(CsvFile csvResult) {
		this.csvResult = csvResult;
	}

	/**
	 * Executes calculations on all known infograms.
	 * 
	 * @return
	 */
	public PatternResult[] executeCalculations() {
		ArrayList<PatternResult> patternResult = new ArrayList<PatternResult>();

		IInfogram[] infograms = new IInfogram[] { new CategoryInfogram(),
				new TagInfogram(), new HistogramInfogram(), new DateInfogram(),
				new Top10ListerInfogram(), new TotalInfogram() };
		for (IInfogram infogram : infograms) {
			infogram.executeCalculations(csvResult, patternResult);
		}

		PatternResult patternResults[] = new PatternResult[patternResult.size()];
		return patternResult.toArray(patternResults);
	}
}
