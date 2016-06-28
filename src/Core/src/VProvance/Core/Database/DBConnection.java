/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.Database;

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
public class DBConnection {
    
    private static DBConnection _instance;
    private static String _currentUserName;
    
    private Connection _connection;
    
    public static DBConnection instance()
    {
        return _instance;
    }
    
    public static String currentUserName()
    {
        return _currentUserName;
    }
    
    public static void SetConnection(String user, String password) 
            throws DataUnreachableException, UnsupportedOperationException
    {
        if (_instance != null) throw new UnsupportedOperationException("Connection already established");
        
        _instance = new DBConnection(user, password);
        _currentUserName = user;
    }
    
    public static void ResetConnection() {
        _instance = null;
        _currentUserName = null;
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
        String role = GetCurrentUserInfo().getRole();        
        
        switch (role) {
            case "Менеджер":
                return GetBatches("UsefullBatches");
            case "Винодел":
                return GetBatches("VinemakerBatches"); 
            case "Продавец":
                return GetBatches("SellerBatches");
            default:
                return new ArrayList<>();
        }
    }

    public List<UsefullBatch> GetArrivedBatches() {
        return GetBatches("ArrivedBatches");
    }
    
    private List<UsefullBatch> GetBatches(String viewName)
    {
        List<UsefullBatch> res = new ArrayList<>();
        
        try {
            try (Statement stmt = _connection.createStatement()) {            
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + viewName)) {
                    while (rs.next()) {
                        UsefullBatch batch = new UsefullBatch();

                        batch.setId(rs.getLong("id"));
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
            return new ArrayList<>();
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
    
    public void SendBatchToSeller(long batchId) throws SQLException 
    {
        try (CallableStatement  stmt = _connection.prepareCall("{? = call dbo.SendBatchTo(?,?)}")) {
            stmt.setLong(2, batchId);
            stmt.setString(3, "seller");
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute(); 
            
            int res = stmt.getInt(1);
            
            if (res == 3)
                throw new SQLException("Невозможно переместить партию: партия товара уже отправлена");            
            else if (res != 0)
                throw new SQLException("Невозможно переместить партию: некорректные данные");
        }
    }
    
    public void AcceptBatch(long batchId) throws SQLException
    {
        try (CallableStatement  stmt = _connection.prepareCall("{? = call dbo.AcceptBatch(?)}")) {
            stmt.setLong(2, batchId);
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute(); 
            
            int res = stmt.getInt(1);
            
            if (res == 1)
                throw new SQLException("Невозможно подтвердить получение партии: данная партия не направлена текущему пользователю");
            else if (res != 0)
                throw new SQLException("Невозможно подтвердить получение партии");
        }       
    }
    
    public void Sale(long batchId) throws SQLException
    {
        try (CallableStatement  stmt = _connection.prepareCall("{? = call dbo.SaleWine(?)}")) {
            stmt.setLong(2, batchId);
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute(); 
            
            int res = stmt.getInt(1);
            
            if (res == 1)
                throw new SQLException("Невозможно зарегистрировать продажу партии: данная партия не находится в магазине");
            else if (res == 3)
                throw new SQLException("Невозможно зарегистрировать продажу партии: вы не продавец");
            else if (res != 0)
                throw new SQLException("Невозможно зарегистрировать продажу партии");
        }       
    }
    
    public List<Transaction> GetTransactions()
    {
        List<Transaction> res = new ArrayList<>();
        
        try {
            try (Statement stmt = _connection.createStatement()) {            
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM UsefullTransactions")) {
                    while (rs.next()) {
                        Transaction trans = new Transaction();

                        trans.setTime(rs.getDate("time"));
                        trans.setAction(rs.getString("action"));
                        trans.setResource(rs.getString("resource"));
                        trans.setCount(rs.getFloat("count"));
                        trans.setMeasure(rs.getString("measure"));
                        trans.setSubject(rs.getString("subject"));
                        trans.setObject(rs.getString("object"));
                        trans.setAccepted(rs.getBoolean("accepted"));
                        trans.setAcceptedTime(rs.getDate("accepted time"));

                        res.add(trans);
                    }    
                }
            }
        }
        catch (SQLException ex) {
            return new ArrayList<>();
        }
        
        return res;
    }
    
    public void CreateUser(String username, String password, String role) throws SQLException {
        if(username == null || username.isEmpty())
            throw new IllegalArgumentException("username");
        
        if(password == null || password.isEmpty())
            throw new IllegalArgumentException("password");
        
        if(role == null || role.isEmpty())
            throw new IllegalArgumentException("role");
        
        try (CallableStatement  stmt = _connection.prepareCall("{? = call sp_addlogin(?,?,?)}")) {
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, "vProvance");
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute();            
            int res = stmt.getInt(1);
            
            if (res != 0)
                throw new SQLException("Невозможно добавить нового пользователя");
        }
        
        try (CallableStatement  stmt = _connection.prepareCall("{? = call sp_adduser(?,?)}")) {
            stmt.setString(2, username);
            stmt.setString(3, username);
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute();            
            int res = stmt.getInt(1);
            
            if (res != 0)
                throw new SQLException("Невозможно добавить нового пользователя");
        }
        
        try (CallableStatement  stmt = _connection.prepareCall("{? = call sp_addrolemember(?,?)}")) {
            stmt.setString(2, "db_owner");
            stmt.setString(3, username);
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute();            
            int res = stmt.getInt(1);
            
            if (res != 0)
                throw new SQLException("Невозможно добавить нового пользователя");
        }
        
        try (Statement stmt = _connection.createStatement()) {
            stmt.executeUpdate(
                "insert into users(name, roleID, userID)" +
                "values ('" + username + "'," +
                        "(select top(1) id from userTypes where description like '" + role + "'), "+
                        "(select top(1) sid from sys.syslogins where name like '" + username + "'))");
        }
    }
    
    public List<String> GetUserRoles()
    {
        List<String> res = new ArrayList<>();
        
        try {
            try (Statement stmt = _connection.createStatement()) {            
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM userTypes")) {
                    while (rs.next()) {
                        res.add(rs.getString("description"));
                    }    
                }
            }
        }
        catch (SQLException ex) {
            return null;
        }
        
        return res;        
    }
    
    public UserInfo GetCurrentUserInfo()
    {
        try {
            try (Statement stmt = _connection.createStatement()) {            
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM CurrentUserInfo")) {
                    while (rs.next()) {
                        UserInfo result  = new UserInfo();
                        
                        result.setUsername(rs.getString("username"));
                        result.setName(rs.getString("name"));
                        result.setRole(rs.getString("role"));
                        
                        return result;
                    }    
                }
            }
        }
        catch (SQLException ex) {   }  
        
        return null;
    }
    
    public void SetCurrentUserInfo(UserInfo info) throws SQLException 
    {
        try (CallableStatement  stmt = _connection.prepareCall("{? = call [dbo].[SetCurrentUserInfo](?,?)}")) {
            stmt.setString(2, info.getName());
            stmt.setString(3, info.getRole());
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute(); 
            
            int res = stmt.getInt(1);
            
            if (res != 0)
                throw new SQLException("Невозможно добавить партию: некорректные данные");
            }
    }
    
    public List<Field> GetFields()
    {
        List<Field> res = new ArrayList<>();
        
        try {
            try (Statement stmt = _connection.createStatement()) {            
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM UsefullFields")) {
                    while (rs.next()) {
                        Field field = new Field();

                        field.setDescription(rs.getString("description"));
                        field.setSquare(rs.getFloat("square"));
                        field.setPrecipitation(rs.getFloat("precipitation"));
                        field.setGroundType(rs.getString("ground type"));
                        field.setSeedingBy(rs.getString("seeding by"));

                        res.add(field);
                    }    
                }
            }
        }
        catch (SQLException ex) {
            return null;
        }
        
        return res;        
    }
    
    public void UpdateField(String oldName, Field newParams) throws SQLException 
    {
        try (CallableStatement  stmt = _connection.prepareCall("{? = call [dbo].[SetFieldInfo](?,?,?,?,?,?)}")) {
            stmt.setString(2, oldName);
            stmt.setString(3, newParams.getDescription());
            stmt.setDouble(4, newParams.getSquare());
            stmt.setDouble(5, newParams.getPrecipitation());
            stmt.setString(6, newParams.getGroundType());
            stmt.setString(7, newParams.getSeedingBy());
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.execute(); 
            
            int res = stmt.getInt(1);
            
            if (res != 0)
                throw new SQLException("Невозможно изменить информацию о поле: некорректные данные");
            }
    }
}