package com.paranoia.engine.execution;

public record ConfigExecucao(
    int numeroThreads,
    int tentativasPorThread
) {
    public static ConfigExecucao padrao() {
        return new ConfigExecucao(20, 1);
    }
}
