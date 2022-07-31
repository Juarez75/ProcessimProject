package be.heh.std.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlcConfDao {

    @Insert
    void addConf(PlcConf conf);

    @Query("DELETE FROM plcconf WHERE id = :id;")
    void deleteConfById(int id);

    @Query("SELECT * FROM plcconf;")
    List<PlcConf> getAllConfs();


    @Query("SELECT * FROM plcconf WHERE id = :id;")
    PlcConf getConfById(int id);
}
