import java.util.*;

public class TournamentSelection {
    private FitnessFunction fitnessFunction;
    private Random random;

    public TournamentSelection(FitnessFunction fitnessFunction, Random random) {
        this.fitnessFunction = fitnessFunction;
        this.random = random;
    }

    /**
     * Memilih satu individu terbaik dari populasi menggunakan metode turnamen.
     * @param population List dari kromosom (int[]) yang dihasilkan PopulationInitializer
     * @param tournamentSize Jumlah individu yang diadu (k)
     * @return Kromosom pemenang yang terpilih sebagai parent
     */

    public int[] selectParent(List<int[]> population, int tournamentSize) {
        int[] best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < tournamentSize; i++) {
            //Ambil individu acak dari populasi
            int randomIndex = random.nextInt(population.size());
            int[] contestant = population.get(randomIndex);

            //Hitung fitness kontestan tersebut
            double currentFitness = fitnessFunction.evaluate(contestant);

            //Bandingkan untuk mencari yang terbaik
            if (best == null || currentFitness > bestFitness) {
                best = contestant;
                bestFitness = currentFitness;
            }
        }

        // Kembalikan salinan kromosom terbaik hasil turnamen
        return best.clone();
    }
}