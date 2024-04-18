package com.soundalerts;

import com.soundalerts.config.SoundMode;
import net.runelite.client.config.*;
import net.runelite.client.plugins.PluginDescriptor;

@ConfigGroup(SoundAlertsConfig.CONFIG_GROUP)
public interface SoundAlertsConfig extends Config
{

	String CONFIG_GROUP = "soundalerts";

	//Sections

	@ConfigSection(
			name = "Idle Notifications",
			description = "Idle Notification section.",
			position = 0,
			closedByDefault = false
	)
	String idleNotificationsSection = "idleNotifications";

	@ConfigSection(
			name = "Sound Settings",
			description = "Sound Settings section.",
			position = 1,
			closedByDefault = true
	)
	String soundSettingsSection = "soundSettingsNotifications";

	@ConfigItem(
			keyName = "animationidle",
			name = "Idle Animation Notifications",
			description = "Configures if idle animation notifications are enabled",
			position = 0,
			section = idleNotificationsSection
	)
	default Notification animationIdle()
	{
		return Notification.ON;
	}
	@ConfigItem(
			keyName = "timeout",
			name = "Idle Notification Delay",
			description = "The notification delay after the player is idle",
			position = 2,
			section = idleNotificationsSection
	)
	@Units(Units.MILLISECONDS)
	default int getIdleNotificationDelay()
	{
		return 2500;
	}
	@ConfigItem(
			keyName = "hitpoints",
			name = "Hitpoints Threshold",
			description = "The amount of hitpoints to send a notification at. A value of 0 will disable notification.",
			position = 3,
			section = idleNotificationsSection
	)
	default int getHitpointsThreshold()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "prayer",
			name = "Prayer Threshold",
			description = "The amount of prayer points to send a notification at. A value of 0 will disable notification.",
			position = 4,
			section = idleNotificationsSection
	)
	default int getPrayerThreshold()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "mode",
			name = "Sound Mode",
			description = "Choose which Voice pack to use or disable this feature.",
			position = 0,
			section = soundSettingsSection
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
			position = 1,
			section = soundSettingsSection
	)
	default int audioVolume()
	{
		return  100;
	}

}
