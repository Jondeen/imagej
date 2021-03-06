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

package imagej.io.plugins;

import imagej.command.ContextCommand;
import imagej.data.Dataset;
import imagej.io.IOService;
import imagej.log.LogService;
import imagej.menu.MenuConstants;
import imagej.module.ItemIO;
import imagej.plugin.Menu;
import imagej.plugin.Parameter;
import imagej.plugin.Plugin;
import imagej.ui.DialogPrompt;
import imagej.ui.UIService;

import java.io.File;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.io.ImgIOException;

/**
 * Opens the selected file as a {@link Dataset}.
 * 
 * @author Curtis Rueden
 * @author Mark Hiner
 */
@Plugin(iconPath = "/icons/plugins/folder_picture.png", menu = {
	@Menu(label = MenuConstants.FILE_LABEL, weight = MenuConstants.FILE_WEIGHT,
		mnemonic = MenuConstants.FILE_MNEMONIC),
	@Menu(label = "Open...", weight = 1, mnemonic = 'o',
		accelerator = "control O") })
public class OpenImage extends ContextCommand {

	@Parameter
	private LogService log;

	@Parameter
	private IOService ioService;

	@Parameter
	private UIService uiService;

	@Parameter(label = "File to open")
	private File inputFile;

	@Parameter(type = ItemIO.OUTPUT)
	private Dataset dataset;

	@Override
	public void run() {
		final String source = inputFile.getAbsolutePath();
		try {
			dataset = ioService.loadDataset(source);
		}
		catch (final ImgIOException e) {
			log.error(e);
			uiService.showDialog(e.getMessage(),
				DialogPrompt.MessageType.ERROR_MESSAGE);
		}
		catch (final IncompatibleTypeException e) {
			log.error(e);
			uiService.showDialog(e.getMessage(),
				DialogPrompt.MessageType.ERROR_MESSAGE);
		}
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(final File inputFile) {
		this.inputFile = inputFile;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(final Dataset dataset) {
		this.dataset = dataset;
	}

}
