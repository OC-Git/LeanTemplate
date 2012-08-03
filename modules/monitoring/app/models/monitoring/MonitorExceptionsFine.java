package models.monitoring;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class MonitorExceptionsFine extends Model {

	@Id
	private Long id;
	private Timestamp timestamp;
	private String nodeId;
	private String exceptionType;
	private int exceptionsSum;

	public static final Finder<Long, MonitorExceptionsFine> find = new Finder<Long, MonitorExceptionsFine>(Long.class,
			MonitorExceptionsFine.class);

	public Long getId() {
		return id;
	}

	public void setId(Long _id) {
		id = _id;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String _nodeId) {
		nodeId = _nodeId;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String _exceptionType) {
		exceptionType = _exceptionType;
	}

	public int getExceptionsSum() {
		return exceptionsSum;
	}

	public void setExceptionsSum(int _exceptionsSum) {
		exceptionsSum = _exceptionsSum;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 17;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final MonitorExceptionsFine other = (MonitorExceptionsFine) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "MonitorExceptionsFine [id=" + id + ", timestamp=" + timestamp + ", nodeId=" + nodeId
				+ ", exceptionType=" + exceptionType + ", exceptionsSum=" + exceptionsSum + "]";
	}

}
