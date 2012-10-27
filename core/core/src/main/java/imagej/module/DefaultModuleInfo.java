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

package imagej.module;

import imagej.AbstractUIDetails;
import imagej.ValidityProblem;
import imagej.event.EventService;
import imagej.module.event.ModulesUpdatedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link ModuleInfo} implementation.
 * <p>
 * By default, {@link ModuleItem}s are stored in {@link HashMap}s and
 * {@link ArrayList}s, internally. The {@link Module} {@link Class} given in the
 * {@link #setModuleClass(Class)} method is given as the delegate class name for
 * {@link #getDelegateClassName()}, and instantiated using a constructor that
 * takes a single {@link ModuleInfo} parameter.
 * </p>
 * By default, {@link ModuleItem}s are stored in {@link HashMap}s and
 * {@link ArrayList}s, internally. </p>
 * <p>
 * It is important for downstream code to call the
 * {@link #setModuleClass(Class)} method to associate the module info with its
 * module class prior to using the module info for anything; the
 * {@link #getDelegateClassName()} and {@link #createModule()} methods will fail
 * if the module class has not been set.
 * </p>
 * 
 * @author Curtis Rueden
 */
public class DefaultModuleInfo extends AbstractUIDetails implements ModuleInfo
{

	/** Table of items, keyed on name. */
	private final Map<String, ModuleItem<?>> itemMap =
		new HashMap<String, ModuleItem<?>>();

	/** Ordered list of items. */
	private final List<ModuleItem<?>> items = new ArrayList<ModuleItem<?>>();

	/** Ordered list of inputs. This list is a subset of {@link #items}. */
	private final List<ModuleItem<?>> inputs = new ArrayList<ModuleItem<?>>();

	/** Ordered list of outputs. This list is a subset of {@link #items}. */
	private final List<ModuleItem<?>> outputs = new ArrayList<ModuleItem<?>>();

	private Class<? extends Module> moduleClass;

	// -- DefaultModuleInfo methods --

	/** Sets the module class described by this {@link ModuleInfo}. */
	public void setModuleClass(final Class<? extends Module> moduleClass) {
		this.moduleClass = moduleClass;
	}

	/** Gets the module class described by this {@link ModuleInfo}. */
	public Class<? extends Module> getModuleClass() {
		return moduleClass;
	}

	/** Adds an item to the list. */
	public void addItem(final ModuleItem<?> item) {
		itemMap.put(item.getName(), item);
		items.add(item);
		if (item.isInput()) inputs.add(item);
		if (item.isOutput()) outputs.add(item);
	}

	/** Removes an item from the list. */
	public void removeItem(final ModuleItem<?> item) {
		itemMap.remove(item.getName());
		items.remove(item);
		inputs.remove(item);
		outputs.remove(item);
	}

	// -- ModuleInfo methods --

	@Override
	public ModuleItem<?> getItem(final String name) {
		return itemMap.get(name);
	}

	@Override
	public Iterable<ModuleItem<?>> items() {
		return Collections.unmodifiableList(items);
	}

	@Override
	public Iterable<ModuleItem<?>> inputs() {
		return Collections.unmodifiableList(inputs);
	}

	@Override
	public Iterable<ModuleItem<?>> outputs() {
		return Collections.unmodifiableList(outputs);
	}

	@Override
	public String getDelegateClassName() {
		return moduleClass.getName();
	}

	@Override
	public Module createModule() throws ModuleException {
		try {
			return moduleClass.newInstance();
		}
		catch (final Exception e) {
			// NB: Several types of exceptions; simpler to handle them all the same.
			throw new ModuleException(e);
		}
	}

	@Override
	public boolean canPreview() {
		return false;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public boolean canRunHeadless() {
		return false;
	}

	@Override
	public String getInitializer() {
		return null;
	}

	@Override
	public void update(final EventService eventService) {
		eventService.publish(new ModulesUpdatedEvent(this));
	}

	// -- UIDetails methods --

	@Override
	public String getTitle() {
		final String title = super.getTitle();
		if (!title.equals(getClass().getSimpleName())) return title;

		// use delegate class name rather than actual class name
		final String className = getDelegateClassName();
		final int dot = className.lastIndexOf(".");
		return dot < 0 ? className : className.substring(dot + 1);
	}

	// -- Validated methods --

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public List<ValidityProblem> getProblems() {
		return null;
	}

}
