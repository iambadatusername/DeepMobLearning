package xt9.deepmoblearning.common.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import xt9.deepmoblearning.DeepConstants;
import xt9.deepmoblearning.api.items.ExperienceItem;
import xt9.deepmoblearning.common.util.KeyboardHelper;
import xt9.deepmoblearning.common.util.ItemStackNBTHelper;

import java.util.List;

/**
 * Created by xt9 on 2017-06-10.
 */
public class ItemMobChip extends ItemBase {
    public ItemMobChip() {
        super("mob_chip", 1, "default", "zombie", "skeleton", "blaze", "enderman", "wither", "witch", "spider", "creeper", "ghast", "witherskeleton");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
        if(stack.getItemDamage() != 0) {
            if(!KeyboardHelper.isHoldingShift()) {
                list.add(I18n.format("deepmoblearning.holdshift"));
            } else {
                list.add(I18n.format("deepmoblearning.mob_chip.tier", getTierName(stack, false)));
            }
        }
    }

    public static String getSubName(ItemStack stack) {
        Item item = stack.getItem();
        return ((ItemMobChip) item).getSubNames()[stack.getItemDamage()];
    }

    public static NonNullList<ItemStack> getValidFromList(NonNullList<ItemStack> list) {
        NonNullList<ItemStack> filteredList = NonNullList.create();

        for(ItemStack stack : list) {
            Item item = stack.getItem();

            if(item instanceof ItemMobChip) {
                String subName = ((ItemMobChip) item).getSubNames()[stack.getItemDamage()];
                if(!subName.equals("default")) {
                    filteredList.add(stack);
                }
            }
        }

        return filteredList;
    }

    public static String toHumdanReadable(ItemStack stack) {
        switch(getSubName(stack)) {
            case "zombie": return "Zombie";
            case "skeleton": return "Skeleton";
            case "blaze": return "Blaze";
            case "enderman": return "Enderman";
            case "wither": return "Wither";
            case "witch": return "Witch";
            case "spider": return "Spider";
            case "creeper": return "Creeper";
            case "ghast": return "Ghast";
            case "witherskeleton": return "Wither Skeleton";
            default: return "Default";
        }
    }

    public static String toHumdanReadablePlural(ItemStack stack) {
        switch(getSubName(stack)) {
            case "zombie": return "Zombies";
            case "skeleton": return "Skeletons";
            case "blaze": return "Blazes";
            case "enderman": return "Endermen";
            case "wither": return "Withers";
            case "witch": return "Witches";
            case "spider": return "Spiders";
            case "creeper": return "Creepers";
            case "ghast": return "Ghasts";
            case "witherskeleton": return "Wither Skeletons";
            default: return "Default";
        }
    }

    public static int getSuccessChance(ItemStack stack) {
        // Todo CONFIGURABLE
        switch(getTier(stack)) {
            case 0: return 0;
            case 1: return 20;
            case 2: return 40;
            case 3: return 65;
            case 4: return 100;
            default: return 0;
        }
    }

    public static String getTierName(ItemStack stack, boolean getNextTierName) {
        int addTiers = getNextTierName ? 1 : 0;
        switch(getTier(stack) + addTiers) {
            case 0: return "§8Faulty§r";
            case 1: return "§aBasic§r";
            case 2: return "§9Advanced§r";
            case 3: return "§dSuperior§r";
            case 4: return "§6Self Aware§r";
            default: return "§8Faulty§r";
        }
    }

    // Called by deep learners
    public static void increaseMobKillCount(ItemStack stack, EntityPlayer player) {
        // Get our current tier before increasing the kill count;
        int tier = getTier(stack);
        int i = getCurrentTierKillCount(stack);
        i = i + 1;
        setCurrentTierKillCount(stack, i);

        // Update the totals
        setTotalKillCount(stack, getTotalKillCount(stack) + 1);

        if(ExperienceItem.shouldIncreaseTier(tier, i, getCurrentTierSimulationCount(stack))) {
            player.sendMessage(new TextComponentString(stack.getDisplayName() + " reached the " + getTierName(stack, true) + " tier"));

            setCurrentTierKillCount(stack, 0);
            setCurrentTierSimulationCount(stack, 0);
            setTier(stack, tier + 1);
        }
    }

    // Called by machines
    public static void increaseSimulationCount(ItemStack stack) {
        int tier = getTier(stack);
        int i = getCurrentTierSimulationCount(stack);
        i = i + 1;
        setCurrentTierSimulationCount(stack, i);

        // Update the totals
        setTotalSimulationCount(stack, getTotalSimulationCount(stack) + 1);

        if(ExperienceItem.shouldIncreaseTier(tier, getCurrentTierKillCount(stack), i)) {
            setCurrentTierKillCount(stack, 0);
            setCurrentTierSimulationCount(stack, 0);
            setTier(stack, tier + 1);
        }
    }




    public static int getTier(ItemStack stack) {
        return ItemStackNBTHelper.getInt(stack, "tier", 0);
    }

    public static void setTier(ItemStack stack, int tier) {
        ItemStackNBTHelper.setInt(stack, "tier", tier);
    }

    public static int getCurrentTierKillCount(ItemStack stack) {
        return ItemStackNBTHelper.getInt(stack, "killCount", 0);
    }

    public static void setCurrentTierKillCount(ItemStack stack, int count) {
        ItemStackNBTHelper.setInt(stack, "killCount", count);
    }

    public static int getCurrentTierSimulationCount(ItemStack stack) {
        return ItemStackNBTHelper.getInt(stack, "simulationCount", 0);
    }

    public static void setCurrentTierSimulationCount(ItemStack stack, int count) {
        ItemStackNBTHelper.setInt(stack, "simulationCount", count);
    }

    public static int getTotalKillCount(ItemStack stack) {
        return ItemStackNBTHelper.getInt(stack, "totalKillCount", 0);
    }

    public static void setTotalKillCount(ItemStack stack, int count) {
        ItemStackNBTHelper.setInt(stack, "totalKillCount", count);
    }

    public static int getTotalSimulationCount(ItemStack stack) {
        return ItemStackNBTHelper.getInt(stack, "totalSimulationCount", 0);
    }

    public static void setTotalSimulationCount(ItemStack stack, int count) {
        ItemStackNBTHelper.setInt(stack, "totalSimulationCount", count);
    }


    /*
     The functions below are not NBT getters/setters
    */
    public static double getCurrentTierKillCountWithSims(ItemStack stack) {
        return ExperienceItem.getCurrentTierKillCountWithSims(getTier(stack), getCurrentTierKillCount(stack), getCurrentTierSimulationCount(stack));
    }

    public static int getCurrentTierSimulationCountWithKills(ItemStack stack) {
        return ExperienceItem.getCurrentTierSimulationCountWithKills(getTier(stack), getCurrentTierKillCount(stack), getCurrentTierSimulationCount(stack));
    }

    /* This is not the same as getCurrentTierKillCount before kills to  */
    public static double getKillsToNextTier(ItemStack stack) {
        return ExperienceItem.getKillsToNextTier(getTier(stack), getCurrentTierKillCount(stack), getCurrentTierSimulationCount(stack));
    }

    public static int getSimulationsToNextTier(ItemStack stack) {
        return ExperienceItem.getSimulationsToNextTier(getTier(stack), getCurrentTierKillCount(stack), getCurrentTierSimulationCount(stack));
    }

    public static int getTierRoofAsKills(ItemStack stack) {
        if(getTier(stack) == DeepConstants.MOB_CHIP_MAXIMUM_TIER) {
            return 0;
        }
        return ExperienceItem.getTierRoof(getTier(stack), true);
    }

    public static int getTierRoof(ItemStack stack) {
        if(getTier(stack) == DeepConstants.MOB_CHIP_MAXIMUM_TIER) {
            return 0;
        }
        return ExperienceItem.getTierRoof(getTier(stack), false);
    }


    public static boolean entityLivingMatchesType(EntityLivingBase entityLiving, ItemStack stack) {
        String subName = getSubName(stack);

        return (entityLiving instanceof EntityZombie && subName.equals("zombie")) ||
                (entityLiving instanceof EntitySkeleton && subName.equals("skeleton")) ||
                (entityLiving instanceof EntityBlaze && subName.equals("blaze")) ||
                (entityLiving instanceof EntityEnderman && subName.equals("enderman")) ||
                (entityLiving instanceof EntityWither && subName.equals("wither")) ||
                (entityLiving instanceof EntityWitch && subName.equals("witch")) ||
                (entityLiving instanceof EntitySpider && subName.equals("spider")) ||
                (entityLiving instanceof EntityCreeper && subName.equals("creeper")) ||
                (entityLiving instanceof EntityGhast && subName.equals("ghast")) ||
                (entityLiving instanceof EntityWitherSkeleton && subName.equals("witherskeleton"));
    }

}
