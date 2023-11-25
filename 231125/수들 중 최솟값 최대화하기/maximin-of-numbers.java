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
  private static boolean[] isSelectedColumn;
  private static int[][] grid;
  private static List<Integer> selectedNums = new ArrayList<>();
  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    n = Integer.parseInt(br.readLine());

    // initialize variables
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
    go(Integer.MAX_VALUE, 0);

    System.out.println(maxNum);
  }

  private static void go(int minNum, int row) {
    if (selectedNums.size() == n) {
      maxNum = Math.max(maxNum, minNum);
      return;
    }

    for (int column = 0; column < n; column++) {
      if (!isSelectedColumn[column] && row < n) {
        isSelectedColumn[column] = true;
        selectedNums.add(grid[row][column]);
        go(Math.min(minNum, grid[row][column]), ++row);
        row--;
        isSelectedColumn[column] = false;
        selectedNums.remove(selectedNums.size() - 1);
      }
    }
  }
}