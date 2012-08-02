package de.objectcode.play2.plugin.monitoring;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import play.Logger;
import de.objectcode.play2.plugin.monitoring.infoadapter.DbInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.GcInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.HeapMemoryInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.NodeInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.SwapInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.ThreadInfoAdapter;
import models.monitoring.MonitorFine;

public class Aggregator implements Runnable {

	private static Aggregator INSTANCE; 
	
	private Object syncSemaphor;
	private ThreadInfoAdapter threadInfoAdapter;
	private SwapInfoAdapter swapInfoAdapter;
	private NodeInfoAdapter nodeInfoAdapter;
	private LoadAverageInfoAdapter loadAverageInfoAdapter;
	private HeapMemoryInfoAdapter heapMemoryInfoAdapter;
	private GcInfoAdapter gcInfoAdapter;
	private DbInfoAdapter dbInfoAdapter;
	
	private long lastGcCollectionCount;
	private long lastGcTime;
	
	private long requestCounter; 
	private long requestDuration;
	private AtomicInteger exceptionCounter;

	protected Aggregator(ThreadInfoAdapter _threadInfoAdapter, SwapInfoAdapter _swapInfoAdapter,
			NodeInfoAdapter _nodeInfoAdapter, LoadAverageInfoAdapter _loadAverageInfoAdapter,
			HeapMemoryInfoAdapter _heapMemoryInfoAdapter, GcInfoAdapter _gcInfoAdapter, DbInfoAdapter _dbInfoAdapter) {

		threadInfoAdapter = _threadInfoAdapter;
		swapInfoAdapter = _swapInfoAdapter;
		nodeInfoAdapter = _nodeInfoAdapter;
		loadAverageInfoAdapter = _loadAverageInfoAdapter;
		heapMemoryInfoAdapter = _heapMemoryInfoAdapter;
		gcInfoAdapter = _gcInfoAdapter;
		dbInfoAdapter = _dbInfoAdapter;
		
		exceptionCounter = new AtomicInteger();
		syncSemaphor = new Object();
	}
	
	protected static void set(final Aggregator instance) {
		INSTANCE = instance;
	}
	
	public static Aggregator get() {
		return INSTANCE;
	}
	
	public Aggregator incrementRequestCounter(final long duration) {
		//FIXME: synchronize sucks ! 
		synchronized (syncSemaphor) {
			requestCounter++;
			requestDuration += duration;
		}
		return this;
	}
	
	public Aggregator incrementExceptionCounter() {
		exceptionCounter.getAndIncrement();
		return this;
	}
	
	@Override
	public void run() {
		final String nodeId = nodeInfoAdapter.getNodeId();

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
		
		mf.setLoadAvg(loadAverageInfoAdapter.getAvgOneMinute());

		mf.setSwapUsed(swapInfoAdapter.getUsedSwapBytes());
		mf.setThreadCount(threadInfoAdapter.getThreadCount());
		mf.setExceptionsSum(exceptionCounter.getAndSet(0));

		synchronized (syncSemaphor) {
			mf.setRequestCount(requestCounter);
			if (requestCounter != 0) mf.setResponseTimeAvg((int) (requestDuration / requestCounter));
			requestCounter = 0; 
			requestDuration = 0;
		}
		
		if (Logger.isDebugEnabled()) {
			Logger.debug("About to save MonitorFine=" + mf);
		}
		mf.save();
	}

	@Override
	public String toString() {
		return "Aggregator [threadInfoAdapter=" + threadInfoAdapter + ", swapInfoAdapter=" + swapInfoAdapter
				+ ", nodeInfoAdapter=" + nodeInfoAdapter + ", loadAverageInfoAdapter=" + loadAverageInfoAdapter
				+ ", heapMemoryInfoAdapter=" + heapMemoryInfoAdapter + ", gcInfoAdapter=" + gcInfoAdapter
				+ ", dbInfoAdapter=" + dbInfoAdapter + ", lastGcCollectionCount=" + lastGcCollectionCount
				+ ", lastGcTime=" + lastGcTime + ", requestCounter=" + requestCounter + ", requestDuration="
				+ requestDuration + ", exceptionCounter=" + exceptionCounter + "]";
	}

}
