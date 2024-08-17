package com.soundalerts;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.soundalerts.config.SoundMode;
import com.soundalerts.config.SoundPlayer;
import com.soundalerts.config.Sounds;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.api.AnimationID.*;


import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import static net.runelite.api.AnimationID.IDLE;

@Slf4j
@PluginDescriptor(
		name = "Sound Alerts",
		description = "Send a Sound Alert when going idle, or when HP/Prayer reaches a threshold",
		tags = {"health", "hitpoints", "notifications", "prayer"}
)

public class SoundAlertsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SoundAlertsConfig config;

	@Inject
	private SoundPlayer soundPlayer;

	private Instant lastAnimating;
	private int lastAnimation = IDLE;

	int delay = 0;
	private final Map<Skill, Integer> last_xp = new EnumMap<>(Skill.class);
	private Skill lastSkill = null;
	private int last_experience;
	private long time;
	boolean notify_idle;

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
	public void onAnimationChanged(AnimationChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer != event.getActor())
		{
			return;
		}

		int graphic = localPlayer.getGraphic();
		int animation = localPlayer.getAnimation();
		switch (animation)
		{
			/* Woodcutting */
			case WOODCUTTING_BRONZE:
			case WOODCUTTING_IRON:
			case WOODCUTTING_STEEL:
			case WOODCUTTING_BLACK:
			case WOODCUTTING_MITHRIL:
			case WOODCUTTING_ADAMANT:
			case WOODCUTTING_RUNE:
			case WOODCUTTING_GILDED:
			case WOODCUTTING_DRAGON:
			case WOODCUTTING_DRAGON_OR:
			case WOODCUTTING_INFERNAL:
			case WOODCUTTING_3A_AXE:
			case WOODCUTTING_CRYSTAL:
			case WOODCUTTING_TRAILBLAZER:
			case WOODCUTTING_2H_BRONZE:
			case WOODCUTTING_2H_IRON:
			case WOODCUTTING_2H_STEEL:
			case WOODCUTTING_2H_BLACK:
			case WOODCUTTING_2H_MITHRIL:
			case WOODCUTTING_2H_ADAMANT:
			case WOODCUTTING_2H_RUNE:
			case WOODCUTTING_2H_DRAGON:
			case WOODCUTTING_2H_CRYSTAL:
			case WOODCUTTING_2H_CRYSTAL_INACTIVE:
			case WOODCUTTING_2H_3A:
				/* Woodcutting: Ents & Canoes */
			case WOODCUTTING_ENT_BRONZE:
			case WOODCUTTING_ENT_IRON:
			case WOODCUTTING_ENT_STEEL:
			case WOODCUTTING_ENT_BLACK:
			case WOODCUTTING_ENT_MITHRIL:
			case WOODCUTTING_ENT_ADAMANT:
			case WOODCUTTING_ENT_RUNE:
			case WOODCUTTING_ENT_GILDED:
			case WOODCUTTING_ENT_DRAGON:
			case WOODCUTTING_ENT_DRAGON_OR:
			case WOODCUTTING_ENT_INFERNAL:
			case WOODCUTTING_ENT_INFERNAL_OR:
			case WOODCUTTING_ENT_3A:
			case WOODCUTTING_ENT_CRYSTAL:
			case WOODCUTTING_ENT_CRYSTAL_INACTIVE:
			case WOODCUTTING_ENT_TRAILBLAZER:
			case WOODCUTTING_ENT_2H_BRONZE:
			case WOODCUTTING_ENT_2H_IRON:
			case WOODCUTTING_ENT_2H_STEEL:
			case WOODCUTTING_ENT_2H_BLACK:
			case WOODCUTTING_ENT_2H_MITHRIL:
			case WOODCUTTING_ENT_2H_ADAMANT:
			case WOODCUTTING_ENT_2H_RUNE:
			case WOODCUTTING_ENT_2H_DRAGON:
			case WOODCUTTING_ENT_2H_CRYSTAL:
			case WOODCUTTING_ENT_2H_CRYSTAL_INACTIVE:
			case WOODCUTTING_ENT_2H_3A:
			case BLISTERWOOD_JUMP_SCARE:
				/* Firemaking */
			case FIREMAKING_FORESTERS_CAMPFIRE_ARCTIC_PINE:
			case FIREMAKING_FORESTERS_CAMPFIRE_BLISTERWOOD:
			case FIREMAKING_FORESTERS_CAMPFIRE_LOGS:
			case FIREMAKING_FORESTERS_CAMPFIRE_MAGIC:
			case FIREMAKING_FORESTERS_CAMPFIRE_MAHOGANY:
			case FIREMAKING_FORESTERS_CAMPFIRE_MAPLE:
			case FIREMAKING_FORESTERS_CAMPFIRE_OAK:
			case FIREMAKING_FORESTERS_CAMPFIRE_REDWOOD:
			case FIREMAKING_FORESTERS_CAMPFIRE_TEAK:
			case FIREMAKING_FORESTERS_CAMPFIRE_WILLOW:
			case FIREMAKING_FORESTERS_CAMPFIRE_YEW:
				/* Cooking(Fire, Range) */
			case COOKING_FIRE:
			case COOKING_RANGE:
			case COOKING_WINE:
				/* Crafting(Gem Cutting, Glassblowing, Spinning, Weaving, Battlestaves, Pottery) */
			case GEM_CUTTING_OPAL:
			case GEM_CUTTING_JADE:
			case GEM_CUTTING_REDTOPAZ:
			case GEM_CUTTING_SAPPHIRE:
			case GEM_CUTTING_EMERALD:
			case GEM_CUTTING_RUBY:
			case GEM_CUTTING_DIAMOND:
			case 885: // Dragonstone Gems
			case GEM_CUTTING_AMETHYST:
			case CRAFTING_GLASSBLOWING:
			case CRAFTING_SPINNING:
			case CRAFTING_LOOM:
			case CRAFTING_BATTLESTAVES:
			case CRAFTING_LEATHER:
			case CRAFTING_POTTERS_WHEEL:
			case CRAFTING_POTTERY_OVEN:
				/* Fletching(Cutting, Stringing, Adding feathers and heads) */
			case FLETCHING_BOW_CUTTING:
			case FLETCHING_STRING_NORMAL_SHORTBOW:
			case FLETCHING_STRING_OAK_SHORTBOW:
			case FLETCHING_STRING_WILLOW_SHORTBOW:
			case FLETCHING_STRING_MAPLE_SHORTBOW:
			case FLETCHING_STRING_YEW_SHORTBOW:
			case FLETCHING_STRING_MAGIC_SHORTBOW:
			case FLETCHING_STRING_NORMAL_LONGBOW:
			case FLETCHING_STRING_OAK_LONGBOW:
			case FLETCHING_STRING_WILLOW_LONGBOW:
			case FLETCHING_STRING_MAPLE_LONGBOW:
			case FLETCHING_STRING_YEW_LONGBOW:
			case FLETCHING_STRING_MAGIC_LONGBOW:
			case FLETCHING_ATTACH_FEATHERS_TO_ARROWSHAFT:
			case FLETCHING_ATTACH_HEADS:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_BRONZE_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_IRON_BROAD_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_BLURITE_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_STEEL_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_MITHRIL_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_ADAMANT_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_RUNE_BOLT:
			case FLETCHING_ATTACH_BOLT_TIPS_TO_DRAGON_BOLT:
				/* Smithing(Anvil, Furnace, Cannonballs */
			case SMITHING_ANVIL:
			case SMITHING_IMCANDO_HAMMER:
			case SMITHING_SMELTING:
			case 3243://Smelting Jewellery
			case SMITHING_CANNONBALL:
				/* Fishing */
			case FISHING_CRUSHING_INFERNAL_EELS:
			case FISHING_CRUSHING_INFERNAL_EELS_IMCANDO_HAMMER:
			case FISHING_CUTTING_SACRED_EELS:
			case FISHING_BIG_NET:
			case FISHING_NET:
			case FISHING_POLE_CAST:
			case FISHING_CAGE:
			case FISHING_HARPOON:
			case FISHING_BARBTAIL_HARPOON:
			case FISHING_DRAGON_HARPOON:
			case FISHING_DRAGON_HARPOON_OR:
			case FISHING_INFERNAL_HARPOON:
			case FISHING_CRYSTAL_HARPOON:
			case FISHING_TRAILBLAZER_HARPOON:
			case FISHING_OILY_ROD:
			case FISHING_KARAMBWAN:
			case FISHING_BAREHAND:
			case FISHING_PEARL_ROD:
			case FISHING_PEARL_FLY_ROD:
			case FISHING_PEARL_BARBARIAN_ROD:
			case FISHING_PEARL_ROD_2:
			case FISHING_PEARL_FLY_ROD_2:
			case FISHING_PEARL_BARBARIAN_ROD_2:
			case FISHING_PEARL_OILY_ROD:
			case FISHING_BARBARIAN_ROD:
				/* Mining(Normal) */
			case MINING_BRONZE_PICKAXE:
			case MINING_IRON_PICKAXE:
			case MINING_STEEL_PICKAXE:
			case MINING_BLACK_PICKAXE:
			case MINING_MITHRIL_PICKAXE:
			case MINING_ADAMANT_PICKAXE:
			case MINING_RUNE_PICKAXE:
			case MINING_GILDED_PICKAXE:
			case MINING_DRAGON_PICKAXE:
			case MINING_DRAGON_PICKAXE_UPGRADED:
			case MINING_DRAGON_PICKAXE_OR:
			case MINING_DRAGON_PICKAXE_OR_TRAILBLAZER:
			case MINING_INFERNAL_PICKAXE:
			case MINING_3A_PICKAXE:
			case MINING_CRYSTAL_PICKAXE:
			case MINING_TRAILBLAZER_PICKAXE:
			case MINING_TRAILBLAZER_PICKAXE_2:
			case MINING_TRAILBLAZER_PICKAXE_3:
			case DENSE_ESSENCE_CHIPPING:
			case DENSE_ESSENCE_CHISELING:
				/* Mining(Motherlode) */
			case MINING_MOTHERLODE_BRONZE:
			case MINING_MOTHERLODE_IRON:
			case MINING_MOTHERLODE_STEEL:
			case MINING_MOTHERLODE_BLACK:
			case MINING_MOTHERLODE_MITHRIL:
			case MINING_MOTHERLODE_ADAMANT:
			case MINING_MOTHERLODE_RUNE:
			case MINING_MOTHERLODE_GILDED:
			case MINING_MOTHERLODE_DRAGON:
			case MINING_MOTHERLODE_DRAGON_UPGRADED:
			case MINING_MOTHERLODE_DRAGON_OR:
			case MINING_MOTHERLODE_DRAGON_OR_TRAILBLAZER:
			case MINING_MOTHERLODE_INFERNAL:
			case MINING_MOTHERLODE_3A:
			case MINING_MOTHERLODE_CRYSTAL:
			case MINING_MOTHERLODE_TRAILBLAZER:
				/* Mining(Crashed Star) */
			case MINING_CRASHEDSTAR_BRONZE:
			case MINING_CRASHEDSTAR_IRON:
			case MINING_CRASHEDSTAR_STEEL:
			case MINING_CRASHEDSTAR_BLACK:
			case MINING_CRASHEDSTAR_MITHRIL:
			case MINING_CRASHEDSTAR_ADAMANT:
			case MINING_CRASHEDSTAR_RUNE:
			case MINING_CRASHEDSTAR_GILDED:
			case MINING_CRASHEDSTAR_DRAGON:
			case MINING_CRASHEDSTAR_DRAGON_UPGRADED:
			case MINING_CRASHEDSTAR_DRAGON_OR:
			case MINING_CRASHEDSTAR_DRAGON_OR_TRAILBLAZER:
			case MINING_CRASHEDSTAR_INFERNAL:
			case MINING_CRASHEDSTAR_3A:
			case MINING_CRASHEDSTAR_CRYSTAL:
				/* Herblore */
			case HERBLORE_PESTLE_AND_MORTAR:
			case HERBLORE_POTIONMAKING:
			case HERBLORE_MAKE_TAR:
				/* Magic */
			case MAGIC_CHARGING_ORBS:
			case MAGIC_LUNAR_PLANK_MAKE:
			case MAGIC_LUNAR_STRING_JEWELRY:
			case MAGIC_MAKE_TABLET:
			case MAGIC_ENCHANTING_JEWELRY:
			case MAGIC_ENCHANTING_AMULET_1:
			case MAGIC_ENCHANTING_AMULET_2:
			case MAGIC_ENCHANTING_AMULET_3:
			case MAGIC_ENCHANTING_BOLTS:
				/* Prayer */
			case USING_GILDED_ALTAR:
			case ECTOFUNTUS_FILL_SLIME_BUCKET:
			case ECTOFUNTUS_INSERT_BONES:
			case ECTOFUNTUS_GRIND_BONES:
			case ECTOFUNTUS_EMPTY_BIN:
				/* Farming */
			case FARMING_MIX_ULTRACOMPOST:
			case FARMING_HARVEST_BUSH:
			case FARMING_HARVEST_HERB:
			case FARMING_HARVEST_FRUIT_TREE:
			case FARMING_HARVEST_FLOWER:
			case FARMING_HARVEST_ALLOTMENT:
				/* Misc */
			case PISCARILIUS_CRANE_REPAIR:
			case HOME_MAKE_TABLET:
			case SAND_COLLECTION:
			case MILKING_COW:
			case CHURN_MILK_SHORT:
			case CHURN_MILK_MEDIUM:
			case CHURN_MILK_LONG:
			case CLEANING_SPECIMENS_1:
			case CLEANING_SPECIMENS_2:
			case LOOKING_INTO:
				resetTimers();
				lastAnimation = animation;
				lastAnimating = Instant.now();
				break;
			case MAGIC_LUNAR_SHARED:
				if (graphic == GraphicID.BAKE_PIE)
				{
					resetTimers();
					lastAnimation = animation;
					lastAnimating = Instant.now();
					break;
				}
			case IDLE:
				lastAnimating = Instant.now();
				break;
			default:
				// On unknown animation simply assume the animation is invalid and dont throw notification
				lastAnimation = IDLE;
				lastAnimating = null;
		}
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		final Player local = client.getLocalPlayer();
		final Duration waitDuration = Duration.ofMillis(config.getIdleNotificationDelay());
		long diff = (currentTimeMillis() - time);

		/* Delay for only notifying if health or prayer is under x amount every 5 seconds or so. */
		if (delay == 8)
			delay = 0;
		/* Hitpoints Alert */
		if (config.getHitpointsThreshold() > 0) {
			if (client.getBoostedSkillLevel(Skill.HITPOINTS) + client.getVarbitValue(Varbits.NMZ_ABSORPTION) <= config.getHitpointsThreshold() && delay == 0)
				playSoundClip(config.audioMode() == SoundMode.Female ? Sounds.SoundFiles.HEALTH_FEMALE.getPath() : Sounds.SoundFiles.HEALTH_MALE.getPath());
		}
		/* Prayer Alert */
		if (config.getPrayerThreshold() > 0) {
			if (client.getBoostedSkillLevel(Skill.PRAYER) <= config.getPrayerThreshold() && delay == 0)
				playSoundClip(config.audioMode() == SoundMode.Female ? Sounds.SoundFiles.PRAYER_FEMALE.getPath() : Sounds.SoundFiles.PRAYER_MALE.getPath());
		}

		/* Idle Animation Alert */
		if (checkAnimationIdle(waitDuration, local) && config.animationIdle())
		{
			playSoundClip(config.audioMode() == SoundMode.Female ? Sounds.SoundFiles.IDLE_FEMALE.getPath() : Sounds.SoundFiles.IDLE_MALE.getPath());
		}

		/* Xp Drop Alert */
		if ((diff > config.getExperienceNotificationDelay() && !notify_idle) && config.experience()) {
			playSoundClip(config.audioMode() == SoundMode.Female ? Sounds.SoundFiles.IDLE_FEMALE.getPath() : Sounds.SoundFiles.IDLE_MALE.getPath());
			notify_idle = true;
		}

		delay++;
	}

	@Subscribe
	public void onStatChanged(StatChanged stat)
	{
		final Skill skill = stat.getSkill();
		final int xp = stat.getXp();

		lastSkill = skill;

		Integer previous = last_xp.put(skill, xp);
		if (previous != null)
		{
			last_experience = xp - previous;
			if (last_experience != 0 && (config.getExperienceThreshold() < last_experience)) {
				time = currentTimeMillis();
				notify_idle = false;
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		GameState state = gameStateChanged.getGameState();

		switch (state)
		{
			case LOGIN_SCREEN:
				resetTimers();
				break;
		}
	}

	private void playSoundClip(String soundFile)
	{
		if (config.audioMode() == SoundMode.Disabled)
			return;
		soundPlayer.playSoundClip(soundFile);
	}

	private boolean checkAnimationIdle(Duration waitDuration, Player local)
	{
		if (lastAnimation == IDLE)
		{
			return false;
		}

		final int animation = local.getAnimation();

		if (animation == IDLE)
		{
			if (lastAnimating != null && Instant.now().compareTo(lastAnimating.plus(waitDuration)) >= 0)
			{
				lastAnimation = IDLE;
				lastAnimating = null;
				return true;
			}
		}
		else
		{
			lastAnimating = Instant.now();
		}
		return false;
	}

	private void resetTimers()
	{
		final Player local = client.getLocalPlayer();

		// Reset animation idle timer
		lastAnimating = null;
		if (client.getGameState() == GameState.LOGIN_SCREEN || local == null || local.getAnimation() != lastAnimation)
		{
			lastAnimation = IDLE;
		}
	}

	private static long timeCorrection;
	private static long lastTimeUpdate;
	public static synchronized long currentTimeMillis() {
		long l = System.currentTimeMillis();
		if (l < lastTimeUpdate)
			timeCorrection += lastTimeUpdate - l;
		lastTimeUpdate = l;
		return l + timeCorrection;
	}

	@Provides
	SoundAlertsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoundAlertsConfig.class);
	}
}
