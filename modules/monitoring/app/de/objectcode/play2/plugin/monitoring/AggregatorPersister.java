package de.objectcode.play2.plugin.monitoring;

import java.util.List;

import models.monitoring.MonitorExceptionsFine;
import models.monitoring.MonitorFine;
import models.monitoring.MonitorResponseTimeFine;

public interface AggregatorPersister {

	public void persist(final MonitorFine mf, final List<MonitorExceptionsFine> efList,
		final List<MonitorResponseTimeFine> responseFineList);
}
