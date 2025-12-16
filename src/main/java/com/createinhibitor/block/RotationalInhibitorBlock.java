package com.createinhibitor.block;

import com.createinhibitor.block.entity.RotationalInhibitorBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RotationalInhibitorBlock extends KineticBlock implements IBE<RotationalInhibitorBlockEntity>, IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    
    private static final VoxelShape SHAPE_X = Block.box(0, 4, 4, 16, 12, 12);
    private static final VoxelShape SHAPE_Y = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_Z = Block.box(4, 4, 0, 12, 12, 16);
    
    public RotationalInhibitorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, LIT);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing.getAxis()) {
            case X -> SHAPE_X;
            case Y -> SHAPE_Y;
            case Z -> SHAPE_Z;
        };
    }
    
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }
    
    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }
    
    @Override
    public Class<RotationalInhibitorBlockEntity> getBlockEntityClass() {
        return RotationalInhibitorBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends RotationalInhibitorBlockEntity> getBlockEntityType() {
        return com.createinhibitor.CreateInhibitor.ROTATIONAL_INHIBITOR_BLOCK_ENTITY.get();
    }
    
    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.NONE;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                  InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        
        if (heldItem.is(AllItems.WRENCH.get())) {
            return InteractionResult.PASS;
        }
        
        if (hand == InteractionHand.MAIN_HAND) {
            if (player.isShiftKeyDown()) {
                if (!level.isClientSide) {
                    RotationalInhibitorBlockEntity blockEntity = getBlockEntity(level, pos);
                    if (blockEntity != null) {
                        blockEntity.toggleDebugMode();
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.PASS;
            } else {
                if (!level.isClientSide) {
                    boolean currentlyLit = state.getValue(LIT);
                    
                    if (!currentlyLit) {
                        RotationalInhibitorBlockEntity blockEntity = getBlockEntity(level, pos);
                        if (blockEntity == null || !blockEntity.isActive()) {
                            return InteractionResult.FAIL;
                        }
                    }
                    
                    BlockState newState = state.cycle(LIT);
                    level.setBlock(pos, newState, Block.UPDATE_ALL);
                    level.updateNeighborsAt(pos, this);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }
    
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Direction currentFacing = state.getValue(FACING);
        
        Direction newFacing;
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            newFacing = getPreviousDirection(currentFacing);
        } else {
            newFacing = getNextDirection(currentFacing);
        }
        
        BlockState newState = state.setValue(FACING, newFacing);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        IWrenchable.playRotateSound(level, pos);
        
        return InteractionResult.SUCCESS;
    }
    
    private Direction getNextDirection(Direction current) {
        return switch (current) {
            case DOWN -> Direction.UP;
            case UP -> Direction.NORTH;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.EAST;
            case EAST -> Direction.DOWN;
        };
    }
    
    private Direction getPreviousDirection(Direction current) {
        return switch (current) {
            case DOWN -> Direction.EAST;
            case UP -> Direction.DOWN;
            case NORTH -> Direction.UP;
            case SOUTH -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.WEST;
        };
    }
}

