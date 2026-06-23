package com.paranoia.engine.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.CompilationUnit;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.FragilidadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class JavaParserAnaliseService implements AnaliseService {

    private static final Logger log = LoggerFactory.getLogger(JavaParserAnaliseService.class);

    private static final Set<String> TIPOS_IMUTAVEIS = Set.of(
        "String", "Integer", "Long", "Double", "Float", "Boolean",
        "Short", "Byte", "Character", "BigDecimal", "BigInteger",
        "LocalDate", "LocalTime", "LocalDateTime", "Instant",
        "ZonedDateTime", "OffsetDateTime", "OffsetTime",
        "Year", "YearMonth", "MonthDay"
    );

    @Override
    public List<FragilidadePoint> analisar(String caminhoDiretorio) {
        Path diretorio = Paths.get(caminhoDiretorio);
        if (!Files.isDirectory(diretorio)) {
            throw new IllegalArgumentException(
                "Caminho não é um diretório válido: " + caminhoDiretorio);
        }

        List<FragilidadePoint> pontos = new ArrayList<>();

        try (Stream<Path> files = Files.walk(diretorio)) {
            files.filter(p -> p.toString().endsWith(".java"))
                 .forEach(file -> {
                     try {
                         pontos.addAll(analisarArquivo(file));
                     } catch (IOException e) {
                         log.warn("Erro ao processar {}: {}", file, e.getMessage());
                     } catch (Exception e) {
                         log.warn("Erro inesperado ao processar {}: {}", file, e.getMessage());
                     }
                 });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao percorrer diretório: " + caminhoDiretorio, e);
        }

        return pontos;
    }

    private List<FragilidadePoint> analisarArquivo(Path filePath) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(filePath);
        List<String> linhas = Files.readAllLines(filePath);
        List<FragilidadePoint> pontos = new ArrayList<>();

        String nomeClasse = cu.getPrimaryTypeName().orElse("Unknown");
        String pacote = cu.getPackageDeclaration()
                         .map(pd -> pd.getNameAsString())
                         .orElse("");
        String classeOrigem = pacote.isEmpty() ? nomeClasse : pacote + "." + nomeClasse;

        pontos.addAll(detectarTransacoes(cu, classeOrigem, linhas));
        pontos.addAll(detectarChamadasExternas(cu, classeOrigem, linhas));
        pontos.addAll(detectarEstadoCompartilhado(cu, classeOrigem, linhas));

        return pontos;
    }

    // ─── @Transactional ──────────────────────────────────────────────────────

    private List<FragilidadePoint> detectarTransacoes(
            CompilationUnit cu, String classeOrigem, List<String> linhas) {
        List<FragilidadePoint> pontos = new ArrayList<>();

        boolean classeTransacional = false;
        for (TypeDeclaration<?> td : cu.findAll(TypeDeclaration.class)) {
            for (AnnotationExpr ann : td.getAnnotations()) {
                if (nomeAnotacaoEhTransactional(ann.getNameAsString())) {
                    classeTransacional = true;
                    break;
                }
            }
            if (classeTransacional) break;
        }

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            boolean methodAnnotated = false;
            for (AnnotationExpr ann : method.getAnnotations()) {
                if (nomeAnotacaoEhTransactional(ann.getNameAsString())) {
                    methodAnnotated = true;
                    break;
                }
            }

            boolean isTransactional = methodAnnotated
                || (classeTransacional && method.isPublic());

            if (isTransactional) {
                int linha = method.getBegin().map(r -> r.line).orElse(0);
                String snippet = extrairSnippet(linhas, linha, 10);

                String descricao = methodAnnotated
                    ? "Método anotado com @Transactional — pode expor a transação a contenção, "
                      + "deadlock ou isolamento inadequado em cenários concorrentes."
                    : "Método público em classe anotada com @Transactional — "
                      + "herda o comportamento transacional da classe.";

                pontos.add(new FragilidadePoint(
                    FragilidadeType.TRANSACAO, classeOrigem,
                    method.getNameAsString(), linha, snippet, descricao));
            }
        }

        return pontos;
    }

    private boolean nomeAnotacaoEhTransactional(String nome) {
        return nome.equals("Transactional")
            || nome.equals("org.springframework.transaction.annotation.Transactional");
    }

    // ─── Chamadas externas ───────────────────────────────────────────────────

    private List<FragilidadePoint> detectarChamadasExternas(
            CompilationUnit cu, String classeOrigem, List<String> linhas) {
        List<FragilidadePoint> pontos = new ArrayList<>();

        pontos.addAll(detectarJpaRepository(cu, classeOrigem, linhas));
        pontos.addAll(detectarHttpClients(cu, classeOrigem, linhas));

        return pontos;
    }

    private List<FragilidadePoint> detectarJpaRepository(
            CompilationUnit cu, String classeOrigem, List<String> linhas) {
        List<FragilidadePoint> pontos = new ArrayList<>();

        for (ClassOrInterfaceDeclaration td : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            if (!td.isInterface()) continue;

            boolean extendsJpa = td.getExtendedTypes().stream()
                .anyMatch(ext -> ext.getNameAsString().equals("JpaRepository")
                    || ext.getNameAsString().equals("ReactiveJpaRepository"));

            if (!extendsJpa) continue;

            for (MethodDeclaration method : td.findAll(MethodDeclaration.class)) {
                int linha = method.getBegin().map(r -> r.line).orElse(0);
                String snippet = extrairSnippet(linhas, linha, 10);

                pontos.add(new FragilidadePoint(
                    FragilidadeType.CHAMADA_EXTERNA, classeOrigem,
                    method.getNameAsString(), linha, snippet,
                    "Método em interface que estende JpaRepository — cada invocação "
                    + "resulta em operação de banco de dados (recurso externo)."));
            }
        }

        return pontos;
    }

    private List<FragilidadePoint> detectarHttpClients(
            CompilationUnit cu, String classeOrigem, List<String> linhas) {
        List<FragilidadePoint> pontos = new ArrayList<>();

        Map<String, String> camposHttp = new HashMap<>();
        for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {
            String tipo = field.getCommonType().toString();
            if (ehHttpClient(tipo)) {
                for (VariableDeclarator var : field.getVariables()) {
                    camposHttp.put(var.getNameAsString(), tipo);
                }
            }
        }

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            boolean temParamHttp = method.getParameters().stream()
                .anyMatch(p -> ehHttpClient(p.getType().toString()));

            if (temParamHttp) {
                int linha = method.getBegin().map(r -> r.line).orElse(0);
                String snippet = extrairSnippet(linhas, linha, 10);
                pontos.add(new FragilidadePoint(
                    FragilidadeType.CHAMADA_EXTERNA, classeOrigem,
                    method.getNameAsString(), linha, snippet,
                    "Método recebe RestTemplate/WebClient por parâmetro — "
                    + "pode realizar chamadas HTTP externas."));
                continue;
            }

            Set<String> varsHttpLocais = new HashSet<>();
            for (VariableDeclarationExpr vd : method.findAll(VariableDeclarationExpr.class)) {
                for (VariableDeclarator v : vd.getVariables()) {
                    if (ehHttpClient(v.getType().toString())) {
                        varsHttpLocais.add(v.getNameAsString());
                    }
                }
            }

            boolean chamadoViaCampo = method.findAll(MethodCallExpr.class).stream()
                .filter(call -> call.getScope().isPresent())
                .anyMatch(call -> {
                    String escopo = call.getScope().get().toString()
                        .replace("this.", "");
                    return camposHttp.containsKey(escopo)
                        || varsHttpLocais.contains(escopo);
                });

            if (chamadoViaCampo) {
                int linha = method.getBegin().map(r -> r.line).orElse(0);
                String snippet = extrairSnippet(linhas, linha, 10);
                pontos.add(new FragilidadePoint(
                    FragilidadeType.CHAMADA_EXTERNA, classeOrigem,
                    method.getNameAsString(), linha, snippet,
                    "Método invoca RestTemplate/WebClient — depende de recurso externo "
                    + "(rede) que pode falhar, ser lento ou ficar indisponível."));
            }
        }

        return pontos;
    }

    private boolean ehHttpClient(String tipo) {
        String simples = tipo.contains(".")
            ? tipo.substring(tipo.lastIndexOf('.') + 1)
            : tipo;
        return simples.equals("RestTemplate")
            || simples.equals("WebClient");
    }

    // ─── Estado compartilhado mutável ────────────────────────────────────────

    private List<FragilidadePoint> detectarEstadoCompartilhado(
            CompilationUnit cu, String classeOrigem, List<String> linhas) {
        List<FragilidadePoint> pontos = new ArrayList<>();

        Set<String> camposMutiveis = new HashSet<>();
        Map<String, String> tipoPorCampo = new HashMap<>();

        for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {
            if (field.isFinal()) continue;

            String tipo = field.getCommonType().toString();
            String tipoSimples = tipo.contains(".")
                ? tipo.substring(tipo.lastIndexOf('.') + 1)
                : tipo;

            if (TIPOS_IMUTAVEIS.contains(tipoSimples)) continue;

            for (VariableDeclarator var : field.getVariables()) {
                camposMutiveis.add(var.getNameAsString());
                tipoPorCampo.put(var.getNameAsString(), tipo);
            }
        }

        if (camposMutiveis.isEmpty()) return pontos;

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (!method.isPublic()) continue;

            Set<String> modificados = new HashSet<>();

            for (AssignExpr assign : method.findAll(AssignExpr.class)) {
                String nome = extrairNomeAlvo(assign.getTarget());
                if (nome != null && camposMutiveis.contains(nome)) {
                    modificados.add(nome);
                }
            }

            for (UnaryExpr unary : method.findAll(UnaryExpr.class)) {
                String nome = extrairNomeAlvo(unary.getExpression());
                if (nome != null && camposMutiveis.contains(nome)) {
                    modificados.add(nome);
                }
            }

            for (String nome : modificados) {
                int linha = method.getBegin().map(r -> r.line).orElse(0);
                String snippet = extrairSnippet(linhas, linha, 10);
                String tipo = tipoPorCampo.getOrDefault(nome, "desconhecido");

                pontos.add(new FragilidadePoint(
                    FragilidadeType.ESTADO_COMPARTILHADO, classeOrigem,
                    method.getNameAsString(), linha, snippet,
                    String.format(
                        "Campo não-final '%s' (%s) é modificado no método público '%s'. "
                        + "Em cenário concorrente, múltiplas threads podem ler/escrever "
                        + "simultaneamente sem sincronização.",
                        nome, tipo, method.getNameAsString())));
            }
        }

        return pontos;
    }

    private String extrairNomeAlvo(Expression expr) {
        if (expr instanceof NameExpr name) {
            return name.getNameAsString();
        }
        if (expr instanceof FieldAccessExpr field) {
            return field.getNameAsString();
        }
        return null;
    }

    // ─── Utilitário de snippet ───────────────────────────────────────────────

    private String extrairSnippet(List<String> linhas, int linhaCentral, int maxLinhas) {
        int idx = Math.max(0, linhaCentral - 1 - maxLinhas / 2);
        int fim = Math.min(linhas.size(), idx + maxLinhas);
        if (fim - idx < maxLinhas) {
            idx = Math.max(0, fim - maxLinhas);
        }
        return String.join("\n", linhas.subList(idx, fim));
    }
}
