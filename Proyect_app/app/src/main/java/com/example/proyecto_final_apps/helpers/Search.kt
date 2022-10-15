package com.example.proyecto_final_apps.helpers

import org.jetbrains.annotations.NotNull

object Search {

    fun hasCoincidences (query:String, vararg words: String):Boolean{
            for(word:String in words){
                if(word.startsWith(query.trim(), true)) return true
            }
        return false
    }
}