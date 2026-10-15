package com.ccx87.annihilator_type_a.datagen;




import com.ccx87.annihilator_type_a.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;


public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output,MOD_ID, existingFileHelper);
    }
    @Override
    protected void registerModels() {

        withExistingParent(ModItems.MINIATURE_UAV_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.GN001_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
    }
}

