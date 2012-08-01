package de.objectcode.play2.plugin.monitoring.infoadapter;


public interface JvmCpuUsageInfoAdapter {
	Long getUserCpuTime();
	Long getSystemCpuTime();
	Long getCpuTime();
}
