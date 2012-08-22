package de.objectcode.play2.plugin.monitoring;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.monitoring.MonitorExceptionsFine;
import models.monitoring.MonitorFine;
import models.monitoring.MonitorResponseTimeFine;


public class EbeanAggregatorPersister implements AggregatorPersister {

	@Override
	public void persist(final MonitorFine mf, final List<MonitorExceptionsFine> efList,
		final List<MonitorResponseTimeFine> responseFineList) {
		
		try {
			Ebean.beginTransaction();
			mf.save();
			for (final MonitorExceptionsFine ef : efList) {
				ef.save();
			}
			for (final MonitorResponseTimeFine f : responseFineList) {
				f.save();
			}
			Ebean.commitTransaction();
		} finally {
			Ebean.endTransaction();
		}		
		
	}

}
