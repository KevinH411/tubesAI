import java.util.*;

public class RankSelection {
    private FitnessFunction fitnessFunction;
    private Random random;

    public RankSelection(FitnessFunction fitnessFunction, Random random) {
        this.fitnessFunction = fitnessFunction;
        this.random = random;
    }

    public int[] selectParent(List<int[]> population) {
        int n = population.size();
        
        // 1. Hitung semua fitness dan simpan dalam array primitive
        double[] scores = new double[n];
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            scores[i] = fitnessFunction.evaluate(population.get(i));
            indices[i] = i; // Simpan indeks asli
        }

        // 2. Urutkan INDEKS berdasarkan nilai di array scores
        // (Indeks dengan fitness terkecil akan berada di urutan pertama)
        Arrays.sort(indices, (a, b) -> Double.compare(scores[a], scores[b]));

        // 3. Hitung total rank (1 + 2 + ... + n)
        int totalRankSum = n * (n + 1) / 2;

        // 4. Roulette Wheel pada tingkatan Rank
        int spin = random.nextInt(totalRankSum) + 1;
        int currentSum = 0;

        for (int i = 0; i < n; i++) {
            // Rank diberikan berdasarkan urutan setelah sort (indeks 0 dapat rank 1)
            currentSum += (i + 1);
            if (currentSum >= spin) {
                // Ambil kromosom dari populasi asli menggunakan indeks yang sudah terurut
                return population.get(indices[i]).clone();
            }
        }

        return population.get(indices[n - 1]).clone();
    }
}