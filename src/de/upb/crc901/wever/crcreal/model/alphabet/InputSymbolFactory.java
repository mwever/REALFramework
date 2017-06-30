package de.upb.crc901.wever.crcreal.model.alphabet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This factory allows for creation of input symbols. Each identifier string
 * is mapped to a unique input symbol.
 * 
 * @author wever
 */
public class InputSymbolFactory {

	private final AtomicInteger idCounter = new AtomicInteger(0);
	private final Map<String, InputSymbol> inputCache = new HashMap<>();
	
	/**
	 * @param identifier String to create the input symbol for.
	 * @return returns an input symbol for a given string.
	 */
	public synchronized InputSymbol getInput(String identifier) {
		InputSymbol cachedInput = inputCache.get(identifier);
		if(cachedInput == null) {
			cachedInput = new InputSymbol(identifier, idCounter.getAndIncrement());
			inputCache.put(identifier, cachedInput);
		}
		return cachedInput;
	}
	
	/**
	 * @return Returns a set of all inputs contained in the cache.
	 */
	public Set<InputSymbol> getSetOfAllInputs() {
		return new HashSet<>(inputCache.values());
	}
}
