package xland.mcmod.enchlevellangpatch.config;

import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Map;

public class LangPatchModMenuCompat implements ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of("enchlevel-langpatch", parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setTitle(new TranslatableText("title.enchlevel-langpatch.config"))
                    .setParentScreen(parent);
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory category = builder.getOrCreateCategory(
                    new TranslatableText("title.enchlevel-langpatch.config.algorithms"));
            Identifier enchantmentId = LangPatchConfig.getInstance().getEnchantmentId();
            Identifier potionId = LangPatchConfig.getInstance().getPotionId();

            DropdownMenuBuilder<Identifier> dropdown1 = entryBuilder.startDropdownMenu(
                    new TranslatableText("enchlevel-langpatch.config.enchantment.registry"),
                    DropdownMenuBuilder.TopCellElementBuilder.of(enchantmentId, s -> {
                        Identifier id = Identifier.tryParse(s);
                        return LangPatchConfig.ENCHANTMENT_REGISTRY.containsId(id) ? id : null;
                    }),
                    DropdownMenuBuilder.CellCreatorBuilder.of(id -> Utils.translate(id, true))
            );
            dropdown1.setDefaultValue(DEFAULT_VALUE).setSelections(LangPatchConfig.ENCHANTMENT_REGISTRY.getIds())
                            .setSaveConsumer(LangPatchConfig.getInstance()::setEnchantmentId);

            DropdownMenuBuilder<Identifier> dropdown2 = entryBuilder.startDropdownMenu(
                    new TranslatableText("enchlevel-langpatch.config.potion.registry"),
                    DropdownMenuBuilder.TopCellElementBuilder.of(potionId, s -> {
                        Identifier id = Identifier.tryParse(s);
                        return LangPatchConfig.POTION_REGISTRY.containsId(id) ? id : null;
                    }),
                    DropdownMenuBuilder.CellCreatorBuilder.of(id -> Utils.translate(id, false))
            );
            dropdown2.setDefaultValue(DEFAULT_VALUE).setSelections(LangPatchConfig.POTION_REGISTRY.getIds())
                            .setSaveConsumer(LangPatchConfig.getInstance()::setPotionId);

            category.addEntry(dropdown1.build()).addEntry(dropdown2.build());

            return builder.build();
        });
    }

    private static final Identifier DEFAULT_VALUE = new Identifier("enchlevel-langpatch:default");
}
