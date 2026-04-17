package net.dingletherat.block.custom;

import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

import com.mojang.serialization.MapCodec;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.DoubloonCompressorEntity;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.item.custom.Wallet;
import net.dingletherat.state.ShopState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class DoubloonCompressor extends BaseEntityBlock {
    public static final MapCodec<DoubloonCompressor> CODEC = DoubloonCompressor.simpleCodec(DoubloonCompressor::new);

    public DoubloonCompressor(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any());
    }

    @Override
    public MapCodec<DoubloonCompressor> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DoubloonCompressorEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MoneyBlockEntities.DOUBLOON_COMPRESSOR_ENTITY, DoubloonCompressorEntity::tick);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide() && placer instanceof Player player) {
            DoubloonCompressorEntity be = (DoubloonCompressorEntity) world.getBlockEntity(pos);
            if (be != null) {
                be.setOwner(player.getUUID());
                Component name = itemStack.getHoverName();

                be.setName(name.getString());

                be.setChanged();
                ShopState.get((world).getServer()).register(player.getUUID(), pos);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof DoubloonCompressorEntity compressor)) {
            return InteractionResult.PASS;
        }

        if (player.getItemInHand(hand).is(MoneyItems.WALLET) && compressor.getItem(0).isEmpty()) {
            compressor.setItem(0, player.getItemInHand(hand).copy());
            int dollars = Wallet.getDollars(player.getItemInHand(hand));
            compressor.setTransactionCoins(Math.round(dollars/10)*10);
            compressor.setWalletOwner(Wallet.getOwner(player.getItemInHand(hand)));
            player.getItemInHand(hand).setCount(0);
            return InteractionResult.SUCCESS;
        }

        if (player.getItemInHand(hand).is(Items.PAPER)) {
            ItemStack receipt = new ItemStack(Items.PAPER);
            receipt.set(DataComponents.CUSTOM_NAME, Component.literal(compressor.getName() + " receipt")
                    .withStyle(style -> style.withItalic(false).withColor(ChatFormatting.AQUA)));
            receipt.set(DataComponents.LORE, new ItemLore(List.of(
                            Component.literal(compressor.getWalletOwner())
                                    .withStyle(style -> style.withItalic(false).withColor(ChatFormatting.BLUE)),
                            Component.literal(compressor.getTransactionCoins() + " dollars")
                                    .withStyle(style -> style.withItalic(false).withColor(ChatFormatting.BLUE)),
                            Component.literal("Day " + Long.toString(level.getGameTime() / 24000))
                                    .withStyle(style -> style.withItalic(false).withColor(ChatFormatting.RED))
            )));
            player.getItemInHand(hand).shrink(1);

            if (!player.getInventory().add(receipt)) {
                player.drop(receipt, false);
            }
        }

        player.openMenu(compressor);

        return InteractionResult.SUCCESS;
    }
}
