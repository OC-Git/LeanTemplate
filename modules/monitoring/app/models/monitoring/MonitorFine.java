package models.monitoring;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
@Table(name = "monitor_fine")
public class MonitorFine extends Model {

	@Id
	private Long id;
	private Timestamp timestamp;
	private String nodeId;
	private long requestCount;
	private int responseTimeAvg;
	private int exceptionsSum;
	private int dbConnectionsOpen;
	private int dbConnectionsLeased;
	private long heapUsed;
	private long heapMax;
	private long heapFree;
	private long swapUsed;
	private long gcCount;
	private long gcTimeAvg;
	private double loadAvg;
	private long threadCount;

	public static final Finder<Long, MonitorFine> find = new Finder<Long, MonitorFine>(Long.class, MonitorFine.class);

	public MonitorFine() {
		timestamp = new Timestamp(System.currentTimeMillis());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long _id) {
		id = _id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp _timestamp) {
		timestamp = _timestamp;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String _nodeId) {
		nodeId = _nodeId;
	}

	public long getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(long _requestCount) {
		requestCount = _requestCount;
	}

	public int getResponseTimeAvg() {
		return responseTimeAvg;
	}

	public void setResponseTimeAvg(int _responseTimeAvg) {
		responseTimeAvg = _responseTimeAvg;
	}

	public int getExceptionsSum() {
		return exceptionsSum;
	}

	public void setExceptionsSum(int _exceptionsSum) {
		exceptionsSum = _exceptionsSum;
	}

	public int getDbConnectionsOpen() {
		return dbConnectionsOpen;
	}

	public void setDbConnectionsOpen(int _dbConnections) {
		dbConnectionsOpen = _dbConnections;
	}

	public long getHeapUsed() {
		return heapUsed;
	}

	public void setHeapUsed(long _heapUsed) {
		heapUsed = _heapUsed;
	}

	public long getHeapMax() {
		return heapMax;
	}

	public void setHeapMax(long _heapMax) {
		heapMax = _heapMax;
	}

	public long getHeapFree() {
		return heapFree;
	}

	public void setHeapFree(long _heapFree) {
		heapFree = _heapFree;
	}

	public long getSwapUsed() {
		return swapUsed;
	}

	public void setSwapUsed(long _swapUsed) {
		swapUsed = _swapUsed;
	}

	public long getGcCount() {
		return gcCount;
	}

	public void setGcCount(long _gcCount) {
		gcCount = _gcCount;
	}

	public long getGcTimeAvg() {
		return gcTimeAvg;
	}

	public void setGcTimeAvg(long _gcTimeAvg) {
		gcTimeAvg = _gcTimeAvg;
	}

	public double getLoadAvg() {
		return loadAvg;
	}

	public void setLoadAvg(double _loadAvg) {
		loadAvg = _loadAvg;
	}

	public long getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(long _threadCount) {
		threadCount = _threadCount;
	}
	
	public void setDbConnectionsLeased(int dbConnectionsLeased) {
		this.dbConnectionsLeased = dbConnectionsLeased;
	}

	public int getDbConnectionsLeased() {
		return dbConnectionsLeased;
	}

	@Override
	public int hashCode() {
		final int prime = 7;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final MonitorFine other = (MonitorFine) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "MonitorFine [id=" + id + ", timestamp=" + timestamp + ", nodeId=" + nodeId + ", requestCount="
				+ requestCount + ", responseTimeAvg=" + responseTimeAvg + ", exceptionsSum=" + exceptionsSum
				+ ", dbConnectionsOpen=" + dbConnectionsOpen + ", dbConnectionsLeased=" + dbConnectionsLeased
				+ ", heapUsed=" + heapUsed + ", heapMax=" + heapMax + ", heapFree=" + heapFree + ", swapUsed="
				+ swapUsed + ", gcCount=" + gcCount + ", gcTimeAvg=" + gcTimeAvg + ", loadAvg=" + loadAvg
				+ ", threadCount=" + threadCount + "]";
	}
	
}
