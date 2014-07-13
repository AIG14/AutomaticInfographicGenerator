/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern.infogram;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import csv.entity.CsvColumn;
import csv.entity.CsvFile;
import csv.entity.CsvUtilities;
import pattern.PatternResult;

/**
 * Tag Infogram.
 * 
 * @author Peter
 */
public class TagInfogram implements IInfogram {

	/**
	 * Random used for allocating colours.
	 */
	private Random random = new Random(1);

	/**
	 * Initialize a instance of a Tag Infogram.
	 */
	public TagInfogram() {

	}

	/**
	 * Execute calculations for tag infogram.
	 * 
	 * @param csvResult
	 * @param patternResults
	 */
	public void executeCalculations(CsvFile csvResult,
			ArrayList<PatternResult> patternResults) {
		ArrayList<CsvColumn> columns = csvResult.getColumns();
		AffineTransform affineTransform = new AffineTransform();
		FontRenderContext fontRenderContext = new FontRenderContext(
				affineTransform, true, true);
		int maxResults = 6;
		for (CsvColumn column : columns) {
			if (maxResults < 0) {
				return;
			}
			if (column.getProperty("IsDescriptiveText")) {
				Map<String, Integer> wordGroups = new HashMap<String, Integer>();
				Map<String, Integer> groupCounts = column.getCachedCounts();
				for (Entry<String, Integer> entry : groupCounts.entrySet()) {
					String[] words = entry.getKey().split("[,/\\s:;\"]+");
					for (String word : words) {
						if (!word.equalsIgnoreCase("and")
								&& !word.equalsIgnoreCase("or")
								&& !word.equalsIgnoreCase("of")
								&& !word.equalsIgnoreCase("the")
								&& !word.equalsIgnoreCase("to")
								&& !word.equalsIgnoreCase("in")
								&& !word.equalsIgnoreCase("from")
								&& !word.equalsIgnoreCase("for")) {
							word = CsvUtilities.toProper(word);
							if (word.length() > 1) {
								int wordCounter = 1;
								if (wordGroups.containsKey(word)) {
									wordCounter = wordGroups.get(word)
											+ entry.getValue();
								}
								wordGroups.put(word, wordCounter);
							}
						}
					}
				}
				Entry<String, Integer>[] groupItemCounts = CsvUtilities
						.extractMaximumCounts(wordGroups, column, 50, true);

				if (groupItemCounts.length > 0) {
					ArrayList<Integer> fontWidth = new ArrayList<Integer>();
					ArrayList<Integer> fontHeight = new ArrayList<Integer>();
					ArrayList<Integer> leftPositions = new ArrayList<Integer>();
					ArrayList<Integer> topPositions = new ArrayList<Integer>();

					StringBuilder jsonOutput = new StringBuilder();
					int maxWidth = 600;
					int maxHeight = 600;

					double logBase = 1.2;
					double scale = 36 / (Math.log((groupItemCounts[0]
							.getValue() + 1)) / Math.log(logBase));

					for (Entry<String, Integer> groupItemCount : groupItemCounts) {
						if (groupItemCount != null
								&& groupItemCount.getKey() != "Other") {

							String label = groupItemCount.getKey();

							int fontSize = (int) (scale
									* Math.log((groupItemCount.getValue() + 1)) / Math
									.log(logBase)) + 7;
							Font font = new Font("helvetica", Font.BOLD,
									fontSize);

							int textWidth = (int) (font.getStringBounds(label,
									fontRenderContext).getWidth());

							int textHeight = (int) (font.getStringBounds(label,
									fontRenderContext).getHeight() - (fontSize / 4));

							// Set left Positions and top Positions
							Integer topPosition = null;
							Integer leftPosition = null;
							int maxAttempts = 300;
							for (int attemptCounter = 0; attemptCounter < maxAttempts
									&& leftPosition == null; attemptCounter++) {
								double randomAngle = random.nextFloat()
										* Math.PI * 2;

								int p1x = (int) (Math.cos(randomAngle)
										* (attemptCounter / (float) maxAttempts) * (maxWidth / 2))
										+ (maxWidth / 2);
								int p1y = (int) (Math.sin(randomAngle)
										* (attemptCounter / (float) maxAttempts)
										* maxHeight / 2)
										+ (maxHeight / 2);

								int p2x = p1x + textWidth;
								int p2y = p1y + textHeight;
								if (p2x < maxWidth) {

									boolean conflictFound = false;
									for (int previouslyAllocatedCounter = 0; previouslyAllocatedCounter < fontWidth
											.size() && !conflictFound; previouslyAllocatedCounter++) {

										Integer p3x = leftPositions
												.get(previouslyAllocatedCounter);
										Integer p3y = topPositions
												.get(previouslyAllocatedCounter);
										if (p3x != null) {
											int p4x = p3x
													+ fontWidth
															.get(previouslyAllocatedCounter);
											int p4y = p3y
													+ fontHeight
															.get(previouslyAllocatedCounter);

											if (p2y >= p3y && p1y <= p4y
													&& p2x >= p3x && p1x <= p4x) {
												conflictFound = true;
											}
										}
									}

									if (!conflictFound) {
										leftPosition = p1x;
										topPosition = p1y;
									}
								}
							}

							fontWidth.add(textWidth);
							fontHeight.add(textHeight);
							topPositions.add(topPosition);
							leftPositions.add(leftPosition);
							if (leftPosition != null) {
								jsonOutput.append(groupItemCount.getKey()
										+ ": " + fontSize + ":" + leftPosition
										+ ":" + topPosition + "\r\n");
							}

						}
					}

					PatternResult patternResult = new PatternResult();
					patternResult.setPatternType(2);
					patternResult.setPatternTitle(column.getTextName());
					patternResult.setPatternJson(jsonOutput.toString());
					patternResults.add(patternResult);
					maxResults--;
				}
			}
		}
	}
}
