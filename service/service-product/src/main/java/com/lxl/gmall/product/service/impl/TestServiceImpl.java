package com.lxl.gmall.product.service.impl;

import com.lxl.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/18 16:29
 * @PackageName:com.lxl.gmall.product.service.impl
 * @ClassName: TestServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public synchronized void testLock() {
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid,2, TimeUnit.SECONDS);
        if(lock){
            String value = this.redisTemplate.opsForValue().get("num");
            if(StringUtils.isEmpty(value)){
                return;
            }
            //有值就转换成int 然后++
            int num= Integer.parseInt(value);
            this.redisTemplate.opsForValue().set("num",String.valueOf(++num));
//            // 2. 释放锁 del
//            if(uuid.equals((String) redisTemplate.opsForValue().get("lock"))){
//                this.redisTemplate.delete("lock");
//            }
            //定义lua脚本
            //  定义一个lua 脚本
            String script = " if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            //  准备执行lua 脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //  将lua脚本放入DefaultRedisScript 对象中
            redisScript.setScriptText(script);
            //  设置DefaultRedisScript 这个对象的泛型
            redisScript.setResultType(Long.class);
            //  执行删除
            redisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);

        }else {
            // 3. 每隔1秒钟回调一次，再次尝试获取锁
            try {
                Thread.sleep(100);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
