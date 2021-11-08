/*************************************************************************
* @Author John Morrisett
* @since 11/21/2020
* Program: Checkers.java
* Purpose:  Defines a class that can play checkers.  All of the standard
            rules for checkers, including double jumps and obtaining "queen"
            pieces apply.
         
* NOTES AND CONTROLS:

*********************SPACE will end the turn**********************

Click on a piece that you own to "select" it.  If that piece has valid moves,
it will be highlighted.  If not, it will not be selected.

Then, click on the empty square that you want to move to.  If an enemy piece
is one square away from you, and the square behind that is empty, you can
perform a jump.

-Note:  The game will not automatically end the turn after jumping, because
part of checkers is noticing that you can make double, triple, quadruple etc.
jumps.  If the game automatically ended your turn when you didn't have any more
double jumps, it would be too easy for the player to spot them.  Press SPACE
for easier turn-ending.
********************************************************************/

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;

import javafx.geometry.Pos;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import javafx.scene.input.KeyCode;

public class Checkers extends Application{
   
   //Checkers links together the disparate parts of the program
   
   //defining some important variables
   Object nullObj = null;
   
   //*************************************************
   //the coordGrid is central for keeping score of the game
   public Cell[][] coordGrid = new Cell[8][8];
   //*************************************************
   
   public char turn = 'R';
   public String hasMoved = "none";
   public Cell selectedCell = null;
   public Cell[][] currentMoveList = new Cell[4][2];
   public int redPieces = 12;
   public int bluePieces = 12;
   public int redWins = 0;
   public int blueWins = 0;
   
   
   //the different elements of the program.  Organized by tree.
   //e.g. an item with 3 next to it belongs to the 2 item directly below it.
 
   //define topping - 4
   Rectangle topping = new Rectangle(700, 40, Color.RED);
   
   //alertText
   Text alertText = new Text("");
   
   //header - 3
   StackPane header = new StackPane(topping, alertText);
   
   //backfill - 5
   Rectangle backfill = new Rectangle(480, 480, Color.BLACK);
   
   //grid - 5
   GridPane grid = resetGrid();
   
   //define background - 4
   StackPane background = new StackPane(backfill, grid);
   
   //greyFill - 5
   Rectangle greyFill = new Rectangle(220, 480, Color.GAINSBORO);
   
   //define endTurn button - 6
   Button endTurn = new Button("End Turn");
   
   //define concede button - 6
   Button concede = new Button("Concede Game");
   
   //menuControl - 5
   VBox menuControl = new VBox(30, endTurn, concede);
   
   //menu - 4
   StackPane menu = new StackPane(greyFill, menuControl);
   
   //gameUI - 3
   FlowPane gameUI = new FlowPane(background, menu);
   
   //setting - 2
   VBox setting = new VBox(header, gameUI);
   
   //scene - 1
   HBox scene = new HBox(setting);
   
   
   
   //startMenuBackground -3
   Rectangle startMenuBackground = new Rectangle(700, 520, Color.GAINSBORO);
   
   //welcomeText - 4
   Text welcomeText = new Text("Welcome to Checkers!");
   
   //define newGame button - 4
   Button newGameButton = new Button("New Game");
   
   //startMenuControl - 3
   VBox startMenuControl = new VBox(40, welcomeText, newGameButton);
   
   //startMenu -2
   StackPane startMenu = new StackPane(startMenuBackground, startMenuControl);
   
   //startScreen - 1
   HBox startScreen = new HBox(startMenu); 
   
   StackPane transitionBox = new StackPane(startScreen);
   Scene transitionScene = new Scene(transitionBox, 700, 520);
   
   public void setUIStandards(){
      //sets the above objects to be correctly oriented
   
      //below makes it so that a turn can be ended with the spacebar.
      transitionBox.requestFocus();
      transitionBox.setOnKeyPressed(e ->{
         if (e.getCode() == KeyCode.SPACE && hasMoved.equals("double")){
            setNewTurn();
         }
      });
      
      //defines endTurn, which ends the turn in the case of a jump.
      endTurn.setDisable(true);
      endTurn.setOnMouseClicked(e->setNewTurn());

      //concede throws the game to the other player
      header.setAlignment(Pos.CENTER);
      concede.setOnMouseClicked(e->endGame(0));
      menuControl.setAlignment(Pos.CENTER);
      menu.setAlignment(Pos.CENTER);
      startMenuControl.setAlignment(Pos.CENTER);
      newGameButton.setOnMouseClicked(e->handleNewGameAction());
   }

   @Override
   public void start(Stage primaryStage){
      setUIStandards();
      primaryStage.setTitle("Checkers");
      primaryStage.setScene(transitionScene);
      primaryStage.show();
   }
   
//*************************************************************************************
//Cell class

   public class Cell extends Pane{
      //the Cell class incorporates most of the functionality of the program
      public int row = -1;
      public int column = -1;
      public String pieceType = "NA";
      
      public Cell(){
      
         //no args constructor.  Not ever used.
         this.setPrefSize(60, 60);
         this.setStyle("-fx-background-color: " + Color.WHITE);
         this.row = -1;
         this.column = -1;
         setPieceType("NA");
         this.setOnMouseClicked(e -> handleAction());
      }
      
      
      public Cell(int row, int column){
      
         //row and column constructor.
         this.setPrefSize(60, 60);
         this.setStyle("-fx-background-color: White; -fx-border-color:  Black");
         this.row = row;
         this.column = column;
         setPieceType("NA");
         this.setOnMouseClicked(e -> handleAction());
      }
      
      public Cell(int row, int column, String pieceType){
      
         //row, column, and pieceType constructor.  Designates what kind of cell a given type
         //will be.
         
         this.setPrefSize(60, 60);
         this.setStyle("-fx-background-color: White; -fx-border-color:  Black");
         this.row = row;
         this.column = column;
         setPieceType(pieceType);
         this.setOnMouseClicked(e -> handleAction());
      }
      
      private void handleAction(){
      
         //handleAction can do one of three things.
      
         //the below defines the two actions that can be taken before a jump action.
         if (hasMoved.equals("none")){
            if (selectedCell == this){
            
               //deselect action.  If a cell that is selected is clicked on, deselect it.
               deselectAll();
               setNull();
            }
            else{
            
               //select action.  If this piece belongs to you, and can move, select it.
               if (turn == pieceType.charAt(0)){
                  boolean hasMoves = false;
                  this.setMoves();
                  for (int i = 0; i < currentMoveList.length; i++){
                     if (currentMoveList[i][0] != nullObj)
                        hasMoves = true;
                  }
                  if (hasMoves){
                     selectCell(this);
                  }
               }
            }//end of else
         }//end of the hasMoved
         
         //below is responsible for the moving actions.  When an empty square is clicked on, check if it can be moved to.
         if (pieceType.equals("NA")){
            if (selectedCell != nullObj){
               for (int i = 0; i < currentMoveList.length; i++){
                  //when a cell is clicked on, check if it is a valid move from the selected cell.
                  
                  if (currentMoveList[i][0] == this){
                  
                     //if this is valid, the below executes a move.
                     this.setPieceType(selectedCell.pieceType);
                     selectedCell.setPieceType("NA");
                     selectCell(this);
                     
                     //the below determines if this was a simple move (single), or jump (double)
                     hasMoved = "single";
                     if (currentMoveList[i][1] != nullObj){
                        currentMoveList[i][1].setPieceType("NA");
                        hasMoved = "double";
                        endTurn.setDisable(false);
                        if (turn == 'R')
                           bluePieces--;
                        else if (turn == 'B')
                           redPieces--;
                     }

                     selectedCell.setMoves();
                     if (selectedCell.pieceType.equals("RP") && selectedCell.column == 7){
                     
                        //if red reaches the end, make that piece a queen.
                        selectedCell.setPieceType("RQ");
                        setNewTurn();
                        break;
                     }
                     if (selectedCell.pieceType.equals("BP") && selectedCell.column == 0){
                     
                        //if blue reaches the end, make that piece a queen.
                        selectedCell.setPieceType("BQ");
                        setNewTurn();
                        break;
                     }
                     if (hasMoved.equals("single"))
                        //if this was a single jump, just move to the next player.
                        setNewTurn();
                     break;
                  }
               }
            }
         }//end of if
         
         //debugging information
         System.out.println("\n\n ");
         System.out.println("hasMoved? " + hasMoved);
         System.out.println("selectedCell? " + selectedCell);
         System.out.println("this Cell? " + this);
         System.out.println("type of piece? " + this.pieceType);
         System.out.println("Move list: ");
         for (int i = 0; i < currentMoveList.length; i++){
            System.out.println(currentMoveList[i][0]);
         }
   
      }
      
      public void setMoves(){
      
         //method determines the four available moves for any given position.
         setNull();
            
         //check the red directions
         if (this.pieceType.charAt(0) == 'R' || 
            this.pieceType.charAt(1) == 'Q'){
            
            checkDirection(-1, 1);
            checkDirection(1, 1);
         }
         
         //check the blue directions
         if (this.pieceType.charAt(0) == 'B' || 
            this.pieceType.charAt(1) == 'Q'){
            
            checkDirection(-1, -1);
            checkDirection(1, -1);
         }
      }
      
      public void checkDirection(int rowDir, int columnDir){
      
         //checkDirection checks a given direction for valid moves.
         char oppose = 'R';
         if (turn == 'R')
            oppose = 'B';
         
         //column/rowBound determines if a piece is in an extreme bound, like 
         //row 0 or 7.  If this is true, the piece cannot move up or down respectively.
         int columnBound = column + columnDir;
         int rowBound = row + rowDir;
         
         int currentIndex = 0;
         if (rowDir > 0)
            currentIndex += 1;
         if (columnDir > 0)
            currentIndex += 2;
            
         //below checks if a single space move is available.  This kind of move is only
         //available before a jump (you cannot jump and then move one single space)
         if ((columnBound > -1 && columnBound < 8) && 
            (rowBound > -1 && rowBound < 8) && hasMoved.equals("none")){
            if (coordGrid[column+columnDir][row+rowDir].pieceType.equals("NA")){

               currentMoveList[currentIndex][0] = coordGrid[column+columnDir][row+rowDir];
               currentMoveList[currentIndex][1] = null;
            }
         }
         
         //below checks if a double jump is available.  This kind of move is always
         //available, even if the player has already jumped.
         if ((columnBound + columnDir > -1 && columnBound + columnDir < 8) &&
            (rowBound + rowDir > -1 && rowBound + rowDir < 8) && 
            (hasMoved.equals("double") || hasMoved.equals("none"))){
            if (coordGrid[column+(2*columnDir)][row+(2*rowDir)].pieceType.equals("NA") &&
               coordGrid[column+columnDir][row+rowDir].pieceType.charAt(0) == oppose){
               
               currentMoveList[currentIndex][0] = coordGrid[column+(2*columnDir)][row+(2*rowDir)];
               currentMoveList[currentIndex][1] = coordGrid[column+columnDir][row+rowDir];
            }
         }
      }
      
      public void setPieceType(String pieceType){
      
         //setPieceType changes a cell to contain the information needed, and display it visually.
         this.pieceType = pieceType;
         this.getChildren().clear();
         
         //basic pawn piece
         Circle c = new Circle(30, 30, 25);
         c.setStroke(Color.BLACK);
         
         //queen piece
         Circle q = new Circle(30, 30, 10);
         q.setStroke(Color.BLACK);
         q.setFill(Color.BLUEVIOLET);
         
         if (pieceType.charAt(0) == 'R'){
            c.setFill(Color.RED);
            this.getChildren().add(c);
         }
         else if (pieceType.charAt(0) == 'B'){
            c.setFill(Color.BLUE);
            this.getChildren().add(c);
         }
         if (pieceType.charAt(1) == 'Q'){
            this.getChildren().add(q);
         }
      }
      
      @Override
      public String toString(){
         return ("" + row + ", " + column);
      }
   
   
   }//end of cell
   
//*****************************************************************************
//end of cell
   
   public void setNewTurn(){
      boolean inGame = true;
      
      //setNewTurn resets variables so that the next player can execute a turn.
      if (turn == 'R'){
         turn = 'B';
         topping.setFill(Color.BLUE);
         if (bluePieces < 1)
            //if blue has run out of pieces, set a lose condition
            inGame = false;
      }
      else{
         turn = 'R';
         topping.setFill(Color.RED);
         if (redPieces < 1)
            //if red has run out of pieces, set a lose condition.
            inGame = false;
      }
      
      //first check if there are any available moves
      
      hasMoved = "none";
      deselectAll();
      setNull();
      
      //below checks if there are any available moves.  If not, the player that cannot move loses.
      boolean canMove = false;
      for (int i = 0; i < coordGrid.length; i++){
         for (int j = 0; j < coordGrid[i].length; j++){
            
            //above checks the entire coordGrid
            if (coordGrid[i][j] != nullObj && coordGrid[i][j].pieceType.charAt(0) == turn){
            
               //if this cell is not null and belongs to the current player, check the moves.
               coordGrid[i][j].setMoves();
               
               for (int x = 0; x < currentMoveList.length; x++){
                  
                  //check each move.  If a match is found, break out of this loop and continue.
                  if (currentMoveList[x][0] != nullObj)
                     canMove = true;
                  if (canMove)
                     break;
               }//end for x
            }//end if
            if (canMove)
               break;
         }// end j for
         if (canMove)
            break;
      }//end i for
    
      //if a player has not reached a lose condition, keep going.  Else initiate a lose condition.
      if (inGame == false)
         endGame(1);
      else if (canMove == false)
         endGame(2);
      else{
         if (redPieces == 6 && bluePieces == 9 ||
         redPieces == 9 && bluePieces == 6)
            alertText.setText("Nice :D");
         else
            alertText.setText("");
         
         endTurn.setDisable(true);
         hasMoved = "none";
         deselectAll();
         setNull();
         transitionBox.requestFocus();
      }
   }
   
   public void setNull(){
      
      //setNull sets the move list to be null.
      for (int i = 0; i < currentMoveList.length; i++){
         for (int j = 0; j < currentMoveList[i].length; j++){
            currentMoveList[i][j] = null;
         }
      }
   }
   
   public void endGame(int condition){
   
      //executes the endGame sequence.
      concede.setVisible(false);
      endTurn.setVisible(false);
      
      //loss condition tells the player why they lost.
      String lossCondition = "";
      if (condition == 0)
         lossCondition = " conceded the game.";
      else if (condition == 1)
         lossCondition = " lost all their pieces.";
      else if (condition == 2)
         lossCondition = " can no longer make a move.";
      
      String winString = "";
      if (turn == 'R'){
         winString = "Blue won!";
         blueWins++;
         lossCondition = "Red" + lossCondition;
      }
      else{
         winString = "Red won!";
         redWins++;
         lossCondition = "Blue" + lossCondition;
      }
      

      //winScreen text information
      Text winText = new Text(winString);
      winText.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
      
      Text conditionText = new Text(lossCondition);
      conditionText.setFont(Font.font("Verdana", 20));
      
      Text scoreText = new Text("Red: " + redWins + " games --- Blue: " + blueWins + " games");
      scoreText.setFont(Font.font("Verdana", 15));
      
      //finishButton
      Button finishButton = new Button("Finish?");
      finishButton.setOnMouseClicked(e->{
         String finishContent = "Tie!";
         if (redWins > blueWins)
            finishContent = "Red wins the match!";
         if (blueWins > redWins)
            finishContent = "Blue wins the match!";
         Text finishText = new Text(finishContent);
         finishText.setFont(Font.font("Verdana",FontWeight.BOLD, 40));
         if (redWins > blueWins)
            finishText.setFill(Color.RED);
         if (blueWins > redWins)
            finishText.setFill(Color.BLUE);
         
         VBox finishScreen = new VBox(10);
         finishScreen.setAlignment(Pos.CENTER);
         finishScreen.getChildren().addAll(finishText, scoreText);
         transitionBox.getChildren().clear();
         transitionBox.getChildren().addAll(startMenuBackground, finishScreen);
         System.out.println("\n\tBrought to you by John Morrisett, 11/23/2020");
      });
      
      //newGameButton functionality
      newGameButton.setOnMouseClicked(e -> handleNewGameAction());

      //controlFrame
      HBox controlFrame = new HBox(10, newGameButton, finishButton);
      controlFrame.setAlignment(Pos.CENTER);
      
      //VBox for the win screen
      VBox winScreen = new VBox(10, winText, conditionText, scoreText, controlFrame);
      winScreen.setAlignment(Pos.CENTER);
      
      //set the winning display
      if (turn == 'R'){
         winText.setFill(Color.BLUE);
         conditionText.setFill(Color.RED);
      }
      else{
         winText.setFill(Color.RED);
         conditionText.setFill(Color.BLUE);
      }
      transitionBox.getChildren().clear();
      transitionBox.getChildren().addAll(startMenuBackground, winScreen);
   }
   
   public void selectCell(Cell cell){
   
      //selectCell changes which cell is selected, and visually indicates it.
      if (selectedCell != nullObj){
         selectedCell.setStyle("-fx-background-color:  White");
         selectedCell = cell;
         selectedCell.setStyle("-fx-background-color:  Gold");
      }
      else if (selectedCell == nullObj){
         selectedCell = cell;
         selectedCell.setStyle("-fx-background-color:  Gold");
      }
   }
   
   public void deselectAll(){
      //deselctAll makes it so that no cells are selected, and visually indicates it.
      if (selectedCell != nullObj){
         selectedCell.setStyle("-fx-background-color:  White");
         selectedCell = null;
      }
   }
   
   public GridPane resetGrid(){
   
      //resets the grid to have the standard checkers formation
      GridPane grid = new GridPane();
      for (int i = 0; i < 8; i++){
         for (int j = 0; j < 8; j += 2){
            if  (i % 2 == 1 && j < 2)
               j = 1;
            String pieceType = "NA";
            if (i < 3)
               pieceType = "RP";
            if (i > 4)
               pieceType = "BP";
            grid.add((coordGrid[i][j]) = new Cell(j, i, pieceType), i, j);
         }
      }
      return grid;
   }
   
   public void handleNewGameAction(){
   
      //resets the board for a new game of checkers.
   
      //hide the buttons that won't be used
      concede.setVisible(true);
      endTurn.setVisible(true);
      
      //resets aspects of the board
      setUIStandards();
      background.getChildren().clear();
      GridPane newGrid = resetGrid();
      background.getChildren().addAll(backfill, newGrid);
      transitionBox.getChildren().clear();
      transitionBox.getChildren().add(scene);

      topping.setFill(Color.RED);
      deselectAll();
      bluePieces = 12;
      redPieces = 12;
      turn = 'R';
      hasMoved = "none";
      setNull();
   }
   
   public static void main(String args[]){
      Application.launch(args);
   }
}