package regular;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by liaoli
 * date: 2019/6/25
 *
 * @author: liaoli
 */
public class NFA {

    public static void main(String[] args) {

        //( ) * cat |     ab(a|bc)*cd*
        //nfa.test("ab(a|bc)*cd*");
        test("(a|b)*abb");
        test(" a(b|c)*");
        test("fee|fie");
        //test(" who|what|where");

    }
    public static void test(String input){
        NFA nfa = new NFA();
        DFA dfa = new DFA();
        FaGraph faGraph = nfa.thompsonCons(input);
        System.out.println(faGraph.toGraphViz());
        FaGraph dfaGraph = dfa.subsetCons(faGraph);
        System.out.println(dfaGraph.dfaToGraphViz());
        FaGraph hopcroft = dfa.hopcroft(dfaGraph.cloneGraph());
        System.out.println(hopcroft.toGraphViz());

    }
    private FaGraph thompsonCons(String input) {
        FaGraph faGraph = process(input);
        faGraph.getStart().setStart(true);
        faGraph.getEnd().setAccepted(true);
        faGraph.setInput(input);
        return faGraph;
    }

    private FaGraph process(String input) {
        char[] chars = input.toCharArray();
        int pos = 0;
        Set<Character> characters = new HashSet<>();
        Stack<OptEnum> operatorStack = new Stack<>();
        Stack<FaGraph> operandStack = new Stack<>();
        while (pos < chars.length) {
            if (chars[pos] == OptEnum.LEFT_PAREN.getOpt()) {
                if (pos - 1 >= 0 && isNormalChar(chars[pos - 1])) {
                    operatorStack.push(OptEnum.CAT);
                }
                operatorStack.push(OptEnum.LEFT_PAREN);
                pos++;
            } else if (chars[pos] == OptEnum.RIGHT_PAREN.getOpt()) {
                while (!operatorStack.empty() && !OptEnum.LEFT_PAREN.equals(operatorStack.peek())) {
                    OptEnum pop = operatorStack.pop();
                    FaGraph pop1 = operandStack.pop();
                    FaGraph pop2 = operandStack.pop();
                    if (OptEnum.CAT.equals(pop)) {
                        FaGraph faGraph = FaGraph.reduceCat(pop2, pop1);
                        operandStack.push(faGraph);
                    } else {
                        FaGraph faGraph = FaGraph.reduceAlt(pop2, pop1);
                        operandStack.push(faGraph);
                    }
                }
                if (operatorStack.empty()) {
                    throw new RuntimeException("不匹配的括號");
                }
                operatorStack.pop();
                pos++;

            } else if (chars[pos] == OptEnum.STAR.getOpt()) {
                int next = pos + 1;
                while (next < chars.length && chars[next] == OptEnum.STAR.getOpt()) {
                    next++;
                }
                if (operandStack.empty()) {
                    throw new RuntimeException("找不到匹配*的表达式");
                }

                FaGraph pop = operandStack.pop();
                FaGraph faGraph = FaGraph.reduceStar(pop);
                operandStack.push(faGraph);
                pos = next;

            } else if (chars[pos] == OptEnum.Alt.getOpt()) {
                while (!operatorStack.empty()
                        && OptEnum.Alt.getPriority() <= operatorStack.peek().getPriority()
                        && !OptEnum.LEFT_PAREN.equals(operatorStack.peek())) {
                    OptEnum top = operatorStack.pop();
                    FaGraph pop1 = operandStack.pop();
                    FaGraph pop2 = operandStack.pop();
                    if (OptEnum.CAT.equals(top)) {
                        FaGraph faGraph = FaGraph.reduceCat(pop2, pop1);
                        operandStack.push(faGraph);
                    } else {
                        FaGraph faGraph = FaGraph.reduceAlt(pop2, pop1);
                        operandStack.push(faGraph);
                    }
                }
                operatorStack.push(OptEnum.Alt);
                pos++;
            } else {
                if (pos - 1 >= 0 && (isNormalChar(chars[pos - 1]) || OptEnum.RIGHT_PAREN.getOpt() == chars[pos - 1]
                        || OptEnum.STAR.getOpt() == chars[pos - 1])) {
                    while (!operatorStack.empty()
                            && OptEnum.CAT.getPriority() <= operatorStack.peek().getPriority()) {
                        OptEnum top = operatorStack.pop();
                        FaGraph pop1 = operandStack.pop();
                        FaGraph pop2 = operandStack.pop();
                        if (OptEnum.CAT.equals(top)) {
                            FaGraph faGraph = FaGraph.reduceCat(pop2, pop1);
                            operandStack.push(faGraph);
                        } else {
                            FaGraph faGraph = FaGraph.reduceAlt(pop2, pop1);
                            operandStack.push(faGraph);
                        }
                    }
                    operatorStack.push(OptEnum.CAT);
                    operandStack.push(FaGraph.fromChar(chars[pos]));
                    characters.add(chars[pos]);
                    pos++;
                } else {
                    operandStack.push(FaGraph.fromChar(chars[pos]));
                    characters.add(chars[pos]);
                    pos++;
                }

            }
        }
        while (!operatorStack.empty()) {
            OptEnum top = operatorStack.pop();
            FaGraph pop1 = operandStack.pop();
            FaGraph pop2 = operandStack.pop();
            if (OptEnum.CAT.equals(top)) {
                FaGraph faGraph = FaGraph.reduceCat(pop2, pop1);
                operandStack.push(faGraph);
            } else {
                FaGraph faGraph = FaGraph.reduceAlt(pop2, pop1);
                operandStack.push(faGraph);
            }
        }
        FaGraph pop = operandStack.pop();
        pop.setCharacterSet(characters);
        return pop;
    }

    private boolean isNormalChar(char ch) {
        for (OptEnum value : OptEnum.values()) {
            if (ch == value.getOpt()) {
                return false;
            }
        }
        return true;
    }

}
