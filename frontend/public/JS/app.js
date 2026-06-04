const API_URL = "http://localhost:8080/api/v1/carteiras";
let carteiraAtual = "";
let extratoAtual = [];

// --- CONTROLE DE INTERFACE (ONBOARDING) ---
function mostrarFormLogin() {
    document.getElementById("botoes-iniciais").style.display = "none";
    document.getElementById("form-login").style.display = "flex";
}

function voltarOnboarding() {
    document.getElementById("botoes-iniciais").style.display = "flex";
    document.getElementById("form-login").style.display = "none";
    document.getElementById("input-uuid").value = "";
}

function alternarTelas(mostrarDashboard) {
    const telaOnboarding = document.getElementById("onboarding-screen");
    const telaDashboard = document.getElementById("dashboard-screen");

    if (mostrarDashboard) {
        telaOnboarding.classList.add("screen-hidden");
        telaDashboard.classList.remove("screen-hidden");
    } else {
        telaDashboard.classList.add("screen-hidden");
        telaOnboarding.classList.remove("screen-hidden");
        voltarOnboarding();
    }
}

// --- CONTROLE DOS MODAIS ---
function abrirModal(idModal) {
    const modal = document.getElementById(idModal);
    modal.classList.remove("screen-hidden");
    modal.style.display = "flex";
}

function fecharModal(idModal) {
    const modal = document.getElementById(idModal);
    modal.classList.add("screen-hidden");
    modal.style.display = "none";
    const form = modal.querySelector('form');
    if(form) form.reset();
}

// --- ROTAS DA API ---
async function criarNovaCarteira() {
    try {
        const resposta = await fetch(API_URL, {
            method: "POST"
        });

        if (!resposta.ok) throw new Error("Falha ao gerar nova carteira no servidor.");

        const dados = await resposta.json();
        carteiraAtual = dados.enderecoCarteira;
        
        // Exibe o Alerta Crítico exigido pela arquitetura de Segurança
        alert(`${dados.alerta}\n\nSEU UUID (Acesso):\n${dados.enderecoCarteira}\n\nSUA CHAVE PRIVADA (Assinatura):\n${dados.chavePrivada}`);
        carregarDashboard();

    } catch (erro) {
        alert(erro.message);
    }
}

async function acessarCarteira() {
    const uuid = document.getElementById("input-uuid").value.trim();
    if (!uuid) return alert("Por favor, cole seu UUID.");
    carteiraAtual = uuid;
    carregarDashboard();
}

async function carregarDashboard() {
    try {
        const [resSaldos, resExtrato] = await Promise.all([
            fetch(`${API_URL}/${carteiraAtual}/saldos`),
            fetch(`${API_URL}/${carteiraAtual}/extrato`)
        ]);

        if (!resSaldos.ok) throw new Error("Carteira não encontrada. Verifique o UUID.");

        const saldos = await resSaldos.json();
        const extrato = await resExtrato.json();

        renderizarSaldos(saldos);
        renderizarExtrato(extrato);

        document.getElementById("uuid-logado").innerText = `UUID: ${carteiraAtual.substring(0, 13)}...`;
        alternarTelas(true);

    } catch (erro) {
        alert(erro.message);
        carteiraAtual = "";
    }
}

function sair() {
    carteiraAtual = "";
    extratoAtual = [];
    alternarTelas(false);
}

// --- RENDERIZAÇÃO DE DADOS ---
function renderizarSaldos(listaSaldos) {
    const container = document.getElementById("cards-saldo");
    container.innerHTML = "";
    listaSaldos.forEach(item => {
        container.innerHTML += `
            <div class="card-saldo">
                <h4>Saldo em ${item.idMoeda === 1 ? 'BTC' : item.idMoeda === 2 ? 'ETH' : item.idMoeda === 3 ? 'SOL' : item.idMoeda === 4 ? 'USD' : 'BRL'}</h4>
                <p class="valor">${item.saldo}</p>
            </div>
        `;
    });
}

function renderizarExtrato(listaExtrato) {
    extratoAtual = listaExtrato; 
    const tbody = document.getElementById("corpo-extrato");
    tbody.innerHTML = "";
    
    if(listaExtrato.length === 0) {
        tbody.innerHTML = "<tr><td colspan='6' style='text-align: center; padding: 30px;'>Nenhuma transação encontrada.</td></tr>";
        return;
    }

    listaExtrato.forEach((tx, index) => {
        const dataFormatada = new Date(tx.dataHora).toLocaleString('pt-PT');
        const operacaoLimpa = tx.tipoOperacao.replace('_', ' ');

        tbody.innerHTML += `
            <tr>
                <td>${dataFormatada}</td>
                <td><strong>${operacaoLimpa}</strong></td>
                <td>${tx.moeda.codigo}</td>
                <td>${tx.valor}</td>
                <td class="taxa">${tx.taxaCobrada > 0 ? '- ' + tx.taxaCobrada : 'Isento'}</td>
                <td><button class="btn-sm" onclick="abrirRecibo(${index})">Ver</button></td>
            </tr>
        `;
    });
}

// --- RENDERIZAÇÃO DO COMPROVANTE ---
function abrirRecibo(index) {
    const tx = extratoAtual[index]; 
    const dataFormatada = new Date(tx.dataHora).toLocaleString('pt-PT');
    const operacaoLimpa = tx.tipoOperacao.replace('_', ' ');
    const corpoRecibo = document.getElementById("recibo-detalhes");

    corpoRecibo.innerHTML = `
        <p><span>Data/Hora:</span> <strong>${dataFormatada}</strong></p>
        <p><span>Operação:</span> <strong>${operacaoLimpa}</strong></p>
        <p><span>Moeda:</span> <strong>${tx.moeda.codigo}</strong></p>
        <p><span>Valor Bruto:</span> <strong>${tx.valor}</strong></p>
        <p><span>Taxa do Sistema:</span> <strong>${tx.taxaCobrada > 0 ? tx.taxaCobrada : 'Isento'}</strong></p>
        <div style="margin-top: 15px; border-top: 1px dotted #ccc; padding-top: 10px;">
            <p style="font-size: 12px; margin-bottom: 2px;"><span>Conta Base (UUID):</span></p>
            <p style="font-size: 11px; word-break: break-all;"><strong>${carteiraAtual}</strong></p>
            <p style="font-size: 12px; margin-top: 10px; margin-bottom: 2px;"><span>ID da Transação:</span></p>
            <p style="font-size: 11px; word-break: break-all;"><strong>${tx.id ? tx.id : 'Autenticador-CD-' + Math.floor(Math.random() * 100000)}</strong></p>
        </div>
    `;

    abrirModal('modal-recibo');
}

// --- INTEGRAÇÃO DAS OPERAÇÕES COM O BACKEND ---
async function dispararRequisicao(endpoint, payload) {
    const resposta = await fetch(`${API_URL}/${carteiraAtual}/${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!resposta.ok) {
        const erroJson = await resposta.json();
        throw new Error(erroJson.erro || erroJson.message || "Assinatura Rejeitada ou Falha na regra de negócio.");
    }
    return await resposta.json();
}

async function realizarDeposito(event) {
    event.preventDefault();
    const valor = parseFloat(document.getElementById("dep-valor").value);
    const moeda = document.getElementById("dep-moeda").value;

    try {
        await dispararRequisicao("depositos", { codigoMoeda: moeda, valor: valor });
        fecharModal('modal-deposito');
        carregarDashboard();
    } catch (erro) {
        alert("Erro no Depósito: " + erro.message);
    }
}

async function realizarSaque(event) {
    event.preventDefault();
    const valor = parseFloat(document.getElementById("saq-valor").value);
    const moeda = document.getElementById("saq-moeda").value;
    const chave = document.getElementById("saq-chave").value.trim();

    try {
        await dispararRequisicao("saques", { codigoMoeda: moeda, valor: valor, chavePrivada: chave });
        fecharModal('modal-saque');
        carregarDashboard();
    } catch (erro) {
        alert("Erro no Saque: " + erro.message);
    }
}

async function realizarTransferencia(event) {
    event.preventDefault();
    const destino = document.getElementById("transf-destino").value.trim();
    const valor = parseFloat(document.getElementById("transf-valor").value);
    const moeda = document.getElementById("transf-moeda").value;
    const chave = document.getElementById("transf-chave").value.trim();

    try {
        await dispararRequisicao("transferencias", { 
            enderecoDestino: destino, 
            codigoMoeda: moeda, 
            valor: valor,
            chavePrivada: chave 
        });
        fecharModal('modal-transferencia');
        carregarDashboard();
    } catch (erro) {
        alert("Erro na Transferência: " + erro.message);
    }
}

async function realizarCambio(event) {
    event.preventDefault();
    const valor = parseFloat(document.getElementById("cambio-valor").value);
    const origem = document.getElementById("cambio-origem").value;
    const destino = document.getElementById("cambio-destino").value;
    const chave = document.getElementById("cambio-chave").value.trim();

    if(origem === destino) return alert("As moedas de origem e destino devem ser diferentes.");

    try {
        await dispararRequisicao("conversoes", { 
            moedaOrigem: origem, 
            moedaDestino: destino, 
            valorOrigem: valor,
            chavePrivada: chave
        });
        fecharModal('modal-cambio');
        carregarDashboard();
    } catch (erro) {
        alert("Erro no Câmbio: " + erro.message);
    }
}