import java.util.*;

/*
   Population Initializer untuk Algoritma Genetik
   Referensi: Inisialisasi populasi random dari "Genetic Algorithms in Search, Optimization and Machine Learning" (Goldberg, 1989)
 */
public class PopulationInitializer {
    private BoardState board; // status papan puzzle
    private Random random; // generator angka acak untuk inisialisasi

    // menerima objek BoardState dan Random generator
    public PopulationInitializer(BoardState board, Random random) {
        this.board = board;
        this.random = random;
    }
    
    /* 
       membuat satu individu generator secara acak
       individu direpresentasikan sebagai array biner (0 = putih, 1 = hitam) 
    */
    private int[] generateIndividual() {
        int varCount = board.getVariableCount();
        int[] chromosome = new int[varCount];
        for (int i=0; i<chromosome.length; i++) {
            chromosome[i] = random.nextInt(2); // 0 atau 1
        }
        
        return chromosome;
    }
    
    /* 
       membentuk populasi awal untuk algoritma genetik
       populasi adalah kumpulan dari beberapa individu solusi kandidat
    */
    public List<int[]> generatePopulation(int populationSize) {
        List<int[]> population = new ArrayList<>();
        for (int i= 0; i<populationSize; i++) {
            population.add(generateIndividual());
        }
        
        return population;
    }
}