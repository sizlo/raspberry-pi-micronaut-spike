package com.timsummertonbrier

import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
	Micronaut
		.build(*args)
		.defaultEnvironments("default")
		.start()
}

