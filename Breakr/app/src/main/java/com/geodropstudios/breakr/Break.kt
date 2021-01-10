package com.geodropstudios.breakr

// Define general abstract break class.
// Takes the start time of the break in minutes from the start of the session.
abstract class Break(val start : Int) {
    var active: Boolean = false // Indicates whether the break is currently taking place.
    open val duration: Int = 0  // The duration of the break in minutes from the start of the break.
    open val priority: Int = 0  // The priority of the break over other types of breaks. Lower value means higher priority.
    open val frequency: Int = 0 // The time between breaks in minutes between the starts of the different

    override fun toString(): String {
        return "%s: {active:%b, start:%d. duration:%d, priority:%d, frequency:%d}".format(this.javaClass.name, active, start, duration, priority, frequency)
    }
}

// Define break of type exercise break.
class ExerciseBreak(start: Int) : Break(start) {
    override val duration: Int = 7
    override val priority: Int = 10
    override val frequency: Int = 120
}

// Define break of type rest break.
class RestBreak(start: Int) : Break(start) {
    override val duration: Int = 3
    override val priority: Int = 20
    override val frequency: Int = 30
}

// Define break of type eye break.
class EyeBreak(start: Int) : Break(start) {
    override val duration: Int = 1
    override val priority: Int = 30
    override val frequency: Int = 15
}