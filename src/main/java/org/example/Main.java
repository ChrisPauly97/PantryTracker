package org.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@RestController
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

    }

    @RequestMapping("/hello")
    public List<Item> stuff2(){
        return stuff();
    }

    public List<Item> stuff(){
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.1.120:3306/pantrytracker", "java", "password");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from items");
            List<Item> items = new ArrayList<>();
            while (rs.next()) {
                Item i = new Item();
                i.setItemCount(rs.getInt(3));
                i.setItemName(rs.getString(2));
                i.setItemId(rs.getInt(1));
                items.add(i);
            }
            System.out.println("Fetched the items below:");
            System.out.println(items);
            con.close();
            return items;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return List.of();
        }

    }
}