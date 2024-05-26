package sh.talonfloof.draco_std.resources

import net.minecraft.server.packs.repository.Pack

interface PackChildrenHolder {
    fun `draco$isHidden`() : Boolean
    fun `draco$setHidden`(hidden: Boolean)
    fun `draco$getChildren`() : MutableList<Pack>?
    fun `draco$setChildren`(children: MutableList<Pack>?)
}