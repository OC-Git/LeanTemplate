package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import de.objectcode.play2.plugin.monitoring.infoadapter.ThreadInfoAdapter;


public class DefaultThreadInfoAdapter implements ThreadInfoAdapter {

	private final ThreadMXBean bean;
	
	public DefaultThreadInfoAdapter() {
		bean = ManagementFactory.getThreadMXBean();
	}
	
	@Override
	public long getThreadCount() {
		return bean.getAllThreadIds().length;
	}

}
