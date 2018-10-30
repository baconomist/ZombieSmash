package com.fcfruit.monstersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.fcfruit.monstersmash.Environment;

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
        if(!com.fcfruit.monstersmash.Environment.settings.isMusicEnabled())
            for(Music music : this.musics.values())
                music.setVolume(0);
        else
            for(Music music : this.musics.values())
                music.setVolume(com.fcfruit.monstersmash.Environment.settings.getMusicVolume());
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
        if(music != null)
            music.play();
        else
            Gdx.app.error("MusicManager::playMusic()", "Could not play music **" + musicKey + "**, NullPointerException");
    }

    public void addMusic(String musicKey, Music music, boolean loop)
    {
        if(!Environment.settings.isMusicEnabled())
            music.pause();

        if(!this.musics.containsKey(musicKey))
            this.musics.put(musicKey, music);

        music.setLooping(loop);
        if(!loop)
        {
            music.setOnCompletionListener(new Music.OnCompletionListener()
            {
                @Override
                public void onCompletion(Music music)
                {
                    removeMusic(music);
                }
            });
        }
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

    public void removeMusic(String key)
    {

        if(this.musics.get(key) == null)
        {
            Gdx.app.error("MusicManager::dispose()","No such music exists in music manager.");
            return;
        }

        this.musics.get(key).dispose();
        this.musics.remove(key);
    }

    public void removeMusic(Music music)
    {
        String key = "";
        for(String k : this.musics.keySet())
        {
            if(this.musics.get(k).equals(music))
                key = k;
        }
        if(this.musics.get(key) != null)
            this.musics.remove(key);
        else
            Gdx.app.error("MusicManager::dispose()","No such music exists in music manager.");

        music.dispose();
    }
}
