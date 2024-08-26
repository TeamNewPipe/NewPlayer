package net.newpipe.newplayer

class NewPlayerException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}