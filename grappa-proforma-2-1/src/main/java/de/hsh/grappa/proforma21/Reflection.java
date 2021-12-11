package de.hsh.grappa.proforma21;

//import java.lang.reflect.InvocationTargetException;
//
//public class Reflection {
//	private static <R> R handleReflectionException(Throwable e) {
//		try {
//			throw e;
//		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e1) {
//			throw new AssertionError(e1);
//		} catch (InvocationTargetException e2) {
//			if (e2.getCause() instanceof Error) throw (Error)e2.getCause();
//			throw new AssertionError(e2.getCause());
//		} catch (Throwable e3) {
//			throw new AssertionError(e3);
//		}
//		// never returns a value. The return value is needed as a hint to the compiler
//		// when calling this method from a method with a return value.
//	}
//	
//	public static <R> R call(Object instance, String methodName, Class<?> clazz, Object parameter) {
//		try {
//			@SuppressWarnings("unchecked")
//			R result = (R) instance.getClass().getDeclaredMethod(methodName, clazz).invoke(instance, parameter);
//			return result;
//		} catch (Throwable e) {
//			return Reflection.handleReflectionException(e); // never returns, always throws
//		}
//	}
//
//	public static <R> R call(Object instance, String methodName) {
//		try {
//			@SuppressWarnings("unchecked")
//			R result = (R) instance.getClass().getDeclaredMethod(methodName).invoke(instance);
//			return result;
//		} catch (Throwable e) {
//			return Reflection.handleReflectionException(e); // never returns, always throws
//		}
//	}
//
//}
