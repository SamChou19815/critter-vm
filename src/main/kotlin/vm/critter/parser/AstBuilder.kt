package vm.critter.parser

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import vm.critter.ast.*
import vm.critter.parser.generated.PLBaseVisitor
import vm.critter.parser.generated.PLParser.*
import vm.critter.errors.SyntaxErrors
import vm.critter.parser.generated.PLLexer
import vm.critter.parser.generated.PLParser
import vm.critter.ast.*
import java.io.InputStream

fun buildProgram(inputStream: InputStream): CritterProgram {
    val parser = PLParser(CommonTokenStream(PLLexer(ANTLRInputStream(inputStream))))
    val errorListener = SyntaxErrorListener()
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)
    val programContext = parser.program()
    val errors = errorListener.syntaxErrors
    if (errors.isNotEmpty()) {
        throw SyntaxErrors(errors = errors)
    }
    return CritterProgram(
        rules = programContext.oneRule().map { rule ->
            Rule(
                condition = rule.condition().accept(ConditionBuilder),
                command = buildCommand(ctx = rule.command())
            )
        }
    )
}

private fun buildCommand(ctx: CommandContext): Command {
    val updates = mutableListOf<Update>()
    updates.addAll(elements = ctx.update().map(::buildUpdate))
    var actionOpt: Action? = null
    ctx.updateOrAction().accept(object : PLBaseVisitor<Unit>() {
        override fun visitUpdateCommand(ctx: UpdateCommandContext) {
            updates.add(element = buildUpdate(ctx = ctx.update()))
        }

        override fun visitActionCommand(ctx: ActionCommandContext) {
            actionOpt = ctx.action().accept(ActionBuilder)
        }
    })
    return Command(updates = updates, action = actionOpt)
}

private fun buildUpdate(ctx: UpdateContext): Update = Update(
    memLocation = ctx.expr(0).accept(ExpressionBuilder),
    assignedExpr = ctx.expr(1).accept(ExpressionBuilder)
)

private object ActionBuilder : PLBaseVisitor<Action>() {

    override fun visitWaitAction(ctx: WaitActionContext): Action = Action.WaitAction
    override fun visitForwardAction(ctx: ForwardActionContext): Action = Action.ForwardAction
    override fun visitBackwardAction(ctx: BackwardActionContext): Action = Action.BackwardAction
    override fun visitLeftAction(ctx: LeftActionContext): Action = Action.LeftAction
    override fun visitRightAction(ctx: RightActionContext): Action = Action.RightAction
    override fun visitEatAction(ctx: EatActionContext): Action = Action.EatAction
    override fun visitAttackAction(ctx: AttackActionContext): Action = Action.AttackAction
    override fun visitGrowAction(ctx: GrowActionContext): Action = Action.GrowAction
    override fun visitBudAction(ctx: BudActionContext): Action = Action.BudAction
    override fun visitMateAction(ctx: MateActionContext): Action = Action.MateAction

    override fun visitTagAction(ctx: TagActionContext): Action =
        Action.TagAction(expr = ctx.expr().accept(ExpressionBuilder))

    override fun visitServeAction(ctx: ServeActionContext): Action =
        Action.ServeAction(expr = ctx.expr().accept(ExpressionBuilder))
}

private object ConditionBuilder : PLBaseVisitor<Condition>() {

    override fun visitNestedCondition(ctx: NestedConditionContext): Condition = ctx.condition().accept(this)

    private val RelContext.op: RelationOp
        get() = when {
            EQ() != null -> RelationOp.EQ
            NE() != null -> RelationOp.NE
            LT() != null -> RelationOp.LT
            LE() != null -> RelationOp.LE
            GT() != null -> RelationOp.GT
            GE() != null -> RelationOp.GE
            else -> error(message = "Impossible!")
        }

    override fun visitComparisonCondition(ctx: ComparisonConditionContext): Condition = Condition.Relation(
        op = ctx.rel().op,
        e1 = ctx.expr(0).accept(ExpressionBuilder),
        e2 = ctx.expr(1).accept(ExpressionBuilder)
    )

    override fun visitAndCondition(ctx: AndConditionContext): Condition = Condition.AndCondition(
        e1 = ctx.condition(0).accept(ConditionBuilder),
        e2 = ctx.condition(1).accept(ConditionBuilder)
    )

    override fun visitOrCondition(ctx: OrConditionContext): Condition = Condition.OrCondition(
        e1 = ctx.condition(0).accept(ConditionBuilder),
        e2 = ctx.condition(1).accept(ConditionBuilder)
    )
}

private object ExpressionBuilder : PLBaseVisitor<Expression>() {
    override fun visitIntExpr(ctx: IntExprContext): Expression =
        Expression.IntExpr(value = ctx.IntLiteral().symbol.text.toLong())

    override fun visitNearByExpr(ctx: NearByExprContext): Expression =
        Expression.NearByExpr(expr = ctx.expr().accept(ExpressionBuilder))

    override fun visitAheadExpr(ctx: AheadExprContext): Expression =
        Expression.AheadExpr(expr = ctx.expr().accept(ExpressionBuilder))

    override fun visitRandomExpr(ctx: RandomExprContext): Expression =
        Expression.RandomExpr(expr = ctx.expr().accept(ExpressionBuilder))

    override fun visitSmellExpr(ctx: SmellExprContext): Expression = Expression.SmellExpr

    override fun visitMemExpr(ctx: MemExprContext): Expression =
        Expression.MemExpr(expr = ctx.expr().accept(ExpressionBuilder))

    override fun visitNestedExpr(ctx: NestedExprContext): Expression = ctx.expr().accept(ExpressionBuilder)

    override fun visitNegExpr(ctx: NegExprContext): Expression =
        Expression.NegExpr(expr = ctx.expr().accept(ExpressionBuilder))

    override fun visitAddOpExpr(ctx: AddOpExprContext): Expression {
        val e1 = ctx.expr(0).accept(ExpressionBuilder)
        val e2 = ctx.expr(1).accept(ExpressionBuilder)
        return when {
            ctx.addOp().PLUS() != null -> Expression.PlusExpr(e1 = e1, e2 = e2)
            ctx.addOp().MINUS() != null -> Expression.MinusExpr(e1 = e1, e2 = e2)
            else -> error(message = "Impossible")
        }
    }

    override fun visitMulOpExpr(ctx: MulOpExprContext): Expression {
        val e1 = ctx.expr(0).accept(ExpressionBuilder)
        val e2 = ctx.expr(1).accept(ExpressionBuilder)
        return when {
            ctx.mulOp().MUL() != null -> Expression.MulExpr(e1 = e1, e2 = e2)
            ctx.mulOp().DIV() != null -> Expression.DivExpr(e1 = e1, e2 = e2)
            ctx.mulOp().MOD() != null -> Expression.ModExpr(e1 = e1, e2 = e2)
            else -> error(message = "Impossible")
        }
    }
}
