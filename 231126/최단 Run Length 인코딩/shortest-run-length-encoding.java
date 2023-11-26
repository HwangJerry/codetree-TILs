import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {
    private static int n;
    private static Queue<Integer> pq = new PriorityQueue<>();
    public static void main(String[] args) throws IOException {
        /*
        * 길이가 n(<= 10)인 문자열 A
        * 특정 횟수만큼 오른쪽으로 shift
        * 처리 이후 문자열에 run length encoding을 진행헀을 때의 길이가 최소가 되도록
        *
        * run length encoding이란, 비손실 압축 방식으로,
        * 연속해서 나온 문자와 연속해서 나온 개수로 나타내는 방식.
        * ex) aaabbbbcaa -> a3b4c1a2
        *
        * 길이 n인 문자열
        * aaabbbcccd
        * n번 shift하면서 encoding 하여 길이 pq에 저장
        *
        * encoding 과정은 다음과 같다.
        * 문자열 iteration하면서
        * if 다음 문자가 존재할 경우
        *   현재 문자랑 다음 문자가 같은지 검사
        *       if (같다면) map.putIfAbsent(문자, 1); map.replace(문자, map.get(문자) + 1);
        *       else (다르다면) map.putIfAbsent(다음문자, 1);
        * */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine().trim();
        n = s.length();

        for (int i = 0; i < n; i++) {
            String temp = s.substring(0, 1);
            String left = s.substring(1, n);
            StringBuilder sb = new StringBuilder();
            s = sb.append(left).append(temp).toString();
            encoding(s);
        }
        System.out.println(pq.poll());
    }

    private static void encoding(String s) {
        StringBuilder sb = new StringBuilder();
        int length = 1;
        sb.append(s.charAt(0));
        // 첫 항은 넘기고, 첫 항의 길이만큼 length 초기화한채로 진행
        for (int i = 1; i < n; i++) {
            char prevChar = s.charAt(i-1);
            char crtChar = s.charAt(i);
            if (prevChar == crtChar) {
                length++;
            } else {
                sb.append(length);
                sb.append(crtChar);
                length = 1;
            }
        }
        sb.append(length);
        pq.add(sb.toString().length());
    }

}