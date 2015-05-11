package export;

import java.sql.*;
import java.security.*;
import javax.swing.*;



 public class DatabaseManager {
   public java.sql.Statement stmt_delete, stmt_retrieve, stmt_update, stmt_insert;
 // protected static Connection con;
   java.sql.Time time;
   public java.sql.Connection  con;

   //java.sql.Statement   statement;
   private String     type;
   private String     name;
   private String     username;
  private  String     password;
   private String     driverName;
   private String     url;
   ResultSet   resultSet;

   String database_name = "";
   String database_host = "";
   String database_user = "";
   String database_pass = "";

   String sConnection = "";
   public String commonurl;
   
  public DatabaseManager(String host,String name,String user,String pass) {

    try{

        this.database_host = host;
        this.database_name = name;
        this.database_user = user;
        this.database_pass = pass;
        this.url       = "jdbc:postgresql://"+database_host+"/"+database_name;
        this.username   = database_user;
        this.password   = database_pass;

        this.commonurl = "jdbc:postgresql://"+database_host+"/"+database_name;
        
        this.driverName = ("org.postgresql.Driver");

        //  this.openDbConnection();

        openDbConnection();
    }catch (Exception e){
        e.printStackTrace();
    }
  }
  
 

  // open connection
  public void openDbConnection() {
    try
    {
      java.lang.Class.forName(this.driverName);

     if(this.con == null || this.con.isClosed())
      {
        this.con = java.sql.DriverManager.getConnection(this.url, this.username,
            this.password);

        sConnection = this.url;
        
         
      }
    }

    catch (java.lang.ClassNotFoundException e) {
      
     // return false;
    }

    catch (SQLException e) {
      
    } // end try-catch


 //   return true;
  }

  // close connection
  public void closeDbConnection() {
    try {
      this.con.commit();
       if(this.con != null || !this.con.isClosed())
      this.con.close();
       System.out.println("Connection Terminated");
    }
    catch (SQLException e) {
       System.out.println("Failed to close connection");
    } // end try-catch
  }

  // insert
  public synchronized boolean insert(String sql){
    int result = 1;

    try {
      stmt_insert = con.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE,resultSet.CONCUR_READ_ONLY); //To use Postgres versiob 8
     result = stmt_insert.executeUpdate(sql);
      if (result == 1){
         System.out.println("Succesful insertion");
        return true;
      }
      else
        return false;

    } catch (SQLException e) {
       System.out.println("Failed to insert data into database");
      e.printStackTrace();
       System.out.println("----------------------------------------");

    } // end try-catch

    return false;
  }

  // delete
  public synchronized boolean delete(String sql) {
    int result = 1;
     System.out.println(sql);

    try {
     
     stmt_delete = con.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE,resultSet.CONCUR_READ_ONLY); //To use Postgres versiob 8
     result = stmt_delete.executeUpdate(sql);

      if (result == 1){
         System.out.println("Delete success");
        return true;
      } else
        return false;
    }
    catch (SQLException e) {
      System.out.println("Failed to delete data from database");
      return false;
    } // end try-catch
    //return true;
  }
  
  public synchronized boolean deleteMultiple(String sql) {
    int result = 1;
    System.out.println(sql);

    try {
     //FookLai 29032006
     // stmt_delete = con.createStatement();
     stmt_delete = con.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE,resultSet.CONCUR_READ_ONLY); //To use Postgres versiob 8
     result = stmt_delete.executeUpdate(sql);

      if (result > 0){
         System.out.println("Delete success");
        return true;
      } else
        return false;
    }
    catch (SQLException e) {
       System.out.println("Failed to delete data from database");
      return false;
    } // end try-catch
    //return true;
  }

  // retrieve
  public synchronized ResultSet retrieve(String sql) {

    ResultSet rs = null;
    System.out.println(sql);
    try {
   
      stmt_retrieve = con.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE,resultSet.CONCUR_READ_ONLY); //To use Postgres versiob 8
      //todo Udaya
     
      rs = stmt_retrieve.executeQuery(sql);
       
           System.out.println("Query success");

    } catch (SQLException e) {
       System.out.println("Failed to retrieve data from database");
       System.out.println(e.toString());
    } // retrieve data from database
    return rs;
  }

  // update
  public synchronized boolean update(String sql) {
    System.out.println(sql);
    int result = 1;

    try {
    
      stmt_update = con.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE,resultSet.CONCUR_READ_ONLY); //To use Postgres versiob 8

      result = stmt_update.executeUpdate(sql);
      System.out.println("Update success");
      return true;

    }
    catch(SQLException e) {
      System.out.println("Failed to update data to database");
      e.printStackTrace();
      return false;
    } // end try-catch
  }
  
  
   protected synchronized int updateSQL(String sql) {
    System.out.println(sql);
    int result = 1;

    try {
    
      stmt_update = con.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE,resultSet.CONCUR_READ_ONLY); //To use Postgres versiob 8

      result = stmt_update.executeUpdate(sql);
      System.out.println("Update success");
      return result;

    

    }
    catch(SQLException e) {
      System.out.println("Failed to update data to database");
      e.printStackTrace();
      return -1;
    } // end try-catch
  }
 
}
