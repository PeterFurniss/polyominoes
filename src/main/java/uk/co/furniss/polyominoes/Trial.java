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
		Polyom monomino = new Polyom("X");
		writeForExcel("1", monomino);
		Set<Polyom> polys = Collections.singleton(monomino);
		
		for (int order = 2; order <= LIMIT; order++) {
//			System.out.println("----------------");
			Set<Polyom> nextPolys = new HashSet<>();
			for (Polyom polyom : polys) {
				nextPolys.addAll(polyom.nextGeneration());
			}
//			System.out.println("Order " + order + " : " + nextPolys.size());
			List<Polyom> inOrder = new ArrayList<>(nextPolys);
//			if (order == LIMIT) {
//				Polyom.dbg = true;
//			}
			Collections.sort(inOrder);
			String orderString = Integer.toString(order);
			for (Polyom child : inOrder) {
				writeForExcel(orderString, child);
			}
			polys = nextPolys;
		}
	}

	private static void writeForExcel( String orderString, Polyom child )
	{
//		System.out.println(child.getRowCount() + " : " + child.getColCount() + "   :" + child);
		System.out.println(child.show().replaceAll("","\t").replaceAll("X", orderString).replaceAll("/","\n") + "\n");
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
