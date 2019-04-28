package Algorithms;

import java.util.ArrayList;
import java.util.Random;

import Classes.CommonSearcher;
import Classes.Solution;
import Classes.State;
import Interface.Searchable;
import Interface.Searcher;
import Interface.StateGrader;

public class HillClimbing<T> extends CommonSearcher<T> implements Searcher<T> {

	private long timeToRun;
	private StateGrader<T> grader;

	public HillClimbing(long timeToRun, StateGrader<T> grader) {
		
		this.timeToRun = timeToRun;
		this.grader=grader;

	}

	@Override
	public Solution<T> Search(Searchable<T> genericSearchable) {
		State<T> currentState = genericSearchable.getInitialState();
		Solution<T> solution = new Solution<T>();
		long time0 = System.currentTimeMillis();

		State<T> bestNeighborState = new State<T>();

		while (System.currentTimeMillis() - time0 < timeToRun) {

			ArrayList<State<T>> neighbors = new ArrayList<State<T>>(genericSearchable.getAllPossibleStates(currentState));
			
			if (Math.random() < 0.7) {
				
				bestNeighborState = getBestNeighborGrade(neighbors);
				
				if (bestNeighborState == null)
					bestNeighborState = currentState;
				
				if (genericSearchable.IsGoalState(bestNeighborState)) {
					solution = updateSolution(bestNeighborState, solution);

					return solution;
				}
				
				
				if (grader.grade(currentState) > grader.grade(bestNeighborState)) 
				{

					solution = updateSolution(bestNeighborState, solution);
					currentState = bestNeighborState;

				}

				else 
				{
					
					solution = updateSolution(currentState, solution);
				
				}

			}

			else {
				if(neighbors.size()<=0)
				{
					solution = updateSolution(currentState, solution);
					break;
				}
				Random r = new Random();
				int randomIndex = r.nextInt(neighbors.size());
				currentState = neighbors.get(randomIndex);
				solution = updateSolution(currentState, solution);
			}

		}
		
		return solution;
	}

	public long getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(long timeToRun) {
		this.timeToRun = timeToRun;
	}

	public StateGrader<T> getGrader() {
		return grader;
	}

	public void setGrader(StateGrader<T> grader) {
		this.grader = grader;
	}

	private Solution<T> updateSolution(State<T> currentSolution, Solution<T> solution) {
		solution.clear();
		do {
			solution.add(currentSolution.getState());
			currentSolution = currentSolution.getCameFrom();
		} while (currentSolution != null);
		return solution;
	}

	private State<T> getBestNeighborGrade(ArrayList<State<T>> neighbors) {

		State<T> bestNeighborState = null;
		double grade=0;
		for(int i=0; i<neighbors.size();i++)
		{
			double g = grader.grade(neighbors.get(i));
			if(g>grade)
			{
				grade = g;
				bestNeighborState=neighbors.get(i);
			}
		}
		
		return bestNeighborState;
	}

	@Override
	public int getNumberOfNodesEvaluated() {
		return 0;
	}

	}