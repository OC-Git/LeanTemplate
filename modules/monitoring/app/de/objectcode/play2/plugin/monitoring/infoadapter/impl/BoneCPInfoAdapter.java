package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.lang.reflect.Field;

import play.db.DB;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.Statistics;

import de.objectcode.play2.plugin.monitoring.infoadapter.DbInfoAdapter;

/**
 * BoneCPInfoAdapter. 
 * 
 * BoneCP API is stil somewhat broken in versions < 0.8, see http://jolbox.com/forum/viewtopic.php?f=3&t=310 
 * Therefore we access the {@link BoneCP} object by reflection
 * 
 */
public class BoneCPInfoAdapter implements DbInfoAdapter {

	private Statistics statistics;
	
	public BoneCPInfoAdapter() {
		// get a fresh connection to operate on
		this((BoneCPDataSource) DB.getDataSource());
	}

	public BoneCPInfoAdapter(final BoneCPDataSource ds) {
		statistics = retrieveBoneCP(ds).getStatistics();
	}

	private BoneCP retrieveBoneCP(final BoneCPDataSource ds) {
		Field field;
		try {
			field = BoneCPDataSource.class.getDeclaredField("pool");
			field.setAccessible(true);
			return (BoneCP) field.get(ds);
		} catch (final Exception e) {
			throw new RuntimeException("Could not access private field 'pool' on Datasource due to " + e, e);
		}
	}

	@Override
	public long getCreatedConnectionCount() {
		return statistics.getTotalCreatedConnections();
	}

	@Override
	public long getFreeConnectionCount() {
		return statistics.getTotalFree();
	}

	@Override
	public long getLeasedCounnectionCount() {
		return statistics.getTotalLeased();
	}

}
