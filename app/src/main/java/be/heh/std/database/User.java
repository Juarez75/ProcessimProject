package be.heh.std.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices= {@Index(value={"mail"},unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String lastname;
    @NonNull
    public String firstname;
    @NonNull
    public String mail;
    @NonNull
    public String password;
    @NonNull
    public Role role;
}
