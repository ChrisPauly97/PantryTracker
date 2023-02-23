package org.example;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class Main {

    private Statement stmt;
    private Connection con;

    private void initCon(){
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://192.168.1.120:3306/pantrytracker", "java", "password");
            this.stmt = con.createStatement();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void createTable(){
        initCon();
        String tableSQL = "CREATE TABLE items (itemId int NOT NULL, PRIMARY KEY (itemId),itemName text,itemCount int);";
        try{
            this.stmt.executeUpdate(tableSQL);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @RequestMapping("/setup")
    public void setup(){
        createTable();
    }
    @RequestMapping("/getItemById")
    public Item getItemById(@RequestHeader String id){
        initCon();
        Item test = getById(id);
        if(test != null){
            System.out.println(test.getItemId() +'\n'+ test.getItemName() +'\n'+ test.getItemCount());
        }
        return test;
    }

    @RequestMapping("/addItemById")
    public Object addItemById(@RequestHeader String id, @RequestHeader String itemName, @RequestHeader Integer itemCount){
        initCon();
        Item i = new Item(id, itemName, itemCount);
        return addById(i);
    }

    private boolean itemExists(String id){
        Item results = getById(id);
        return results != null;
    }

    private Item addById(Item item){
        try{
            if(!itemExists(item.getItemId())){
                PreparedStatement insertQuery = con.prepareStatement("insert into items (itemId, itemName, itemCount) Values (?,?,?);");
                insertQuery.setString(1,item.getItemId());
                insertQuery.setString(2,item.getItemName());
                insertQuery.setInt(3,item.getItemCount());
                insertQuery.executeUpdate();
                return item;
            }else{
                PreparedStatement insertQuery = con.prepareStatement("update items SET itemCount = itemCount + ? where itemId = ?");
                insertQuery.setInt(1,item.getItemCount());
                insertQuery.setString(2,item.getItemId());
                insertQuery.executeUpdate();
                Item i = getById(item.getItemId());
                return i;
            }

        }catch(Exception e){
            return null;
        }finally {
            try {
                this.con.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }
    private Item getById(String id){
        try{
            ResultSet rs = this.stmt.executeQuery("select * from items where itemId = '" + id + "';" );
            rs.next();
            Item i = new Item();
            i.setItemCount(rs.getInt(3));
            i.setItemName(rs.getString(2));
            i.setItemId(rs.getString(1));
            return i;
        }catch(Exception e){
            return null;
        }

    }
}