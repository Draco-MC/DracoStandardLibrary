package sh.talonfloof.draco_std.loading

import sh.talonfloof.draco_std.debug.DracoEarlyLog
import java.awt.*
import java.lang.management.ManagementFactory
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder

class JMultilineLabel() : JTextArea() {
    init {
        isEditable = false
        cursor = null
        isOpaque = true
        isFocusable = false
        font = UIManager.getFont("Label.font")
        wrapStyleWord = true
        lineWrap = true
        border = EmptyBorder(0,0,0,0)
        alignmentY = BOTTOM_ALIGNMENT
        background = Color(239, 50, 61)
    }
}
class DracoLoadingScreen : JFrame() {
    companion object {
        @JvmField
        var screen: DracoLoadingScreen? = null
        @JvmField
        var image = ImageIcon(ImageIO.read(javaClass.classLoader.getResourceAsStream("assets/draco/draco-banner.png")))
        @JvmField
        var imageLabel = JLabel()
        @JvmField
        var logLabel = JMultilineLabel()
        @JvmField
        var memoryLabel = JLabel("")
        @JvmField
        var memoryBar = JProgressBar()
        @JvmField
        var memoryThread: Thread? = null
    }

    init {
        val f = Font.createFont(Font.TRUETYPE_FONT, javaClass.classLoader.getResourceAsStream("assets/draco/monocraft.ttf"))
        setTitle("Draco")
        setSize(854, 480)
        //background = Color(239, 50, 61)
        //rootPane.background = Color(239, 50, 61)
        layeredPane.background = Color(239, 50, 61)
        contentPane.background = Color(239, 50, 61)
        isResizable = false
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        image.image = image.image.getScaledInstance(600,204, Image.SCALE_FAST)
        imageLabel.icon = image
        imageLabel.horizontalAlignment = SwingConstants.CENTER

        val topPanel = JPanel(GridBagLayout())
        topPanel.background = Color(239, 50, 61)
        val c = GridBagConstraints()
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        run {
            val l = JPanel(BorderLayout())
            memoryLabel.font = f.deriveFont(16.0F)
            memoryLabel.horizontalAlignment = SwingConstants.CENTER
            memoryLabel.foreground = Color.WHITE
            memoryBar.background = Color(239, 50, 61)
            memoryBar.foreground = Color.WHITE
            memoryBar.isBorderPainted = false
            l.add(memoryBar, BorderLayout.CENTER)
            l.border = BorderFactory.createLineBorder(Color.WHITE, 2)
            c.gridy = 0
            topPanel.add(memoryLabel, c)
            c.gridy = 1
            topPanel.add(l, c)
        }
        c.gridy = 2
        c.ipady = 20
        topPanel.add(imageLabel, c)
        c.ipady = 0
        run {
            c.gridy = 3
            val l = JLabel("Booting Minecraft")
            l.foreground = Color.WHITE
            l.font = f.deriveFont(16.0F)
            topPanel.add(l, c)
        }
        run {
            val l = JPanel(BorderLayout())
            val progressBar = JProgressBar()
            progressBar.isIndeterminate = true
            progressBar.font = f.deriveFont(16.0F)
            progressBar.background = Color(239, 50, 61)
            progressBar.foreground = Color.WHITE
            progressBar.isBorderPainted = false
            l.add(progressBar, BorderLayout.CENTER)
            l.border = BorderFactory.createLineBorder(Color.WHITE, 2)
            c.gridy = 4
            topPanel.add(l, c)
        }
        add(topPanel, BorderLayout.NORTH)
        val panel = JPanel(GridBagLayout())
        panel.background = Color(239, 50, 61)
        logLabel.font = f.deriveFont(16.0F)
        logLabel.foreground = Color.WHITE
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        c.weightx = 1.0
        panel.add(logLabel, c)
        add(panel, BorderLayout.SOUTH)
        startMemoryThread()
        setLocationRelativeTo(null)
    }

    private fun startMemoryThread() {
        memoryThread = Thread({
            while (true) {
                val mem = ManagementFactory.getMemoryMXBean()
                val heapusage = mem.heapMemoryUsage
                val heap = String.format(
                    "Heap: %d/%d MB (%.1f%%) OffHeap: %d MB",
                    heapusage.used shr 20,
                    heapusage.max shr 20,
                    heapusage.used.toFloat() / heapusage.max * 100.0,
                    mem.nonHeapMemoryUsage.used shr 20
                )
                memoryBar.maximum = (heapusage.max shr 20).toInt()
                memoryBar.value = (heapusage.used shr 20).toInt()
                memoryLabel.text = heap
                var s = ""
                for(i in DracoEarlyLog.log.reversed()) {
                    s += i+"\n"
                }
                s = s.removeSuffix("\n")
                logLabel.text = s
                try {
                    Thread.sleep(1000L/60)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }, "MemoryUsageListener")
        memoryThread!!.setDaemon(true)
        memoryThread!!.start()
    }
}