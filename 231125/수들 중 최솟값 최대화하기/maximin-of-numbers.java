import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
  /*
  * n by n 정수 그리드
  * n개의 칸 선택
  * 각 행과 열에 정확히 1개씩만
  * 이렇게 선택한 숫자들의 최솟값의 최댓값을 구해라.
  * */
  private static int n, maxNum;
  private static Queue<Integer> pq = new PriorityQueue<>();
  private static boolean[] isSelectedRow;
  private static boolean[] isSelectedColumn;
  private static int[][] grid;
  private static List<Integer> selectedNums = new ArrayList<>();
  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    n = Integer.parseInt(br.readLine());

    // initialize variables
    isSelectedRow = new boolean[n];
    isSelectedColumn = new boolean[n];
    grid = new int[n][n];
    maxNum = Integer.MIN_VALUE;
    StringTokenizer st;

    // initialize grid
    for (int i = 0; i < n; i++) {
      st = new StringTokenizer(br.readLine());
      for (int j = 0; j < n; j++) {
        grid[i][j] = Integer.parseInt(st.nextToken());
      }
    }

    // do backtracking
    go();
    
    System.out.println(maxNum);
  }

  private static void go() {
    if (selectedNums.size() == n) {
      for (Integer selectedNum : selectedNums) {
        pq.add(selectedNum);
      }
      maxNum = Math.max(maxNum, pq.poll());
      pq = new PriorityQueue<>();
      return;
    }

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (!isSelectedRow[i] && !isSelectedColumn[j]) {
          isSelectedRow[i] = true;
          isSelectedColumn[j] = true;
          selectedNums.add(grid[i][j]);
          go();
          isSelectedRow[i] = false;
          isSelectedColumn[j] = false;
          selectedNums.remove(selectedNums.size() - 1);

        }
      }
    }
  }
}