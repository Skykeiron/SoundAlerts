package com.soundalerts;

import com.soundalerts.config.SoundMode;
import net.runelite.client.config.*;

@ConfigGroup(SoundAlertsConfig.CONFIG_GROUP)
public interface SoundAlertsConfig extends Config
{

	String CONFIG_GROUP = "soundalerts";

	//Sections
	@ConfigSection(
			name = "Animation Notifications",
			description = "Animation Notification section.",
			position = 0,
			closedByDefault = false
	)
	String animationNotificationsSection = "animationNotifications";
	@ConfigSection(
			name = "Experience Notifications",
			description = "Experience Notification section.",
			position = 1,
			closedByDefault = false
	)
	String experienceNotificationsSection = "experienceNotifications";
	@ConfigSection(
			name = "Combat Notifications",
			description = "Combat Notification section.",
			position = 2,
			closedByDefault = false
	)
	String combatNotificationsSection = "combatNotifications";

	@ConfigSection(
			name = "Sound Settings",
			description = "Sound Settings section.",
			position = 2,
			closedByDefault = true
	)
	String soundSettingsSection = "soundSettingsNotifications";

	@ConfigItem(
			keyName = "animationidle",
			name = "Idle Animation Notifications",
			description = "Configures if idle animation notifications are enabled",
			position = 0,
			section = animationNotificationsSection
	)
	default boolean animationIdle()
	{
		return true;
	}
	@ConfigItem(
			keyName = "timeout",
			name = "Idle Notification Delay",
			description = "The notification delay after the player is idle",
			position = 2,
			section = animationNotificationsSection
	)
	@Units(Units.MILLISECONDS)
	default int getIdleNotificationDelay()
	{
		return 2500;
	}
	@ConfigItem(
			keyName = "xpnotifications",
			name = "XP Notifications",
			description = "Configures if Experience notifications are enabled",
			position = 0,
			section = experienceNotificationsSection
	)
	default boolean experience()
	{
		return true;
	}
	@ConfigItem(
			keyName = "xpnotificationsdelay",
			name = "XP Notification Delay",
			description = "The notification delay after the player has not gained experience.",
			position = 1,
			section = experienceNotificationsSection
	)
	@Units(Units.MILLISECONDS)
	default int getExperienceNotificationDelay()
	{
		return 2500;
	}

	@ConfigItem(
			keyName = "hitpoints",
			name = "Hitpoints Threshold",
			description = "The amount of hitpoints to send a notification at. A value of 0 will disable notification.",
			position = 0,
			section = combatNotificationsSection
	)
	default int getHitpointsThreshold()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "prayer",
			name = "Prayer Threshold",
			description = "The amount of prayer points to send a notification at. A value of 0 will disable notification.",
			position = 1,
			section = combatNotificationsSection
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
