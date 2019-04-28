package Interface;

import Classes.Solution;

public interface Solver<T> {

	Solution<T> solve(Searchable<T> s);
	
}
