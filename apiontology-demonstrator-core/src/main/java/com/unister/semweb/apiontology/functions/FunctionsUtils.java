package com.unister.semweb.apiontology.functions;

import java.util.function.Function;

public class FunctionsUtils {

	public static <T, C> T apply(C input, Function<C, T> func) {
		return func.apply(input);
	}
}
