import java.util.*;
import java.io.*;
import java.awt.Point;

public class FruitRage {
   static int n;
   static int p;
   static double time;
   static double timeForMove;
   static long timeForMoveMilli;
   static PrintWriter out;
   static State root;
   static State bestMove;
   static int bestScore;
   static int tempBestScore;
   static State tempBestMove;
   static int maxDepth;
   static int numNodes;
   static long t1,t2;
   static boolean reachedTerminalState;
   
   public static void main (String[]args) throws FileNotFoundException {
      t1 = System.currentTimeMillis();
      State root = Init();
      bestMove = InitialReturnState(root);
      maxDepth = 1;
      reachedTerminalState = false;
      timeForMove = FindTimeForMove(root.board);
      timeForMoveMilli = (long)(timeForMove*1000L);
      System.out.println("Time for Move: " + timeForMove);
      //System.out.println(timeForMoveMilli);
      
      
      while((t2 - t1) <= timeForMoveMilli && !reachedTerminalState) {
         System.out.println("Max Depth = " + maxDepth);
         Minimax(root, 0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
         if(tempBestScore < 0) {
            break;
         }
         bestScore = tempBestScore;
         bestMove = tempBestMove;
         maxDepth += 2;
         tempBestScore = 0;
         tempBestMove = null;
      }
      
      
      /*for(int i = 0; i < root.children.get(2).children.size(); i++) {
         System.out.println(root.children.get(2).children.get(i).initialPoint);
         PrintBoard(root.children.get(2).children.get(i).board);
         System.out.println();
         System.out.println();
      }*/
      
      /*Minimax(root, 0,true, Integer.MIN_VALUE, Integer.MAX_VALUE);
      System.out.println(tempBestScore);
      System.out.println(ConvertMove(tempBestMove.initialPoint));
      PrintBoard(tempBestMove.board);*/
      
      System.out.println("Best Score: " + bestScore);
      System.out.println(ConvertMove(bestMove.initialPoint));
      out.println(ConvertMove(bestMove.initialPoint));
      PrintBoard(bestMove.board);
      out.close();
   }
   
   static int Minimax(State s, int depth, boolean player, int a, int b) {
      //System.out.println(numNodes);
      //numNodes++;
      
      t2 = System.currentTimeMillis();
      if(t2 - t1 >= timeForMoveMilli) {
         tempBestScore = - 1;
         return -1;
      }
      if(maxDepth == 1 && depth == 1) {
         if(s.score > tempBestScore) {
            tempBestScore = s.score;
            tempBestMove = s;
         }
         if(CheckTerminal(s)) {
            reachedTerminalState = true;
         }
         return s.score;
      }
      
      
      if(depth == maxDepth) {
         return s.score;
      }
      
      else if(CheckTerminal(s)) {
         reachedTerminalState = true;
         return s.score;
      }
            
      ArrayList<HashSet> children = new ArrayList<HashSet>();
      //boolean pruneAlpha = false;
      //boolean pruneBeta = false;
      
      //MaxPlayer
      if(player) {
         outerloopA:
         for(int r = 0; r < n; r++) {
            //if(pruneAlpha) {break;}
            for(int c = 0; c < n; c++) {
               if(s.board[r][c] < 0) {continue;}
               boolean go = true;
               
               for(int i = 0; i < children.size(); i++) {
                  if(children.get(i).contains(new Point(c,r))) {
                     go = false;
                     break;
                  }
               }
               
               if(go) {
                  State child = CreateMove(r,c,s,player);
                  children.add(child.matches);
                  ApplyGravity(child.board);
                  a = Math.max(a, Minimax(child, depth+1, !player, a, b));
                  if(a >= b) {
                     break outerloopA;
                     //pruneAlpha = true;
                     //break;
                  }
               }
            }
         }
         return a;
      }
      
      //MinPlayer
      else {
         outerLoopB:
         for(int r = 0; r < n; r++) {
            //if(pruneBeta) {break;}
            for(int c = 0; c < n; c++) {
               if(s.board[r][c] < 0) {continue;}
               boolean go = true;
               
               for(int i = 0; i < children.size(); i++) {
                  if(children.get(i).contains(new Point(c,r))) {
                     go = false;
                     break;
                  }
               }
               
               if(go) {
                  State child = CreateMove(r,c,s,player);
                  children.add(child.matches);
                  ApplyGravity(child.board);
                  b = Math.min(b, Minimax(child, depth + 1, !player, a, b));
                  if(b <= a) {
                     break outerLoopB;
                     //pruneBeta = true;
                     //break;
                  }
                  else if(depth == 1 && b > tempBestScore) {
                     tempBestScore = b;
                     tempBestMove = s;
                  }
               }
            }
         }
         return b;
      }
      
   }
   
   static State Init () throws FileNotFoundException {
      File input = new File("input.txt");
      Scanner in = new Scanner(input);
      
      n = in.nextInt(); p = in.nextInt(); time = in.nextDouble();
      byte [][] board = new byte [n][n];
      out = new PrintWriter("output.txt");
      in.nextLine();
      
      Scanner inRow; int r = 0;
      while(in.hasNextLine()) {
         String row = in.nextLine();
         inRow = new Scanner(row); inRow.useDelimiter("");
  
         for(int c = 0; c < n; c++) {
            if(inRow.hasNext()) {
               String next = inRow.next();
               if(!next.equals("*"))
                  board[r][c] = Byte.parseByte(next);
               else 
                  board[r][c] = -1;
            }
         }
    
         r++;
         inRow.close();
      }
      in.close();
      return new State(board,0);
   }
   
   static State CreateMove(int r, int c, State s, boolean player) {
      byte[][] ret = new byte[n][n];  
      for(int i = 0; i < n; i++) {
         System.arraycopy(s.board[i], 0, ret[i], 0, n);
      }
         
      byte fruitNum = s.board[r][c];
      HashSet<Point> matches = new HashSet<Point>();
      Queue<Point> cands = new LinkedList<Point>();
      cands.add(new Point(c,r));
      
      while(!cands.isEmpty()) {
         Point next = cands.poll();
         if(matches.contains(next)) {
            continue;
         }
         else {
            matches.add(next);
            ret[next.y][next.x] = -1;
         }
         
         //add North
         if(next.y != 0 && ret[next.y-1][next.x] == fruitNum) {
            cands.add(new Point(next.x,next.y-1));
         }
         
         //add East
         if(next.x != n-1 && ret[next.y][next.x+1] == fruitNum) {
            cands.add(new Point(next.x+1,next.y));
         }
         
         //add South
         if(next.y != n-1 && ret[next.y+1][next.x] == fruitNum) {
            cands.add(new Point(next.x,next.y+1));
         }
         
         //add West
         if(next.x != 0 && ret[next.y][next.x-1] == fruitNum) {
            cands.add(new Point(next.x-1,next.y));
         }
      }
       
      int score;
      if(player) {
         score = s.score + (int)Math.pow(matches.size(),2);
      }
      else {
         score = s.score;
      }
      Point ip = new Point(c,r);
      return new State(ret,score,ip,matches);
   }
   
   static int CountMovesLeft(byte [][] board) {
      int count = 0;
      byte[][] b = new byte[n][n];
      for(int i = 0; i < n; i++) {
         System.arraycopy(board[i], 0, b[i], 0, n);
      }
      
      for(int r = 0; r < n; r++) {
         for(int c = 0; c < n; c++) {
            if(b[r][c] == -1) {continue;}
         
               count++;
               byte fruitNum = b[r][c];
               HashSet<Point> matches = new HashSet<Point>();
               Queue<Point> cands = new LinkedList<Point>();
               cands.add(new Point(c,r));
               
               while(!cands.isEmpty()) {
                  Point next = cands.poll();
                  if(matches.contains(next)) {
                     continue;
                  }
                  else {
                     matches.add(next);
                     b[next.y][next.x] = -1;
                  }
                  
                  //add North
                  if(next.y != 0 && b[next.y-1][next.x] == fruitNum) {
                     cands.add(new Point(next.x,next.y-1));
                  }
                  
                  //add East
                  if(next.x != n-1 && b[next.y][next.x+1] == fruitNum) {
                     cands.add(new Point(next.x+1,next.y));
                  }
                  
                  //add South
                  if(next.y != n-1 && b[next.y+1][next.x] == fruitNum) {
                     cands.add(new Point(next.x,next.y+1));
                  }
                  
                  //add West
                  if(next.x != 0 && b[next.y][next.x-1] == fruitNum) {
                     cands.add(new Point(next.x-1,next.y));
                  }
               }
         }
      }
      if(count == 1) {
         return 1;
      }
      else if (count %2 == 0) {
         return count/2;
      }
      else {
         return (count/2) + 1;
      }
      
   }
   
   static double FindTimeForMove(byte[][] board) {
      double tfm;
      double tpm = time/CountMovesLeft(board);
      System.out.println("tpm: " + tpm);
      System.out.println("moves left: " + CountMovesLeft(board));
      double factor = 1.0 + (((n*n) - CountStars(board))/(n*n)) * 3.0;
      System.out.println("factor: " + factor);
      tfm = factor * tpm;
      return tfm;
   }
   
   static double CountStars(byte [][] board) {
      double count = 0;
      for(int i = 0; i < n; i++)
         for(int j = 0; j < n; j++)
            if(board[i][j] == -1)
               count++;
      return count;
   }
   
   static boolean CheckTerminal(State s) {
      for(int i = 0; i < n; i++) 
         for(int j = 0; j < n; j++) 
            if(s.board[i][j] != -1)
               return false;
      return true;
         
   }
   
   static void PrintBoard (byte [][] mat) {
      for(int i = 0; i < mat.length; i++) {
         for(int j = 0; j < mat[i].length; j++) {
            if(mat[i][j] >= 0) {
               System.out.print(mat[i][j]);
               out.print(mat[i][j]);
            }
            else {
               System.out.print("*");
               out.print("*");
            }
         }
         if(i < mat.length - 1) {
            System.out.println();
            out.println();
         }
      }
   }
   
   static void ApplyGravity(byte [][] mat) {
      for(int c = 0; c < n; c++) {
         boolean b = false;
         for(int r = n-1; r >= 0; r--) {
            if(mat[r][c] == -1) {
               for(int i = r - 1; i >= 0; i--) {
                  if(mat[i][c] != -1) {
                     b = true;
                     while(i >= 0 && mat[i][c] != -1) {
                        mat[r][c] = mat[i][c];
                        mat[i][c] = -1;
                        r--; i--;
                     }
                  }
               }
            }
            if(b) {break;}
         }
      } 
   }
   
   static State InitialReturnState(State s) {
      for(int i = 0; i < n; i++) {
         for(int j = 0; j < n; j++) {
            if(s.board[i][j] != -1)
               return CreateMove(i, j, s, false);
         }
      }
      return s;
   }
   
   static String ConvertMove(Point p) {
      int c = p.x;
      String ret = String.valueOf((char)(c + 65));
      ret += (p.y + 1);
      return ret;
   }
}