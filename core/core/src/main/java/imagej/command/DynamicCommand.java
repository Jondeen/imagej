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

package imagej.command;

import imagej.Contextual;
import imagej.ImageJ;
import imagej.module.DefaultModule;
import imagej.plugin.Parameter;
import imagej.util.ClassUtils;

import java.lang.reflect.Field;

/**
 * A class which can be extended to provide an ImageJ command with a variable
 * number of inputs and outputs. This class provides greater configurability,
 * but also greater complexity, than implementing the {@link Command} interface
 * and using only @{@link Parameter} annotations on instance fields.
 * 
 * @author Curtis Rueden
 */
public abstract class DynamicCommand extends DefaultModule implements Command,
	Contextual
{

	private ImageJ context;
	private DynamicCommandInfo info;

	// -- Module methods --

	@Override
	public DynamicCommandInfo getInfo() {
		return info;
	}

	// -- Map methods --

	@Override
	public Object get(final Object name) {
		final Field field = info.getItemField(name.toString());
		if (field == null) return super.get(name);
		return ClassUtils.getValue(field, this);
	}

	@Override
	public Object put(final String name, final Object value) {
		final Field field = info.getItemField(name);
		if (field == null) return super.put(name, value);
		final Object previous = ClassUtils.getValue(field, this);
		ClassUtils.setValue(field, this, value);
		return previous;
	}

	// -- Contextual methods --

	@Override
	public ImageJ getContext() {
		return context;
	}

	@Override
	public void setContext(final ImageJ context) {
		if (this.context != null) {
			throw new IllegalStateException("Context already set");
		}
		this.context = context;

		// populate service parameters
		final CommandService commandService =
			context.getService(CommandService.class);
		if (commandService == null) {
			throw new IllegalArgumentException("Context has no command service");
		}
		final CommandInfo<DynamicCommand> commandInfo =
			commandService.populateServices(this);

		info = new DynamicCommandInfo(commandInfo, getClass());
	}

}
