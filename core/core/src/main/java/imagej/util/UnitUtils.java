//
// UnitUtils.java
//

/*
ImageJ software for multidimensional image processing and analysis.

Copyright (c) 2010, ImageJDev.org.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the names of the ImageJDev.org developers nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package imagej.util;

/**
 * Utility methods for working with units.
 * 
 * @author Barry DeZonia
 * @author Curtis Rueden
 */
public final class UnitUtils {

	private static final String[] BYTE_UNITS = { "B", "KB", "MB", "GB", "TB",
		"PB", "EB", "ZB", "YB" };

	private static final double LOG1024 = Math.log(1024);

	private UnitUtils() {
		// prevent instantiation of utility class
	}

	public static String getAbbreviatedByteLabel(final double totBytes) {
		if (totBytes < 0) {
			throw new IllegalArgumentException("Bytes must be non-negative");
		}
		if (totBytes == 0) return "0B";

		// compute unit
		final int rawPow = (int) (Math.log(totBytes) / LOG1024);
		final int pow = Math.min(rawPow, BYTE_UNITS.length - 1);

		// compute value from unit
		final double value = totBytes / Math.pow(1024.0, pow);

		// format result with 0 decimal places for bytes, or 1 for larger values
		final String format = pow == 0 ? "%.0f%s" : "%.1f%s";
		return String.format(format, value, BYTE_UNITS[pow]);
	}

}