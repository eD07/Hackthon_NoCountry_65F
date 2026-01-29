package com.hackathon.churninsight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación ChurnInsight.
 * 
 * <p>
 * ChurnInsight es un servicio de predicción de abandono (churn) de clientes
 * para plataformas de streaming. Expone una API REST que consume un servicio
 * de Machine Learning para realizar predicciones basadas en características
 * del comportamiento del cliente.
 * </p>
 * 
 * @author Hackathon Team
 * @version 1.0.0
 */
@SpringBootApplication
public class ChurninsightApplication {

	/**
	 * Punto de entrada de la aplicación Spring Boot.
	 *
	 * @param args Argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChurninsightApplication.class, args);
	}
}
