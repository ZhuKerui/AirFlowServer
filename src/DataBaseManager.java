import java.sql.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseManager {
	
	private Connection connection;
	private Statement statement;

	public DataBaseManager() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/air_flow?useSSL=false&serverTimezone=UTC",
					"root",
					"59236406");
			if (connection == null) {
				return;
			}
			statement = connection.createStatement();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isUserExist(String user_name) {
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM USER_INFO WHERE USER_NAME = ?");
			pstmt.setString(1, user_name);
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addUser(String user_name) {
		try {
			PreparedStatement pstmt = connection.prepareStatement("CREATE TABLE ?(USER_NAME, VARCHAR(50))");
			pstmt.setString(1, user_name);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isValid() {
		return 	connection != null 
				&& statement != null;
	}
}
