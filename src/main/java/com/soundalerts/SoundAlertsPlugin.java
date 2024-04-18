package com.soundalerts;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.soundalerts.config.SoundMode;
import com.soundalerts.config.SoundPlayer;
import com.soundalerts.config.Sounds;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.io.File;

@Slf4j
@PluginDescriptor(
	name = "Sound Alerts"
)
public class SoundAlertsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SoundAlertsConfig config;

	@Inject
	private SoundPlayer soundPlayer;

	int delay = 0;

	@Override
	protected void startUp() throws Exception
	{
		soundPlayer.setVolume(config.audioVolume());
		Sounds.loadSounds(config, soundPlayer);
		log.info("Sound Alerts started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		soundPlayer.unloadAudio();
		log.info("Sound Alerts stopped!");
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (delay == 8)
			delay = 0;
		/* Hitpoints Alert */
		if (config.getHitpointsThreshold() > 0) {
			if (client.getBoostedSkillLevel(Skill.HITPOINTS) + client.getVarbitValue(Varbits.NMZ_ABSORPTION) <= config.getHitpointsThreshold() && delay == 0)
				if (config.audioMode() == SoundMode.Female)
					playSoundClip(Sounds.SoundFiles.HEALTH_FEMALE.getPath());
		}
		/* Prayer Alert */
		if (config.getPrayerThreshold() > 0) {
			if (client.getBoostedSkillLevel(Skill.PRAYER) <= config.getPrayerThreshold() && delay == 0)
				if (config.audioMode() == SoundMode.Female)
					playSoundClip(Sounds.SoundFiles.PRAYER_FEMALE.getPath());
		}
		delay++;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
		}
	}

	private void playSoundClip(String soundFile)
	{
		if (config.audioMode() == SoundMode.Disabled)
			return;
		soundPlayer.playSoundClip(soundFile);
	}

	@Provides
	SoundAlertsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoundAlertsConfig.class);
	}
}
