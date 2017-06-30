package de.upb.crc901.wever.crcreal.core.learner.algorithm.simple;

import java.util.Comparator;
import java.util.List;

import de.upb.crc901.wever.crcreal.util.Pair;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class SimpleTournamentSelector {

	public static <X> Pair<X, X> select(final IRandomGenerator pPRG, final List<? extends X> population, final Comparator<X> comparator) {
		final int indexFirstIndividual = pPRG.nextInteger(population.size());
		int indexSecondIndividual;
		do {
			indexSecondIndividual = pPRG.nextInteger(population.size());
		} while (indexSecondIndividual == indexFirstIndividual);

		final X betterIndividual;
		final X worseIndividual;

		if (comparator.compare(population.get(indexFirstIndividual), population.get(indexSecondIndividual)) < 0) {
			betterIndividual = population.get(indexFirstIndividual);
			worseIndividual = population.get(indexSecondIndividual);
		} else {
			betterIndividual = population.get(indexSecondIndividual);
			worseIndividual = population.get(indexFirstIndividual);
		}

		return new Pair<>(betterIndividual, worseIndividual);
	}
}
