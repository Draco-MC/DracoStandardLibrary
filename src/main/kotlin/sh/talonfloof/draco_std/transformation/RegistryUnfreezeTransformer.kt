package sh.talonfloof.draco_std.transformation

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import sh.talonfloof.dracoloader.transform.IDracoTransformer
import sh.talonfloof.dracoloader.transform.visitAsm

object RegistryUnfreezeTransformer : IDracoTransformer {
    override fun transform(loader: ClassLoader, className: String, originalClassData: ByteArray?): ByteArray? {
        if(className == "net.minecraft.core.MappedRegistry" || className == "net.minecraft.core.DefaultedMappedRegistry") {
            val reader = ClassReader(originalClassData)
            val node = ClassNode()
            reader.accept(node, 0)
            with(node.fields.find { it.name == "frozen" }) {
                if(this != null) {
                    this.access = Opcodes.ACC_PUBLIC
                }
            }
            node.visitMethod(Opcodes.ACC_PUBLIC, "unfreeze", "()V", null, null).visitAsm {
                aload(0)
                int(0)
                putfield("net/minecraft/core/MappedRegistry","frozen","Z")
                _return
            }
            return ClassWriter(reader, ClassWriter.COMPUTE_FRAMES).also { node.accept(it) }.toByteArray()
        }
        return originalClassData
    }
}