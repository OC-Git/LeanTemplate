package de.objectcode.play2.plugin.monitoring.infoadapter;

public interface DbInfoAdapter {
	public int getCreatedConnectionCount();
	public int getFreeConnectionCount();
	public int getLeasedCounnectionCount();
}
