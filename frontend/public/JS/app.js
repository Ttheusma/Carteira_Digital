// ═══════════════════════════════════════════════
//  CONFIG
// ═══════════════════════════════════════════════

const API_URL = 'http://localhost:8080/api/v1/carteiras';
let carteiraAtual = '';
let extratoAtual  = [];

// ═══════════════════════════════════════════════
//  TEMA CLARO / ESCURO
// ═══════════════════════════════════════════════

function alternarTema() {
    const isLight = document.documentElement.classList.toggle('light');
    localStorage.setItem('tema', isLight ? 'light' : 'dark');
    _sincronizarIconesTema(isLight);
}

function _sincronizarIconesTema(isLight) {
    const icone = isLight ? '☀️' : '🌙';
    ['btn-tema', 'btn-tema-onboarding'].forEach(function (id) {
        const btn = document.getElementById(id);
        if (btn) btn.textContent = icone;
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const isLight = document.documentElement.classList.contains('light');
    _sincronizarIconesTema(isLight);
});

// ═══════════════════════════════════════════════
//  MAPEAMENTO DE MOEDAS
// ═══════════════════════════════════════════════

const COINS = {
    BTC: { cor: '#F7931A', nome: 'Bitcoin',         icone: '₿'  },
    ETH: { cor: '#627EEA', nome: 'Ethereum',         icone: 'Ξ'  },
    SOL: { cor: '#9945FF', nome: 'Solana',           icone: '◎'  },
    USD: { cor: '#2775CA', nome: 'Dólar Americano',  icone: '$'  },
    BRL: { cor: '#34d57e', nome: 'Real Brasileiro',  icone: 'R$' },
};

const ID_TO_CODIGO = { 1: 'BTC', 2: 'ETH', 3: 'SOL', 4: 'USD', 5: 'BRL' };

// ═══════════════════════════════════════════════
//  FORMATAÇÃO MONETÁRIA — padrão único para toda a aplicação
//  (saldos, extrato, comprovantes e mensagens de operação)
// ═══════════════════════════════════════════════
function formatarMoeda(valor, codigo) {
    const coin   = COINS[codigo] || { icone: codigo };
    const casas  = (codigo === 'BRL' || codigo === 'USD') ? 2 : 8;
    const numero = parseFloat(valor || 0).toLocaleString('pt-BR', {
        minimumFractionDigits: casas,
        maximumFractionDigits: casas,
    });
    return `${coin.icone} ${numero}`;
}

const PANEL_TITLES = { dashboard: 'Dashboard', historico: 'Histórico' };

// ═══════════════════════════════════════════════
//  NAVEGAÇÃO SIDEBAR
// ═══════════════════════════════════════════════

document.querySelectorAll('.nav-item[data-panel]').forEach(item => {
    item.addEventListener('click', () => {
        const panel = item.dataset.panel;
        document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
        item.classList.add('active');
        document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
        document.getElementById('panel-' + panel).classList.add('active');
        document.getElementById('topbar-title').textContent = PANEL_TITLES[panel] || panel;
        if (panel === 'historico') renderizarExtrato(extratoAtual);
    });
});

// ═══════════════════════════════════════════════
//  ALTERNÂNCIA DE TELAS
// ═══════════════════════════════════════════════

function alternarTelas(mostrarDashboard) {
    const onboarding = document.getElementById('onboarding-screen');
    const dashboard  = document.getElementById('dashboard-screen');
    if (mostrarDashboard) {
        onboarding.style.display = 'none';
        dashboard.style.display  = 'flex';
    } else {
        dashboard.style.display  = 'none';
        onboarding.style.display = '';
        voltarOnboarding();
    }
}

// ═══════════════════════════════════════════════
//  ONBOARDING
// ═══════════════════════════════════════════════

function mostrarFormLogin() {
    document.getElementById('botoes-iniciais').style.display = 'none';
    document.getElementById('form-login').style.display      = 'flex';
}

function voltarOnboarding() {
    document.getElementById('botoes-iniciais').style.display = 'flex';
    document.getElementById('form-login').style.display      = 'none';
    document.getElementById('input-endereco-acesso').value = '';
}

async function criarNovaCarteira() {
    try {
        const res  = await fetch(API_URL, { method: 'POST' });
        if (!res.ok) throw new Error('Falha ao criar carteira no servidor.');
        const data = await res.json();
        carteiraAtual = data.enderecoCarteira;

        document.getElementById('novo-endereco').textContent      = data.enderecoCarteira;
        document.getElementById('novo-chave-privada').textContent = data.chavePrivada;
        abrirModal('modal-nova-carteira');
    } catch (e) {
        toast('error', 'Erro ao criar carteira', e.message);
    }
}

function fecharModalNovaCarteira() {
    document.getElementById('modal-nova-carteira').classList.add('hidden');
    document.getElementById('novo-endereco').textContent      = '';
    document.getElementById('novo-chave-privada').textContent = '';
    carregarDashboard();
}

async function copiarCampo(id) {
    const el = document.getElementById(id);
    const valor = ('value' in el) ? el.value : el.textContent;
    try {
        await navigator.clipboard.writeText(valor);
    } catch (e) {
        const range = document.createRange();
        range.selectNodeContents(el);
        const selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
        document.execCommand('copy');
        selection.removeAllRanges();
    }
    toast('success', 'Copiado!', '');
}

async function acessarCarteira() {
    const endereco = document.getElementById('input-endereco-acesso').value.trim();
    if (!endereco) { toast('error', 'Endereço da carteira obrigatório', ''); return; }
    try {
        const res = await fetch(`${API_URL}/acesso`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ enderecoCarteira: endereco }),
        });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.mensagem || err.erro || 'Endereço inválido.');
        }
        const data = await res.json();
        carteiraAtual = data.enderecoCarteira;
        carregarDashboard();
    } catch (e) {
        toast('error', 'Acesso negado', e.message);
    }
}

function sair() {
    carteiraAtual = '';
    extratoAtual  = [];
    alternarTelas(false);
}

// ═══════════════════════════════════════════════
//  CARREGAR DASHBOARD
// ═══════════════════════════════════════════════

async function carregarDashboard() {
    try {
        const [resSaldos, resExtrato] = await Promise.all([
            fetch(`${API_URL}/${carteiraAtual}/saldos`),
            fetch(`${API_URL}/${carteiraAtual}/extrato`),
        ]);

        if (!resSaldos.ok) throw new Error('Carteira não encontrada. Verifique o endereço.');

        const saldos  = await resSaldos.json();
        const extrato = resExtrato.ok ? await resExtrato.json() : [];

        renderizarSaldos(Array.isArray(saldos) ? saldos : []);
        extratoAtual = Array.isArray(extrato) ? extrato : [];

        const curto = carteiraAtual.length > 26
            ? carteiraAtual.slice(0, 13) + '...' + carteiraAtual.slice(-6)
            : carteiraAtual;
        document.getElementById('sidebar-addr').innerHTML = `<span>${curto}</span>`;
        document.getElementById('topbar-status').style.display = 'inline-block';

        alternarTelas(true);
    } catch (e) {
        toast('error', 'Acesso negado', e.message);
        carteiraAtual = '';
    }
}

// ═══════════════════════════════════════════════
//  RENDERIZAÇÃO — SALDOS
// ═══════════════════════════════════════════════

function renderizarSaldos(listaSaldos) {
    const grid = document.getElementById('balances-grid');

    if (!listaSaldos || listaSaldos.length === 0) {
        grid.innerHTML = `<div class="balance-card" style="grid-column:1/-1">
            <div class="empty-state">Nenhum saldo encontrado.</div></div>`;
        return;
    }

    grid.innerHTML = listaSaldos.map(item => {
        const codigo = item.moeda?.codigo || item.codigo || ID_TO_CODIGO[item.idMoeda] || '';
        const saldo  = item.saldo ?? 0;
        const coin   = COINS[codigo] || { cor: '#7a7f94', nome: codigo, icone: codigo };

        return `
            <div class="balance-card">
                <div class="balance-coin">
                    <span class="coin-dot" style="background:${coin.cor}"></span>
                    ${codigo}
                </div>
                <div class="balance-val">${formatarMoeda(saldo, codigo)}</div>
                <div class="balance-fiat">${coin.nome}</div>
            </div>`;
    }).join('');
}

// ═══════════════════════════════════════════════
//  RENDERIZAÇÃO — EXTRATO
// ═══════════════════════════════════════════════

function renderizarExtrato(lista) {
    const container = document.getElementById('hist-content');

    if (!lista || lista.length === 0) {
        container.innerHTML = '<div class="empty-state">Nenhuma transação encontrada.</div>';
        return;
    }

    const badgeMap = {
        DEPOSITO:                'badge-dep',
        SAQUE:                   'badge-saq',
        CONVERSAO:               'badge-conv',
        TRANSFERENCIA:           'badge-trans',
        'TRANSFERENCIA ENVIADA': 'badge-trans',
        'TRANSFERENCIA RECEBIDA':'badge-trans',
    };

    const rows = lista.map((tx, index) => {
        const tipo  = (tx.tipo || tx.tipoOperacao || '').toUpperCase().replace(/_/g, ' ');
        const moeda = tx.moeda?.codigo || tx.codigo || '';
        const taxa  = parseFloat(tx.taxaValor ?? tx.taxaCobrada ?? 0);
        const dt    = tx.dataHora || tx.data_hora || '';
        const badge = badgeMap[tipo] || 'badge-dep';

        return `
            <tr>
                <td style="color:var(--muted);font-size:11px">${dt ? new Date(dt).toLocaleString('pt-BR') : '—'}</td>
                <td><span class="badge ${badge}">${tipo}</span></td>
                <td style="color:${COINS[moeda]?.cor || 'var(--text)'};font-weight:600">${moeda}</td>
                <td style="font-family:var(--font-mono);color:var(--purple)">${formatarMoeda(tx.valor, moeda)}</td>
                <td class="taxa">${taxa > 0
                    ? `<span style="color:var(--coral)">- ${formatarMoeda(taxa, moeda)}</span>`
                    : `<span style="color:var(--green)">Isento</span>`
                }</td>
                <td><button class="btn-recibo" onclick="abrirRecibo(${index})">🧾 Ver</button></td>
            </tr>`;
    }).join('');

    container.innerHTML = `
        <table class="hist-table">
            <thead>
                <tr>
                    <th>Data/Hora</th><th>Operação</th><th>Moeda</th>
                    <th>Valor</th><th>Taxa</th><th>Comprovante</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>`;
}

// ═══════════════════════════════════════════════
//  COMPROVANTE
// ═══════════════════════════════════════════════

function abrirRecibo(index) {
    const tx    = extratoAtual[index];
    const tipo  = (tx.tipo || tx.tipoOperacao || '').replace(/_/g, ' ');
    const dt    = tx.dataHora || tx.data_hora || '';
    const moeda = tx.moeda?.codigo || tx.codigo || '';
    const taxa  = parseFloat(tx.taxaValor ?? tx.taxaCobrada ?? 0);

    document.getElementById('recibo-detalhes').innerHTML = `
        <div class="recibo-linha"><span>Data/Hora</span><strong>${dt ? new Date(dt).toLocaleString('pt-BR') : '—'}</strong></div>
        <div class="recibo-linha"><span>Operação</span><strong>${tipo.toUpperCase()}</strong></div>
        <div class="recibo-linha"><span>Moeda</span><strong>${moeda}</strong></div>
        <div class="recibo-linha"><span>Valor</span><strong style="font-family:var(--font-mono)">${formatarMoeda(tx.valor, moeda)}</strong></div>
        <div class="recibo-linha"><span>Taxa</span><strong style="font-family:var(--font-mono)">${taxa > 0 ? formatarMoeda(taxa, moeda) : 'Isento'}</strong></div>
        <div class="recibo-divider"></div>
        <div class="recibo-linha"><span>Carteira</span></div>
        <div style="font-family:var(--font-mono);font-size:11px;color:var(--accent);word-break:break-all;margin-bottom:8px">${carteiraAtual}</div>`;

    abrirModal('modal-recibo');
}

// ═══════════════════════════════════════════════
//  MODAIS
// ═══════════════════════════════════════════════

function abrirModal(id) { document.getElementById(id).classList.remove('hidden'); }

function fecharModal(id) {
    document.getElementById(id).classList.add('hidden');
    document.getElementById(id).querySelectorAll('input, select').forEach(el => {
        if (el.tagName === 'SELECT') {
            el.selectedIndex = 0;
            el.dispatchEvent(new Event('change'));
        } else {
            el.value = '';
        }
    });
}

document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', e => {
        if (e.target === overlay) overlay.classList.add('hidden');
    });
});

// ═══════════════════════════════════════════════
//  MÁSCARA MONETÁRIA NOS CAMPOS DE VALOR
//  Mesma regra de casas decimais usada em formatarMoeda():
//  2 casas para BRL/USD, 8 casas para criptos.
//  O campo é text (não number) para poder exibir separador
//  de milhar/decimal em tempo real enquanto o usuário digita.
// ═══════════════════════════════════════════════

// Lê o valor numérico real de um campo mascarado (remove pontos de milhar, troca vírgula por ponto)
function lerValorMoeda(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return 0;
    const bruto = input.value.replace(/\./g, '').replace(',', '.');
    return parseFloat(bruto || '0');
}

// Reformata o conteúdo do campo a cada tecla digitada
function aplicarMascaraMoeda(e) {
    const input = e.target;
    const casas = parseInt(input.dataset.casas || '2', 10);
    const digitos = input.value.replace(/\D/g, '');

    if (!digitos) { input.value = ''; return; }

    const valor = parseInt(digitos, 10) / Math.pow(10, casas);
    input.value = valor.toLocaleString('pt-BR', {
        minimumFractionDigits: casas,
        maximumFractionDigits: casas,
    });
}

// Ajusta a quantidade de casas decimais quando a moeda selecionada muda,
// reformatando o valor já digitado (sem perdê-lo) para a nova precisão.
function ajustarPrecisaoInput(inputId, codigo) {
    const input = document.getElementById(inputId);
    if (!input) return;
    const casas = (codigo === 'BRL' || codigo === 'USD') ? 2 : 8;
    const valorAtual = lerValorMoeda(inputId);

    input.dataset.casas = casas;
    input.placeholder = (0).toLocaleString('pt-BR', {
        minimumFractionDigits: casas,
        maximumFractionDigits: casas,
    });

    if (valorAtual > 0) {
        input.value = valorAtual.toLocaleString('pt-BR', {
            minimumFractionDigits: casas,
            maximumFractionDigits: casas,
        });
    }
}

[
    ['dep-moeda', 'dep-valor'],
    ['saq-moeda', 'saq-valor'],
    ['cambio-origem', 'cambio-valor'],
    ['transf-moeda', 'transf-valor'],
].forEach(([selectId, inputId]) => {
    const select = document.getElementById(selectId);
    const input  = document.getElementById(inputId);
    if (!select || !input) return;

    input.addEventListener('input', aplicarMascaraMoeda);
    select.addEventListener('change', () => ajustarPrecisaoInput(inputId, select.value));
    if (select.value) ajustarPrecisaoInput(inputId, select.value);
});

// ═══════════════════════════════════════════════
//  API HELPER
// ═══════════════════════════════════════════════

async function dispararRequisicao(endpoint, payload) {
    const res = await fetch(`${API_URL}/${carteiraAtual}/${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.mensagem || err.erro || err.message || 'Falha na operação.');
    }
    return res.json();
}

// ═══════════════════════════════════════════════
//  OPERAÇÕES
// ═══════════════════════════════════════════════

async function realizarDeposito() {
    const valor = lerValorMoeda('dep-valor');
    const moeda = document.getElementById('dep-moeda').value;
    if (!valor || valor <= 0) { toast('error', 'Valor inválido', ''); return; }
    try {
        await dispararRequisicao('depositos', { codigoMoeda: moeda, valor });
        fecharModal('modal-deposito');
        toast('success', 'Depósito realizado!', `${formatarMoeda(valor, moeda)} adicionados.`);
        carregarDashboard();
    } catch (e) { toast('error', 'Erro no depósito', e.message); }
}

async function realizarSaque() {
    const valor = lerValorMoeda('saq-valor');
    const moeda = document.getElementById('saq-moeda').value;
    const chave = document.getElementById('saq-chave').value.trim();
    if (!valor || valor <= 0) { toast('error', 'Valor inválido', ''); return; }
    if (!chave)               { toast('error', 'Chave privada obrigatória', ''); return; }
    try {
        await dispararRequisicao('saques', { codigoMoeda: moeda, valor, chavePrivada: chave });
        fecharModal('modal-saque');
        toast('success', 'Saque realizado!', `${formatarMoeda(valor, moeda)} sacados.`);
        carregarDashboard();
    } catch (e) { toast('error', 'Erro no saque', e.message); }
}

async function realizarCambio() {
    const valor   = lerValorMoeda('cambio-valor');
    const origem  = document.getElementById('cambio-origem').value;
    const destino = document.getElementById('cambio-destino').value;
    const chave   = document.getElementById('cambio-chave').value.trim();
    if (!valor || valor <= 0)  { toast('error', 'Valor inválido', ''); return; }
    if (!origem || !destino)   { toast('error', 'Selecione as moedas', ''); return; }
    if (origem === destino)    { toast('error', 'Moedas iguais', 'Escolha moedas diferentes.'); return; }
    if (!chave)                { toast('error', 'Chave privada obrigatória', ''); return; }
    try {
        await dispararRequisicao('conversoes', { moedaOrigem: origem, moedaDestino: destino, valorOrigem: valor, chavePrivada: chave });
        fecharModal('modal-cambio');
        toast('success', 'Câmbio realizado!', `${formatarMoeda(valor, origem)} → ${destino}`);
        carregarDashboard();
    } catch (e) { toast('error', 'Erro no câmbio', e.message); }
}

async function realizarTransferencia() {
    const destino = document.getElementById('transf-destino').value.trim();
    const moeda   = document.getElementById('transf-moeda').value;
    const valor   = lerValorMoeda('transf-valor');
    const chave   = document.getElementById('transf-chave').value.trim();
    if (!destino)             { toast('error', 'Endereço de destino obrigatório', ''); return; }
    if (!valor || valor <= 0) { toast('error', 'Valor inválido', ''); return; }
    if (!chave)               { toast('error', 'Chave privada obrigatória', ''); return; }
    try {
        await dispararRequisicao('transferencias', { enderecoDestino: destino, codigoMoeda: moeda, valor, chavePrivada: chave });
        fecharModal('modal-transferencia');
        toast('success', 'Transferência realizada!', `${formatarMoeda(valor, moeda)} enviados.`);
        carregarDashboard();
    } catch (e) { toast('error', 'Erro na transferência', e.message); }
}

// ═══════════════════════════════════════════════
//  TOAST
// ═══════════════════════════════════════════════

function toast(type, title, msg) {
    const icons = { success: '✅', error: '❌', info: 'ℹ️' };
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.innerHTML = `
        <span class="toast-icon">${icons[type] || 'ℹ️'}</span>
        <div class="toast-body">
            <div class="toast-title">${title}</div>
            ${msg ? `<div class="toast-msg">${msg}</div>` : ''}
        </div>`;
    document.getElementById('toast-container').appendChild(el);
    setTimeout(() => el.remove(), 5000);
}
