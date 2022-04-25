package xland.mcmod.enchlevellangpatch.config;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatch;
import xland.mcmod.enchlevellangpatch.impl.LangPatchImpl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

class Utils {
    static final Logger LOGGER = LogManager.getLogger("enchlevel-langpatch-config");
    static final boolean LEGACY;

    static {
        Version modVersion = FabricLoader.getInstance().getModContainer("enchlevel-langpatch")
                .orElseThrow()
                .getMetadata().getVersion();
        Version v030;
        try {
            v030 = Version.parse("0.3.0");
        } catch (VersionParsingException e) {
            LOGGER.error("Invalid version: 0.3.0", e);
            v030 = modVersion;
        }
        LEGACY = !(modVersion instanceof SemanticVersion sem) || sem.compareTo(v030) <= 0;
    }

    /** @param potion true for POTION_HOOK or false for ENCHANTMENT_HOOK */
    @SuppressWarnings("all")
    static Registry<EnchantmentLevelLangPatch> getRegistry(boolean potion) {
        try {
            if (LEGACY) {
                Class<?> clazz = LangPatchImpl.class;
                MethodHandle mh = MethodHandles.lookup().findStaticGetter(clazz,
                        potion ? "POTION_HOOK" : "ENCHANTMENT_HOOK", DefaultedRegistry.class);
                DefaultedRegistry<?> o = (DefaultedRegistry<?>) mh.invokeExact();
                return (Registry<EnchantmentLevelLangPatch>) o;
            }
        } catch (Throwable t) {
            LOGGER.error("Can't get LangPatchImpl." +
                    (potion ? "POTION_HOOK" : "ENCHANTMENT_HOOK"), t);
        }
        return potion ? EnchantmentLevelLangPatch.POTION_HOOK
                : EnchantmentLevelLangPatch.ENCHANTMENT_HOOK;
    }

    static Text translate(Identifier algorithm, boolean isEnchantment) {
        String s = (isEnchantment ? "langpatch.algorithm.enchantment." : "langpatch.algorithm.potion.") +
                algorithm.getNamespace() + '.'
                + algorithm.getPath();
        return new TranslatableText(s);
    }
}
