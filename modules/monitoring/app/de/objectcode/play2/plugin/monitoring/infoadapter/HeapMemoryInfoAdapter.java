package de.objectcode.play2.plugin.monitoring.infoadapter;


public interface HeapMemoryInfoAdapter {
	long getMax();
	long getCommitted();
	long getUsed();
	long getFree();
}
