package sh.talonfloof.draco_std.loading

import sh.talonfloof.draco_std.CommonEntrypoint
import sh.talonfloof.draco_std.debug.DracoEarlyLog
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
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
    }
}

class DracoScreenAdapter : ComponentAdapter() {
    override fun componentResized(e: ComponentEvent) {
        if(DracoLoadingScreen.screen != null) {
            val guiW = DracoLoadingScreen.screen!!.rootPane.width/DracoLoadingScreen.screen!!.calculateScale()
            val guiH = DracoLoadingScreen.screen!!.rootPane.height/DracoLoadingScreen.screen!!.calculateScale()
            val height = (((guiW * 0.75).coerceAtMost(guiH.toDouble()))*0.25).toInt()
            val width = height*4
            for (c in DracoLoadingScreen.topPanel.components) {
                if (c is JLabel) {
                    if(c.icon != null) {
                        c.icon = ImageIcon(DracoLoadingScreen.image.image.getScaledInstance(width*DracoLoadingScreen.screen!!.calculateScale(),height*DracoLoadingScreen.screen!!.calculateScale(), Image.SCALE_FAST))
                        c.isOpaque = false
                    }
                    c.font = DracoLoadingScreen.f.deriveFont(8.0F * DracoLoadingScreen.screen!!.calculateScale())
                }
                if(c is JPanel) {
                    val cm = c.components.first()
                    cm.maximumSize = Dimension(width,8*DracoLoadingScreen.screen!!.calculateScale())
                    cm.minimumSize = cm.maximumSize
                    cm.preferredSize = cm.maximumSize
                    cm.size = cm.maximumSize
                    cm.repaint()
                    c.border = BorderFactory.createLineBorder(Color.WHITE, DracoLoadingScreen.screen!!.calculateScale())
                    (cm as JProgressBar).border = BorderFactory.createLineBorder(Color.WHITE, DracoLoadingScreen.screen!!.calculateScale())
                }
            }
            DracoLoadingScreen.dracoLabel.icon = ImageIcon(DracoLoadingScreen.draco.image.getScaledInstance(39*DracoLoadingScreen.screen!!.calculateScale(),50*DracoLoadingScreen.screen!!.calculateScale(), Image.SCALE_FAST))
            DracoLoadingScreen.logLabel.font = DracoLoadingScreen.f.deriveFont(8.0F * DracoLoadingScreen.screen!!.calculateScale())
            DracoLoadingScreen.versionLabel.font = DracoLoadingScreen.f.deriveFont(8.0F * DracoLoadingScreen.screen!!.calculateScale())
            DracoLoadingScreen.screen!!.repaint()
        }
    }
}

class DracoLoadingScreen(val localColor: Color) : JFrame() {
    companion object {
        @JvmField
        var screen: DracoLoadingScreen? = null
        @JvmField
        var image = ImageIcon(ImageIO.read(javaClass.classLoader.getResourceAsStream("assets/draco/draco-banner.png")))
        @JvmField
        var draco = ImageIcon(ImageIO.read(javaClass.classLoader.getResourceAsStream("assets/draco/draco_monochrome.png")))
        @JvmField
        var dracoLabel = JLabel()
        @JvmField
        var imageLabel = JLabel()
        @JvmField
        var logLabel = JMultilineLabel()
        @JvmField
        var versionLabel = JLabel(CommonEntrypoint.VERSION)
        @JvmField
        var memoryLabel = JLabel("")
        @JvmField
        var memoryBar = JProgressBar()
        @JvmField
        var memoryThread: Thread? = null
        @JvmField
        var diffX = 0
        @JvmField
        var diffY = 0
        @JvmField
        var color: Color = Color.BLACK
        @JvmField
        val topPanel = JPanel(GridBagLayout())
        @JvmStatic
        val f: Font = Font.createFont(Font.TRUETYPE_FONT, javaClass.classLoader.getResourceAsStream("assets/draco/monocraft.ttf"))
        private val progressBarLabel: MutableMap<String, JLabel> = mutableMapOf()
        private val progressBars: MutableMap<String, JPanel> = mutableMapOf()

        @JvmStatic
        fun createCustomProgressBar(id: String, title: String?, max: Int) {
            val guiW = DracoLoadingScreen.screen!!.rootPane.width/DracoLoadingScreen.screen!!.calculateScale()
            val guiH = DracoLoadingScreen.screen!!.rootPane.height/DracoLoadingScreen.screen!!.calculateScale()
            val height = (((guiW * 0.75).coerceAtMost(guiH.toDouble()))*0.25).toInt()
            val width = height*4
            run {
                val c = GridBagConstraints()
                c.fill = GridBagConstraints.HORIZONTAL
                c.gridx = 0
                c.gridy = (progressBarLabel.size*2)+3
                val l = JPanel(BorderLayout())
                val progressBar = JProgressBar()
                progressBar.isIndeterminate = max == 0
                progressBar.background = color
                progressBar.foreground = Color.WHITE
                progressBar.isBorderPainted = false
                progressBar.maximum = max
                progressBar.maximumSize = Dimension(width,8*screen!!.calculateScale())
                progressBar.minimumSize = progressBar.maximumSize
                progressBar.preferredSize = progressBar.maximumSize
                progressBar.size = progressBar.maximumSize
                progressBar.border = BorderFactory.createLineBorder(Color.WHITE, screen!!.calculateScale())
                l.add(progressBar, BorderLayout.CENTER)
                l.border = BorderFactory.createLineBorder(Color.WHITE, screen!!.calculateScale())
                val label = JLabel(title)
                label.foreground = Color.WHITE
                label.font = f.deriveFont(8.0F * screen!!.calculateScale())
                topPanel.add(label,c)
                topPanel.revalidate()
                progressBarLabel[id] = label
                c.gridy++
                topPanel.add(l,c)
                topPanel.revalidate()
                progressBars[id] = l
            }
            topPanel.repaint()
        }

        @JvmStatic
        fun updateCustomBar(id: String, title: String?, v: Int?, max: Int?) {
            if(v == null && title == null && max == null) {
                topPanel.remove(progressBarLabel[id]!!)
                topPanel.remove(progressBars[id]!!)
                return
            }
            if(max != null) {
                (progressBars[id]!!.components.first() as JProgressBar).maximum = max
                (progressBars[id]!!.components.first() as JProgressBar).isIndeterminate = max == 0
            }
            if(v != null) {
                (progressBars[id]!!.components.first() as JProgressBar).value = v
            }
            if(title != null) {
                progressBarLabel[id]!!.text = title
            }
        }
    }

    fun calculateScale() : Int {
        var i: Int = 1
        val w = width - diffX
        val h = height - diffY
        while (i < w && i < h && w / (i + 1) >= 320 && h / (i + 1) >= 240) {
            ++i
        }

        return i
    }

    init {
        color = localColor
        addComponentListener(DracoScreenAdapter())
        setTitle("Minecraft: Draco Loading...")
        iconImage = null
        setSize(854, 480)
        layeredPane.background = localColor
        contentPane.background = localColor
        //isResizable = false
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        image.image = image.image.getScaledInstance(1,1, Image.SCALE_FAST)
        imageLabel.icon = image
        imageLabel.horizontalAlignment = SwingConstants.CENTER

        topPanel.background = localColor
        val c = GridBagConstraints()
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        run {
            val l = JPanel(BorderLayout())
            memoryLabel.horizontalAlignment = SwingConstants.CENTER
            memoryLabel.foreground = Color.WHITE
            memoryBar.background = localColor
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

        add(topPanel, BorderLayout.NORTH)
        val panel = JPanel(GridBagLayout())
        panel.background = localColor
        logLabel.font = f.deriveFont(16.0F)
        logLabel.foreground = Color.WHITE
        logLabel.background = localColor
        versionLabel.foreground = Color.WHITE
        versionLabel.font = f.deriveFont(16.0F)
        versionLabel.horizontalAlignment = SwingConstants.RIGHT
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        c.anchor = GridBagConstraints.SOUTH
        c.weightx = 1.0
        panel.add(logLabel, c)
        run {
            val box = JPanel(BorderLayout(1,1))
            box.background = localColor
            dracoLabel.icon = ImageIcon(draco.image.getScaledInstance(39 * 2, 50 * 2, Image.SCALE_FAST))
            box.add(dracoLabel, BorderLayout.EAST)
            box.add(versionLabel, BorderLayout.SOUTH)
            c.gridx = 1
            panel.add(box, c)
        }
        add(panel, BorderLayout.SOUTH)
        startMemoryThread()
        setLocationRelativeTo(null)
    }

    private fun startMemoryThread() {
        memoryThread = Thread({
            while (true) {
                setExtendedState(extendedState and MAXIMIZED_BOTH.inv())
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