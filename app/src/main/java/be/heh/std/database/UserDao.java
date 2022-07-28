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

    @Query("SELECT * FROM user WHERE id = :id;")
    User getUserById(int id);

    @Query("UPDATE user " +
            "SET firstname = :firstname, lastname = :lastname, mail = :mail " +
            "WHERE id = :id;")
    void updateUser(int id, String firstname, String lastname, String mail);

    @Query("UPDATE user SET password = :password WHERE id = :id;")
    void updatePassword(int id, String password);
}

