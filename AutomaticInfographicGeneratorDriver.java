/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

import java.io.FileNotFoundException;
import java.io.IOException;

import csv.CsvProcessor;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;
import pattern.PatternFinder;
import pattern.PatternResult;

/**
 * 
 * Why use the Automatic Infographic Generator tool? 1) Make better decisions
 * with clear facts, discover things you never knew. Make discoveries in data
 * before any other human. 2) Don't waste your own time manually analysing data,
 * let the computer do the hard work for you. No need to spend money on data
 * consultants, find discoveries right away. 3) Impress your boss with pretty
 * graphs and happy facts. Discover facts and impress others. Look smart and
 * discover something amazing.
 * 
 * @author Peter
 * 
 */
public class AutomaticInfographicGeneratorDriver {
	/**
	 * The Driver for the Automatic Infographic Generator (AIG).
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		long startTime = System.currentTimeMillis();
		String filepath = "";

		if (args.length > 0) {
			filepath = args[0];
		}

		CsvProcessor csvProcessor = new CsvProcessor();
		CsvFile csvResult = csvProcessor.Process(filepath);
		String title = null;
		if (args.length > 1) {
			title = CsvUtilities.toProper(CsvUtilities.formatName(args[1]));
		} else {
			title = csvResult.getTitle();
		}

		System.out.println("#Title: " + title);
		System.out.println("#MD5: " + csvProcessor.getMD5Checksum());
		PatternFinder patternFinder = new PatternFinder(csvResult);
		PatternResult[] patterns = patternFinder.executeCalculations();
		for (PatternResult patternResult : patterns) {
			System.out.println(patternResult);
		}

		long endTime = System.currentTimeMillis();
		System.out.println("#Total execution time: " + (endTime - startTime));
	}
}
