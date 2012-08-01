package de.objectcode.play2.plugin.monitoring.infoadapter;


public interface LoadAverageInfoAdapter {
	public double getAvgOneMinute();
	public double getAvgFiveMinutes();
	public double getAvgFifteenMinutes();
}
