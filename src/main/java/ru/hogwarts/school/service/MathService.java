package ru.hogwarts.school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.stream.LongStream;

@Service
@Slf4j
public class MathService {
    private static final long LIMIT = 1_000_000L;

    public long calculateSum() {
        log.info("Calculating sum from 1 to 1,000,000 using parallel streams");
        return LongStream.iterate(1, a -> a + 1)
                .limit(LIMIT)
                .parallel()
                .reduce(0, Long::sum);
    }
}
