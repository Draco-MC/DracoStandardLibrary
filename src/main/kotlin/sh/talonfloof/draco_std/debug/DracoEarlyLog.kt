package sh.talonfloof.draco_std.debug

object DracoEarlyLog {
    val log = mutableListOf("")
    @JvmField
    var customBarName: String = ""
    @JvmField
    var customBarProgress: Float = 0.0F
    @JvmStatic
    fun addToLog(s: String) {
        log.add(0,s)
        if(log.size > 6)
            log.removeLast()
    }
}