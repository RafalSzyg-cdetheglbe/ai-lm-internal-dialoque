package com.llmagent.lmagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LmagentApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmagentApplication.class, args);
		printAppName();
	}

	public static void printAppName() {
		System.out.println("\n" + "  _                                           _   \n" + " | |                    /\\                   | |  \n" + " | |     _ __ ___      /  \\   __ _  ___ _ __ | |_ \n" + " | |    | '_ ` _ \\    / /\\ \\ / _` |/ _ \\ '_ \\| __|\n" + " | |____| | | | | |  / ____ \\ (_| |  __/ | | | |_ \n" + " |______|_| |_| |_| /_/    \\_\\__, |\\___|_| |_|\\__|\n" + "                              __/ |               \n" + "                             |___/                \n");
	}
}
