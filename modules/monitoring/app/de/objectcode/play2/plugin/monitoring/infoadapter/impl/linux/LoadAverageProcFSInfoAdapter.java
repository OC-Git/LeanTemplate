package de.objectcode.play2.plugin.monitoring.infoadapter.impl.linux;

import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.AbstractFileLoaderAdapter;

public class LoadAverageProcFSInfoAdapter extends AbstractFileLoaderAdapter implements LoadAverageInfoAdapter {

	public static final String PROC_FILE_LOAD_AVERAGE = "/proc/loadavg";

	private double avg1;
	private double avg5;
	private double avg15;

	@Override
	public double getAvgOneMinute() {
		return avg1;
	}

	@Override
	public double getAvgFiveMinutes() {
		return avg5;
	}

	@Override
	public double getAvgFifteenMinutes() {
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

		avg1 = Double.parseDouble(split[0]);
		avg5 = Double.parseDouble(split[1]);
		avg15 = Double.parseDouble(split[2]);
	}
}
