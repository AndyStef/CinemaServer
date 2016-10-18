package Model;

import java.io.Serializable;
import java.sql.Date;

public class Session implements Serializable {
	public int objectId;
	public int cost;
	public String format;
	public int movieId;
	public int cinemaId;
	public Date date;
	
	public Session(int objectId, int cost, String format, int movieId, int cinemaId, Date date) {
		this.objectId = objectId;
		this.cost = cost;
		this.format = format;
		this.movieId = movieId;
		this.cinemaId = cinemaId;
		this.date = date;
	}
}
