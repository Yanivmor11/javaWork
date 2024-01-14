package javaWork;

import java.util.Scanner;

public class FatmaNewCheckers {

	static Scanner sc = new Scanner(System.in);
	static int redCount=12;//count how many red tools on the board
	static int whiteCount=12;//count how many white tools on the board
	static int countComTurns=0;
	public static int column,row;
	public static String [][] board = new String [8][8];


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
		if(board[startRow][startCol].equals("R") && board[endCol][endRow].equals("*"))
		{
			if(jumpROneSquare(strPosition) || jumpLOneSquare(strPosition))// Only takes step 1 (not including eating).
			{
				board[endRow][endCol]="R";//Moving according to the ASCII table from Char to number-(-48),and moving from the list position to the board position (1-).
				board[startRow][startCol]="*";
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
			whiteCount--;
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
		if(countComTurns > 0)// If the computer performed more than one turn. He will only be allowed to eat.
		{
			printAfterComTurn();
			String[] optionalEatings = findAllOptionalEatings();
	        if (optionalEatings.length > 0)  // If there are optional eating moves, choose one randomly.
	        {
	            int randomIndex = (int) (Math.random() * optionalEatings.length);
	            String chosenMove = optionalEatings[randomIndex];
	            computerEats(chosenMove);
	        }
	        else if(comCantMove(board))
	        {
	        	endGame("cantMove");
	        }
	        else
	        {
	        	countComTurns=0;
	        	playerPlay();
	        }
		}
		if(countComTurns == 0)
		{
			printComTurn();
			String[] optionalEatings = findAllOptionalEatings();
	        if (optionalEatings.length > 0)  // If there are optional eating moves, choose one randomly.
	        {
	            int randomIndex = (int) (Math.random() * optionalEatings.length);
	            String chosenMove = optionalEatings[randomIndex];
	            if (chosenMove != null && !chosenMove.isEmpty())
	            	computerEats(chosenMove);
	        } 
	        else if(comCantMove(board))
	        {
	        	endGame("cantMove");
	        }
	        else // If no eating moves, decide whether to move right with a 30% probability. 
	        { 
	        	if((legalMovesRight(board)) && (legalMovesLeft(board))) 
	        	{
	        		if(movingRight())
	        		{
	        			MoveToRightRandomly(board);
	        			playerPlay();
	        		}
	        		else
	        		{
	        			MoveToLeftRandomly(board);
	        			playerPlay();
	        		}
	        	}
	        	/*if(comCanMoveRight(i,j,board) && !comCanMoveRight(i,j,board))
	        	{

	        	}*/
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
		redCount--;
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
        String[] optionalEatingsArray = new String[32]; // Maximum possible size is 32 only the black squares.

        int index = 0; // Index to keep track of the array position.

        for (int i = 0; i < 8; i++) 
        {
            for (int j = 0; j < 8; j++) 
            {
                if (i + 2 < 8 && j + 2 < 8 && board[i + 1][j + 1].equals("W") && board[i + 2][j + 2].equals("*") && board[i + 1][j + 1].equals("R")) // Check for possible eating moves to the right.
                {
                    optionalEatingsArray[index++] = (i + 3) + "" + (j + 3) + "-" + (i + 1) + "" + (j + 1);
                }
                if (i + 2 < 8 && j - 2 >= 0 && board[i + 1][j - 1].equals("W") && board[i + 2][j - 2].equals("*") && board[i + 1][j - 1].equals("R")) // Check for possible eating moves to the left.
                {
                    optionalEatingsArray[index++] = (i + 3) + "" + (j - 1) + "-" + (i + 1) + "" + (j + 1);
                }
                if (i - 2 >= 0 && j + 2 < 8 && board[i - 1][j + 1].equals("W") && board[i - 2][j + 2].equals("*") && board[i - 1][j + 1].equals("R")) // Check for possible eating moves backward to the right.
                {
                    optionalEatingsArray[index++] = (i - 1) + "" + (j + 3) + "-" + (i + 1) + "" + (j + 1);
                }
                if (i - 2 >= 0 && j - 2 >= 0 && board[i - 1][j - 1].equals("W") && board[i - 2][j - 2].equals("*") && board[i - 1][j - 1].equals("R")) // Check for possible eating moves backward to the left.
                {
                    optionalEatingsArray[index++] = (i - 1) + "" + (j - 1) + "-" + (i + 1) + "" + (j + 1);
                }
            }
        }  
        String[] trimmedArray = new String[index];
        for (int i = 0; i < index; i++) {
			trimmedArray[i] = optionalEatingsArray[i];
        }
        
        return trimmedArray;
    }
    public static boolean legalMovesLeft(String[][] board) // Checks if there is any left move that the computer can do.
    {
    	for (int i = 1; i < 8; i++) // Iterate through the board to find all legal moves to the left
    	{
    		for (int j = 0; j < 7; j++) 
    		{
    			if (board[i][j].equals("W") && board[i - 1][j + 1].equals("*")) 
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }
    public static boolean legalMovesRight(String[][] board) // Checks if there is any right move that the computer can do.
    {
    	for (int i = 1; i < 8; i++) // Iterate through the board to find all legal moves to the left
    	{
    		for (int j = 1; j < 8; j++) 
    		{
    			if (board[i][j].equals("W") && board[i - 1][j - 1].equals("*")) 
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }
    public static String MoveToRightRandomly(String[][] board) 
    {
    	int countLegalMoves = 0;
    	String[] legalMoves = new String[64]; // Maximum possible size is 64
    	for (int i = 0; i < 8; i++) // Iterate through the board to find all legal moves to the left
    	{
    		for (int j = 0; j < 7; j++) 
    		{
    			if (board[i][j].equals("W") && board[i - 1][j + 1].equals("*")) 
    			{
    				legalMoves[countLegalMoves++] = (i + 1) + "" + (j + 1);
    			}
    		}
    	}
    	int randomIndex = (int) (Math.random() * countLegalMoves);
    	return legalMoves[randomIndex];
    }
    public static String MoveToLeftRandomly(String[][] board) 
    {
    	int countLegalMoves = 0;
    	String[] legalMoves = new String[64]; // Maximum possible size is 64
    	for (int i = 0; i < 8; i++) // Iterate through the board to find all legal moves to the left
    	{
    		for (int j = 1; j < 8; j++) 
    		{
    			if (board[i][j].equals("W") && board[i - 1][j - 1].equals("*")) 
    			{
    				legalMoves[countLegalMoves++] = (i + 1) + "" + (j + 1);
    			}
    		}
    	}
    	int randomIndex = (int) (Math.random() * countLegalMoves);
    	return legalMoves[randomIndex];
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