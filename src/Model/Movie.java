package Model;

import java.io.Serializable;

public class Movie implements Serializable {
	public int objectId;
	public String name;
	public String genre;
	public int duration;
	public String producer;
	public boolean isCurrentlyShown;
	//public bit isCurrentlyShown;
	
	public Movie(int objectId, String name, String genre, int duration, String producer, boolean isCurrentlyShown) {
		this.objectId = objectId;
		this.name = name;
		this.genre = genre;
		this.duration = duration;
		this.producer = producer;
		this.isCurrentlyShown = isCurrentlyShown;
	}
}
