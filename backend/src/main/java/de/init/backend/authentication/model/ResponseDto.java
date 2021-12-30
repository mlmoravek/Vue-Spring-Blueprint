package de.init.backend.authentication.model;

import java.io.Serializable;
import java.util.Date;

public class ResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date timestamp;
	private int status;

	public ResponseDto(int status) {
		this.timestamp = new Date();
		this.status = status;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
