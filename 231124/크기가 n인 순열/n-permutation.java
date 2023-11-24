import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static int n;
    private static boolean[] selected;
    private static List<Integer> li = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        selected = new boolean[n];
        go();
    }

    private static void go() {
        if (li.size() == n) {
            li.forEach(i -> System.out.print(i + " "));
            System.out.println();
            return;
        }
        for (int i = 0; i < 3; i++) {
            if (!selected[i]) {
                selected[i] = true;
                li.add(i+1);
                go();
                selected[i] = false;
                li.remove(li.size() - 1);
            }
        }
    }
}