import java.util.*;

/*
    Program utama - Menerima file input dari command line
    Usage: java Main <puzzle_file> [population_size] [seed]
    Referensi: Command-line parsing patterns dari "Effective Java" (Bloch, 2018)
 */
public class Main {
    public static void main(String[] args) {
        String filename = null; // nama file input
        int populationSize = 0; // ukuran populasi
        long seed = 0;          // masukkan untuk seed

        try {
            if (args.length == 1) {
                // hanya filename, maka error butuh seed
                System.err.println("Error: Seed harus disertakan");
                System.exit(1);
            } else if (args.length == 2) {
                // filename + seed (population size default)
                filename = args[0];
                seed = Long.parseLong(args[1]);
                populationSize = 10; // default
            } else if (args.length >= 3) {
                // filename + population + seed
                filename = args[0];
                populationSize = Integer.parseInt(args[1]);
                seed = Long.parseLong(args[2]);
            }
        } catch (Exception e){
            System.err.println("Error parsing arguments: " + e.getMessage());
            System.err.println("Format: java Main <puzzle_file> <seed>");
            System.err.println("   atau: java Main <puzzle_file> <population_size> <seed>");
            e.printStackTrace(); // untuk debug
            System.exit(1);
        }
        
        System.out.println("=== MOSAIC PUZZLE INITIALIZATION ===");
        System.out.println("Puzzle file: " + filename);
        System.out.println("Population size: " + populationSize);
        System.out.println("Random seed: " + seed);
        System.out.println();
        
        try {
            // load board dari file yang diberikan
            BoardState board = new BoardState();
            board.load(filename);
            board.findGuaranteedCells();
            
            // setup random dengan seed
            Random random = new Random(seed);
            
            // create initializer dan generate populasi
            PopulationInitializer initializer = new PopulationInitializer(board, random);
            List<int[]> population = initializer.generatePopulation(populationSize);
            
            // tampilkan hasil
            printResults(population, board, seed);
            
        } catch (Exception e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            System.err.println("Format: java Main <puzzle_file> <seed>");
            System.err.println("   atau: java Main <puzzle_file> <population_size> <seed>");
            e.printStackTrace(); // untuk debug
            System.exit(1);
        }
    }
    
    /*
    cara penggunaan program
    private static void printUsage() {
        System.out.println("Usage: java Main <puzzle_file> [population_size] [seed]");
        System.out.println();
        System.out.println("Parameters:");
        System.out.println("  puzzle_file    : Path to puzzle file (required)");
        System.out.println("  population_size: Number of individuals (default: 10)");
        System.out.println("  seed           : Random seed (default: current time)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main puzzle_5x5.txt");
        System.out.println("  java Main puzzle_10x10.txt 50");
        System.out.println("  java Main puzzle_15x15.txt 100 123456789");
    }
        */
    
    // bagian sampai bawah bisa diganti aja ges
    // tampilkan hasil initialization
    private static void printResults(List<int[]> population, BoardState board, long seed) {
        System.out.println("\n=== INITIALIZATION COMPLETE ===");
        System.out.println("Configuration:");
        System.out.println("  Random seed: " + seed);
        System.out.println("  Population size: " + population.size());
        System.out.println("  Variables per individual: " + board.getVariableCount());
        
        // tampilkan sample individu
        if (!population.isEmpty()) {
            System.out.println("\nSample individuals (first 3):");
            for (int i = 0; i < Math.min(3, population.size()); i++) {
                System.out.print("  Ind " + i + ": ");
                int[] chrom = population.get(i);
                for (int j = 0; j < Math.min(15, chrom.length); j++) {
                    System.out.print(chrom[j]);
                }
                if (chrom.length > 15) System.out.print("...");
                System.out.println();
            }

            System.out.println("\nPopulation statistics:");
            calculateStats(population);
        }
    }
    
    // hitung statistik populasi
    private static void calculateStats(List<int[]> population) {
        int totalGenes = population.size() * population.get(0).length;
        int totalOnes = 0;
        
        for (int[] chrom : population) {
            for (int gene : chrom) {
                totalOnes += gene;
            }
        }
        
        double blackRatio = (totalOnes * 100.0) / totalGenes;
        
        System.out.printf("  Total black cells (1s): %d/%d\n", totalOnes, totalGenes);
        System.out.printf("  Black ratio: %.1f%%\n", blackRatio);
        System.out.printf("  White ratio: %.1f%%\n", 100 - blackRatio);
    }
}