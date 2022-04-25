package xland.mcmod.enchlevellangpatch.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatch;
import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatchConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class LangPatchConfig {
	private static final LangPatchConfig INSTANCE = new LangPatchConfig();

	static final Registry<EnchantmentLevelLangPatch>
		POTION_REGISTRY = Utils.getRegistry(true),
		ENCHANTMENT_REGISTRY = Utils.getRegistry(false);

	private static final Gson GSON = new GsonBuilder().setLenient().create();

	private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir()
			.resolve(Paths.get("enchlevel-langpatch", "config-mod.json"));

	private Identifier potionId;
	private Identifier enchantmentId;

	private LangPatchConfig() {
		if (!Files.exists(CONFIG_FILE)) {
			try {
				Files.createDirectories(CONFIG_FILE.getParent());
				Files.createFile(CONFIG_FILE);
			} catch (IOException e) {
				Utils.LOGGER.error("Cannot create config file: " + CONFIG_FILE, e);
			}
			potionId = enchantmentId = new Identifier("enchlevel-langpatch:default");
			writeToJson();
		} else {
			readFromJson();
		}
		EnchantmentLevelLangPatchConfig.setCurrentEnchantmentHooks(ENCHANTMENT_REGISTRY.get(enchantmentId));
		EnchantmentLevelLangPatchConfig.setCurrentPotionHooks(POTION_REGISTRY.get(potionId));
	}

	public Identifier getPotionId() {
		return potionId;
	}

	public void setPotionId(Identifier potionId) {
		this.potionId = potionId;
	}

	public Identifier getEnchantmentId() {
		return enchantmentId;
	}

	public void setEnchantmentId(Identifier enchantmentId) {
		this.enchantmentId = enchantmentId;
	}

	public void readFromJson() {
		JsonObject obj;
		try {
			obj = JsonHelper.deserialize(Files.newBufferedReader(CONFIG_FILE), true);
		} catch (IOException e) {
			Utils.LOGGER.error("Can't read from config file: " + CONFIG_FILE, e);
			return;
		}
		potionId = new Identifier(JsonHelper.getString(obj,
				"potion_id", "enchlevel-langpatch:default"));
		enchantmentId = new Identifier(JsonHelper.getString(obj,
				"enchantment_id", "enchlevel-langpatch:default"));
	}

	public void writeToJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("potion_id", potionId.toString());
		obj.addProperty("enchantment_id", enchantmentId.toString());

		try {
			GSON.toJson(obj, GSON.newJsonWriter(Files.newBufferedWriter(CONFIG_FILE)));
		} catch (IOException e) {
			Utils.LOGGER.error("Failed to write config to file: " + CONFIG_FILE, e);
		}
	}

	public static LangPatchConfig getInstance() {
		return INSTANCE;
	}

	public static void load() {
		// noop
	}
}