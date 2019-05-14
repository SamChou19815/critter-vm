package vm.critter.ast

data class CritterProgram(val rules: List<Rule>) {

    override fun toString(): String {
        val sb = StringBuilder()
        rules.forEach { sb.append(it).append('\n') }
        return sb.toString()
    }

}

data class Rule(val condition: Condition, val command: Command) {
    override fun toString(): String = "$condition --> $command;"
}

data class Command(val updates: List<Update>, val action: Action?) {
    override fun toString(): String {
        val sb = StringBuilder()
        updates.forEach { sb.append(it).append(' ') }
        action?.let { sb.append(action) }
        return sb.toString()
    }
}

data class Update(val memLocation: Expression, val assignedExpr: Expression) {
    override fun toString(): String = "mem[$memLocation] := $assignedExpr"
}

sealed class Action(val name: String) {
    override fun toString(): String = name

    object WaitAction : Action(name = "wait")
    object ForwardAction : Action(name = "forward")
    object BackwardAction : Action(name = "backward")
    object LeftAction : Action(name = "left")
    object RightAction : Action(name = "right")
    object EatAction : Action(name = "eat")
    object AttackAction : Action(name = "attack")
    object GrowAction : Action(name = "grow")
    object BudAction : Action(name = "bud")
    object MateAction : Action(name = "mate")

    data class TagAction(val expr: Expression) : Action(name = "tag") {
        override fun toString(): String = "$name[$expr]"
    }

    data class ServeAction(val expr: Expression) : Action(name = "serve") {
        override fun toString(): String = "$name[$expr]"
    }
}

sealed class Condition {
    data class Relation(val op: RelationOp, val e1: Expression, val e2: Expression) : Condition() {
        override fun toString(): String = "$e1 $op $e2"
    }

    data class AndCondition(val e1: Condition, val e2: Condition) : Condition() {
        override fun toString(): String = "{ $e1 and $e2 }"
    }

    data class OrCondition(val e1: Condition, val e2: Condition) : Condition() {
        override fun toString(): String = "{ $e1 or $e2 }"
    }
}

enum class RelationOp(private val displayName: String) {
    LT(displayName = "<"), LE(displayName = "<="), GT(displayName = ">"),
    GE(displayName = ">="), EQ(displayName = "="), NE(displayName = "!=");

    override fun toString(): String = displayName
}

sealed class Expression {

    data class IntExpr(val value: Long) : Expression() {
        override fun toString(): String = value.toString()
    }

    data class NearByExpr(val expr: Expression) : Expression() {
        override fun toString(): String = "nearby[$expr]"
    }

    data class AheadExpr(val expr: Expression) : Expression() {
        override fun toString(): String = "ahead[$expr]"
    }

    data class RandomExpr(val expr: Expression) : Expression() {
        override fun toString(): String = "random[$expr]"
    }

    object SmellExpr : Expression() {
        override fun toString(): String = "smell"
    }

    data class MemExpr(val expr: Expression) : Expression() {
        override fun toString(): String = "mem[$expr]"
    }

    data class NegExpr(val expr: Expression) : Expression() {
        override fun toString(): String = "-($expr)"
    }

    data class MulExpr(val e1: Expression, val e2: Expression) : Expression() {
        override fun toString(): String = "($e1 * $e2)"
    }

    data class DivExpr(val e1: Expression, val e2: Expression) : Expression() {
        override fun toString(): String = "($e1 / $e2)"
    }

    data class ModExpr(val e1: Expression, val e2: Expression) : Expression() {
        override fun toString(): String = "($e1 mod $e2)"
    }

    data class PlusExpr(val e1: Expression, val e2: Expression) : Expression() {
        override fun toString(): String = "($e1 + $e2)"
    }

    data class MinusExpr(val e1: Expression, val e2: Expression) : Expression() {
        override fun toString(): String = "($e1 - $e2)"
    }
}
