/**
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package pattern;

/**
 * Represents a Pattern Result.
 * 
 * @author Peter
 * 
 */
public class PatternResult {
	/**
	 * The pattern type for the pattern result.
	 */
	private int patternType;

	/**
	 * The pattern title for the pattern result.
	 */
	private String patternTitle;

	/**
	 * The pattern Json for the pattern result.
	 */
	private String patternJson;

	/**
	 * Initializes a new PatternResult.
	 */
	public PatternResult() {
	}

	/**
	 * Get pattern type.
	 * 
	 * @return an int representing the pattern type.
	 */
	public int getPatternType() {
		return patternType;
	}

	/**
	 * Sets the pattern type.
	 * 
	 * @param patternType
	 *            the pattern type.
	 */
	public void setPatternType(int patternType) {
		this.patternType = patternType;
	}

	/**
	 * Gets the pattern title.
	 * 
	 * @return
	 */
	public String getPatternTitle() {
		return this.patternTitle;
	}

	/**
	 * Sets the pattern title.
	 * 
	 * @param patternTitle
	 *            the pattern title.
	 */
	public void setPatternTitle(String patternTitle) {
		this.patternTitle = patternTitle;
	}

	/**
	 * Gets the pattern Json.
	 * 
	 * @return the pattern Json.
	 */
	public String getPatternJson() {
		return patternJson;
	}

	/**
	 * Sets the pattern Json.
	 * 
	 * @param patternJson
	 *            the pattern Json.
	 */
	public void setPatternJson(String patternJson) {
		this.patternJson = patternJson;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("[%d: %s\r\n%s]", patternType, patternTitle,
				patternJson);
	}
}
