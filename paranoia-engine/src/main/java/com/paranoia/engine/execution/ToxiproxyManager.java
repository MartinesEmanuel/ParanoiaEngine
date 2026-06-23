package com.paranoia.engine.execution;

import com.paranoia.engine.model.TipoCenario;

public interface ToxiproxyManager {

    void aplicarFalha(TipoCenario tipo, String proxyNome) throws Exception;

    void restaurar(String proxyNome) throws Exception;
}
