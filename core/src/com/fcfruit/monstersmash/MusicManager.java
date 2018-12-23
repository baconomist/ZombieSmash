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
        if(!Environment.settings.isMusicEnabled())
            for(Music music : this.musics.values())
            {
                if (music.getVolume() != 0)
                    music.setVolume(0);
            }
        else
            for(Music music : this.musics.values())
            {
                if (music.getVolume() != Environment.settings.getMusicVolume())
                    music.setVolume(Environment.settings.getMusicVolume());
            }
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

    public boolean playMusic(String musicKey)
    {
        Music music = this.musics.get(musicKey);
        if(music != null)
        {
            music.play();
            return true;
        }

        Gdx.app.error("MusicManager::playMusic()", "Could not play music **" + musicKey + "**, NullPointerException");
        return false;
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

    public boolean stopMusic(String musicKey)
    {
        if(this.musics.get(musicKey) == null)
        {
            Gdx.app.error("MusicManager::stopMusic()","No such music **" + musicKey + "** exists in music manager.");
            return false;
        }
        this.musics.get(musicKey).stop();
        this.musics.get(musicKey).dispose();
        this.musics.remove(musicKey);
        return true;
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

    public boolean removeMusic(String musicKey)
    {

        if(this.musics.get(musicKey) == null)
        {
            Gdx.app.error("MusicManager::dispose()","No such music **" + musicKey +"** exists in music manager.");
            return false;
        }

        this.musics.get(musicKey).dispose();
        this.musics.remove(musicKey);
        return true;
    }

    public boolean removeMusic(Music music)
    {
        String key = "";
        for(String k : this.musics.keySet())
        {
            if(this.musics.get(k).equals(music))
                key = k;
        }
        if(this.musics.get(key) != null)
        {
            this.musics.remove(key);
            music.dispose();
            return true;
        }

        Gdx.app.error("MusicManager::dispose()","No such music exists in music manager.");
        return false;
    }
}
