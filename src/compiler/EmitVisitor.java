package compiler;

import grammar.firstBaseVisitor;
import grammar.firstParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;


public class EmitVisitor extends firstBaseVisitor<ST> {

    private final STGroup stGroup;

    public EmitVisitor(STGroup group) {
        super();
        this.stGroup = group;
    }

    @Override
    public ST visitAssign(final firstParser.AssignContext ctx) {
        return stGroup.getInstanceOf("declare").add("name", ctx.ID()).add("value", visit(ctx.expr()));
    }

    @Override
    protected ST defaultResult() {
        return stGroup.getInstanceOf("deflt");
    }

    @Override
    protected ST aggregateResult(ST aggregate, ST nextResult) {
        if (nextResult != null) {
            aggregate.add("elem", nextResult);
        }
        return aggregate;
    }

    @Override
    public ST visitTerminal(TerminalNode node) {
        return stGroup.getInstanceOf("brk");
    }

    @Override
    public ST visitInt_tok(firstParser.Int_tokContext ctx) {
        ST st = stGroup.getInstanceOf("int");
        st.add("i", ctx.INT().getText());
        return st;
    }

    @Override
    public ST visitId_tok(firstParser.Id_tokContext ctx) {
        ST st = stGroup.getInstanceOf("var");
        st.add("i", ctx.ID().getText());
        return st;
    }

    @Override
    public ST visitBinOp(firstParser.BinOpContext ctx) {
        return (switch (ctx.op.getType()) {
            case firstParser.ADD -> stGroup.getInstanceOf("add");
            case firstParser.SUB -> stGroup.getInstanceOf("sub");
            case firstParser.MUL -> stGroup.getInstanceOf("mul");
            case firstParser.DIV -> stGroup.getInstanceOf("div");
            default -> throw new IllegalStateException("Unexpected value: " + ctx.op.getType());
        }
        ).add("p1", visit(ctx.l)).add("p2", visit(ctx.r));
    }
}
