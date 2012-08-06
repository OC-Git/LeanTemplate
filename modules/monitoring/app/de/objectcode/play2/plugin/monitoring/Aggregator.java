package de.objectcode.play2.plugin.monitoring;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import models.monitoring.MonitorExceptionsFine;
import models.monitoring.MonitorFine;
import models.monitoring.MonitorResponseTimeFine;

import com.avaje.ebean.Ebean;

import de.objectcode.play2.plugin.monitoring.infoadapter.DbInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.GcInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.HeapMemoryInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.NodeInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.SwapInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.ThreadInfoAdapter;

public class Aggregator implements Runnable {

	private static Aggregator INSTANCE;

	private Object syncMutexRequestCounter;
	private Object syncMutexExceptionFineCounter;

	private ThreadInfoAdapter threadInfoAdapter;
	private SwapInfoAdapter swapInfoAdapter;
	private NodeInfoAdapter nodeInfoAdapter;
	private LoadAverageAggregator loadAverageAggregator;
	private HeapMemoryInfoAdapter heapMemoryInfoAdapter;
	private GcInfoAdapter gcInfoAdapter;
	private DbInfoAdapter dbInfoAdapter;

	private long lastGcCollectionCount;
	private long lastGcTime;

	private long requestCounter;
	private long requestDuration;
	private int exceptionCounter;

	private Map<Class<? extends Throwable>, Integer> exceptionTypeCounterMap;
	private Map<String, Tupel<Long, Integer>> fineRequestDurationMap;
	
	private static class Tupel<V1, V2> {
		public V1 value1; 
		public V2 value2; 
	}
	
	protected Aggregator(ThreadInfoAdapter _threadInfoAdapter, SwapInfoAdapter _swapInfoAdapter,
			NodeInfoAdapter _nodeInfoAdapter, LoadAverageAggregator _loadAverageAggregator,
			HeapMemoryInfoAdapter _heapMemoryInfoAdapter, GcInfoAdapter _gcInfoAdapter, DbInfoAdapter _dbInfoAdapter) {

		threadInfoAdapter = _threadInfoAdapter;
		swapInfoAdapter = _swapInfoAdapter;
		nodeInfoAdapter = _nodeInfoAdapter;
		loadAverageAggregator = _loadAverageAggregator;
		heapMemoryInfoAdapter = _heapMemoryInfoAdapter;
		gcInfoAdapter = _gcInfoAdapter;
		dbInfoAdapter = _dbInfoAdapter;

		syncMutexRequestCounter = new Object();
		syncMutexExceptionFineCounter = new Object();
		
		exceptionTypeCounterMap = new HashMap<Class<? extends Throwable>, Integer>(64);
		fineRequestDurationMap = new HashMap<String, Aggregator.Tupel<Long,Integer>>(64);
	}

	protected static void set(final Aggregator instance) {
		INSTANCE = instance;
	}

	public static Aggregator get() {
		return INSTANCE;
	}

	public Aggregator incrementRequestCounter(final long duration, final String controllerMethod) {
		// FIXME: synchronize sucks !
		synchronized (syncMutexRequestCounter) {
			requestCounter++;
			requestDuration += duration;
			
			// fine logging
			if (controllerMethod != null) {
				Tupel<Long, Integer> durationAndCountTupel = fineRequestDurationMap.get(controllerMethod);
				
				if (durationAndCountTupel == null) {
					durationAndCountTupel = new Tupel<Long, Integer>();
					fineRequestDurationMap.put(controllerMethod, durationAndCountTupel);
					
					durationAndCountTupel.value1 = duration; 
					durationAndCountTupel.value2 = 1;
				}
				else {
					durationAndCountTupel.value1 = durationAndCountTupel.value1 + duration;
					durationAndCountTupel.value2 = durationAndCountTupel.value2 + 1;
				}
			}
		}
		return this;
	}

	public Aggregator incrementExceptionCounter(final Throwable e) {
		final Class<? extends Throwable> exceptionClass = e.getClass();

		synchronized (syncMutexExceptionFineCounter) {
			exceptionCounter++;
			Integer i = exceptionTypeCounterMap.get(exceptionClass);
			if (i == null) {
				exceptionTypeCounterMap.put(exceptionClass, new Integer(1));
			} else {
				exceptionTypeCounterMap.put(exceptionClass, new Integer(i.intValue() + 1));
			}
		}
		return this;
	}

	@Override
	public void run() {
		final String nodeId = nodeInfoAdapter.getNodeId();
		final Timestamp now = new Timestamp(System.currentTimeMillis());		

		final MonitorFine mf = new MonitorFine();
		mf.setNodeId(nodeId);

		mf.setDbConnectionsOpen(dbInfoAdapter.getCreatedConnectionCount());
		mf.setDbConnectionsLeased(dbInfoAdapter.getLeasedCounnectionCount());

		final long gcCount = gcInfoAdapter.getCollectionCount();
		mf.setGcCount(gcCount - lastGcCollectionCount);
		lastGcCollectionCount = gcCount;

		final long gcTime = gcInfoAdapter.getCollectionTimeMillis();
		mf.setGcTimeAvg(gcTime - lastGcTime);
		lastGcTime = gcTime;

		mf.setHeapFree(heapMemoryInfoAdapter.getFree());
		mf.setHeapMax(heapMemoryInfoAdapter.getMax());
		mf.setHeapUsed(heapMemoryInfoAdapter.getUsed());
		mf.setLoadAvg(loadAverageAggregator.getAndResetAverage());
		mf.setSwapUsed(swapInfoAdapter.getUsedSwapBytes());
		mf.setThreadCount(threadInfoAdapter.getThreadCount());

		final List<MonitorResponseTimeFine> responseFineList = new ArrayList<MonitorResponseTimeFine>(
				fineRequestDurationMap.size());
		
		synchronized (syncMutexRequestCounter) {
			mf.setRequestCount(requestCounter);
			
			if (requestCounter != 0) mf.setResponseTimeAvg((int) (requestDuration / requestCounter));
			requestCounter = 0;
			requestDuration = 0;
			
			for (final String methodName : fineRequestDurationMap.keySet()) {
				final Tupel<Long, Integer> tupel = fineRequestDurationMap.get(methodName);
				if (tupel.value2 == 0) continue;
				
				final MonitorResponseTimeFine f = new MonitorResponseTimeFine();
				f.setTimestamp(now);
				f.setNodeId(nodeId);
				f.setRequestMethod(methodName);
				f.setResponseTime(tupel.value1 / tupel.value2);
				
				responseFineList.add(f);
			}
			fineRequestDurationMap.clear();
			
		}

		final List<MonitorExceptionsFine> efList = new ArrayList<MonitorExceptionsFine>(exceptionTypeCounterMap.size());

		synchronized (syncMutexExceptionFineCounter) {
			mf.setExceptionsSum(exceptionCounter);
			
			for (final Class<? extends Throwable> clazz : exceptionTypeCounterMap.keySet()) {
				final Integer exceptionCount = exceptionTypeCounterMap.get(clazz);
				final MonitorExceptionsFine ef = new MonitorExceptionsFine();
				ef.setTimestamp(now);
				ef.setExceptionsSum(exceptionCount);
				ef.setExceptionType(clazz.getName());
				ef.setNodeId(nodeId);
				efList.add(ef);
			}
			exceptionTypeCounterMap.clear();
		}

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

	@Override
	public String toString() {
		return "Aggregator [threadInfoAdapter=" + threadInfoAdapter + ", swapInfoAdapter=" + swapInfoAdapter
				+ ", nodeInfoAdapter=" + nodeInfoAdapter + ", loadAverageAggregator=" + loadAverageAggregator
				+ ", heapMemoryInfoAdapter=" + heapMemoryInfoAdapter + ", gcInfoAdapter=" + gcInfoAdapter
				+ ", dbInfoAdapter=" + dbInfoAdapter + ", lastGcCollectionCount=" + lastGcCollectionCount
				+ ", lastGcTime=" + lastGcTime + ", requestCounter=" + requestCounter + ", requestDuration="
				+ requestDuration + ", exceptionCounter=" + exceptionCounter + "]";
	}

}
