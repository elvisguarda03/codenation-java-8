package com.challenge.desafio;

import com.challenge.annotation.Somar;
import com.challenge.annotation.Subtrair;
import com.challenge.interfaces.Calculavel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.stream.Stream;

public class CalculadorDeClasses implements Calculavel {
    private static final String GETTER = "get";

    @Override
    public BigDecimal somar(Object obj) {
        return sumWithAnnotation(obj, Somar.class);
    }

    private <T extends Annotation> BigDecimal sumWithAnnotation(Object obj, Class<T> annotation) {
        BigDecimal total = BigDecimal.ZERO;
        return Stream.of(obj.getClass().getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(BigDecimal.class)
                        && field.isAnnotationPresent(annotation))
                .map(field -> getValue(obj, field))
                .reduce(total, BigDecimal::add);
    }

    @Override
    public BigDecimal subtrair(Object obj) {
        return sumWithAnnotation(obj, Subtrair.class);
    }

    @Override
    public BigDecimal totalizar(Object obj) {
        return somar(obj).subtract(subtrair(obj));
    }

    private BigDecimal getValue(Object obj, Field field) {
        try {
            String methodName = GETTER + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1);
            return (BigDecimal) obj.getClass()
                    .getMethod(methodName)
                    .invoke(obj);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ite) {
            throw new RuntimeException(ite);
        }
    }
}
