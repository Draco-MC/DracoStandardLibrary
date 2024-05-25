package sh.talonfloof.draco_std.builder

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

fun interface DracoBlockEntitySupplier<T : BlockEntity?> {
    fun create(pos: BlockPos?, state: BlockState?): T
}


object BlockEntityTypeBuilder {
    @JvmStatic
    fun <T : BlockEntity> of(supplier: DracoBlockEntitySupplier<T>, block: Block) : BlockEntityType.Builder<T> {
        return BlockEntityType.Builder.of({ a, b -> supplier.create(a,b) },block)
    }
}