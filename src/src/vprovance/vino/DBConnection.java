/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DexpUser
 */
class DBConnection {
    
    private static DBConnection _instance;
    
    private Connection _connection;
    
    public static DBConnection instance()
    {
        return _instance;
    }
    
    public static void SetConnection(String user, String password) 
            throws DataUnreachableException
    {
        _instance = new DBConnection(user, password);
    }
    
    public DBConnection(String user, String password) throws DataUnreachableException
    {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost;databaseName=vProvance";
            _connection = DriverManager.getConnection(url, user, password);   
        }
        catch (SQLException ex)
        {
            throw new DataUnreachableException(DataUnreachableReason.QueryError);
        }
        catch (ClassNotFoundException ex)
        {
            throw new DataUnreachableException(DataUnreachableReason.DriverNotFound);
        }        
    }
    
    public List<UsefullBatch> GetBatches()
    {
        List<UsefullBatch> res = new ArrayList<UsefullBatch>();
        
        try {
            try (Statement stmt = _connection.createStatement()) {            
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM UsefullBatches")) {
                    while (rs.next()) {
                        UsefullBatch batch = new UsefullBatch();

                        batch.setResourceType(rs.getString("resource type"));
                        batch.setCount(rs.getFloat("count"));
                        batch.setMeasure(rs.getString("measure"));
                        batch.setDescription(rs.getString("description"));
                        batch.setCost(rs.getBigDecimal("cost"));
                        batch.setProductionDate(rs.getDate("productionDate"));
                        batch.setPlaceName(rs.getString("place name"));

                        res.add(batch);
                    }    
                }
            }
        }
        catch (SQLException ex) {
            return null;
        }
        
        return res;        
    }
    
    public void AddBatch(UsefullBatch batch) throws SQLException 
    {
        try (CallableStatement  stmt = _connection.prepareCall("{? = call dbo.AddBatch(?,?,?,?,?,?)}")) {
            stmt.setString(2, batch.getResourceType());
            stmt.setFloat(3, batch.getCount());
            stmt.setBigDecimal(4, batch.getCost());
            stmt.setString(5, batch.getPlaceName());
            stmt.setDate(6, (Date) batch.getProductionDate());
            stmt.setString(7, batch.getDescription());  
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute(); 
            
            int res = stmt.getInt(1);
            
            if (res != 0)
                throw new SQLException("Невозможно добавить партию: некорректные данные");
            }
    }
}

class DataUnreachableException extends Exception
{
    DataUnreachableReason _reason;
    
    public DataUnreachableException(DataUnreachableReason reason) {
        _reason = reason;
    }
    
    public DataUnreachableReason GetReason() {
        return _reason;
    }
}

enum DataUnreachableReason {
    DriverNotFound,
    QueryError
}
