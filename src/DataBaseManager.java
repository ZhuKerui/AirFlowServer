import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.cj.jdbc.Blob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseManager {
	
	private Connection connection;
	private Statement statement;
	
	public static boolean initDataBase() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/air_flow?useSSL=false&serverTimezone=UTC",
					"root",
					"59236406");
			if (conn == null) {
				return false;
			}
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SHOW TABLES");
			ArrayList<String> table_list = new ArrayList<String>();
			while (rs.next()) {
				table_list.add(rs.getString("tables_in_air_flow"));
			}
			rs.close();
			for (String table : table_list) {
				stmt.executeUpdate("DROP TABLE " + table);
			}
			
			stmt.executeUpdate("CREATE TABLE USER_INFO(USER_NAME VARCHAR(50))");
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO USER_INFO VALUES(?)");
			pstmt.setString(1, "_default");
			pstmt.executeUpdate();
			stmt.executeUpdate("CREATE TABLE _default(AIR_FLOW_DATA BLOB)");
			
			pstmt.close();
			stmt.close();
			conn.close();
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

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
			pstmt.close();
			return exist;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addUser(String user_name) {
		try {
			String create_table = "CREATE TABLE " + user_name + "(AIR_FLOW_DATA BLOB)";
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
	
	public Blob	getBlob() {
		if (connection != null) {
			try {
				return (Blob) connection.createBlob();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public boolean insertData(String user_name, Blob air_flow_data) {
		if (user_name == null || air_flow_data == null)
			return false;
		
		if (!isUserExist(user_name)) 
			return false;
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO " + user_name + " VALUES(?)");
			pstmt.setBlob(1, air_flow_data);
			boolean result = (pstmt.executeUpdate() == 1);
			pstmt.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
	}
}
