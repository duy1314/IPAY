package com.duy.ipay.common.utils;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 雪花算法生成随机且唯一的数字字符串
 */
@ToString
@Slf4j
public class SnowflakeIdFactory {
    private static final long twepoch = 1288834974657L;

    private static final long workerIdBits = 5L;

    private static final long datacenterIdBits = 5L;

    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private static final long sequenceBits = 12L;

    private static final long workerIdShift = sequenceBits;

    private static final long datacenterIdShift = sequenceBits + workerIdBits;

    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);


    private static long workerId;

    private static long datacenterId;

    private static long sequence = 0L;

    private static long lastTimestamp = -1L;


    public SnowflakeIdFactory(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }


    public static synchronized String nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            //服务器时钟被调整了,ID生成器停止服务.
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return String.valueOf(((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence);

    }


    protected static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;

    }


    protected static long timeGen() {
        return System.currentTimeMillis();
    }





    public static void testProductIdByMoreThread(int dataCenterId, int workerId, int n) throws InterruptedException {
        List<Thread> tlist = new ArrayList<>();

        Set<String> setAll = new HashSet<>();

        CountDownLatch cdLatch = new CountDownLatch(10);

        long start = System.currentTimeMillis();

        int threadNo = dataCenterId;

        Map<String,SnowflakeIdFactory> idFactories = new HashMap<>();

        for(int i=0;i<10;i++){
            //用线程名称做map key
            idFactories.put("snowflake"+i,new SnowflakeIdFactory(workerId, threadNo++));
        }

        for(int i=0;i<10;i++){
            Thread temp =new Thread(new Runnable() {
                @Override
                public void run() {
                    Set<String> setId = new HashSet<>();
                    SnowflakeIdFactory idWorker = idFactories.get(Thread.currentThread().getName());

                    for(int j=0;j<n;j++){
                        setId.add(idWorker.nextId());
                    }

                    synchronized (setAll){
                        setAll.addAll(setId);
                        log.info("{}生产了{}个id,并成功加入到setAll中.",Thread.currentThread().getName(),n);
                    }

                    cdLatch.countDown();

                }

            },"snowflake"+i);

            tlist.add(temp);
        }

        for(int j=0;j<10;j++){
            tlist.get(j).start();
        }

        cdLatch.await();

        long end1 = System.currentTimeMillis() - start;
        log.info("共耗时:{}毫秒,预期应该生产{}个id, 实际合并总计生成ID个数:{}",end1,10*n,setAll.size());
    }


    public static void main(String[] args) {

        /** 多线程-测试多个生产者同时生产N个id, 全部id在全局范围内是否会重复?
         * 结论: 验证通过,没有重复. */
//        try {
//            testProductIdByMoreThread(1, 2, 100000);//单机测试此场景,性能损失至少折半!
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//
//        }
        System.out.println(SnowflakeIdFactory.nextId());

    }

}