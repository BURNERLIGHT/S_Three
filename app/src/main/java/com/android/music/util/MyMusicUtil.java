package com.android.music.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.music.database.DBManager;
import com.android.music.entity.FolderInfo;
import com.android.music.entity.MusicInfo;
import com.android.music.service.MusicPlayerService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyMusicUtil {

    private static final String TAG = MyMusicUtil.class.getName();
    //获取当前播放列表
    public static List<MusicInfo> getCurPlayList(Context context){
        DBManager dbManager = DBManager.getInstance(context);
        int playList = MyMusicUtil.getIntShared(Constant.KEY_LIST);
        List<MusicInfo> musicInfoList = new ArrayList<>();
        switch (playList){
            case Constant.LIST_ALLMUSIC:
                musicInfoList = dbManager.getAllMusicFromMusicTable();
                break;
            case Constant.LIST_MYLOVE:
                musicInfoList = dbManager.getAllMusicFromTable(Constant.LIST_MYLOVE);
                break;
            case Constant.LIST_LASTPLAY:
                musicInfoList = dbManager.getAllMusicFromTable(Constant.LIST_LASTPLAY);
                break;
            case Constant.LIST_PLAYLIST:
                int listId = MyMusicUtil.getIntShared(Constant.KEY_LIST_ID);
                musicInfoList = dbManager.getMusicListByPlaylist(listId);
                break;
            case Constant.LIST_SINGER:
                String singerName = MyMusicUtil.getStringShared(Constant.KEY_LIST_ID);
                if (singerName == null){
                    musicInfoList = dbManager.getAllMusicFromMusicTable();
                }else {
                    musicInfoList = dbManager.getMusicListBySinger(singerName);
                }
                break;
            case Constant.LIST_ALBUM:
                String albumName = MyMusicUtil.getStringShared(Constant.KEY_LIST_ID);
                if (albumName == null){
                    musicInfoList = dbManager.getAllMusicFromMusicTable();
                }else {
                    musicInfoList = dbManager.getMusicListByAlbum(albumName);
                }
                break;
            case Constant.LIST_FOLDER:
                String folderName = MyMusicUtil.getStringShared(Constant.KEY_LIST_ID);
                if (folderName == null){
                    musicInfoList = dbManager.getAllMusicFromMusicTable();
                }else {
                    musicInfoList = dbManager.getMusicListByFolder(folderName);
                }
                break;
        }
        return musicInfoList;
    }

    /**
     * 播放下一首
     * @param context
     */
    public static void playNextMusic(Context context){
        //获取下一首ID
        DBManager dbManager = DBManager.getInstance(context);
        int playMode = MyMusicUtil.getIntShared(Constant.KEY_MODE);
        Log.d(TAG,"next play mode ="+playMode);
        int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
        List<MusicInfo> musicList = getCurPlayList(context);
        ArrayList<Integer> musicIdList =new ArrayList<>();
        for (MusicInfo info : musicList){
            musicIdList.add(info.getId());
        }
        musicId = dbManager.getNextMusic(musicIdList,musicId,playMode);
        MyMusicUtil.setShared(Constant.KEY_ID,musicId);
        if (musicId == -1) {
            Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
            context.sendBroadcast(intent);
            Toast.makeText(context, "歌曲不存在",Toast.LENGTH_LONG).show();
            return;
        }

        //获取播放歌曲路径
        String path = dbManager.getMusicPath(musicId);
        Log.d(TAG,"next path ="+path);
        //发送播放请求
        Log.d(TAG,"next  id = "+musicId+"path = "+ path);
        Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
        intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
        intent.putExtra(Constant.KEY_PATH, path);
        context.sendBroadcast(intent);
    }

    /**
     * 播放上一首
      * @param context
     */
    public static void playPreMusic(Context context){
        //获取下一首ID
        DBManager dbManager = DBManager.getInstance(context);
        int playMode = MyMusicUtil.getIntShared(Constant.KEY_MODE);
        Log.d(TAG,"pre play mode ="+playMode);
        int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
        List<MusicInfo> musicList = getCurPlayList(context);
        ArrayList<Integer> musicIdList =new ArrayList<>();
        for (MusicInfo info : musicList){
            musicIdList.add(info.getId());
        }
        musicId = dbManager.getPreMusic(musicIdList,musicId,playMode);
        MyMusicUtil.setShared(Constant.KEY_ID,musicId);
        if (musicId == -1) {
            Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
            context.sendBroadcast(intent);
            Toast.makeText(context, "歌曲不存在",Toast.LENGTH_LONG).show();
            return;
        }

        //获取播放歌曲路径
        String path = dbManager.getMusicPath(musicId);
        Log.d(TAG,"pre path ="+path);
        //发送播放请求
        Log.d(TAG,"pre  id = "+musicId+"path = "+ path);
        Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
        intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
        intent.putExtra(Constant.KEY_PATH, path);
        context.sendBroadcast(intent);
    }

    public static void setMusicMylove(Context context,int musicId){
        if (musicId == -1){
            Toast.makeText(context, "歌曲不存在",Toast.LENGTH_LONG).show();
            return;
        }
        DBManager dbManager = DBManager.getInstance(context);
        dbManager.setMyLove(musicId);
    }


    // 设置sharedPreferences
    public static void setShared(String key,int value){
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("music",MyApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setShared(String key,String value){
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("music",MyApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // 获取sharedPreferences
    public static int getIntShared(String key) {
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("music", MyApplication.getContext().MODE_PRIVATE);
        int value;
        if (key.equals(Constant.KEY_CURRENT)){
            value = pref.getInt(key, 0);
        }else{
            value = pref.getInt(key, -1);
        }
        return value;
    }

    public static String getStringShared(String key) {
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("music", MyApplication.getContext().MODE_PRIVATE);
        String value;
        value = pref.getString(key,null);
        return value;
    }




    //按文件夹分组
    public static ArrayList<FolderInfo> groupByFolder(ArrayList list) {
        Map<String, List<MusicInfo>> musicMap = new HashMap<>();
        ArrayList<FolderInfo> folderInfoList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MusicInfo musicInfo = (MusicInfo) list.get(i);
            if (musicMap.containsKey(musicInfo.getParentPath())) {
                ArrayList folderList = (ArrayList) musicMap.get(musicInfo.getParentPath());
                folderList.add(musicInfo);
            } else {
                ArrayList temp = new ArrayList();
                temp.add(musicInfo);
                musicMap.put(musicInfo.getParentPath(), temp);
            }
        }

        for (Map.Entry<String,List<MusicInfo>> entry : musicMap.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            FolderInfo folderInfo = new FolderInfo();
            File file = new File(entry.getKey());
            folderInfo.setName(file.getName());
            folderInfo.setPath(entry.getKey());
            folderInfo.setCount(entry.getValue().size());
            folderInfoList.add(folderInfo);
        }

        return folderInfoList;
    }




    //得到主题
    public static int getTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.THEME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("theme_select", 0);
    }




    // 设置必用图片 sharedPreferences
    public static void setBingShared(String value){
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("bing_pic",MyApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pic", value);
        editor.commit();
    }

    // 获取必用图片 sharedPreferences
    public static String getBingShared() {
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("bing_pic", MyApplication.getContext().MODE_PRIVATE);
        String value = pref.getString("pic",null);
        return value;
    }


}
