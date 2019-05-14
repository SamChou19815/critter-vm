@file:JvmName(name = "Main")

package vm.critter

import vm.critter.interpreter.interpret
import vm.critter.parser.buildProgram
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

private fun buildAndInterpret(inputStream: InputStream): Unit =
    interpret(program = buildProgram(inputStream = inputStream))

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        buildAndInterpret(inputStream = System.`in`)
        return
    }
    for (filename in args) {
        try {
            val inputStream: InputStream = FileInputStream(filename)
            println("Interpreting $filename")
            buildAndInterpret(inputStream = inputStream)
        } catch (e: FileNotFoundException) {
            println("$filename not found.")
            continue
        }
    }
}
