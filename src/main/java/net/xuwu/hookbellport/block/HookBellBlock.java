package net.xuwu.hookbellport.block;

import net.xuwu.hookbellport.content.HookBellContent;
import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/** A self-contained, manually rung bell that calls nearby raiders. */
public class HookBellBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BellAttachType> ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.box(0, 0, 4, 16, 16, 12);
    private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.box(4, 0, 0, 12, 16, 16);
    private static final VoxelShape BELL_TOP_SHAPE = Block.box(5, 6, 5, 11, 13, 11);
    private static final VoxelShape BELL_BOTTOM_SHAPE = Block.box(4, 4, 4, 12, 6, 12);
    private static final VoxelShape BELL_SHAPE = Shapes.or(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
    private static final VoxelShape NORTH_SOUTH_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(7, 13, 0, 9, 15, 16));
    private static final VoxelShape EAST_WEST_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(0, 13, 7, 16, 15, 9));
    private static final VoxelShape TO_WEST = Shapes.or(BELL_SHAPE, Block.box(0, 13, 7, 13, 15, 9));
    private static final VoxelShape TO_EAST = Shapes.or(BELL_SHAPE, Block.box(3, 13, 7, 16, 15, 9));
    private static final VoxelShape TO_NORTH = Shapes.or(BELL_SHAPE, Block.box(7, 13, 0, 9, 15, 13));
    private static final VoxelShape TO_SOUTH = Shapes.or(BELL_SHAPE, Block.box(7, 13, 3, 9, 15, 16));
    private static final VoxelShape CEILING_SHAPE = Shapes.or(BELL_SHAPE, Block.box(7, 13, 7, 9, 16, 9));

    public HookBellBlock() {
        super(Properties.of().forceSolidOn().mapColor(MapColor.COLOR_GREEN).requiresCorrectToolForDrops()
                .strength(5.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.ANVIL));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(ATTACHMENT, BellAttachType.FLOOR).setValue(POWERED, false));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), 3);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        Entity owner = projectile.getOwner();
        this.onHit(level, state, hit, owner instanceof Player player ? player : null, true);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        return this.onHit(level, state, hit, player, true)
                ? InteractionResult.sidedSuccess(level.isClientSide)
                : InteractionResult.PASS;
    }

    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                             BlockHitResult hit) {
        return this.onHit(level, state, hit, player, true)
                ? InteractionResult.sidedSuccess(level.isClientSide)
                : InteractionResult.PASS;
    }

    public boolean onHit(Level level, BlockState state, BlockHitResult hit, @Nullable Player player, boolean checkHit) {
        Direction side = hit.getDirection();
        BlockPos pos = hit.getBlockPos();
        boolean properHit = !checkHit || isProperHit(state, side, hit.getLocation().y - pos.getY());
        if (properHit && player != null) {
            if (attemptToRing(player, level, pos, side)) {
                player.awardStat(Stats.BELL_RING);
            }
            return true;
        }
        return false;
    }

    private static boolean isProperHit(BlockState state, Direction side, double hitY) {
        if (side.getAxis() == Direction.Axis.Y || hitY > 0.8124F) {
            return false;
        }
        Direction facing = state.getValue(FACING);
        BellAttachType attachment = state.getValue(ATTACHMENT);
        return switch (attachment) {
            case FLOOR -> facing.getAxis() == side.getAxis();
            case SINGLE_WALL, DOUBLE_WALL -> facing.getAxis() != side.getAxis();
            case CEILING -> true;
        };
    }

    public boolean attemptToRing(Level level, BlockPos pos, @Nullable Direction direction) {
        return attemptToRing(null, level, pos, direction);
    }

    public boolean attemptToRing(@Nullable Entity source, Level level, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide && blockEntity instanceof HookBellBlockEntity bell) {
            Direction ringDirection = direction == null ? level.getBlockState(pos).getValue(FACING) : direction;
            bell.onHit(ringDirection);
            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 10.0F, 0.5F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        8, 0.25, 0.25, 0.25, 0.02);
            }
            level.gameEvent(source, GameEvent.BLOCK_CHANGE, pos);
            return true;
        }
        return false;
    }

    private VoxelShape getBellShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        BellAttachType attachment = state.getValue(ATTACHMENT);
        if (attachment == BellAttachType.FLOOR) {
            return facing.getAxis() == Direction.Axis.Z ? NORTH_SOUTH_FLOOR_SHAPE : EAST_WEST_FLOOR_SHAPE;
        }
        if (attachment == BellAttachType.CEILING) return CEILING_SHAPE;
        if (attachment == BellAttachType.DOUBLE_WALL) {
            return facing.getAxis() == Direction.Axis.Z ? NORTH_SOUTH_BETWEEN : EAST_WEST_BETWEEN;
        }
        return switch (facing) {
            case NORTH -> TO_NORTH;
            case SOUTH -> TO_SOUTH;
            case EAST -> TO_EAST;
            case WEST -> TO_WEST;
            default -> BELL_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getBellShape(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getBellShape(state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Direction.Axis axis = clickedFace.getAxis();
        if (axis == Direction.Axis.Y) {
            BlockState state = defaultBlockState()
                    .setValue(ATTACHMENT, clickedFace == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)
                    .setValue(FACING, context.getHorizontalDirection());
            if (state.canSurvive(level, pos)) return state;
        } else {
            boolean betweenWalls = (axis == Direction.Axis.X
                    && level.getBlockState(pos.west()).isFaceSturdy(level, pos.west(), Direction.EAST)
                    && level.getBlockState(pos.east()).isFaceSturdy(level, pos.east(), Direction.WEST))
                    || (axis == Direction.Axis.Z
                    && level.getBlockState(pos.north()).isFaceSturdy(level, pos.north(), Direction.SOUTH)
                    && level.getBlockState(pos.south()).isFaceSturdy(level, pos.south(), Direction.NORTH));
            BlockState state = defaultBlockState().setValue(FACING, clickedFace.getOpposite())
                    .setValue(ATTACHMENT, betweenWalls ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
            if (state.canSurvive(level, pos)) return state;

            boolean supportedFromBelow = level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
            state = state.setValue(ATTACHMENT, supportedFromBelow ? BellAttachType.FLOOR : BellAttachType.CEILING);
            if (state.canSurvive(level, pos)) return state;
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState neighbor, LevelAccessor level,
                                  BlockPos pos, BlockPos neighborPos) {
        BellAttachType attachment = state.getValue(ATTACHMENT);
        Direction connected = getConnectedDirection(state).getOpposite();
        if (connected == side && !state.canSurvive(level, pos) && attachment != BellAttachType.DOUBLE_WALL) {
            return Blocks.AIR.defaultBlockState();
        }
        if (side.getAxis() == state.getValue(FACING).getAxis()) {
            if (attachment == BellAttachType.DOUBLE_WALL && !neighbor.isFaceSturdy(level, neighborPos, side)) {
                return state.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL).setValue(FACING, side.getOpposite());
            }
            if (attachment == BellAttachType.SINGLE_WALL && connected.getOpposite() == side
                    && neighbor.isFaceSturdy(level, neighborPos, state.getValue(FACING))) {
                return state.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
        }
        return super.updateShape(state, side, neighbor, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction support = getConnectedDirection(state).getOpposite();
        return support == Direction.UP
                ? Block.canSupportCenter(level, pos.above(), Direction.DOWN)
                : FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, support);
    }

    private static Direction getConnectedDirection(BlockState state) {
        return switch (state.getValue(ATTACHMENT)) {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            default -> state.getValue(FACING).getOpposite();
        };
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHMENT, POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HookBellBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != HookBellContent.HOOK_BELL_ENTITY.get()) return null;
        BlockEntityTicker<HookBellBlockEntity> ticker = level.isClientSide
                ? HookBellBlockEntity::clientTick
                : HookBellBlockEntity::serverTick;
        return (BlockEntityTicker<T>) (BlockEntityTicker<?>) ticker;
    }

}
