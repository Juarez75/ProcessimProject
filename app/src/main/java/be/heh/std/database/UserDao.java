package be.heh.std.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void addUser(User user);

    @Query("SELECT * from user")
    List<User> getAllUsers();

    @Query("SELECT COUNT(*) FROM user;")
    int getCountOfUsers();

    @Query("SELECT * FROM user WHERE mail = :mail;")
    User getUserByEmail(String mail);
}

