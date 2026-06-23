package com.paranoia.engine.execution;

import com.paranoia.engine.model.TipoCenario;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToxiproxyManagerImpl implements ToxiproxyManager {

    private static final Logger log = LoggerFactory.getLogger(ToxiproxyManagerImpl.class);

    private final ToxiproxyClient client;
    private final String proxyNome;

    public ToxiproxyManagerImpl(String toxiproxyUrl, String proxyNome) {
        String[] parts = toxiproxyUrl.split(":");
        this.client = new ToxiproxyClient(parts[0], Integer.parseInt(parts[1]));
        this.proxyNome = proxyNome;
    }

    @Override
    public void aplicarFalha(TipoCenario tipo, String proxyNome) throws Exception {
        Proxy proxy = client.getProxy(proxyNome);

        switch (tipo) {
            case FALHA_REDE -> {
                log.info("FALHA_REDE: desabilitando proxy {}", proxyNome);
                proxy.disable();
            }
            case FALHA_BANCO -> {
                log.info("FALHA_BANCO: desabilitando proxy {}", proxyNome);
                proxy.disable();
            }
            case TIMEOUT -> {
                log.info("TIMEOUT: aplicando latencia de 30s no proxy {}", proxyNome);
                proxy.toxics().latency("timeout_latencia", ToxicDirection.DOWNSTREAM, 30_000);
            }
            default -> log.warn("Tipo {} nao requer toxic", tipo);
        }
    }

    @Override
    public void restaurar(String proxyNome) throws Exception {
        Proxy proxy = client.getProxy(proxyNome);
        proxy.enable();
        client.reset();
        log.info("Proxy {} restaurado e toxics limpos", proxyNome);
    }
}
