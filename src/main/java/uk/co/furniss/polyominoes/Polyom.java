package uk.co.furniss.polyominoes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Polyom implements Comparable<Polyom> {

	private final List<List<Boolean>> cells;
	private final int nr;
	private final int nc;

	Polyom(String in) {
		cells = new ArrayList<>();
		
		String [] rows = in.split("/");
		nr = rows.length;
		nc = rows[0].length();
		for (String row : rows) {
			if (row.length() != nc) {
				throw new RuntimeException("different length of rows");
			}
			List<Boolean> ro = new ArrayList<>();
			cells.add(ro);
			for (int c = 0; c < nc; c++) {
				ro.add( row.charAt(c) == 'X' );
			}
		}
	}
	
	Polyom makeBest() {
		Polyom better = this;
		if (nr > nc) {
			// need to rotate
			better = rotateWiddershins();
		}
//		System.out.println("rotated   ." + better.show() + ".");
		better = bestOrientation(better);
		if (nr == nc) {
			// rotate to check again
			Polyom rot = rotateWiddershins();
			rot = bestOrientation(rot);
			if (better.compareTo(rot) > 0) {
				better = rot;
			}
		}

		
		return better;
	}

	private Polyom bestOrientation(Polyom start) {
		Polyom better = start;
		Polyom lr = better.flipLeftRight();
		Polyom tb = better.flipTopBottom();
		Polyom both = tb.flipLeftRight();
		if (better.compareTo(lr) > 0) {
			better = lr;
		}
		if (better.compareTo(tb) > 0) {
			better = tb;
		}
		if (better.compareTo(both) > 0) {
			better = both;
		}
		return better;
	}

	public int compareTo(Polyom other) {
		// assumme packed
		if  (nr == other.getRowCount()) {
			// as a (questionable) convention, treat wide as higher
			if (nc == other.getColCount()) {
				for (int r = nr-1; r >= 0; r--) {
					List<Boolean> ours = cells.get(r);
					List<Boolean> theirs = other.cells.get(r);
					for (int c = nc-1; c >= 0; c--) {
						if (ours.get(c)) {
							if (!theirs.get(c)) {
	//							System.out.println("cell at " + r + ", " + c + ": this");
								return 1;
							}
						} else if (theirs.get(c)) {
	//						System.out.println("cell at " + r + ", " + c + ": that");
							return -1;
						}
					}
				}
				return 0;
			} else {
				return nc - other.getColCount();
			}
		} else {
			return nr - other.getRowCount();
		}
	}
	
	Polyom(List<List<Boolean>> givenCells) {
		cells = givenCells;
		nr = givenCells.size();
		nc = givenCells.get(0).size();
	}
	
	int getRowCount() {
		return nr;
	}
	
	int getColCount() {
		return nc;
	}
	
	String show() {
		return cells.stream().map(r -> r.stream().map(cell -> cell ? "X" : " ")
				.collect(Collectors.joining()))
				.collect(Collectors.joining("/"));
	}
	
	Polyom pruneEmpties() {
		// may have extra rows or columns
		List<List<Boolean>> result = cells;
		
		// check top row
		if (!result.get(0).stream().anyMatch(Boolean::booleanValue)) {
			result.remove(0);
		}
		// check bottom row
		if (!result.get(result.size()).stream().anyMatch(Boolean::booleanValue)) {
			result.remove(result.size());
		}
		//check left column 
		if (!result.stream().anyMatch(r -> r.get(0))) {
			result.stream().map(r -> r.remove(0)).close();
		}
		//check right column
		int cc = result.get(0).size();
		if (!result.stream().anyMatch(r -> r.get(cc))) {
			result.stream().map(r -> r.remove(cc)).close();
		}
		return new Polyom(result);
	}
	
	Polyom rotateWiddershins() {
		List<List<Boolean>> result = new ArrayList<>();
		for (int c = cells.get(0).size()-1 ; c >= 0; c--) {
			List<Boolean> newRow = new ArrayList<>();
			result.add(newRow);
			for (List<Boolean> oldRow : cells) {
				newRow.add(oldRow.get(c));		
			}
		}
		return new Polyom(result);
	}
	
	Polyom flipTopBottom() {
		List<List<Boolean>> result = new ArrayList<>();
		for (int r = cells.size()-1; r >= 0; r--) {
			result.add(cells.get(r));
		}
		return new Polyom(result);
	}

	Polyom flipLeftRight() {
		List<List<Boolean>> result = new ArrayList<>();
		for (List<Boolean> row : cells) {
			List<Boolean> newRow = new ArrayList<>();
			result.add(newRow);
			for (int c = nc-1; c >= 0 ; c--) {
				newRow.add(row.get(c));
			}
		}
		return new Polyom(result);
	}
	
	Set<Polyom> nextGeneration() {
		Set<Polyom> candidates = new HashSet<>();
		
		// first, just tweak within the box
		for (int rr = 0 ; rr < nr; rr++) {
			List<Boolean> row = cells.get(rr);
			for (int cc = 0; cc < nc ; cc++) {
				if (!row.get(cc)) {
					// it's a space - is it next to a cell
					if ( (rr > 0 && cells.get(rr-1).get(cc)) 
					||   (rr < nr-1 && cells.get(rr+1).get(cc))
					||   (cc > 0 && row.get(cc-1))
					||   (cc < nc-1 && row.get(cc+1)) ) {
						// take a copy
						List<List<Boolean>> expand = new ArrayList<>();
						for (int r=0; r < nr; r++) {
							expand.add(new ArrayList<>(cells.get(r)));
						}
						expand.get(rr).set(cc, true);
						candidates.add(new Polyom(expand).makeBest());
					}
				}
			}

			// for the edges, modify by extending the bottom row, then do it with the
			// other orientations
			extendBottomRow(candidates);
			flipTopBottom().extendBottomRow(candidates);
			Polyom rotation = rotateWiddershins();
			rotation.extendBottomRow(candidates);
			rotation.flipTopBottom().extendBottomRow(candidates);
			
		}
		return candidates;
	}

	
	public void extendBottomRow(Set<Polyom> candidates) {
		// go through the bottom row. if set, make a copy with an extra bottom row
		List<Boolean> lastRow = cells.get(nr-1);
		for (int cc=0; cc < nc; cc++) {
			if ( lastRow.get(cc)) {
				// take a copy
				List<List<Boolean>> expand = new ArrayList<>();
				for (int r=0; r < nr; r++) {
					expand.add(new ArrayList<>(cells.get(r)));
				}
				List<Boolean> newTop = new ArrayList<>();
				for (int c=0; c < nc; c++) {
					newTop.add(false);
				}				
				newTop.set(cc, true);
				expand.add(newTop);
				candidates.add(new Polyom(expand).makeBest());
				
			}
			
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cells == null) ? 0 : cells.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Polyom other = (Polyom) obj;
		if (nr != other.nr)
			return false;
		if (nc != other.nc)
			return false;
		if (!cells.equals(other.cells))
			return false;
		return true;
	}
}
