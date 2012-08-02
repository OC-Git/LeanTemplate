package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import de.objectcode.play2.plugin.monitoring.infoadapter.HeapMemoryInfoAdapter;

public class DefaultHeapMemoryInfoAdapter implements HeapMemoryInfoAdapter {

	final MemoryUsage mu;

	public DefaultHeapMemoryInfoAdapter() {
		mu = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
	}

	@Override
	public long getMax() {
		return mu.getMax();
	}

	@Override
	public long getCommitted() {
		return mu.getCommitted();
	}

	@Override
	public long getUsed() {
		return mu.getUsed();
	}

	@Override
	public long getFree() {
		return getCommitted() - getUsed();
	}

}
