package Model;

import java.io.Serializable;
import java.util.List;

public class Responce implements Serializable {
	public List<Cinema> cinemaArray;
	public List<Movie> movieArray;
	public List<Session> sessionArray;
	public String statusString; 
	public ResponceType responceType;
	public ObjectType objectType;
	
	public Responce(ResponceType responceType, ObjectType objectType){
		this.responceType = responceType;
		this.objectType = objectType;
	}
}
