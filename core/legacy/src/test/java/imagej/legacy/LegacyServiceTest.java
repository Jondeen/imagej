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

import java.util.Collection;
import java.util.Collections;

import imagej.ImageJ;
import imagej.Prioritized;
import imagej.command.CommandService;
import imagej.command.DefaultCommandService;
import imagej.event.StatusService;
import imagej.legacy.ij1plugin.Show_Progress;
import imagej.service.Service;
import imagej.service.ServiceHelper;
import imagej.service.ServiceIndex;
import imagej.util.AppUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Test the legacy service
 *
 * @author Johannes Schindelin
 */

public class LegacyServiceTest {
	/**
	 * Ensures that multiple ImageJ contexts with their own, independent
	 * LegacyServices can co-exist.
	 */
	@Test
	public void testNoSingleton() {
		final String IJ1_PLUGIN = Show_Progress.class.getName();
		System.setProperty("ij.dir", AppUtils.getBaseDirectory(IJ1_PLUGIN).getParent());
		final ImageJ[] contexts = new ImageJ[2];
		for (int i = 0; i < 2; i++) {
			contexts[i] = new ImageJ();
			final ServiceHelper helper = new ServiceHelper(contexts[i]);
			helper.createExactService(BufferedStatusService.class);
			//helper.createExactService(DefaultCommandService.class);
			helper.createExactService(DefaultLegacyService.class);
			assertNotNull(contexts[i].getService(CommandService.class));
			assertNotNull(contexts[i].getService(LegacyService.class));
			LegacyService legacyService =  contexts[i].getService(LegacyService.class);
			assertNotNull(legacyService);
			legacyService.runLegacyCommand(IJ1_PLUGIN, "progress=" + i + " maximum=2");
		}
		assertEquals("0/2\n", contexts[0].getService(StatusService.class).toString());
		assertEquals("1/2\n", contexts[1].getService(StatusService.class).toString());
	}

	private static void addService(final ImageJ context, final Service service) {
		service.setContext(context);
		service.initialize();
		context.getServiceIndex().add(service);
	}

	private static class BufferedStatusService implements StatusService {
		private ImageJ context;
		private double priority;
		private StringBuilder builder = new StringBuilder();
		private String latestMessage;

		/** Updates the progress bar. */
		@Override
		public void showProgress(int value, int maximum) {
			builder.append(value).append('/').append(maximum).append('\n');
		}

		/** Updates ImageJ's status message. */
		@Override
		public void showStatus(String message) {
			builder.append(message).append('\n');
			latestMessage = message;
		}

		/** Updates ImageJ's status message and progress bar. */
		@Override
		public void showStatus(int progress, int maximum, String message) {
			showStatus(progress, maximum, message, false);
		}

		/**
		 * Updates ImageJ's status message and progress bar, optionally flagging the
		 * status notification as a warning.
		 *
		 * @param progress New progress value
		 * @param maximum New progress maximum
		 * @param message New status message
		 * @param warn Whether or not this notification constitutes a warning
		 */
		@Override
		public void showStatus(int progress, int maximum, String message, boolean warn) {
			showStatus(message);
			if (warn) {
				warn(message);
			} else {
				showProgress(progress, maximum);
			}
		}

		/** Issues a warning message. */
		@Override
		public void warn(String message) {
			showStatus("WARNING: " + message);
		}

		/** Clears ImageJ's status message. */
		@Override
		public void clearStatus() {
			latestMessage = "";
		}

		/** retrieve the currently displayed status string */
		@Override
		public String getCurrentStatusString() {
			return latestMessage;
		}

		@Override
		public String toString() {
			return builder.toString();
		}

		@Override
		public void initialize() {
			// do nothing
		}

		@Override
		public ImageJ getContext() {
			return context;
		}

		@Override
		public void setContext(final ImageJ context) {
			this.context = context;
		}

		@Override
		public double getPriority() {
			return priority;
		}

		@Override
		public void setPriority(double priority) {
			this.priority = priority;
		}

		@Override
		public int compareTo(Prioritized other) {
			return Double.compare(getPriority(), other.getPriority());
		}
	}
}
