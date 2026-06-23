package com.paranoia.engine.config;

import com.paranoia.engine.execution.AlvoOperacao;
import com.paranoia.engine.execution.VerificadorEstado;
import com.paranoia.engine.model.CenarioEntity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

@Configuration(proxyBeanMethods = false)
public class RestPipelineConfig {

    private static final String FC = "http://localhost:9000";

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    Function<CenarioEntity, AlvoOperacao> provedorOperacao(RestTemplate restTemplate) {
        return cenario -> () -> {
            String token = obterToken(restTemplate);
            String nome = cenario.getNome().toLowerCase();
            if (nome.contains("creditar") || nome.contains("income")) {
                criarTransacao(restTemplate, token, "credito", 100.0, "INCOME");
            } else {
                criarTransacao(restTemplate, token, "debito", 50.0, "EXPENSE");
            }
        };
    }

    @Bean
    Function<CenarioEntity, VerificadorEstado> provedorVerificador(RestTemplate restTemplate) {
        return cenario -> () -> {
            String health = restTemplate.getForEntity(
                FC + "/api/actuator/health", String.class).getBody();
            return "{\"health\": " + health + "}";
        };
    }

    private static String obterToken(RestTemplate rt) {
        var resp = rt.postForEntity(
            FC + "/api/auth/login",
            Map.of("username", "admin", "password", "admin123"),
            Map.class);
        return (String) resp.getBody().get("token");
    }

    private static void criarTransacao(RestTemplate rt, String token,
                                       String desc, double valor, String tipo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var body = Map.<String, Object>of(
            "description", desc,
            "amount", valor,
            "type", tipo,
            "date", LocalDate.now().toString());
        rt.exchange(FC + "/api/transactions", HttpMethod.POST,
            new HttpEntity<>(body, headers), String.class);
    }
}
