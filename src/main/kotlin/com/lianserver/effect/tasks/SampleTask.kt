/*
 * Copyright (c) 2022 BaeHyeonWoo
 *
 *  Licensed under the General Public License, Version 3.0. (https://opensource.org/licenses/gpl-3.0/)
 */

package com.lianserver.effect.tasks

import com.lianserver.effect.objects.SampleObject.plugin

/***
 * @author BaeHyeonWoo
 *
 * "Until my feet are crushed,"
 * "Until I can get ahead of myself."
 */

class SampleTask : Runnable {
    override fun run() {
        plugin.logger.info("Hello World!")
    }
}