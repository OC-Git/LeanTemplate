package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import play.Logger;


public abstract class AbstractFileLoaderAdapter {

	protected String fileContent; 
	
	protected abstract String getAbsoluteFilePath();
	protected abstract void parse();
	
	public AbstractFileLoaderAdapter() {
		fileContent = readFile();
		parse();
	}
	
	public String readFile() {
		try {
			return FileUtils.readFileToString(new File(getAbsoluteFilePath()));
		}
		catch(final IOException e) {
			Logger.error("Could not read file=" + getAbsoluteFilePath() + " due to " + e, e);
		}
		return null;
	}
	
	public String getFileContent() {
		return fileContent;
	}
	
}
