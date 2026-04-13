import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    record Order(String item, int price, boolean paid) {}

    public static void main(String[] args) throws Exception {
        System.out.println("準備OK");
        // --- Step 1: ラムダ式 ---
        List<String> names = new ArrayList<>(List.of("Bob", "Alice", "Anna", "Charlie"));
        names.sort((a, b) -> a.compareTo(b));
        System.out.println(names);

        // --- Step 2: Stream 基本 ---
        names.stream()
            .filter(n -> n.startsWith("A"))
            .forEach(System.out::println);

        names.stream()
            .map(String::toUpperCase)
            .filter(n -> n.startsWith("A"))
            .map(String::toUpperCase)
            .forEach(System.out::println);

        List<String> filteredNames = names.stream()
            .filter(n -> n.startsWith("A"))
            .map(String::toUpperCase)
            .collect(Collectors.toList());

        System.out.println("Filtered Names: " + filteredNames);

        // --- Step 3: ハンズオン課題 ---

        List<Order> orders = List.of(
            new Order("ケーブル",   500,  true),
            new Order("センサー",  3000,  true),
            new Order("基板",     8000, false),
            new Order("コネクタ",  200,  true)
        );

        // 課題①：支払済みの注文だけ取り出してprintln
        // ヒント: filter → forEach
        orders.stream()
            .filter(o -> { return o.paid() == true;})
            .forEach(System.out::println);

        // 課題②：支払済みの合計金額を求める
        // ヒント: filter → mapToInt(Order::price) → sum()
        int totalPaid = orders.stream()
            .filter(o -> o.paid())
            .mapToInt(o -> o.price())
            .sum();

        System.out.println("Total Paid: " + totalPaid);

        // 課題③：全注文のアイテム名リストを作る
        // ヒント: map(Order::item) → collect(Collectors.toList())
        List<String> itemNames = orders.stream()
            .map(o-> o.item())
            .collect(Collectors.toList());

        System.out.println("Item Names: " + itemNames);
    }
}
