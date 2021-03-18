package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ru.penekgaming.mc.povertycharm.Config;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.handler.GuiHandler;
import ru.penekgaming.mc.povertycharm.item.ItemBlockVariative;
import ru.penekgaming.mc.povertycharm.item.ItemMouthpiece;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;
import ru.penekgaming.mc.povertycharm.util.MetaSet;

import java.util.HashMap;
import java.util.Random;

public class BlockHookah extends TileEntityBlock<TileEntityHookah> implements IBlockVariative {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public static final PropertyInteger COALS = PropertyInteger.create("coals", 0, 3);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    //public static final AxisAlignedBB BB = new AxisAlignedBB();
    public static final MetaSet META = new MetaSet(2, 2);

    public BlockHookah() {
        super("hookah", Material.GLASS, TileEntityHookah.class);

        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(VARIANT, Variant.RED)
                .withProperty(COALS, 0)
                .withProperty(ACTIVE, false)
        );

        item = new ItemBlockVariative(this);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!(stack.getItem() instanceof ItemMouthpiece)) {
            if (!worldIn.isRemote) {
                playerIn.openGui(PovertyCharm.INSTANCE, GuiHandler.Id.HOOKAH.getId(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }

        TileEntityHookah tileEntity = getTileEntity(worldIn, pos);

        if (!stack.isItemDamaged() || !tileEntity.isActive())
            return false;

        stack.setItemDamage(0);
        tileEntity.useCharge();

        if (!worldIn.isRemote) {
            tileEntity.getEffectList().stream()
                    .filter(potionEffect -> !potionEffect.getPotion().isInstant())
                    .forEach(potionEffect -> playerIn.addPotionEffect(new PotionEffect(potionEffect)));

            HashMap<Potion, Integer> instantPotions = new HashMap<>();

            tileEntity.getEffectList().stream()
                    .filter(potionEffect -> potionEffect.getPotion().isInstant())
                    .forEach(potionEffect -> {
                        Potion potion = potionEffect.getPotion();
                        if (!instantPotions.containsKey(potion))
                            instantPotions.put(potion, potionEffect.getAmplifier());
                        else
                            instantPotions.put(potion, instantPotions.get(potion) + potionEffect.getAmplifier());
                    });

            instantPotions.forEach((potion, ampl) -> potion.affectEntity(null, null, playerIn, ampl, 1.0D));

            int nauseaPoints = 0;
            for (PotionEffect potionEffect : tileEntity.getEffectList()) {
                nauseaPoints += 1 + potionEffect.getAmplifier();
            }

            playerIn.addPotionEffect(
                    new PotionEffect(
                            MobEffects.NAUSEA,
                            Math.max(Config.Hookah.nauseaTicksPerPotionMin, nauseaPoints * Config.Hookah.nauseaTicksPerPotion),
                            0)
            );
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityHookah tile = getTileEntity(world, pos);
        IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

        for (int i = 0; i < (itemHandler != null ? itemHandler.getSlots() : 0); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                world.spawnEntity(item);
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityHookah tileEntity = getTileEntity(world, pos);
        boolean hookahActive = tileEntity.isActive();

        return state
                .withProperty(COALS, hookahActive ? 0 : tileEntity.getCoalCount())
                .withProperty(ACTIVE, hookahActive);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {


        return getDefaultState()
                .withProperty(FACING, placer.getHorizontalFacing())
                .withProperty(VARIANT, Variant.values()[META.fromMeta(meta)[0]]);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Variant value : Variant.values()) {
            items.add(new ItemStack(this, 1, META.toMeta(value.getMetadata(), 0)));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int[] values = META.fromMeta(meta);

        return getDefaultState()
                .withProperty(FACING, EnumFacing.byHorizontalIndex(values[1]))
                .withProperty(VARIANT, Variant.values()[values[0]]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int iFacing = state.getValue(FACING).getHorizontalIndex();
        int iVariant = state.getValue(VARIANT).getMetadata();

        return META.toMeta(iVariant, iFacing);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return META.toMeta(state.getValue(VARIANT).getMetadata(), 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, VARIANT, COALS, ACTIVE);
    }

    @Override
    public String getVariationName(int meta) {
        return Variant.values()[META.fromMeta(meta)[0]].getName();
    }

    @Override
    public int getVariationCount() {
        return Variant.values().length;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntityHookah createTileEntity(World world, IBlockState blockState) {
        return new TileEntityHookah();
    }

    public enum Variant implements IBlockVariants {
        RED(0, "red"),
        GREEN(1, "green"),
        BLUE(2, "blue");

        private final String name;
        private final int meta;

        Variant(int meta, String name) {
            this.name = name;
            this.meta = meta;
        }

        @Override
        public int getMetadata() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
