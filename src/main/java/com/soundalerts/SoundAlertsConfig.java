package com.soundalerts;

import com.soundalerts.config.SoundMode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("example")
public interface SoundAlertsConfig extends Config
{

	@ConfigItem(
			keyName = "hitpoints",
			name = "Hitpoints Threshold",
			description = "The amount of hitpoints to send a notification at. A value of 0 will disable notification."
	)
	default int getHitpointsThreshold()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "prayer",
			name = "Prayer Threshold",
			description = "The amount of prayer points to send a notification at. A value of 0 will disable notification."
	)
	default int getPrayerThreshold()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "mode",
			name = "Sound Mode",
			description = "Choose which Voice pack to use or disable this feature.",
			position = 99
	)
	default SoundMode audioMode()
	{
		return SoundMode.Female;
	}

	@Range(
			min = 0,
			max = 200
	)
	@ConfigItem(
			keyName = "volume",
			name = "Audio volume",
			description = "Volume relative to the source (0-200)%.",
			position = 100
	)
	default int audioVolume()
	{
		return  100;
	}

}
