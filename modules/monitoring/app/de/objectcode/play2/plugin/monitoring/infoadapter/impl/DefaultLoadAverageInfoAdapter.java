package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.lang.management.ManagementFactory;

import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;


public class DefaultLoadAverageInfoAdapter implements LoadAverageInfoAdapter {
	
	public static final double UNDEFINED = Double.MIN_VALUE; 
	
	@Override
	public double getAvgOneMinute() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

	@Override
	public double getAvgFiveMinutes() {
		return UNDEFINED;
	}

	@Override
	public double getAvgFifteenMinutes() {
		return UNDEFINED;
	}

}
