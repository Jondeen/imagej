/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.legacy;

import ij.ImagePlus;

import java.util.HashSet;
import java.util.Set;

/**
 * The legacy output tracker is responsible for tracking important changes to
 * the IJ1 environment as a result of running a plugin. Important changes
 * include newly created {@link ImagePlus}es and {@link ImagePlus}es whose
 * window has closed.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 */
public class LegacyOutputTracker {

	// -- instance variables --

	/** Used to provide one list of {@link ImagePlus} per calling thread. */
	private static ThreadLocal<Set<ImagePlus>> outputImps =
		new ThreadLocal<Set<ImagePlus>>() {

			@Override
			protected synchronized Set<ImagePlus> initialValue() {
				return new HashSet<ImagePlus>();
			}
		};

	/** Used to provide one list of {@link ImagePlus} per calling thread. */
	private static ThreadLocal<Set<ImagePlus>> closedImps =
		new ThreadLocal<Set<ImagePlus>>() {

			@Override
			protected synchronized Set<ImagePlus> initialValue() {
				return new HashSet<ImagePlus>();
			}
		};

	/**
	 * Tracks which {@link ImagePlus}es have their close() method initiated by IJ2.
	 */
	private static ThreadLocal<Set<ImagePlus>> beingClosedByIJ2 =
		new ThreadLocal<Set<ImagePlus>>() {

			@Override
			protected synchronized Set<ImagePlus> initialValue() {
				return new HashSet<ImagePlus>();
			}
		};

	// -- public interface --

	/**
	 * Gets a list for storing the {@link ImagePlus}es generated by a plugin. This
	 * method is (??) thread-safe, because it uses a separate set per thread.
	 */
	public static Set<ImagePlus> getOutputImps() {
		return outputImps.get();
	}

	/**
	 * Gets a list for storing the {@link ImagePlus}es closed by a plugin. This
	 * method is (??) thread-safe, because it uses a separate set per thread.
	 */
	public static Set<ImagePlus> getClosedImps() {
		return closedImps.get();
	}

	/**
	 * Informs tracker that IJ2 has initiated the close() of an {@link ImagePlus}.
	 */
	public static void closeInitiatedByIJ2(final ImagePlus imp) {
		beingClosedByIJ2.get().add(imp);
	}

	/**
	 * Informs tracker that IJ2 has finished the close() of an {@link ImagePlus}.
	 */
	public static void closeCompletedByIJ2(final ImagePlus imp) {
		beingClosedByIJ2.get().remove(imp);
	}

	/**
	 * Returns true if a given {@link ImagePlus} is currently being closed by
	 * ImageJ2.
	 */
	public static boolean isBeingClosedbyIJ2(final ImagePlus imp) {
		return beingClosedByIJ2.get().contains(imp);
	}
}
