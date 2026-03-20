package com.tetris.tetrisburger_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TetrisburgerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TetrisburgerBackendApplication.class, args);
    }

}
  