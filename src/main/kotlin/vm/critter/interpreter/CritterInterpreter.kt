package vm.critter.interpreter

import vm.critter.ast.Condition
import vm.critter.ast.CritterProgram
import vm.critter.ast.Expression
import vm.critter.ast.RelationOp
import vm.critter.errors.InterpreterError
import java.util.*

typealias Mem = MutableMap<Long, Long>

fun interpret(program: CritterProgram) {
    val rules = program.rules
    val mem: Mem = TreeMap()
    // remove 1000 repeats limitation
    while (true) {
        for (rule in rules) {
            var assigned = false
            val (condition, command) = rule
            if (interpret(condition = condition, mem = mem)) {
                val (updates, action) = command
                for ((memLocation, assignedExpr) in updates) {
                    val index = getMemIndex(expr = memLocation, mem = mem)
                    val assignedValue = interpret(expr = assignedExpr, mem = mem)
                    mem[index] = assignedValue
                    assigned = true
                }
                if (action != null) {
                    println(message = action)
                    return
                }
                if (assigned) {
                    break
                }
            }
        }
    }
}

private fun interpret(condition: Condition, mem: Mem): Boolean = when (condition) {
    is Condition.Relation -> {
        val v1 = interpret(expr = condition.e1, mem = mem)
        val v2 = interpret(expr = condition.e2, mem = mem)
        when (condition.op) {
            RelationOp.EQ -> v1 == v2
            RelationOp.NE -> v1 != v2
            RelationOp.LT -> v1 < v2
            RelationOp.LE -> v1 <= v2
            RelationOp.GT -> v1 > v2
            RelationOp.GE -> v1 >= v2
        }
    }
    is Condition.AndCondition ->
        interpret(condition = condition.e1, mem = mem) && interpret(
            condition = condition.e2,
            mem = mem
        )
    is Condition.OrCondition ->
        interpret(condition = condition.e1, mem = mem) || interpret(
            condition = condition.e2,
            mem = mem
        )
}

private fun getMemIndex(expr: Expression, mem: Mem): Long {
    val index = interpret(expr = expr, mem = mem)
    if (index < 8) {
        throw InterpreterError(errorMessage = "Illegal memory access: $index. expr: $expr")
    }
    return index
}

private fun interpret(expr: Expression, mem: Mem): Long = when (expr) {
    is Expression.IntExpr -> expr.value
    is Expression.NearByExpr -> 0L
    is Expression.AheadExpr -> 0L
    is Expression.RandomExpr -> 0L
    Expression.SmellExpr -> 0L
    is Expression.MemExpr -> {
        val index = getMemIndex(expr = expr.expr, mem = mem)
        mem.getOrDefault(key = index, defaultValue = 0L)
    }
    is Expression.NegExpr -> -interpret(expr = expr.expr, mem = mem)
    is Expression.MulExpr -> interpret(
        expr = expr.e1,
        mem = mem
    ) * interpret(expr = expr.e2, mem = mem)
    is Expression.DivExpr -> {
        val v1 = interpret(expr = expr.e1, mem = mem)
        val v2 = interpret(expr = expr.e2, mem = mem)
        if (v2 == 0L) 0 else v1 / v2
    }
    is Expression.ModExpr -> {
        val v1 = interpret(expr = expr.e1, mem = mem)
        val v2 = interpret(expr = expr.e2, mem = mem)
        if (v2 == 0L) 0 else v1 % v2
    }
    is Expression.PlusExpr -> interpret(
        expr = expr.e1,
        mem = mem
    ) + interpret(expr = expr.e2, mem = mem)
    is Expression.MinusExpr -> interpret(
        expr = expr.e1,
        mem = mem
    ) - interpret(expr = expr.e2, mem = mem)
}
