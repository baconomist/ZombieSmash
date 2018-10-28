package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.ArrayList;

public class MusicManager
{
    private ArrayList<Music> musics; // "musics" I'm funny... not...
    public MusicManager()
    {
        this.musics = new ArrayList<Music>();
    }

    public void update()
    {
        if(!Environment.settings.isMusicEnabled())
            for(Music music : this.musics)
                music.setVolume(0);
        else
            for(Music music : this.musics)
                music.setVolume(Environment.settings.getMusicVolume());
    }

    public void playMusic(Music music)
    {
        if(!Environment.settings.isMusicEnabled())
            music.setVolume(0);

        this.musics.add(music);
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

    public void stopMusic(Music music)
    {
        this.musics.remove(music);
        music.stop();
        music.dispose();
    }

    public void stopAllMusic()
    {
        for(Music music : musics)
        {
            music.stop();
            music.dispose();
        }
        this.musics.clear();
    }

    public void pauseAllMusic()
    {
        for(Music music : musics)
        {
            music.pause();
        }
    }

    private void disposeMusic(Music music)
    {
        this.musics.remove(music);
        music.dispose();
    }
}
