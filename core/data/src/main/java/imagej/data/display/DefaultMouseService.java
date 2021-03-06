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

package imagej.data.display;

import imagej.display.Display;
import imagej.display.event.DisplayDeletedEvent;
import imagej.display.event.input.MsExitedEvent;
import imagej.display.event.input.MsMovedEvent;
import imagej.display.event.input.MsPressedEvent;
import imagej.display.event.input.MsReleasedEvent;
import imagej.event.EventHandler;
import imagej.event.EventService;
import imagej.plugin.Parameter;
import imagej.plugin.Plugin;
import imagej.service.AbstractService;
import imagej.service.Service;

import java.util.HashSet;

/**
 * Default implementation of {@link MouseService}.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultMouseService extends AbstractService implements
	MouseService
{

	@Parameter
	private EventService eventService;

	private HashSet<Integer> buttonsDown;

	private Display<?> display;
	private int lastX, lastY;

	// -- MouseService methods --

	@Override
	public EventService getEventService() {
		return eventService;
	}

	@Override
	public Display<?> getDisplay() {
		return display;
	}

	@Override
	public int getX() {
		return lastX;
	}

	@Override
	public int getY() {
		return lastY;
	}

	@Override
	public boolean isButtonDown(final int button) {
		return buttonsDown.contains(button);
	}

	// -- Service methods --

	@Override
	public void initialize() {
		buttonsDown = new HashSet<Integer>();
		clearCoords();

		subscribeToEvents(eventService);
	}

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final MsMovedEvent evt) {
		updateCoords(evt.getDisplay(), evt.getX(), evt.getY());
	}

	@EventHandler
	protected void onEvent(@SuppressWarnings("unused") final MsExitedEvent evt) {
		clearCoords();
	}

	@EventHandler
	protected void onEvent(final MsPressedEvent evt) {
		buttonsDown.add(evt.getButton());
	}

	@EventHandler
	protected void onEvent(final MsReleasedEvent evt) {
		buttonsDown.remove(evt.getButton());
	}

	@EventHandler
	protected void onEvent(final DisplayDeletedEvent evt) {
		if (display != evt.getObject()) return;
		clearCoords();
	}

	// -- Helper methods --

	private void updateCoords(final Display<?> d, final int x, final int y) {
		display = d;
		lastX = x;
		lastY = y;
	}

	private void clearCoords() {
		updateCoords(null, -1, -1);
	}

}
