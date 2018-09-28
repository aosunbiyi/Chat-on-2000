package com.browncup.chatonx;

import com.orm.SugarRecord;

public class User extends SugarRecord<User> {
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    String Username;

    public  User(){}

    public  User(String name){
   this.Username = name;
    }
}
