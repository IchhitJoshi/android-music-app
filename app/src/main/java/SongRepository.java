//import android.app.Application;
//import android.os.AsyncTask;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Query;
//
//import java.util.ArrayList;
//
//import interfaces.SongDao;
//import models.SongEntity;
//
//public class SongRepository {
//
//    private SongDao songDao;
//    private LiveData<ArrayList<SongEntity>> allSongs;
//
//    public SongRepository(Application application){
//        SongDatabase songDatabase = SongDatabase.getInstance(application);
//        songDao = songDatabase.songDao();
//        allSongs = songDao.getAllSongs();
//
//    }
//
//    public void insert(ArrayList<SongEntity> songEntities){
//
//
//    }
//
//    public void update(ArrayList<SongEntity> songEntities){
//
//    }
//
//    public void delete(SongEntity songEntity){
//
//    }
//
//    public void deleteAllSongs(){
//
//    }
//
//    public LiveData<ArrayList<SongEntity>> getAllSongs() {
//        return allSongs;
//    }
//
//    private static class InsertSongsAsyncTask extends AsyncTask<SongEntity, Void, Void>{
//        private SongDao songDao;
//
//        private InsertSongsAsyncTask(SongDao songDao){
//            this.songDao = songDao;
//        }
//
//        @Override
//        protected Void doInBackground(SongEntity... songEntities) {
////            songDao.insert(songEntities);
//            return null;
//        }
//    }
//}
