//package models;
//
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//@Entity(tableName = "song_table")
//public class SongEntity {
//
//    @PrimaryKey()
//    private String songId;
//    private String songName;
//    private String artistName;
//    private long artistId;
//    private String albumName;
//    private long albumId;
//    private String duration;
//    private int songYear;
//    private int trackNumber = 0;
//    private int discNumber = 0;
//    private int dateAdded;
//    private int numPlays;
//    private boolean favorite = false;
//    private String path;
//    private String durationLabel;
//    private String albumArtistName;
//
//    public SongEntity(Song song){
//        this.songId = song.getSongId();
//
//    }
//
//    public String getSongId() {
//        return songId;
//    }
//
//    public void setSongId(String songId) {
//        this.songId = songId;
//    }
//
//    public String getSongName() {
//        return songName;
//    }
//
//    public void setSongName(String songName) {
//        this.songName = songName;
//    }
//
//    public String getArtistName() {
//        return artistName;
//    }
//
//    public void setArtistName(String artistName) {
//        this.artistName = artistName;
//    }
//
//    public long getArtistId() {
//        return artistId;
//    }
//
//    public void setArtistId(long artistId) {
//        this.artistId = artistId;
//    }
//
//    public String getAlbumName() {
//        return albumName;
//    }
//
//    public void setAlbumName(String albumName) {
//        this.albumName = albumName;
//    }
//
//    public long getAlbumId() {
//        return albumId;
//    }
//
//    public void setAlbumId(long albumId) {
//        this.albumId = albumId;
//    }
//
//    public String getDuration() {
//        return duration;
//    }
//
//    public void setDuration(String duration) {
//        this.duration = duration;
//    }
//
//    public int getSongYear() {
//        return songYear;
//    }
//
//    public void setSongYear(int songYear) {
//        this.songYear = songYear;
//    }
//
//    public int getTrackNumber() {
//        return trackNumber;
//    }
//
//    public void setTrackNumber(int trackNumber) {
//        this.trackNumber = trackNumber;
//    }
//
//    public int getDiscNumber() {
//        return discNumber;
//    }
//
//    public void setDiscNumber(int discNumber) {
//        this.discNumber = discNumber;
//    }
//
//    public int getDateAdded() {
//        return dateAdded;
//    }
//
//    public void setDateAdded(int dateAdded) {
//        this.dateAdded = dateAdded;
//    }
//
//    public int getNumPlays() {
//        return numPlays;
//    }
//
//    public void setNumPlays(int numPlays) {
//        this.numPlays = numPlays;
//    }
//
//    public boolean isFavorite() {
//        return favorite;
//    }
//
//    public void setFavorite(boolean favorite) {
//        this.favorite = favorite;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public String getDurationLabel() {
//        return durationLabel;
//    }
//
//    public void setDurationLabel(String durationLabel) {
//        this.durationLabel = durationLabel;
//    }
//
//    public String getAlbumArtistName() {
//        return albumArtistName;
//    }
//
//    public void setAlbumArtistName(String albumArtistName) {
//        this.albumArtistName = albumArtistName;
//    }
//
//
//
//
//}
