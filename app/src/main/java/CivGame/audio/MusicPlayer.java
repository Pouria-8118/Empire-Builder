package civgame.audio;

import civgame.util.Constants;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class MusicPlayer {

    private Clip musicClip;
    private FloatControl volumeControl;
    private int currentVolumePercent;

    public MusicPlayer() {
        this.currentVolumePercent = Constants.DEFAULT_VOLUME_PERCENT;
    }

    public void loadAndPlay(String resourcePath) {
        try {
            URL soundUrl = getClass().getResource(resourcePath);
            if (soundUrl == null) {
                System.out.println("Music file not found: " + resourcePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);

            if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolumePercent(currentVolumePercent);
            }

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (UnsupportedAudioFileException exception) {
            System.out.println("Unsupported audio file: " + exception.getMessage());
        } catch (IOException exception) {
            System.out.println("Could not read audio file: " + exception.getMessage());
        } catch (LineUnavailableException exception) {
            System.out.println("Audio line unavailable: " + exception.getMessage());
        }
    }

    public void setVolumePercent(int percent) {
        this.currentVolumePercent = clampPercent(percent);

        if (volumeControl == null) {
            return;
        }

        float minGain = volumeControl.getMinimum();
        float maxGain = volumeControl.getMaximum();
        float normalizedValue = currentVolumePercent / 100f;
        float gainValue = minGain + (maxGain - minGain) * normalizedValue;
        volumeControl.setValue(gainValue);
    }

    public int getVolumePercent() {
        return currentVolumePercent;
    }

    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    private int clampPercent(int percent) {
        if (percent < 0) {
            return 0;
        }
        if (percent > 100) {
            return 100;
        }
        return percent;
    }
}