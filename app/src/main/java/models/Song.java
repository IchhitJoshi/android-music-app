package models;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;

import utils.StringUtils;

public class Song {

    private String songId;
    private String songName;
    private String artistName;
    private long artistId;
    private String albumName;
    private long albumId;
    private String duration;
    private int songYear;
    private int trackNumber = 0;
    private int discNumber = 0;
    private long dateAdded;
    private int numPlays;
    private boolean favorite = false;

    private String path;
    private long bookMark;

    private String durationLabel;
    private String bitrateLabel;
    private String sampleRateLabel;
    private String formatLabel;
    private String fileSizeLabel;

    private String mimeType;

    private String albumArtistName;

    private TagInfo tagInfo;


    public static String[] getProjection() {
        return new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.BOOKMARK,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.MIME_TYPE,
                "album_artist"
        };
    }

    public Song() {
    }

    public Song(Cursor cursor) {

        songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

        artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

        songId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

        artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));

        albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

        albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

        duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

        songYear = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));

        dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));

        bookMark = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BOOKMARK));

        trackNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));

        mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));

        if (trackNumber >= 1000) {
            discNumber = trackNumber / 1000;
            trackNumber = trackNumber % 1000;
        }

        if(discNumber == 0){
            discNumber = 1;
        }

        albumArtistName = artistName;
        if (cursor.getColumnIndexOrThrow("album_artist") != -1) {
            String albumArtist = cursor.getString(cursor.getColumnIndex("album_artist"));
            if (albumArtist != null) {
                albumArtistName = albumArtist;
            }
        }

    }

    public String getSongName(){
        return songName;
    }

    public int getNumPlays(){
        return numPlays;
    }

    public boolean getFavorite(){
        return favorite;
    }

    public void setFavorite(boolean Fav){
        favorite = Fav;
    }

    public void setNumPlays(int plays){
        numPlays = plays;
    }

    public String getSongId(){
        return songId;
    }

    public void setSongName(String Name) {
        songName = Name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String ArtistName) {
        artistName = ArtistName;
    }

    public String getSongUrl() {
        return path;
    }

    public void setSongUrl(String Path) {
        path = Path;
    }

    public Long getArtistId(){return artistId;}
    public String getAlbumName(){return albumName;}
    public Long getAlbumId(){return albumId;}

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    public void setAlbumArtistName(String albumArtistName) {
        this.albumArtistName = albumArtistName;
    }

    public String getDuration(){return duration;}

    public String getDurationLabel(Context context){
        Long duration2 = Long.parseLong(duration);
        durationLabel = StringUtils.makeTimeString(context, duration2 / 1000);

        return durationLabel;}


    public Integer getSongYear(){return songYear;}
    public Integer getTrackNumber(){return trackNumber;}
    public Long getDateAdded(){return dateAdded;}
    public Long getBookmark(){return bookMark;}


    public int getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }


    public TagInfo getTagInfo() {
        if (tagInfo == null) {
            tagInfo = new TagInfo(path);
        }
        return tagInfo;
    }

    public String getBitrateLabel(Context context) {
        if (bitrateLabel == null) {
            bitrateLabel = getTagInfo().bitrate + " kbps";
        }
        return bitrateLabel;
    }

    public String getSampleRateLabel(Context context) {
        if (sampleRateLabel == null) {
            int sampleRate = getTagInfo().sampleRate;
            if (sampleRate == -1) {
                sampleRateLabel = "Unknown";
                return sampleRateLabel;
            }
            sampleRateLabel = ((float) sampleRate) / 1000 + " kHz";
        }
        return sampleRateLabel;
    }

    public String getFormatLabel() {
        if (formatLabel == null) {
            formatLabel = getTagInfo().format;
        }
        return formatLabel;
    }

    public String getFileSizeLabel() {
        if (fileSizeLabel == null) {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                fileSizeLabel = getHumanReadableSize(file.length());
            }
        }
        return fileSizeLabel;
    }

    public static String getHumanReadableSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


}