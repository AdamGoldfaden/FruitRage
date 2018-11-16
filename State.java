import java.util.ArrayList;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;

public class State {
   byte [][] board;
   int score;
   boolean player;
   Point initialPoint;
   HashSet<Point> matches;

   
   public State(byte [][] b, int score) {
      this.score = score;
      this.initialPoint = null;
      this.board = new byte[b.length][b.length];
      for(int i = 0; i < b.length; i++) {
         System.arraycopy(b[i], 0, this.board[i], 0, b.length);
      }
      this.matches = null;
   }
   
   public State (byte [][] b, int score,Point ip, HashSet<Point> matches) {
      this.score = score;
      this.initialPoint = ip;
      this.board = new byte[b.length][b.length];
      for(int i = 0; i < b.length; i++) {
         System.arraycopy(b[i], 0, this.board[i], 0, b.length);
      }
      this.matches = matches;
   }
}