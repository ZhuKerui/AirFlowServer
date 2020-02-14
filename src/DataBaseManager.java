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
			boolean exist = rs.next();
			rs.close();
			return exist;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addUser(String user_name) {
		try {
			String create_table = "CREATE TABLE " + user_name + "(USER_NAME VARCHAR(50))";
			statement.executeUpdate(create_table);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO USER_INFO VALUES(?)");
			pstmt.setString(1, user_name);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				String delete_table = "DROP TABLE " + user_name;
				statement.executeUpdate(delete_table);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			return false;
		}
	}
	
	public boolean isValid() {
		return 	connection != null 
				&& statement != null;
	}
	
	public void close() {
		try {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
