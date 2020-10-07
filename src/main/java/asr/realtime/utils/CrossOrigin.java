package asr.realtime.utils;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossOrigin {
    String[] DEFAULT_ORIGINS = { "*" };

    String[] DEFAULT_ALLOWED_HEADERS = { "*" };

    boolean DEFAULT_ALLOW_CREDENTIALS = true;

    long DEFAULT_MAX_AGE = 1800;


    /**
     * 同origins属性一样
     */
    @AliasFor("origins")
    String[] value() default {};

    /**
     * 所有支持域的集合，例如"http://domain1.com"。
     * <p>这些值都显示在请求头中的Access-Control-Allow-Origin
     * "*"代表所有域的请求都支持
     * <p>如果没有定义，所有请求的域都支持
     * @see #value
     */
    @AliasFor("value")
    String[] origins() default {};

    /**
     * 允许请求头重的header，默认都支持
     */
    String[] allowedHeaders() default {};

    /**
     * 响应头中允许访问的header，默认为空
     */
    String[] exposedHeaders() default {};

    /**
     * 请求支持的方法，例如"{RequestMethod.GET, RequestMethod.POST}"}。
     * 默认支持RequestMapping中设置的方法
     */
    RequestMethod[] methods() default {};

    /**
     * 是否允许cookie随请求发送，使用时必须指定具体的域
     */
    String allowCredentials() default "";

    /**
     * 预请求的结果的有效期，默认30分钟
     */
    long maxAge() default -1;
}
