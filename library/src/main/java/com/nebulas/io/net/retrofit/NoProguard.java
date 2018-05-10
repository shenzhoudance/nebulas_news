package com.nebulas.io.net.retrofit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by legend on 2018/5/5.
 */

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface NoProguard {
}
