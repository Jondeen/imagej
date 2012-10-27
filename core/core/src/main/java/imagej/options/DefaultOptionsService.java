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

package imagej.options;

import imagej.command.CommandInfo;
import imagej.command.CommandService;
import imagej.event.EventService;
import imagej.log.LogService;
import imagej.plugin.InitPreprocessor;
import imagej.plugin.Parameter;
import imagej.plugin.Plugin;
import imagej.plugin.PluginInfo;
import imagej.plugin.PluginService;
import imagej.plugin.PreprocessorPlugin;
import imagej.plugin.ValidityPreprocessor;
import imagej.service.AbstractService;
import imagej.service.Service;
import imagej.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default service for keeping track of the available options and their
 * settings.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 * @see OptionsPlugin
 */
@Plugin(type = Service.class)
public class DefaultOptionsService extends AbstractService implements
	OptionsService
{

	@Parameter
	private LogService log;

	@Parameter
	private EventService eventService;

	@Parameter
	private PluginService pluginService;

	@Parameter
	private CommandService commandService;

	// -- OptionsService methods --

	@Override
	public EventService getEventService() {
		return eventService;
	}

	@Override
	public PluginService getPluginService() {
		return pluginService;
	}

	@Override
	public List<OptionsPlugin> getOptions() {
		// get the list of available options plugins
		final List<PluginInfo<OptionsPlugin>> infos =
			pluginService.getPluginsOfType(OptionsPlugin.class);

		// instantiate one instance of each options plugin
		final ArrayList<OptionsPlugin> optionsPlugins =
			new ArrayList<OptionsPlugin>();
		for (final PluginInfo<? extends OptionsPlugin> info : infos) {
			optionsPlugins.add(createInstance(info));
		}

		return optionsPlugins;
	}

	@Override
	public <O extends OptionsPlugin> O getOptions(final Class<O> optionsClass) {
		return createInstance(getOptionsInfo(optionsClass));
	}

	@Override
	public OptionsPlugin getOptions(final String className) {
		return createInstance(getOptionsInfo(className));
	}

	@Override
	public <O extends OptionsPlugin> Object getOption(
		final Class<O> optionsClass, final String name)
	{
		return getInput(getOptionsInfo(optionsClass), name);
	}

	@Override
	public Object getOption(final String className, final String name) {
		return getInput(getOptionsInfo(className), name);
	}

	@Override
	public <O extends OptionsPlugin> Map<String, Object> getOptionsMap(
		final Class<O> optionsClass)
	{
		return getInputs(getOptionsInfo(optionsClass));
	}

	@Override
	public Map<String, Object> getOptionsMap(final String className) {
		return getInputs(getOptionsInfo(className));
	}

	@Override
	public <O extends OptionsPlugin> void setOption(final Class<O> optionsClass,
		final String name, final Object value)
	{
		final CommandInfo<O> info = getOptionsInfo(optionsClass);
		if (info == null) return;
		setOption(info, name, value);
	}

	@Override
	public void setOption(final String className, final String name,
		final Object value)
	{
		final CommandInfo<OptionsPlugin> info = getOptionsInfo(className);
		if (info == null) return;
		setOption(info, name, value);
	}

	@Override
	public <O extends OptionsPlugin> void setOption(final CommandInfo<O> info,
		final String name, final Object value)
	{
		final O optionsPlugin = createInstance(info);
		if (optionsPlugin == null) return; // cannot set option

		// assign value with correct type
		final Class<?> type = info.getItem(name).getType();
		final Object typedValue = ClassUtils.convert(value, type);
		optionsPlugin.put(name, typedValue);

		// persist the option value, and publish an OptionsEvent
		optionsPlugin.run();
	}

	// -- Helper methods --

	/**
	 * Creates an instance of the {@link OptionsPlugin} described by the given
	 * {@link PluginInfo}, preprocessing it with available preprocessors.
	 */
	private <O extends OptionsPlugin> O createInstance(final PluginInfo<O> info)
	{
		if (info == null) return null;

		// instantiate the options plugin
		final O optionsPlugin = pluginService.createInstance(info);
		if (optionsPlugin == null) return null;

		// execute key preprocessors on the newly created options plugin
		final ArrayList<PluginInfo<? extends PreprocessorPlugin>> preInfos =
			new ArrayList<PluginInfo<? extends PreprocessorPlugin>>();
		preInfos.add(pluginService.getPlugin(ValidityPreprocessor.class));
		preInfos.add(pluginService.getPlugin(InitPreprocessor.class));
		final List<? extends PreprocessorPlugin> pre =
			pluginService.createInstances(preInfos);
		for (final PreprocessorPlugin pp : pre) {
			pp.process(optionsPlugin);
		}

		return optionsPlugin;
	}

	private <O extends OptionsPlugin> CommandInfo<O> getOptionsInfo(
		final Class<O> optionsClass)
	{
		final CommandInfo<O> info = commandService.getCommand(optionsClass);
		if (info == null) {
			log.error("No such options class: " + optionsClass.getName());
		}
		return info;
	}

	private CommandInfo<OptionsPlugin> getOptionsInfo(final String className) {
		final CommandInfo<?> info = commandService.getCommand(className);
		if (info == null) {
			log.error("No such options class: " + className);
			return null;
		}
		if (!OptionsPlugin.class.isAssignableFrom(info.getPluginType())) {
			log.error("Not an options plugin: " + className);
			// not an options plugin
			return null;
		}
		@SuppressWarnings("unchecked")
		final CommandInfo<OptionsPlugin> typedInfo =
			(CommandInfo<OptionsPlugin>) info;
		return typedInfo;
	}

	private <O extends OptionsPlugin> Object getInput(final CommandInfo<O> info,
		final String name)
	{
		if (info == null) return null;
		final O optionsPlugin = createInstance(info);
		if (optionsPlugin == null) return null;
		return optionsPlugin.get(name);
	}

	private <O extends OptionsPlugin> Map<String, Object> getInputs(
		final CommandInfo<O> info)
	{
		if (info == null) return null;
		final O optionsPlugin = createInstance(info);
		if (optionsPlugin == null) return null;
		return optionsPlugin.getInputs();
	}

}
