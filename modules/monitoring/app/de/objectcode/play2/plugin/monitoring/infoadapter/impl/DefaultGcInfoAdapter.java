package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import de.objectcode.play2.plugin.monitoring.infoadapter.GcInfoAdapter;


public class DefaultGcInfoAdapter implements GcInfoAdapter{

	final List<GarbageCollectorMXBean> garbageCollectorMXBeans; 
	
	public DefaultGcInfoAdapter() {
		this.garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
	}
	
	@Override
	public long getCollectionCount() {
		long sum = 0; 
		for (final GarbageCollectorMXBean b : garbageCollectorMXBeans) {
			sum += b.getCollectionCount();
		}
		return sum;
	}

	@Override
	public long getCollectionTimeMillis() {
		long sum = 0; 
		for (final GarbageCollectorMXBean b : garbageCollectorMXBeans) {
			sum += b.getCollectionTime();
		}
		return sum;
	}

}
