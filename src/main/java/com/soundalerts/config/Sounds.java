package com.soundalerts.config;

import com.soundalerts.SoundAlertsConfig;

public class Sounds {

    public enum SoundFiles {
        HEALTH_FEMALE("/audio/health_female.wav"),
        HEALTH_MALE("/audio/health_male.wav"),
        PRAYER_FEMALE("/audio/prayer_female.wav"),
        PRAYER_MALE("/audio/prayer_male.wav"),
        IDLE_FEMALE("/audio/idle_female.wav"),
        IDLE_MALE("/audio/idle_male.wav");


        private String path;

        SoundFiles(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public static void loadSounds(SoundAlertsConfig config, SoundPlayer sound) {
        for (SoundFiles s : SoundFiles.values()) {
            sound.tryLoadAudio(config, s.getPath());
        }
    }
}
