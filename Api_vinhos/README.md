# 🍷 Api_vinhos — Módulo de Integração com WineAPI

Módulo Java independente dedicado à consulta e consumo de uma **base de dados externa de vinhos (WineAPI)**. Permite que o SipLog sugira dados padronizados de garrafas comerciais, evitando preenchimento manual pelo usuário.

---

## Sumário

1. [Propósito](#propósito)
2. [Estrutura](#estrutura)
3. [Como Funciona](#como-funciona)
4. [Como Compilar e Executar](#como-compilar-e-executar)
5. [Integração com o SipLog](#integração-com-o-siplog)

---

## Propósito

Quando um usuário do SipLog quer registrar a experiência com um vinho comercial (ex.: "Concha y Toro Casillero del Diablo 2020"), este módulo pode consultar a WineAPI e retornar dados padronizados como:

- Nome da garrafa
- Uva(s) utilizada(s)
- Safra (ano)
- Teor alcoólico
- País / região de origem
- Avaliação média externa

Isso popula automaticamente o cadastro de bebida no SipLog, enriquecendo o catálogo colaborativo.

---

## Estrutura

```
Api_vinhos/
├── API_Wineapi.java        ← Classe principal: chamadas HTTP à WineAPI e parse da resposta
├── API_Wineapi.class       ← Bytecode compilado (não versionar em projetos reais)
├── pom.xml                 ← Descritor Maven do projeto
└── README.md               ← Este arquivo
```

---

## Como Funciona

O arquivo `API_Wineapi.java` realiza requisições HTTP à API externa de vinhos, processa o JSON de resposta e extrai os campos relevantes.

O fluxo básico:

```
1. Recebe como entrada o nome ou identificador do vinho
2. Monta a requisição HTTP GET para o endpoint da WineAPI
3. Processa a resposta JSON
4. Retorna os dados estruturados (nome, uva, safra, teor alcoólico, região)
```

---

## Como Compilar e Executar

O projeto usa Maven padrão (`pom.xml`):

```bash
cd Api_vinhos

# Compilar e gerar o JAR
mvn clean package

# Ou usar diretamente o compilador Java (modo standalone)
javac API_Wineapi.java
java API_Wineapi
```

> Se Maven não estiver instalado, use o Maven Wrapper de outro módulo:
> ```bash
> ../core_api/apiCore-sipLog/mvnw -f pom.xml clean package
> ```

---

## Integração com o SipLog

Este módulo é um **utilitário independente** e não está acoplado diretamente ao runtime do BFF ou da Core API. Pode ser invocado:

1. **Como biblioteca** — compilado como JAR e adicionado como dependência na Core API
2. **Como script batch** — para pré-popular o catálogo `tb_bebida` com vinhos comuns
3. **Como microserviço futuro** — evoluindo para um serviço REST dedicado consultado pelo BFF

Para integração ao catálogo, os dados retornados devem ser mapeados para o formato `NovaBebidaDTO`:

```json
{
  "nome": "Casillero del Diablo Reserva",
  "categoria": "TINTO",
  "fabricante": "Concha y Toro",
  "caracteristicas": {
    "uva": "Cabernet Sauvignon",
    "safra": "2020",
    "regiao": "Valle Central, Chile",
    "teor_alcoolico": "13.5"
  }
}
```

---

> **Dependência externa:** WineAPI (base de dados de vinhos)
> **Tecnologia:** Java · Maven
> **Status:** Módulo utilitário / integração experimental
