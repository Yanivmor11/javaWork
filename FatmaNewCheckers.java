package javaWork;

import java.util.Scanner;

public class FatmaNewCheckers {

	static Scanner sc = new Scanner(System.in);
	private final static int PlayersCount = 12;
	static int redCount = PlayersCount;//count how many red tools on the board
	static int whiteCount = PlayersCount;//count how many white tools on the board
	static int countComTurns = 0;
	static int countPlayerTurns = 0;
	static String eatPlayer = "";
	public static int column,row;
	public static String [][] board = new String [8][8];
	public static String [] WhitePlayers = {"8,8","8,6","8,4","8,2","7,1","7,3","7,5","7,7","6,2","6,4","6,6","6,8"}; // 12 players and position for example with 12-34
	public static String [] RedPlayers = {"1,1","1,3","1,5","1,7","2,2","2,4","2,6","2,8","3,1","3,3","3,5","3,7"}; // 12 players and position for example with 12-34;

	public static void main(String[] args) 
	{
		resetBoard();
		startGame();
	}
	public static void endGame(String endType)//The game ended for some reason. After that the game will restart.
	{
		switch (endType) {
			case "Surrendered", "ComputerWin" -> {
				System.out.println("Sorry, computer has won :( ");
				resetBoard();
				startGame();
			}
			case "userWin" -> {
				System.out.println("Congratulations, user has won :)");
				resetBoard();
				startGame();
			}
			case "cantMove" -> {
				System.out.println("Well, unfortunately it’s a Tie…");
				resetBoard();
				startGame();
			}
		}
	}
	public static void playerPlay()//Shows the player the board and tells him it's his turn.
	{
		printPlayerTurn();
		String strPosition;
		do
		{
			strPosition = sc.nextLine();
		}
		while(strPosition.length()==0);
		validInput(strPosition);
	}
	public static void validInput(String strPosition)// Check if the input of the user valid.
	{
		if(strPosition.equals("STOP"))
		{
			endGame("Surrendered");
		}
		else if(strPosition.length()!=5)
		{
			System.out.println();
			System.out.print("The input is not valid, please enter your move again.");
			playerPlay();
		}
		else 
		{

			for(int i=0; i<5; i++)
			{
				if(i!=2)
				{
					if (strPosition.charAt(i)<'1' || strPosition.charAt(i)>'8')
					{
						System.out.println();
						System.out.print("The input is not valid, please enter your move again.");
						playerPlay();
					}
				}
				else if((strPosition.charAt(2)!='-'))
				{
						System.out.println();
						System.out.print("The input is not valid, please enter your move again.");
						playerPlay();
				}
			}
			if(((strPosition.charAt(0)+strPosition.charAt(1))%2!=0) || ((strPosition.charAt(3)+strPosition.charAt(4))%2!=0))// An extreme case where we got a location of the white squares.
			{
				System.out.println();
				System.out.print("The input is not valid, please enter your move again.");
				playerPlay();
			}
			legalMove(strPosition);
		}
	}
	public static void legalMove(String strPosition)// Checking whether the move is legal.
	{
		int endRow=strPosition.charAt(0)-'1';//According to the ASKII table - (-48) died to the number, (-1) from the position on the board to the position in the formation.
		int endCol=strPosition.charAt(1)-'1';
		int startRow=strPosition.charAt(3)-'1';
		int startCol=strPosition.charAt(4)-'1';
		String start_position = startRow + "," + startCol;
		String end_position = endRow + "," + endCol;
		if(board[startRow][startCol].equals("R") && board[endCol][endRow].equals("*"))
		{
			if(jumpROneSquare(strPosition) || jumpLOneSquare(strPosition))// Only takes step 1 (not including eating).
			{
				changeBoard(startRow,startCol,endRow,endCol,'R');
				computerTurn();// The user made a correct move - the turn goes to the computer.
			}
			System.out.println();
			System.out.print("This move is invalid, please enter a new move.");
			playerPlay(); // If the user did illegal move.
		}
		else 
			if(userEats(strPosition))
			{
				String position=strPosition.substring(0,2);
				if(whiteCount==0) 
				{
					endGame("userWin");
				}
				else 
				{
					eatingMore(position);
				}
			}
			else
			{
				System.out.println();
				System.out.print("This move is invalid, please enter a new move.");
				playerPlay(); 
			}
	}
	public static boolean userEats(String strPosition) // A function that describes the user's eating behavior.
	{
		int endRow=strPosition.charAt(0)-49;
		int endCol=strPosition.charAt(1)-49;
		int startRow=strPosition.charAt(3)-49;
		int startCol=strPosition.charAt(4)-49;
		int midRow=(endRow+startRow)/2;
		int midCol=(endCol+startCol)/2;
		if(((jumpRTwoSquares(strPosition) && (eatRight(strPosition)))) || ((jumpLTwoSquares(strPosition) && (eatLeft(strPosition)))))
		{
			board[startRow][startCol]="*";
			board[endRow][endCol]="R";
			board[midRow][midCol]="*";
			moveRedPlayer(startRow,startCol,endRow,endCol);
			removeWhitePlayer(midRow,midCol);

			return true;
		}
		return false;
	}
	public static void eatingMore(String position)//check if the user can eat again,get the current position after first eating 
	{
		String[] optionalEatings = {"", "", ""};// {right, left, reverse}

		int row = position.charAt(0) - '1';
		int col = position.charAt(1) - '1';
		int multiPossible=0;
		if (row + 2 < 8 && col + 2 < 8 && board[row + 1][col + 1].equals("W") && board[row + 2][col + 2].equals("*")) // Check for possible eating moves to the right.
		{
			optionalEatings[0]= (row+3)+(col+3)+'-'+position;
			multiPossible++;
		}
		if (row + 2 < 8 && col - 2 >= 0 && board[row + 1][col - 1].equals("W") && board[row + 2][col - 2].equals("*")) // Check for possible eating moves to the left.
		{
			optionalEatings[1]= (row+3)+(col-1)+'-'+position;
			multiPossible++;
		}
		if (row - 2 >= 0 && col + 2 < 8 && board[row - 1][col + 1].equals("W") && board[row - 2][col + 2].equals("*")) // Check for possible eating moves backward to the right. 
		{
			optionalEatings[2]= (row-1)+(col+3)+'-'+position;
			multiPossible++;
		}
		else if (row - 2 >= 0 && col - 2 >= 0 && board[row - 1][col - 1].equals("W") && board[row - 2][col - 2].equals("*")) // Check for possible eating moves backward to the left. 
		{
			optionalEatings[3]= (row-1)+(col-1)+'-'+position;
			multiPossible++;
		}
		if(multiPossible>0)
		{
			playerPlayTimes(optionalEatings);
		}
		else
		{
			computerTurn();
		}
	}
	public static void playerPlayTimes(String[] optionalEatings)
	{
		printPlayerTurn();
		String strPosition;
		do
		{
			strPosition = sc.nextLine();
		}
		while(strPosition.length()==0);
		for (String optionalEating : optionalEatings) {
			if (strPosition.equals(optionalEating)) {
				userEats(strPosition);
			} else {
				System.out.println("This move is invalid, please enter a new move.");
				playerPlayTimes(optionalEatings);
			}
		}
	}
	public static void computerTurn() // computer turn.
	{
		if(comCantMove(board))
		{
			endGame("cantMove");
		}
		printAfterComTurn();
		if(countComTurns > 0)// If the computer performed more than one turn. He will only be allowed to eat.
		{
			String[] optionalEatings = findAllOptionalEatingsAfterEating(eatPlayer);
			if (optionalEatings.length > 0)  // If there are optional eating moves, choose one randomly.
			{
				int randomIndex = (int) (Math.random() * optionalEatings.length);
				eatPlayer = optionalEatings[randomIndex];
				computerEats(eatPlayer);
			}
			else
			{
				countComTurns=0;
				playerPlay();
			}
		}
		if(countComTurns == 0) {
			printComTurn();
			String[] optionalEatings = findAllOptionalEatings();
			if (optionalEatings.length > 0)  // If there are optional eating moves, choose one randomly.
			{
				int randomIndex = (int) (Math.random() * optionalEatings.length);
				String chosenMove = optionalEatings[randomIndex];
				eatPlayer = chosenMove;
				if (chosenMove != null && !chosenMove.isEmpty())
					computerEats(chosenMove);
			} else // If no eating moves, decide whether to move right with a 30% probability.
			{
				String[] legalMovesRight = legalMovesRight();
				String[] legalMovesLeft = legalMovesLeft();
				if (legalMovesLeft.length != 0 && legalMovesRight.length != 0) {
					if (movingRight()) {
						MoveToRightRandomly(legalMovesRight);
					} else {
						MoveToLeftRandomly(legalMovesLeft);
					}
					playerPlay();
				} else if (legalMovesLeft.length != 0) {
					MoveToLeftRandomly(legalMovesLeft);

				} else if (legalMovesRight.length != 0) {
					MoveToRightRandomly(legalMovesRight);

				} else {
					endGame("cantMove");
				}
				playerPlay();
			}
		}
	}
	public static void changeBoard(int beforeRow , int beforeColumn, int afterRow , int afterColumn , char player){
		board[beforeRow][beforeColumn]="*";
		board[afterRow][afterColumn]=""+player;
		if (player == 'W'){
			moveWhitePlayer(beforeRow,beforeColumn,afterRow,afterColumn);
		}
		else{
			moveRedPlayer(beforeRow,beforeColumn,afterRow,afterColumn);
		}
	}
	public static void moveWhitePlayer(int beforeRow,int beforeCol,int newRow,int newCol){
		for(int i=0;i<PlayersCount;i++){
			if(WhitePlayers[i].isEmpty()){
				continue;
			}
			int rowWhite = WhitePlayers[i].charAt(0)-49;
			int columnWhite = WhitePlayers[i].charAt(2)-49;
			if(rowWhite == beforeRow && columnWhite == beforeCol){
				WhitePlayers[i] = newRow + "," + newCol;
				break;
			}
		}
	}
	public static void moveRedPlayer(int beforeRow,int beforeCol,int newRow,int newCol){
		for(int i=0;i<PlayersCount;i++){
			if(RedPlayers[i].isEmpty()){
				continue;
			}
			int rowWhite = RedPlayers[i].charAt(0)-49;
			int columnWhite = RedPlayers[i].charAt(2)-49;
			if(rowWhite == beforeRow && columnWhite == beforeCol){
				RedPlayers[i] = newRow + "," + newCol;
				break;
			}
		}
	}
	public static void removeWhitePlayer(int beforeRow,int beforeCol){
		for(int i=0;i<PlayersCount;i++){
			if(WhitePlayers[i].isEmpty()){
				continue;
			}
			int rowRed = WhitePlayers[i].charAt(0)-49;
			int columnRed = WhitePlayers[i].charAt(2)-49;
			if(rowRed == beforeRow && columnRed == beforeCol){
				WhitePlayers[i] = "";
				whiteCount--;
				break;
			}
		}
	}
	public static void removeRedPlayer(int beforeRow,int beforeCol){
		for(int i=0;i<PlayersCount;i++){
			if(RedPlayers[i].isEmpty()){
				continue;
			}
			int rowRed = RedPlayers[i].charAt(0)-49;
			int columnRed = RedPlayers[i].charAt(2)-49;
			if(rowRed == beforeRow && columnRed == beforeCol){
				RedPlayers[i] = "";
				redCount--;
				break;
			}
		}
	}

	public static void computerEats(String chosenMove)
	{
		int newRow = chosenMove.charAt(0)-49;
		int newCol = chosenMove.charAt(1)-49;
		int beforeRow = chosenMove.charAt(3)-49;
		int beforeCol = chosenMove.charAt(4)-49;
		int midRow = (newRow + beforeRow)/2;
		int midCol = (newCol + beforeCol)/2;
		board[newRow][newCol]="W";
		board[beforeRow][beforeCol]="*";
		board[midRow][midCol]="*";
		moveWhitePlayer(beforeRow,beforeCol,newRow,newCol);
		removeRedPlayer(beforeRow,beforeCol);
		countComTurns++;
		if(redCount==0)
		{
			endGame("ComputerWin");
		}
		else
		{
			computerTurn();
		}
	}
    public static String[] findAllOptionalEatings() // Array to store all optional eating moves for the computer.
    {
		String [] canEat = new String[100];
		int count = 0;
		for (int i = 0; i<PlayersCount; i++){
			if (WhitePlayers[i].equals("") || WhitePlayers[i]==null ){
				continue;
			}
			int rowWhite = WhitePlayers[i].charAt(0)-49;
			int columnWhite = WhitePlayers[i].charAt(2)-49;
			for(int j = 0; j<PlayersCount; j++){
				if (RedPlayers[i].equals("") || RedPlayers[i]==null ){
					continue;
				}
				int rowRed = RedPlayers[j].charAt(0)-49;
				int columnRed = RedPlayers[j].charAt(2)-49;
				if ((rowWhite - 1 == rowRed && columnRed == columnWhite - 1)) {
					if (rowWhite - 2 > 0 && columnWhite - 2 > 0 && board[rowWhite - 2][columnWhite - 2].equals("*")) {
						canEat[count] =rowWhite + "" +columnWhite +"-"+(rowWhite - 2) +""+ (columnWhite - 2);
						count++;
					}
				}
				if(rowWhite - 1 == rowRed && columnRed == columnWhite + 1) {
					if (rowWhite - 2 > 0 && columnWhite + 2 <= 8 && board[rowWhite - 2][columnWhite + 2].equals("*")) {
						canEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite - 2) + "" + (columnWhite + 2);
						count++;
					}
				}
				if (countComTurns != 0){
					if ((rowWhite + 1 == rowRed && columnRed == columnWhite + 1)) {
						if (rowWhite + 2 < 8 && columnWhite + 2 < 8 && board[rowWhite + 2][columnWhite + 2].equals("*")) {
							canEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite + 2);
							count++;
						}
					}
					if(rowWhite + 1 == rowRed && columnRed == columnWhite - 1) {
						if (rowWhite + 2 < 8 && columnWhite - 2 > 0 && board[rowWhite + 2][columnWhite - 2].equals("*")) {
							canEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite - 2);
							count++;
						}
					}
				}
			}
		}
		String [] newCanEat = new String[count];
		for (int i=0; i<count;i++){
			newCanEat[i]=canEat[i];
		}
		return newCanEat;
    }
	public static String[] findAllOptionalEatingsAfterEating(String player) // player 1,2
	{
		String [] CanEat = new String[32];
		int count = 0;
		int rowWhite = player.charAt(0)-49;
		int columnWhite = player.charAt(2)-49;
		for(int i = 0; i<PlayersCount; i++){
			if (RedPlayers[i].equals("") || RedPlayers[i]==null ){
				continue;
			}
			int rowRed = RedPlayers[i].charAt(0) -49;
			int columnRed = RedPlayers[i].charAt(2) -49;
			if (rowWhite - 1 == rowRed && columnRed == columnWhite - 1) {
				if (rowWhite - 2 > 0 && columnWhite - 2 > 0 && board[rowWhite - 2][columnWhite - 2].equals("*")) {
					CanEat[count] =rowWhite + "" +columnWhite +"-"+(rowWhite - 2) +""+ (columnWhite - 2);
					count++;
				}
			}
			if(rowWhite - 1 == rowRed && columnRed == columnWhite + 1) {
				if (rowWhite - 2 > 0 && columnWhite + 2 <= 8 && board[rowWhite - 2][columnWhite + 2].equals("*")) {
					CanEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite - 2) + "" + (columnWhite + 2);
					count++;
				}
			}
			if (countComTurns != 0){
				if ((rowWhite + 1 == rowRed && columnRed == columnWhite + 1)) {
					if (rowWhite + 2 < 9 && columnWhite + 2 < 9 && board[rowWhite + 2][columnWhite + 2].equals("*")) {
						CanEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite + 2);
						count++;
					}
				}
				if(rowWhite + 1 == rowRed && columnRed == columnWhite - 1) {
					if (rowWhite + 2 < 9 && columnWhite - 2 > 0 && board[rowWhite + 2][columnWhite - 2].equals("*")) {
						CanEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite - 2);
						count++;
					}
				}
			}
		}
		return CanEat;
	}
	public static String[] findAllOptionalEatingsUser() // Array to store all optional eating moves for the computer.
	{
		String [] canEat = new String[100];
		int count = 0;
		for (int i = 0; i<PlayersCount; i++){
			if (RedPlayers[i].equals("") || RedPlayers[i]==null ){
				continue;
			}
			int rowRed = RedPlayers[i].charAt(0)-49;
			int columnRed = RedPlayers[i].charAt(2)-49;
			for(int j = 0; j<PlayersCount; j++){
				if (RedPlayers[i].equals("") || RedPlayers[i]==null ){
					continue;
				}
				int rowWhite = WhitePlayers[j].charAt(0)-49;
				int columnWhite = WhitePlayers[j].charAt(2)-49;
				if ((rowRed - 1 == rowWhite && columnWhite == columnRed - 1)) {
					if (rowRed - 2 > 0 && columnRed - 2 > 0 && board[rowRed - 2][columnRed - 2].equals("*")) {
						canEat[count] =rowRed + "" +columnRed +"-"+(rowRed - 2) +""+ (columnRed - 2);
						count++;
					}
				}
				if(rowRed - 1 == rowWhite && columnWhite == columnRed + 1) {
					if (rowRed - 2 > 0 && columnRed - 2 < 8 && board[rowRed - 2][columnRed - 2].equals("*")) {
						canEat[count] =rowRed + "" +columnRed +"-"+(rowRed - 2) +""+ (columnRed - 2);
						count++;
					}
				}
				if (countComTurns != 0){
					if ((rowWhite + 1 == rowRed && columnRed == columnWhite + 1)) {
						if (rowWhite + 2 < 8 && columnWhite + 2 < 8 && board[rowWhite + 2][columnWhite + 2].equals("*")) {
							canEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite + 2);
							count++;
						}
					}
					if(rowWhite + 1 == rowRed && columnRed == columnWhite - 1) {
						if (rowWhite + 2 < 8 && columnWhite - 2 > 0 && board[rowWhite + 2][columnWhite - 2].equals("*")) {
							canEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite - 2);
							count++;
						}
					}
				}
			}
		}
		String [] newCanEat = new String[count];
		for (int i=0; i<count;i++){
			newCanEat[i]=canEat[i];
		}
		return newCanEat;
	}
	public static String[] findAllOptionalEatingsAfterEatingUser(String player) // player 1,2
	{
		String [] CanEat = new String[32];
		int count = 0;
		int rowWhite = player.charAt(0)-49;
		int columnWhite = player.charAt(2)-49;
		for(int i = 0; i<PlayersCount; i++){
			if (WhitePlayers[i].equals("") || WhitePlayers[i]==null ){
				continue;
			}
			int rowRed = WhitePlayers[i].charAt(0) -49;
			int columnRed = WhitePlayers[i].charAt(2) -49;
			if (rowWhite - 1 == rowRed && columnRed == columnWhite - 1) {
				if (rowWhite - 2 > 0 && columnWhite - 2 > 0 && board[rowWhite - 2][columnWhite - 2].equals("*")) {
					CanEat[count] =rowWhite + "" +columnWhite +"-"+(rowWhite - 2) +""+ (columnWhite - 2);
					count++;
				}
			}
			if(rowWhite - 1 == rowRed && columnRed == columnWhite + 1) {
				if (rowWhite - 2 > 0 && columnWhite + 2 <= 8 && board[rowWhite - 2][columnWhite + 2].equals("*")) {
					CanEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite - 2) + "" + (columnWhite + 2);
					count++;
				}
			}
			if (countComTurns != 0){
				if ((rowWhite + 1 == rowRed && columnRed == columnWhite + 1)) {
					if (rowWhite + 2 < 9 && columnWhite + 2 < 9 && board[rowWhite + 2][columnWhite + 2].equals("*")) {
						CanEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite + 2);
						count++;
					}
				}
				if(rowWhite + 1 == rowRed && columnRed == columnWhite - 1) {
					if (rowWhite + 2 < 9 && columnWhite - 2 > 0 && board[rowWhite + 2][columnWhite - 2].equals("*")) {
						CanEat[count] = rowWhite + "" +columnWhite +"-"+(rowWhite + 2) + "" + (columnWhite - 2);
						count++;
					}
				}
			}
		}
		return CanEat;
	}
    public static String[] legalMovesLeft() // Checks if there is any left move that the computer can do.
    {
		String [] legal = new String [PlayersCount];
		int count = 0;
		for (int i=0; i<PlayersCount;i++){
			if(WhitePlayers[i].isEmpty() ||WhitePlayers[i] == null )
				continue;
			int rowWhite = WhitePlayers[i].charAt(0) -49;
			int columnWhite = WhitePlayers[i].charAt(2) -49;
			if (rowWhite>0 && columnWhite>0 && board[rowWhite-1][columnWhite-1].equals("*")){
				legal[count] =WhitePlayers[i];
				count++;
			}
		}
		String [] newlegal = new String [count];
		for (int i=0; i<count;i++){
			newlegal[i]=legal[i];
		}
		return newlegal;
	}
    public static String[] legalMovesRight() // Checks if there is any right move that the computer can do.
    {
		String [] legal = new String [PlayersCount];
		int count = 0;
		for (int i=0; i<PlayersCount;i++){
			if(WhitePlayers[i].isEmpty() ||WhitePlayers[i] == null )
				continue;
			int rowWhite = WhitePlayers[i].charAt(0) -49;
			int columnWhite = WhitePlayers[i].charAt(2) -49;
			if (rowWhite > 0 && columnWhite < 7 && board[rowWhite-1][columnWhite+1].equals("*")){
				legal[count] =WhitePlayers[i];
				count++;
			}
		}
		String [] newlegal = new String [count];
		for (int i=0; i<count;i++){
			newlegal[i]=legal[i];
		}
		return newlegal;
    }
    public static void MoveToRightRandomly(String [] legalMovesRight)
    {
    	int randomIndex = (int) (Math.random() * legalMovesRight.length);
		String move = legalMovesRight[randomIndex];
		int Row = move.charAt(0) -49;
		int Column = move.charAt(2) -49;
		int newRow = Row - 1;
		int newColumn = Column -1;
		changeBoard(Row,Column,newRow,newColumn,'W');
    }
    public static void MoveToLeftRandomly(String [] legalMovesLeft)
    {
		int randomIndex = (int) (Math.random() * legalMovesLeft.length);
		String move = legalMovesLeft[randomIndex];
		int Row = move.charAt(0)-49;
		int Column = move.charAt(2)-49;
		int newRow = Row - 1;
		int newColumn = Column + 1;
		changeBoard(Row,Column,newRow,newColumn,'W');
    }
    public static boolean comCantMove(String[][] board)
    {
    	for(int i=0; i<8; i++)
    		for(int j=0; j<8; j++)
    		{
    			if(board[i][j].equals("W"))
    			{
    				if(comCanMoveRight(i,j,board) || comCanMoveLeft(i,j,board))
    					return false;
    			}
    		}
    	return true;
    }
    public static boolean comCanMoveRight(int row, int col, String[][] board)//If the computer can move to the right - the visually impaired user can move back to the left.
    {
		return (row - 1 >= 0) && (col - 1 >= 0) && board[row - 1][col - 1].equals("*");
	}
    public static boolean comCanMoveLeft(int row, int col, String[][] board)//If the computer can move to the left - the visually impaired user can move back to the right.
    {
		return (row - 1 >= 0) && (col + 1 < 8) && board[row - 1][col + 1].equals("*");
	}
    public static boolean movingRight() // The odds shift to the right.
    {
        double randomValue = Math.random();
        return randomValue <= 0.3;        // Return true with 30% probability
    }
	public static boolean jumpRTwoSquares(String strPosition)
	{
		return (strPosition.charAt(3) + 2 == strPosition.charAt(0)) && (strPosition.charAt(4) + 2 == strPosition.charAt(1));
	}
	public static boolean eatRight(String strPosition)
	{
		return board[strPosition.charAt(3) - 48][strPosition.charAt(4) - 48].equals("W") ////Moving according to the ASCII table from Char to number-(-48),and moving from the list position to the board position (1-) and check one place up (1+).
				&& board[strPosition.charAt(0) - 49][strPosition.charAt(1) - 49].equals("*");
	}
	public static boolean jumpLTwoSquares(String strPosition)
	{
		return (strPosition.charAt(3) + 2 == strPosition.charAt(0)) && (strPosition.charAt(4) - 2 == strPosition.charAt(1));
	}
	public static boolean eatLeft(String strPosition)
	{
		return board[strPosition.charAt(3) - 48][strPosition.charAt(4) - 50].equals("W")  // ASCII and positions.
				&& board[strPosition.charAt(0) - 49][strPosition.charAt(1) - 49].equals("*");
	}
	public static boolean jumpROneSquare(String strPosition) // Checks if we have moved one step to the right.
	{
		return (strPosition.charAt(3) + 1 == strPosition.charAt(0)) && (strPosition.charAt(4) + 1 == strPosition.charAt(1));
	}
	public static boolean jumpLOneSquare(String strPosition) // Checks if we have moved one step to the left.
	{
		return (strPosition.charAt(3) + 1 == strPosition.charAt(0)) && (strPosition.charAt(4) + 1 == strPosition.charAt(1));
	}
	public static boolean Winner()// The user wins when - there are no white tools.
	{
		return (whiteCount == 0) || (redCount == 0);
	}
	public static void whiteSquare(String[][] board) // Places a line in the white squares of the board (Without any change throughout the game).
	{
		for(int i=0; i<=7; i++) // Goes over rows.
			for(int j=0; j<=7; j++) // Goes over columns.
			{
				if((i+j)%2!=0)
					board[i][j]="-";
			}
	}
	public static void redVessels(String[][] board)// Starting position of red vessels.
	{
		for(int i=0; i<=2; i++) 
			for(int j=0; j<=7; j++) 
			{
				if((i+j)%2==0)
					board[i][j]="R";
			}		
	}
	public static void whiteVessels(String[][] board)// Starting position of white vessels.
	{
		for(int i=5; i<=7; i++) 
			for(int j=0; j<=7; j++) 
			{
				if((i+j)%2==0)
					board[i][j]="W";
			}	
	}
	public static void Asterisk(String[][] board) //Places the asterisks on the board
	{
		for(int i=3; i<=4; i++) 
			for(int j=0; j<=7; j++)
			{
				if((i+j)%2==0)
					board[i][j]="*";
			}
	}
	public static void Print(String[][] board) //Prints the board
	{
		for(int i=7; i>=0; i--) 
		{
			for(int j=0; j<=7; j++)
			{
				System.out.print(board[i][j]+"	");
			}
			System.out.println();
		}
	}
	public static void printAfterComTurn() 
	{
		System.out.println("Computer has played.");
		System.out.println("The board:");
		Print(board);
		System.out.println();
		System.out.println("It's your turn, please enter your move.");

	}
	public static void printComTurn()
	{
		System.out.println();
		System.out.println("The board:");
		Print(board);
		System.out.println();
	}
	public static void printPlayerTurn()
	{
		System.out.println();
		System.out.println("The board:");
		Print(board);
		System.out.println();
		System.out.println("It's your turn, please enter your move.");
	}
	public static void resetBoard() // Reset the array.
	{
		whiteSquare(board); 
		redVessels(board);
		whiteVessels(board);
		Asterisk(board);
		redCount=12;
		whiteCount=12;
		countComTurns=0;
	}
	public static void startGame() //Opening question and transition to the board.
	{
		System.out.println("Welcome to Fatma Checkers. To start the game press 1, to exit press 0:");
		int userInput= sc.nextInt();
		if(userInput==0)
		{
			endGame("Surrendered");
		}
		else
		{
			playerPlay();
		}
	}
}