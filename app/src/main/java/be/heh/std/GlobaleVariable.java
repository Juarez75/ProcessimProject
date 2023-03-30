package be.heh.std;

import android.app.Application;

import be.heh.std.database.Role;

public class GlobaleVariable extends Application {
    private int id_user;
    private Role role_user;

    public int getId_user(){
        return id_user;
    }
    public void setId_user(int id){
        id_user = id;
    }
    public Role getRole_user(){
        return role_user;
    }
    public void setRole_user(Role role){
        role_user = role;
    }
}
