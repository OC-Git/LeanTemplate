package de.objectcode.play2.plugin.monitoring.infoadapter;

public interface DbInfoAdapter {
	public long getCreatedConnectionCount();
	public long getFreeConnectionCount();
	public long getLeasedCounnectionCount();
}
