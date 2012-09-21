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

package imagej.legacy.patches;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import imagej.legacy.DefaultLegacyService;
import imagej.legacy.LegacyOutputTracker;
import imagej.util.Log;

/**
 * Overrides {@link ImageWindow} methods.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 */
public final class ImageWindowMethods {

	private ImageWindowMethods() {
		// prevent instantiation of utility class
	}

	/** Replaces {@link ImageWindow#setVisible(boolean)}. */
	public static void setVisible(final DefaultLegacyService legacyService, final ImageWindow obj, final boolean visible) {
		Log.debug("ImageWindow.setVisible(" + visible + "): " + obj);
		if (!visible) return;
		legacyService.legacyImageChanged(obj.getImagePlus());
		// TODO - not sure this is correct. Does setVisible(true) imply that it
		// becomes the current window? This arose in fixing a bug with 3d Project
		// support.
		WindowManager.setCurrentWindow(obj);
	}

	/** Replaces {@link ImageWindow#show()}. */
	public static void show(final DefaultLegacyService legacyService, final ImageWindow obj) {
		setVisible(legacyService, obj, true);
	}

	/** Appends {@link ImageWindow#close()}. */
	// TODO: LegacyOutputTracker should not be a singleton
	public static void close(@SuppressWarnings("unused") final DefaultLegacyService legacyService, final ImageWindow obj) {
		final ImagePlus imp = obj.getImagePlus();
		if ((imp != null) && (!LegacyOutputTracker.isBeingClosedbyIJ2(imp))) {
			LegacyOutputTracker.getClosedImps().add(imp);
		}
	}
}
