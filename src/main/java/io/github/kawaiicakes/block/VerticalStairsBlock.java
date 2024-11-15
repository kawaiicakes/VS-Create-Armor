package io.github.kawaiicakes.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.StairShape;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import static net.minecraft.block.StairsBlock.*;

// TODO
public class VerticalStairsBlock extends Block implements Waterloggable {
    public static final EnumProperty<BlockHalf> HALF = EnumProperty.of("half", BlockHalf.class);

    private final Block baseBlock;
    private final BlockState baseBlockState;

    public VerticalStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(HALF, BlockHalf.RIGHT)
                        .with(SHAPE, StairShape.STRAIGHT)
                        .with(WATERLOGGED, Boolean.FALSE)
        );

        this.baseBlock = baseBlockState.getBlock();
        this.baseBlockState = baseBlockState;
    }

    public enum BlockHalf implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        BlockHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
