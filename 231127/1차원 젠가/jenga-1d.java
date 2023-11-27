import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        /*
        * 블록 길이 n 입력
        * 길이 n에 맞는 블록 입력
        * 구간에 맞게 블록 빼기
        * 그리고 나서 남은 블록의 개수와 남은 블록 출력
        * */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        List<Integer> blocks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            blocks.add(Integer.parseInt(br.readLine()));
        }

        StringTokenizer st;

        for (int i = 0; i < 2; i++) {
            st = new StringTokenizer(br.readLine());
            int removeIdx = Integer.parseInt(st.nextToken()) - 1;
            int loopCnt = Integer.parseInt(st.nextToken()) - removeIdx + 1;
            for (int j = 0; j < loopCnt; j++) {
                blocks.remove(removeIdx);
            }

        }
        System.out.println(blocks.size());
        blocks.forEach(System.out::println);
    }
}