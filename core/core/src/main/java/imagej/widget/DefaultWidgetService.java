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

package imagej.widget;

import imagej.InstantiableException;
import imagej.log.LogService;
import imagej.plugin.Parameter;
import imagej.plugin.Plugin;
import imagej.plugin.PluginInfo;
import imagej.plugin.PluginService;
import imagej.service.AbstractService;
import imagej.service.Service;

import java.util.List;

/**
 * Default service for managing available {@link InputWidget}s.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultWidgetService extends AbstractService implements
	WidgetService
{

	@Parameter
	private LogService log;

	@Parameter
	private PluginService pluginService;

	// -- WidgetService methods --

	@Override
	public InputWidget<?, ?> createWidget(final WidgetModel model) {
		@SuppressWarnings("rawtypes")
		final List<PluginInfo<InputWidget>> infos =
			pluginService.getPluginsOfType(InputWidget.class);
		for (@SuppressWarnings("rawtypes")
		final PluginInfo<InputWidget> info : infos)
		{
			final InputWidget<?, ?> widget;
			try {
				widget = info.createInstance();
			}
			catch (final InstantiableException e) {
				log.error("Invalid widget: " + info, e);
				continue;
			}
			if (widget.isCompatible(model)) {
				widget.initialize(model);
				return widget;
			}
		}
		log.warn("No widget found for input: " + model.getItem().getName());
		return null;
	}

}
