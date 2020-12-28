package com.geodropstudios.breakr

// Define general abstract break class.
abstract class Break(start : Int) {
    var active: Boolean = false
    val start: Int = 0
    open val duration: Int = 0
    open val priority: Int = 0
}

// Define break of type exercise break.
class ExerciseBreak(start: Int) : Break(start) {
    override val duration: Int = 5
    override val priority: Int = 0
}

// Define break of type rest break.
class RestBreak(start: Int) : Break(start) {
    override val duration: Int = 3
    override val priority: Int = 10
}

// Define break of type eye break.
class EyeBreak(start: Int) : Break(start) {
    override val duration: Int = 2
    override val priority: Int = 20
}