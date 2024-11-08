package atag.lc;

import atag.lc.LeidenConventionParser.EmphasisedContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

import static atag.lc.LeidenConventionParser.*;

public class AtagVisitor extends LeidenConventionBaseVisitor<CharactersAndAnnotations> {

    public record Props(boolean additionalText, String atagType) {}

    /**
     * if true terminal nodes will be amended to resulting string
     */
    private boolean amendMode = true;
    private int currentOffset = 0;
    //private final List<AnnotationRecord> annotations = new ArrayList<>();

    /**
     * Map of block types that provide additional text not being part of the character chain,
     * see https://github.com/THM-Graphs/knowledgebase/blob/main/ATAGAnnotations/Annotationslegende.md
     */
    private final Map<Class<? extends ParserRuleContext>, Props> annotationProps = Map.ofEntries(
            Map.entry(EmphasisedContext.class, new Props(true, "rubricated")),
            Map.entry(ExpandedContext.class, new Props(true, "expansion")),
            Map.entry(NonlinearContext.class, new Props(true, "aboveLine")),
            Map.entry(MarginnoteContext.class, new Props(true, "marginNote")),
            Map.entry(InternentionContext.class, new Props(true, "inPlace")),
            Map.entry(InrasuraContext.class, new Props(true, "inRasura")),
            Map.entry(DeletedContext.class, new Props(false, "deleted")),
            Map.entry(UnreadableContext.class, new Props(true, "unclear")),
            Map.entry(RepeatedContext.class, new Props(true, "repeated")),
//            Map.entry(LeidenConventionParser.Gap.class, new Props(true, "")),
            Map.entry(InterventionContext.class, new Props(true, "correction")),
            Map.entry(AdditionContext.class, new Props(true, "addition")),
            Map.entry(HeadContext.class, new Props(true, "head"))
    );

    /*    @Override
    public String visitBlock(LeidenConventionParser.BlockContext ctx) {
        System.out.println("Block: " + ctx.getText());
        return super.visitBlock(ctx);
    }*/

    /*@Override
    public String visitExpanded(LeidenConventionParser.ExpandedContext ctx) {
        System.out.printf("Expanded: %s\n", ctx.getText());
        return super.visitExpanded(ctx);
    }

    @Override
    public String visitNonlinear(LeidenConventionParser.NonlinearContext ctx) {
        System.out.printf("Nonlinear: %s\n", ctx.getText());
        return super.visitNonlinear(ctx);
    }

    @Override
    public String visitMarginnote(LeidenConventionParser.MarginnoteContext ctx) {
        System.out.printf("Marginnote: %s\n", ctx.getText());
        return super.visitMarginnote(ctx);
    }

    @Override
    public String visitInternention(LeidenConventionParser.InternentionContext ctx) {
        System.out.printf("Internention: %s\n", ctx.getText());
        return super.visitInternention(ctx);
    }

    @Override
    public String visitInrasura(LeidenConventionParser.InrasuraContext ctx) {
        System.out.println("Inrasura: " + ctx.getText());
        return super.visitInrasura(ctx);
    }

    @Override
    public String visitPage(LeidenConventionParser.PageContext ctx) {
        System.out.println("Page: " + ctx.getText());
        return super.visitPage(ctx);
    }
*/

/*    @Override
    public String visitDoc(DocContext ctx) {
        System.out.println("Doc: " + ctx.getText());
        return super.visitDoc(ctx);
    }

    @Override
    public String visitBlocks(BlocksContext ctx) {
        System.out.println("Blocks: " + ctx.getText());
        return super.visitBlocks(ctx);
    }*/

    @Override
    public CharactersAndAnnotations visitTerminal(TerminalNode node) {
        if (amendMode) {
            return switch ( node.getSymbol().getType()) {
                case WORD:
                case WHITESPACE:
                case SEPARATOR:
                    List<UChar> chars = node.getText().chars().mapToObj(i -> (char)i).map(UChar::new).toList();
                    yield new CharactersAndAnnotations(chars, Collections.emptyList());
                default:
                    yield defaultResult();
//                    throw new IllegalStateException("Unexpected value: " + node.getSymbol().getType());
            };
        } else {
            return defaultResult();
        }


//        System.out.printf("Terminal: %d %s\n", node.getSymbol().getType(), node.getText());
//        return super.visitTerminal(node);
    }

    /*@Override
    public String visitEmphasised(EmphasisedContext ctx) {
        return handleAnnotation(ctx.block(), "emphasised");
//        return super.visitEmphasised(ctx);
    }*/

    @Override
    public CharactersAndAnnotations visitComment(CommentContext ctx) {
        CharactersAndAnnotations result = defaultResult();
        int n = ctx.getChildCount();
        for (int i=1; i<n-1; i++) {  // skip first and last
            if (!shouldVisitNextChild(ctx, result)) {
                break;
            }

            ParseTree c = ctx.getChild(i);
            CharactersAndAnnotations childResult = c.accept(this);
            result = aggregateResult(result, childResult);
        }
        List<UChar> text = result.text();
        return new CharactersAndAnnotations( new AnnotationRecord(null, "comment", result.asString(), text.get(0), text.get(text.size()-1)));
    }

    @Override
    public CharactersAndAnnotations visitPage(PageContext ctx) {
        CharactersAndAnnotations result = visitTerminal(ctx.WORD());
        List<UChar> text = result.text();
        return new CharactersAndAnnotations(new AnnotationRecord(null, "page", result.asString(), text.get(0), text.get(text.size()-1)));
    }

    @Override
    public CharactersAndAnnotations visitChildren(RuleNode node) {
        if (node instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) node;
            Props props = annotationProps.get(ctx.getClass());
            if (props != null) {
                assert ctx.getChildCount() == 3;
                BlockContext blockContext = (BlockContext) ctx.getChild(1);
                return handleAnnotation(blockContext, props);
            } else {
                if ((node instanceof DocContext) || (node instanceof BlocksContext) || (node instanceof BlockContext) || (node instanceof AnnotationContext) | (node instanceof TranspositionContext) ) {
                    return super.visitChildren(node);
                } else {
                    throw new IllegalStateException("Unexpected value: " + ctx.getClass());
                }

            }
        } else {
            return super.visitChildren(node);
        }
    }

    private CharactersAndAnnotations handleAnnotation(BlockContext block, Props props) {
        CharactersAndAnnotations content = visitChildren(block); //visitBlock(block);
        int length = block.getStop().getStopIndex() - block.getStart().getStartIndex();
        int endOffset = currentOffset + length;
        currentOffset = endOffset;
        List<UChar> text = content.text();
        return aggregateResult(
                props.additionalText ? content : new CharactersAndAnnotations(content.annotations()),
                new CharactersAndAnnotations(new AnnotationRecord(null, props.atagType, content.asString(), text.get(0), text.get(text.size()-1)))
        );
    }

//    @Override
//    public String visitAddition(AdditionContext ctx) {
//        return handleAnnotation(ctx.block(), "addition");
//    }
//
//    @Override
//    public String visitAnnotation(AnnotationContext ctx) {
//        if (ctx.getChildCount()!=1) {
//            throw new IllegalStateException("Annotation should have exactly one child");
//        }
//        ParseTree annotation = ctx.getChild(0);
//        return super.visitAnnotation(ctx);
//    }

    @Override
    public CharactersAndAnnotations visitErrorNode(ErrorNode node) {
        System.out.printf("ErrorNode: %s\n", node.getText());
        return super.visitErrorNode(node);
    }

    @Override
    protected CharactersAndAnnotations aggregateResult(CharactersAndAnnotations aggregate, CharactersAndAnnotations nextResult) {
        List<UChar> text = new ArrayList<>();
        text.addAll(aggregate.text());
        text.addAll(nextResult.text());
        List<AnnotationRecord> annotations = new ArrayList<>();
        annotations.addAll(aggregate.annotations());
        annotations.addAll(nextResult.annotations());
        return new CharactersAndAnnotations(text, annotations);
    }

    @Override
    protected CharactersAndAnnotations defaultResult() {
        return new CharactersAndAnnotations(new LinkedList<>(), Collections.emptyList());
    }
}
