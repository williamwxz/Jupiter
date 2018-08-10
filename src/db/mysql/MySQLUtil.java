package db.mysql;

public class MySQLUtil {
	private static final String HOSTNAME = "localhost";//test-mysql, localhost
	private static final String PORT_NUM = "3309"; //3309
	public static final String DB_NAME = "laiproject";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://"
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
//	jdbc:mysql://localhost:8889/
}
