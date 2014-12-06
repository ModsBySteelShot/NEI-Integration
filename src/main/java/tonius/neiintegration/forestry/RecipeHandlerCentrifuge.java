package tonius.neiintegration.forestry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import tonius.neiintegration.PositionedStackAdv;
import tonius.neiintegration.RecipeHandlerBase;
import tonius.neiintegration.Utils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import forestry.factory.gadgets.MachineCentrifuge;

public class RecipeHandlerCentrifuge extends RecipeHandlerBase {
    
    private static final int[][] OUTPUTS = new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 } };
    private static Class<? extends GuiContainer> guiClass;
    
    @Override
    public void prepare() {
        guiClass = Utils.getClass("forestry.factory.gui.GuiCentrifuge");
    }
    
    public class CachedCentrifugeRecipe extends CachedBaseRecipe {
        
        public PositionedStack inputs;
        public List<PositionedStack> outputs = new ArrayList<PositionedStack>();
        
        public CachedCentrifugeRecipe(MachineCentrifuge.Recipe recipe, boolean genPerms) {
            if (recipe.resource != null) {
                this.inputs = new PositionedStack(recipe.resource, 29, 26);
            }
            if (recipe.products != null) {
                this.setResults(recipe.products);
            }
        }
        
        public CachedCentrifugeRecipe(MachineCentrifuge.Recipe recipe) {
            this(recipe, false);
        }
        
        public void setResults(Map<ItemStack, Integer> outputs) {
            int i = 0;
            for (Entry<ItemStack, Integer> stack : outputs.entrySet()) {
                if (i >= OUTPUTS.length) {
                    return;
                }
                PositionedStackAdv output = new PositionedStackAdv(stack.getKey(), 93 + OUTPUTS[i][0] * 18, 8 + OUTPUTS[i][1] * 18);
                output.setChance(stack.getValue() / 100F);
                this.outputs.add(output);
                i++;
            }
        }
        
        @Override
        public PositionedStack getIngredient() {
            this.randomRenderPermutation(this.inputs, RecipeHandlerCentrifuge.this.cycleticks / 20);
            return this.inputs;
        }
        
        @Override
        public List<PositionedStack> getOtherStacks() {
            return this.outputs;
        }
        
        @Override
        public PositionedStack getResult() {
            return null;
        }
        
    }
    
    @Override
    public String getRecipeID() {
        return "forestry.centrifuge";
    }
    
    @Override
    public String getRecipeName() {
        return Utils.translate("tile.for.factory.2.name", false);
    }
    
    @Override
    public String getGuiTexture() {
        return "forestry:textures/gui/centrifuge.png";
    }
    
    @Override
    public void loadTransferRects() {
        this.addTransferRect(57, 26, 4, 18);
    }
    
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return guiClass;
    }
    
    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(57, 25, 176, 0, 4, 17, 80, 3);
    }
    
    @Override
    public void loadAllRecipes() {
        for (MachineCentrifuge.Recipe recipe : MachineCentrifuge.RecipeManager.recipes) {
            this.arecipes.add(new CachedCentrifugeRecipe(recipe, true));
        }
    }
    
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (MachineCentrifuge.Recipe recipe : MachineCentrifuge.RecipeManager.recipes) {
            CachedCentrifugeRecipe crecipe = new CachedCentrifugeRecipe(recipe);
            if (crecipe.outputs != null && crecipe.contains(crecipe.outputs, result)) {
                crecipe.setIngredientPermutation(crecipe.outputs, result);
                this.arecipes.add(crecipe);
            }
        }
    }
    
    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        super.loadCraftingRecipes(ingred);
        for (MachineCentrifuge.Recipe recipe : MachineCentrifuge.RecipeManager.recipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.resource, ingred)) {
                this.arecipes.add(new CachedCentrifugeRecipe(recipe, true));
            }
        }
    }
    
}
