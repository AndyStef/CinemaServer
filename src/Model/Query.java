package Model;

import java.io.Serializable;

public class Query implements Serializable {
	public QueryType type;
	public ObjectType objectType;
	public Cinema cinema;
	public Movie movie;
	public Session session;
	public String key;
	public String comparisonValue;
	public ComparisonType comparisonType;

	public Query(QueryType type, ObjectType objectType) {
		this.type = type;	
		this.objectType = objectType;
	}
	
	public static String whereKey(String key,String comparisonValue, ComparisonType type) {
		String sql = "";
		sql = "WHERE " + key + " ";
		switch (type) {
		case equals:
			sql += "=";
			break;
		case greater:
			sql += ">";
			break;
		case lower:
			sql += "<";
			break;
		}
		sql += " " + comparisonValue + ";";
		
		return sql;
	}
}
