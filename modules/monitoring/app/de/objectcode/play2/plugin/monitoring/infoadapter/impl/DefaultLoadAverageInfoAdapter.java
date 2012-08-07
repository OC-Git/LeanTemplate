package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.lang.management.ManagementFactory;

import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;


public class DefaultLoadAverageInfoAdapter implements LoadAverageInfoAdapter {
	
	@Override
	public double getAvgOneMinute() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

	@Override
	public double getAvgFiveMinutes() {
		throw new RuntimeException("getAvgFiveMinutes() is unimplemented");
	}

	@Override
	public double getAvgFifteenMinutes() {
		throw new RuntimeException("getAvgFifteenMinutes() is unimplemented");
	}

}
