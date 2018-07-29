package br.com.helpdesk.api.util;

import java.util.Collection;
import java.util.Map;

public class MethodsUtils {
	
	/**
	 * Método que verifica se o objeto passado é nulo.
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		if (obj == null)
			return true;		
		return false;
	}

	/**
	 * Método que verifica se o objeto passado é nulo ou está vazio.
	 * @param obj
	 * @return
	 */
	public static boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;
        else if (obj instanceof String && ((String)obj).isEmpty())
            return true;
        else if (obj instanceof Long && (Long) obj <= 0)
			return true;
		else if (obj instanceof Integer && (Integer) obj <= 0)
			return true;
		else if (obj instanceof Double && (Double) obj <= 0)
			return true;
		else if (obj instanceof Collection<?> && ((Collection<?>) obj).isEmpty())
			return true;
		else if (obj instanceof Map<?,?> && ((Map<?,?>) obj).isEmpty())
			return true;
        return false;
    }
	
	/**
	 * Método que verifica se o objeto passado é nulo ou está vazio ou contém spaço.
	 * @param obj
	 * @return
	 */
	public static boolean isNullOrEmptyNoSpace(Object obj) {
        if (obj == null)
            return true;
        else if (obj instanceof String && ((String)obj).trim().isEmpty())
            return true;
        else if (obj instanceof Long && (Long) obj <= 0)
			return true;
		else if (obj instanceof Integer && (Integer) obj <= 0)
			return true;
		else if (obj instanceof Double && (Double) obj <= 0)
			return true;
		else if (obj instanceof Collection<?> && ((Collection<?>) obj).isEmpty())
			return true;
		else if (obj instanceof Map<?,?> && ((Map<?,?>) obj).isEmpty())
			return true;
        return false;
    }
	
}
