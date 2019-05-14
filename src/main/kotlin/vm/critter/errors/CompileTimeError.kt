package vm.critter.errors

open class CompileTimeError(errorMessage: String) : RuntimeException(errorMessage) {
    val errorMessage: String = "${javaClass.simpleName}:\n$errorMessage"
}
