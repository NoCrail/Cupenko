package com.home.myapplication

fun countMapDistance(OP: Point, C: Point): Double{
    val xdif = (C.X-OP.X)*(C.X-OP.X)
    val ydif = (C.Y-OP.Y)*(C.Y-OP.Y)
    return Math.sqrt((xdif+ydif).toDouble())
}

fun countLevelCorrection(hOP:Int, hC:Int, dist: Double): Double{
    val level = 30.00 + ((hC-hOP)/(0.001*dist)*0.95)
    return level
}