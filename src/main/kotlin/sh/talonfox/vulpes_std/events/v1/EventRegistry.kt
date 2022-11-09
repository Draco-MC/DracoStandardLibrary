package sh.talonfox.vulpes_std.events.v1

object EventRegistry {
    @JvmStatic
    fun invokeEvent(eventID: String) {
        if(!eventID.contains(":") || eventID.startsWith(":") || eventID.endsWith(":")) {
            System.err.println("A mod attempted to invoke an event with the invalid name, \"$eventID\"")
            return
        }

    }
}