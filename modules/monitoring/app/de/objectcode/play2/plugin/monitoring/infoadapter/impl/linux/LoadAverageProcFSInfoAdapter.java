package de.objectcode.play2.plugin.monitoring.infoadapter.impl.linux;

import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.AbstractFileLoaderAdapter;


public class LoadAverageProcFSInfoAdapter extends AbstractFileLoaderAdapter implements LoadAverageInfoAdapter {

	public static final String PROC_FILE_LOAD_AVERAGE = "/proc/loadavg";	

	private float avg1;
	private float avg5;
	private float avg15;
	
	@Override
	public float getAvgOneMinute() {
		return avg1;
	}

	@Override
	public float getAvgFiveMinutes() {
		return avg5;
	}

	@Override
	public float getAvgfifteenMinutes() {
		return avg15;
	}

	@Override
	protected String getAbsoluteFilePath() {
		return PROC_FILE_LOAD_AVERAGE;
	}

	@Override
	protected void parse() {
		if (getFileContent() == null) return;
		
		final String[] split = getFileContent().split("\\s+");
		if (split.length < 3) return;
		
		avg1 = Float.parseFloat(split[0]);
		avg5 = Float.parseFloat(split[1]);
		avg15 = Float.parseFloat(split[2]);
	}
}
