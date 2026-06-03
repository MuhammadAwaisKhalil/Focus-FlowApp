package org.example;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;

    public static void loadAudio(String fileName){
        if( mediaPlayer!=null){
            mediaPlayer.stop();
        }
        URL audioURL = MusicPlayer.class.getResource("/audio/"+fileName+".mp3");
        if(audioURL==null){
            System.out.println("File not loaded");
            return;
        }
        Media media = new Media(audioURL.toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.5);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public static void play(){
        if(mediaPlayer!=null)
            mediaPlayer.play();
    }
    public static void pause(){
        if(mediaPlayer!=null)
            mediaPlayer.pause();
    }
}
