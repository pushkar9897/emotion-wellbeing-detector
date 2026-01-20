package com.mindwell.emotion.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {




	SpringApplication.run(BackendApplication .class,args);
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("🧠 Emotion Wellbeing Detector is Running!");
        System.out.println("📡 Server: http://localhost:8080");
        System.out.println("🔗 API: http://localhost:8080/api/emotion");
        System.out.println("═══════════════════════════════════════════════════════════");
}
}
