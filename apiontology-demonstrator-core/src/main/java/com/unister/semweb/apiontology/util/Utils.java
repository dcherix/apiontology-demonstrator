package com.unister.semweb.apiontology.util;

import java.lang.reflect.Method;

public class Utils {
	public static String classMethod2Literal(Class<?> className, Method methodName){
		return className.getName().concat("::").concat(methodName.getName());
	}
}
