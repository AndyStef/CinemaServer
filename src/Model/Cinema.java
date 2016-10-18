package Model;

import java.io.Serializable;

public class Cinema implements Serializable {
	public int objectId;
	public String name;
	public String address;
	public int hallNumber;
	
	public Cinema(int objectId, String name, String address, int hallNumber) {
		this.objectId = objectId;
		this.name = name;
		this.address = address;
		this.hallNumber = hallNumber;
	}
}
