package uk.co.furniss.polyominoes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trial {
	private static final int LIMIT = 6;
	public static void main(String[] args) {
		Set<Polyom> polys = Collections.singleton(new Polyom("X"));
		
		for (int order = 2; order <= LIMIT; order++) {
			Set<Polyom> nextPolys = new HashSet<>();
			for (Polyom polyom : polys) {
				nextPolys.addAll(polyom.nextGeneration());
			}
//			System.out.println("Order " + order + " : " + nextPolys.size());
			List<Polyom> inOrder = new ArrayList<>(nextPolys);
			Collections.sort(inOrder);
			for (Polyom child : inOrder) {
//				System.out.println("   ." + child.show().replaceAll("/",".\n   .") + ".\n");
				System.out.println(child.show().replaceAll("","\t").replaceAll("/","\n") + "\n");
//				System.out.println("      ." + child.show() + ".");
			}
			System.out.println();
			polys = nextPolys;
		}
	}
	
	public static void main1(String[] args) {
		List<String> tests = new ArrayList<>(Arrays.asList( "XXX/ X / X ", "X  /XXX/X  "));
//				" XX/XX / X / X "));
		for (String test : tests) {
			Polyom original = new Polyom(test);
			Polyom best = original.makeBest();
			System.out.println("Original  ." + original.show() + ".      best ." + best.show() + ".");
			Set<Polyom> nextGen = best.nextGeneration();
			for (Polyom child : nextGen) {
				System.out.println("      ." + child.show() + ".");
			}
		}
	}

	
	public static void main0(String[] args) {
		List<String> tests = new ArrayList<>(Arrays.asList( "XXX/ X / X ", "X  /XXX/X  "));
		for (String test : tests) {
			Polyom original = new Polyom(test);
			Polyom best = original.makeBest();
			System.out.println("Original  ." + original.show() + ".      best ." + best.show() + ".");
		}
	}	
	
}
