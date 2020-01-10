package com.qxs.base.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qixingshen
 * **/
public class ClassUtil {
	/**
	 * 是否是基本数据类型或基本数据类型包装类型或String类型
	 * **/
	public static boolean isPrimitive(Class<?> clazz){
		try {
			return clazz == String.class || clazz.isPrimitive() || clazz.getField("TYPE") == null || ((Class<?>)clazz.getField("TYPE").get(null)).isPrimitive();
		} catch (IllegalArgumentException e) {
			return false;
		} catch (SecurityException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}
	
	/**
	 * 实例化实体对象
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * **/
	@SuppressWarnings("unchecked")
	public static <T> T instanceEntity(Class<?> clazz) {
		int mode = clazz.getModifiers();
		if(Modifier.isInterface(mode)){
			throw new IllegalArgumentException(clazz+"不能是接口");
		}else if(Modifier.isAbstract(mode)){
			throw new IllegalArgumentException(clazz+"不能是抽象类");
		}
		Map<Class<?>,Constructor<?>> noParameterConstructorMap = new HashMap<Class<?>,Constructor<?>>();
		
		Constructor<?> constructor = null;
		if(noParameterConstructorMap.containsKey(clazz)){
			constructor = noParameterConstructorMap.get(clazz);
		}else{
			//判断是否有无参构造方法
			Constructor<?>[] constructors = clazz.getDeclaredConstructors();
			for(Constructor<?> cons : constructors){
				if(cons.getParameterTypes().length == 0){
					if(!cons.isAccessible()){
						cons.setAccessible(true);
					}
					constructor = cons;
					break;
				}
			}
			if(constructor != null){
				noParameterConstructorMap.put(clazz, constructor);
			}
		}
		if(!noParameterConstructorMap.containsKey(clazz)){
			throw new IllegalArgumentException(clazz+"必须有一个无参的构造函数");
		}
		
		try {
			return (T) constructor.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getCause());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getCause());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getCause());
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
	}
}
