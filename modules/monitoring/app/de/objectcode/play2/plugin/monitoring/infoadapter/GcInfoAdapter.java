package de.objectcode.play2.plugin.monitoring.infoadapter;


public interface GcInfoAdapter {
	long getCollectionCount();
	long getCollectionTimeMillis();
}
