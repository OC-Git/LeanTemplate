package models.monitoring;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class MonitorResponseTimeFine extends Model {

	@Id
	private Long id;
	private Timestamp timestamp;
	private String nodeId;
	private String requestMethod;
	private long responseTime;
	private long requestCount;

	public static final Finder<Long, MonitorResponseTimeFine> find = new Finder<Long, MonitorResponseTimeFine>(
			Long.class, MonitorResponseTimeFine.class);

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

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String _requestMethod) {
		requestMethod = _requestMethod;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long _responseTime) {
		responseTime = _responseTime;
	}
	
	public void setRequestCount(long requestCount) {
		this.requestCount = requestCount;
	}

	public long getRequestCount() {
		return requestCount;
	}	

	@Override
	public int hashCode() {
		final int prime = 23;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final MonitorResponseTimeFine other = (MonitorResponseTimeFine) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "MonitorResponseTimeFine [id=" + id + ", timestamp=" + timestamp + ", nodeId=" + nodeId
				+ ", requestMethod=" + requestMethod + ", responseTime=" + responseTime + "]";
	}



}
