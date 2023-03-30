package be.heh.std.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlcConf {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String ip;
    @NonNull
    public String rack;
    @NonNull
    public String slot;
    @NonNull
    public String data_block;
    @NonNull
    public PlcType type;
}
