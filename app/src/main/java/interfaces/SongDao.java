//package interfaces;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Delete;
//import androidx.room.Insert;
//import androidx.room.Query;
//import androidx.room.Update;
//
//import java.util.ArrayList;
//
//import models.SongEntity;
//
//@Dao
//public interface SongDao {
//
//    @Insert
//    void insert(ArrayList<SongEntity> songEntities);
//
//    @Update
//    void update(ArrayList<SongEntity> songEntities);
//
//    @Delete
//    void delete(SongEntity songEntity);
//
//    @Query("DELETE FROM song_table")
//    void deleteAllSongs();
//
//    @Query("SELECT * FROM song_table ORDER BY songName ASC")
//     LiveData<ArrayList<SongEntity>> getAllSongs();
//
//
//}
