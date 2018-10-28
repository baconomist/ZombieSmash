package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicManager
{
    private HashMap<String, Music> musics; // "musics" I'm funny... not...
    public MusicManager()
    {
        this.musics = new HashMap<String, Music>();
    }

    public void update()
    {
        if(!Environment.settings.isMusicEnabled())
            for(Music music : this.musics.values())
                music.setVolume(0);
        else
            for(Music music : this.musics.values())
                music.setVolume(Environment.settings.getMusicVolume());
    }

    public void resumeMusic()
    {
        for(Music music : this.musics.values())
            music.play();
    }

    public void pauseMusic()
    {
        for(Music music : this.musics.values())
        {
            music.pause();
        }
    }

    public void setVolume(float volume)
    {
        for(Music music : this.musics.values())
            music.setVolume(music.getVolume()/volume);
    }

    public void playMusic(String musicKey)
    {
        Music music = this.musics.get(musicKey);
        music.play();
        music.setOnCompletionListener(new Music.OnCompletionListener()
        {
            @Override
            public void onCompletion(Music music)
            {
                disposeMusic(music);
            }
        });
    }

    public void addMusic(String musicKey, Music music)
    {
        if(!Environment.settings.isMusicEnabled())
            music.pause();

        if(!this.musics.containsKey(musicKey))
            this.musics.put(musicKey, music);
    }

    public void stopMusic(String musicKey)
    {
        this.musics.get(musicKey).stop();
        this.musics.get(musicKey).dispose();
        this.musics.remove(musicKey);
    }

    public void stopAllMusic()
    {
        for(Music music : this.musics.values())
        {
            music.stop();
            music.dispose();
        }
        this.musics.clear();
    }

    private void disposeMusic(Music music)
    {
        String key = "";
        for(String k : this.musics.keySet())
        {
            if(this.musics.get(k).equals(music))
                key = k;
        }
        if(!key.equals(""))
            this.musics.remove(key);
        else
            Gdx.app.error("MusicManager::dispose()","No such music exists in music manager.");

        music.dispose();
    }
}
