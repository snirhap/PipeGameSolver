package Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

//import Algorithms.BestFirstSearch;
//import Algorithms.DFS;
//import Algorithms.HillClimbing;
//import Algorithms.BFS;
import Algorithms.BFS;
import Algorithms.BestFirstSearch;
import Algorithms.DFS;
import Algorithms.HillClimbing;
import Interface.CacheManager;
import Interface.ClientHandler;
import Interface.Solver;

public class MyClientHandler implements ClientHandler
{
	String solutionFilePath = null;

	String solutionDirectoryPath=null;

	CacheManager<PipeGameBoard> fileCM = new MyCacheManager(solutionDirectoryPath = "./pipeGameSolutions//");

	Solver<PipeGameBoard> solver = new MySolver<PipeGameBoard>(new BestFirstSearch<PipeGameBoard>()); // default searcher - hard coded
	
	Solution<PipeGameBoard> solution;

	@Override
	public void handleClient(InputStream in, OutputStream out) {
		try{
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(in));
			
			PrintWriter writer = new PrintWriter(out);
			
			String currentBoard = new String(getDataFromClient(inFromClient));

			String levelID = generateLevelID(currentBoard);
			
			Integer levelHashCode= levelID.hashCode();

			String solutionCommandsToClient=null;
			
			solutionFilePath = fileCM.load(levelHashCode.toString());

			if (fileCM.getExist()) 
			{
				String solutionStringFromFile = fileCM.getStringFromSolutionFile(solutionFilePath);
				solutionCommandsToClient = solutionStringBuilder(currentBoard, solutionStringFromFile);
				
			}
			else
			{

				PipeGameBoard boardToSolve = new PipeGameBoard(currentBoard);
				
				solution = solver.solve(boardToSolve);
				
				if(solution.get(0).isSolved())
					System.out.println("Solved");

				PipeGameBoard solutionBoard = solution.get(0);
				
				String stringSolutionBoard = solutionBoard.toString();
				
				solutionCommandsToClient= solutionStringBuilder(currentBoard,stringSolutionBoard);
				
				if(solution.get(0).isSolved())
				{
					fileCM.save(levelHashCode.toString(), solution.get(0).toString());
				}
			}
			
			sendCommandsToClient(solutionCommandsToClient, writer);
			
			fileCM.updateSolutionsMap(solutionDirectoryPath);
			
		} catch (Exception e) {
		}

	}
  
	private String getDataFromClient (BufferedReader inFromClient) throws IOException {
		
		String line = "";
		String currentBoard = "";
		do {
			line = inFromClient.readLine();
			currentBoard = currentBoard + line + "\n";
		} while (!(line.equals("done")));
		return currentBoard;
	}

	private void sendCommandsToClient(String solutionCommandsToClient, PrintWriter writer) throws InterruptedException 
	{
		String[] splitArr;
		String split=new String(solutionCommandsToClient);
		splitArr = split.split("\n");
		
		for(int i=0; i<splitArr.length;i++)
		{
			if(splitArr[i].length() >=5)
			{
				splitArr[i] += '\n';
				writer.write(splitArr[i]);
				writer.flush();
			}
		}
		
		writer.println("done\n");
		writer.flush();

	}

	private String generateLevelID(String board) {
		String levelID = "";

		for (int i = 0; i < board.length(); i++) {
			switch (board.charAt(i)) {
			case 's': {
				levelID += '1';
				break;
			}
			case 'g': {
				levelID += '2';
				break;
			}
			case '-':
			case '|': {
				levelID += '3';
				break;
			}
			case 'F':
			case 'L':
			case 'J':
			case '7': {
				levelID += '4';
				break;
			}
			case ' ': {
				levelID += '5';
				break;
			}
			case '\n': {
				levelID += '6';
				break;
			}
			default:
				break;
			}
		}

		return levelID;

	}

	private String solutionStringBuilder(String currentBoard, String solutionBoard) {

		Integer i = 0;
		Integer internalIndex = 0;
		String solutionCommands = new String();
		StringTokenizer currentBoardTokenizer = new StringTokenizer(currentBoard, "\n");
		StringTokenizer solutionBoardTokenizer = new StringTokenizer(solutionBoard, "\n");

		// create 2 arraysLists for each board, then compare each line of each board
		List<String> currentBoardArrayList = new ArrayList<String>();

		while (currentBoardTokenizer.hasMoreTokens()) {
			currentBoardArrayList.add(currentBoardTokenizer.nextToken());
		}

		List<String> solutionBoardArrayList = new ArrayList<String>();

		while (solutionBoardTokenizer.hasMoreTokens()) {
			solutionBoardArrayList.add(solutionBoardTokenizer.nextToken());
		}

		
		// build the commands String
		while (!currentBoardArrayList.get(i).equals("done")) {
			for (internalIndex = 0; internalIndex < currentBoardArrayList.get(i).length(); internalIndex++) {
				if (currentBoardArrayList.get(i).charAt(internalIndex) != solutionBoardArrayList.get(i).charAt(internalIndex)) 
				{
					solutionCommands += (i.toString() + "," + internalIndex.toString() + "," + timesToRotate(currentBoardArrayList.get(i).charAt(internalIndex), solutionBoardArrayList.get(i).charAt(internalIndex)));
					solutionCommands += "\n";
				}
			}

			i++;
		}

		return solutionCommands;

	}

	private String timesToRotate(char source, char dest) {
		
		if(source == 'L' && dest == 'F')
			return "1";
		else if (source == 'L' && dest == '7')
			return "2";
		else if (source == 'L' && dest == 'J')
			return "3";
		
		else if (source == 'F' && dest == 'L')
			return "3";
		else if (source == 'F' && dest == '7')
			return "1";
		else if (source == 'F' && dest == 'J')
			return "2";
		
		
		else if (source == '7' && dest == 'L')
			return "2";
		else if (source == '7' && dest == 'F')
			return "3";
		else if (source == '7' && dest == 'J')
			return "1";
		
		else if (source == 'J' && dest == 'L')
			return "1";
		else if (source == 'J' && dest == 'F')
			return "2";
		else if (source == 'J' && dest == '7')
			return "3";
			
		else if (source == '|' && dest == '-')
			return "1";
		else if (source == '-' && dest == '|')
			return "1";
		
		return "0";
		

	}
}