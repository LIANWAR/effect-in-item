package com.lianserver.effect.interfaces

import com.lianserver.effect.interfaces.PrefixedTextInterface
import com.lianserver.effect.Main

interface KommandInterface: PrefixedTextInterface {
    fun getInstance(): Main = Main.instance

    fun kommand() {}
}