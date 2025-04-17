package ru.hogwarts.school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MathService {
    private static final int n = 1_000_000;

    public int calculateSum() {
        log.info("Calculating sum from 1 to 1,000,000");
        return n * (1 + n) / 2;
    }
}