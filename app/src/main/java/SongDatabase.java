//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//import interfaces.SongDao;
//import models.SongEntity;
//
//@Database(entities = {SongEntity.class}, version = 1)
//public abstract class SongDatabase extends RoomDatabase {
//
//    private static SongDatabase instance;
//
//    public abstract SongDao songDao();
//
//    public static synchronized SongDatabase getInstance(Context context){
//
//        if(instance == null){
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                    SongDatabase.class, "song_database")
//                    .fallbackToDestructiveMigration()
//                    .build();
//        }
//        return instance;
//    }
//
//}
