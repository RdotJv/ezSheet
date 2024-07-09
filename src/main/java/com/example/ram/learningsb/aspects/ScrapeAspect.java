package com.example.ram.learningsb.aspects;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ScrapeAspect {
    @Around("execution(* com.example.ram.learningsb.controllers.Controller1.scrape(..))")
    public Object aroundScrape(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Scraping..");
        return joinPoint.proceed();
    }
}
