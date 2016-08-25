package com.yu.common.aspect;

import java.util.List;  

import org.aspectj.lang.JoinPoint;  
import org.aspectj.lang.ProceedingJoinPoint;  
import org.aspectj.lang.annotation.Around;  
import org.aspectj.lang.annotation.Aspect;  
import org.aspectj.lang.annotation.Before;  
import org.aspectj.lang.reflect.MethodSignature;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Component;  

import com.yu.common.JsonUtils;
import com.yu.common.annotation.RedisCache;
import com.yu.common.annotation.RedisEvict;
import com.yu.redis.JedisClient;
  
  
@Aspect  
@Component  
public class CacheInterceptor {  
  
    @Autowired  
    JedisClient jedisClient;  
      
      
    //前置由于数据库数据变更  清理redis缓存  
    @Before("@annotation(redisEvict)")  
    public void doBefore(JoinPoint jp,RedisEvict redisEvict){  
          
          try{  
                
              String modelName = redisEvict.type().getName();  
              // 清除对应缓存  
              jedisClient.del(modelName);    
                
          }catch (Exception e) {  
                  
                e.printStackTrace();  
                System.out.println("缓存服务器出现问题,发邮箱，发信息...");       
        }  
    }  
  
    // 配置环绕方法  
    @Around("@annotation(redisCache)")  
    public Object doAround(ProceedingJoinPoint pjp, RedisCache redisCache)  
            throws Throwable {  
        // 得到被代理的方法  
        // Method me = ((MethodSignature) pjp.getSignature()).getMethod();  
        // Class modelType = me.getAnnotation(RedisCache.class).type();  
  
        //得到注解上类型  
        Class modelType = redisCache.type();  
        //System.out.println(modelType.getName());  
          
        // 去Redis中看看有没有我们的数据 包名+ 类名 + 方法名 + 参数(多个)  
        String cacheKey = getCacheKey(pjp);  
        System.out.println(cacheKey);  
  
        String value = null;  
          
        try {//当取redis发生异常时，为了不影响程序正常执行，需要try..catch()...  
              
            //检查redis中是否有缓存  
            value = jedisClient.hget(modelType.getName(),cacheKey);  
              
        } catch (Exception e) {  
              
            e.printStackTrace();  
            System.out.println("缓存服务器出现问题,发邮箱，发信息...");  
              
        }  
  
        // result是方法的最终返回结果  
        Object result = null;  
        if (null == value) {  
            // 缓存未命中  
            System.out.println("缓存未命中");  
  
            // 后端查询数据    
            result = pjp.proceed();  
             
            try {//当取redis发生异常时，为了不影响程序正常执行，需要try..catch()...  
                  
                // 序列化结果放入缓存  
                String json = serialize(result);  
                jedisClient.hset(modelType.getName(), cacheKey, json);  
                  
                if(redisCache.expire()>0) {   
                    jedisClient.expire(cacheKey, redisCache.expire());//设置缓存时间
                    
                    //redis中不能对于map中一个键值对单独进行缓存过期时间的设置，只能对整个map设置过期时间
                    //真正使用时可以考虑将此处的hset直接改成set，每条缓存记录都是独立单独的，并且并不比放在一个map中性能损失多少
                }  
                  
            } catch (Exception e) {  
                  
                e.printStackTrace();  
                System.out.println("缓存服务器出现问题,发邮箱，发信息...");  
            }  
              
        } else {  
              
            try{//当数据转换失败发生异常时，为了不影响程序正常执行，需要try..catch()...  
                  
                // int i =1/0;  
                  
                // 得到被代理方法的返回值类型  
                Class returnType = ((MethodSignature) pjp.getSignature()).getReturnType();  
                  
                //把json反序列化  
                result = deserialize(value, returnType, modelType);  
                  
                  
                // 缓存命中  
                System.out.println("缓存命中");  
            } catch (Exception e) {  
                  
                //数据转换失败，到后端查询数据    
                result = pjp.proceed();  
                  
                e.printStackTrace();  
                System.out.println("缓存命中,但数据转换失败...");  
            }  
  
        }  
  
        return result;  
    }  
      
  
    protected String serialize(Object target) {  
        return JsonUtils.objectToJson(target);  
    }  
  
    protected Object deserialize(String jsonString, Class clazz, Class modelType) {  
        // 序列化结果应该是List对象  
        if (clazz.isAssignableFrom(List.class)) {  
            return JsonUtils.jsonToList(jsonString, modelType);  
        }  
  
        // 序列化结果是普通对象  
        return JsonUtils.jsonToPojo(jsonString, clazz);  
    }  
  
      
    // 包名+ 类名 + 方法名 + 参数(多个) 生成Key  
    public String getCacheKey(ProceedingJoinPoint pjp) {  
        StringBuffer key = new StringBuffer();  
        // 包名+ 类名 cn.core.serice.product.ProductServiceImpl.productList  
        String packageName = pjp.getTarget().getClass().getName();  
  
        key.append(packageName);  
        // 方法名  
        String methodName = pjp.getSignature().getName();  
        key.append(".").append(methodName);  
  
        // 参数(多个)  
        Object[] args = pjp.getArgs();  
  
        for (Object arg : args) {  
            // 参数  
            key.append(".").append(arg.toString());  
        }  
  
        return key.toString();  
    }  
}  