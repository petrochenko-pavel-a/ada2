package com.onpositive.analitics.model.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.analitics.model.ICounter;

@Retention(RetentionPolicy.RUNTIME)
public @interface Counter {

	Class<? extends ICounter>value();
}
