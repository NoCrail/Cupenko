package com.home.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        CountBtn.setOnClickListener {
            val dist = countMapDistance(getOPPoint(), getCPoint())
            resultDistance.text = dist.toString()
            if((!HOpEdit.text.isNullOrEmpty())&&(!HCEdit.text.isNullOrEmpty()))
                if (dist>3000)
                resultLevel.text = convertLevelToString(countLevelCorrection(getHOP(), getHC(), dist))
                else Toast.makeText(baseContext, "Ошибка дальности. Проверьте данные", Toast.LENGTH_SHORT).show()

        }

    }

    fun getOPPoint(): Point{
        if ((!XOpEdit.text.isNullOrEmpty())&&(!YOpEdit.text.isNullOrEmpty()))
        return Point(XOpEdit.text.toString().toInt(), YOpEdit.text.toString().toInt()) else return Point(0,0)
    }
    fun getCPoint(): Point{
        if ((!XCEdit.text.isNullOrEmpty())&&(!YCEdit.text.isNullOrEmpty()))
        return Point(XCEdit.text.toString().toInt(), YCEdit.text.toString().toInt()) else return Point(0,0)
    }
    fun getHOP(): Int{
        if(!HOpEdit.text.isNullOrEmpty())
            return HOpEdit.text.toString().toInt() else return 0
    }
    fun getHC(): Int{
        if(!HCEdit.text.isNullOrEmpty())
            return HCEdit.text.toString().toInt() else return 0
    }

    fun convertLevelToString(level: Double): String{
        val strlvl = level.toString()
        val splitted = strlvl.split(".")
        val str = splitted[0]+"-"+strlvl[3]+strlvl[4]
        return str
    }
}
