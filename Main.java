import java.io.*;
import java.util.*;

/**
 * Driver untuk menjalankan GA pada Mosaic Puzzle.
 * Cocok dengan API yang ada di projectmu (BoardState, PopulationInitializer, FitnessFunction,
 * TournamentSelection, RankSelection, Crossover, Mutation).
 *
 * Usage:
 *   javac *.java
 *   java Main <puzzle_file> [populationSize] [maxGenerations] [tournamentSize] [crossoverRate] [mutationRate] [seed]
 *
 * Example:
 *   java Main board/input5x5.txt 50 500 3 0.8 0.05 42
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <puzzle_file> [populationSize] [maxGenerations] [tournamentSize] [crossoverRate] [mutationRate] [seed]");
            return;
        }

        String puzzleFile = args[0];
        int populationSize = (args.length >= 2) ? Integer.parseInt(args[1]) : 50;
        int maxGenerations = (args.length >= 3) ? Integer.parseInt(args[2]) : 500;
        int tournamentSize = (args.length >= 4) ? Integer.parseInt(args[3]) : 3;
        double crossoverRate = (args.length >= 5) ? Double.parseDouble(args[4]) : 0.8;
        double mutationRate = (args.length >= 6) ? Double.parseDouble(args[5]) : 0.05;
        long seed = (args.length >= 7) ? Long.parseLong(args[6]) : System.currentTimeMillis();

        System.out.printf("Puzzle: %s | pop=%d | gens=%d | tour=%d | cx=%.2f | mut=%.3f | seed=%d%n",
                puzzleFile, populationSize, maxGenerations, tournamentSize, crossoverRate, mutationRate, seed);

        try {
            Random rng = new Random(seed);

            // 1) Load board
            BoardState board = new BoardState();
            board.load(puzzleFile);
            board.findGuaranteedCells(); // mark fixed whites/blacks and collect variables

            // 2) Initialize GA components (match your constructors)
            PopulationInitializer initializer = new PopulationInitializer(board, rng);
            List<int[]> population = initializer.generatePopulation(populationSize);

            FitnessFunction fitness = new FitnessFunction(board);
            TournamentSelection tournament = new TournamentSelection(fitness, rng);
            RankSelection rankSelection = new RankSelection(fitness, rng); // available if you want to switch
            Crossover crossover = new Crossover(rng);
            Mutation mutation = new Mutation(rng);

            // GA loop variables
            int generation = 0;
            double bestFitness = Double.NEGATIVE_INFINITY;
            int[] bestChromosome = null;

            // Evaluate initial population's best
            for (int[] chrom : population) {
                double f = fitness.evaluate(chrom);
                if (f > bestFitness) {
                    bestFitness = f;
                    bestChromosome = chrom.clone();
                }
            }

            // GA main loop
            while (generation < maxGenerations) {
                generation++;

                // Check if bestChromosome is a valid solution
                if (bestChromosome != null && isSolution(board, bestChromosome)) {
                    System.out.println("✔ Found exact solution at generation " + generation);
                    printSolution(board, bestChromosome);
                    return;
                }

                // Build next generation
                List<int[]> nextGen = new ArrayList<>(populationSize);
                while (nextGen.size() < populationSize) {
                    // Select parents (tournament)
                    int[] parent1 = tournament.selectParent(population, tournamentSize);
                    int[] parent2 = tournament.selectParent(population, tournamentSize);

                    int[] child1 = parent1.clone();
                    int[] child2 = parent2.clone();

                    // Crossover
                    if (rng.nextDouble() < crossoverRate) {
                        List<int[]> offspring = crossover.singlePointCrossover(parent1, parent2);
                        // make sure clones are used
                        child1 = offspring.get(0).clone();
                        child2 = offspring.get(1).clone();
                    }

                    // Mutation
                    mutation.mutate(child1, mutationRate);
                    mutation.mutate(child2, mutationRate);

                    nextGen.add(child1);
                    if (nextGen.size() < populationSize) {
                        nextGen.add(child2);
                    }
                }

                // Replace population
                population = nextGen;

                // Re-evaluate to update bestChromosome
                for (int[] chrom : population) {
                    double f = fitness.evaluate(chrom);
                    if (f > bestFitness) {
                        bestFitness = f;
                        bestChromosome = chrom.clone();
                    }
                }

                // Logging every 10 generations
                if (generation % 10 == 0 || generation == 1) {
                    System.out.printf("Gen %d | bestFitness=%.3f%n", generation, bestFitness);
                }
            }

            // End GA: print best found
            System.out.println("✖ Reached max generations, no exact solution found.");
            System.out.printf("Best fitness: %.3f (generation %d)%n", bestFitness, generation);
            if (bestChromosome != null) {
                printSolution(board, bestChromosome);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check if a decoded board satisfies ALL constraints exactly
    private static boolean isSolution(BoardState board, int[] chromosome) {
        int[][] decoded = board.decodeChromosome(chromosome);

        for (int[] constraint : board.constraints) {
            int r = constraint[0];
            int c = constraint[1];
            int required = constraint[2];

            int blackCount = 0;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < board.size && nc >= 0 && nc < board.size) {
                        if (decoded[nr][nc] == 1) blackCount++;
                    }
                }
            }
            if (blackCount != required) return false;
        }
        return true;
    }

    // Nicely print final board: show original numbers where present, else X for black, . for white
    private static void printSolution(BoardState board, int[] chromosome) {
        int[][] decoded = board.decodeChromosome(chromosome);

        System.out.println("\nFinal board (numbers = constraints):");
        for (int r = 0; r < board.size; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < board.size; c++) {
                if (board.grid[r][c] >= 0) {
                    sb.append(board.grid[r][c]); // show numeric constraint
                } else {
                    sb.append(decoded[r][c] == 1 ? 'X' : '.');
                }
            }
            System.out.println(sb.toString());
        }
    }
}