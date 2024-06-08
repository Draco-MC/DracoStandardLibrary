package sh.talonfloof.draco_std.transformation

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import sh.talonfloof.draco_std.debug.DracoEarlyLog
import sh.talonfloof.dracoloader.api.DracoTransformer
import sh.talonfloof.dracoloader.transform.IDracoTransformer
import sh.talonfloof.dracoloader.transform.visitAsm

/*
This is a hack used to allow newer snapshots of Minecraft to run the standard library due to the changes with the ResourceLocation class
This doesn't affect standard releases and only 24w21a and above, a more permanent solution will be used when 1.21 is released
 */

@DracoTransformer
class CompatibilityHacksTransformer : IDracoTransformer {
    var applyRlHack: Boolean = false
    var applyMllHack: Boolean = false

    override fun transform(loader: ClassLoader, className: String, originalClassData: ByteArray?): ByteArray? {
        if(className == "net.minecraft.client.gui.components.MultiLineLabel") {
            val reader = ClassReader(originalClassData)
            val node = ClassNode()
            reader.accept(node, 0)
            if(!applyMllHack) {
                with(node.methods.find { it.name == "renderCentered" && it.desc == "(Lnet/minecraft/client/gui/GuiGraphics;IIII)V" }) {
                    if (this != null) {
                        DracoEarlyLog.addToLog("Applying MultiLineLabel Hack")
                        applyMllHack = true
                    }
                }
            }
            if(applyMllHack) {
                node.visitMethod(Opcodes.ACC_PUBLIC,"renderCentered","(Lnet/minecraft/client/gui/GuiGraphics;IIII)I",null,null).visitAsm {
                    aload(0)
                    aload(1)
                    iload(2)
                    iload(3)
                    iload(4)
                    iload(5)
                    invokeinterface("net/minecraft/client/gui/components/MultiLineLabel","renderCentered","(Lnet/minecraft/client/gui/GuiGraphics;IIII)V")
                    iload(3)
                    ireturn
                }
                node.visitMethod(Opcodes.ACC_PUBLIC,"renderCentered","(Lnet/minecraft/client/gui/GuiGraphics;II)I",null,null).visitAsm {
                    aload(0)
                    aload(1)
                    iload(2)
                    iload(3)
                    invokeinterface("net/minecraft/client/gui/components/MultiLineLabel","renderCentered","(Lnet/minecraft/client/gui/GuiGraphics;II)V")
                    iload(3)
                    ireturn
                }
                return ClassWriter(reader, ClassWriter.COMPUTE_FRAMES).also { node.accept(it) }.toByteArray()
            }
        } else if(className == "net.minecraft.resources.ResourceLocation") {
            val reader = ClassReader(originalClassData)
            val node = ClassNode()
            reader.accept(node, 0)
            if(!applyRlHack) {
                with(node.methods.find { it.name == "<init>" && it.desc == "(Ljava/lang/String;Ljava/lang/String;)V" }) {
                    if (this != null) {
                        if (this.access == Opcodes.ACC_PROTECTED || this.access == Opcodes.ACC_PRIVATE) {
                            this.access = Opcodes.ACC_PUBLIC
                            DracoEarlyLog.addToLog("Applying ResourceLocation Hack")
                            applyRlHack = true
                        }
                    }
                }
            }
            if(applyRlHack) {
                node.visitMethod(Opcodes.ACC_PUBLIC,"<init>","(Ljava/lang/String;)V",null,null).visitAsm {
                    aload(0)
                    ldc("minecraft")
                    aload(1)
                    invokespecial("net/minecraft/resources/ResourceLocation","<init>","(Ljava/lang/String;Ljava/lang/String;)V")
                    _return
                }
            }
            return ClassWriter(reader, ClassWriter.COMPUTE_FRAMES).also { node.accept(it) }.toByteArray()
        }
        return originalClassData
    }
}