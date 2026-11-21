# Amigo Secreto de Natal — Backend

API do site do Amigo Secreto (frontend em `NatalFront`). Implementa o contrato de `docs/API.md` e `docs/openapi.yaml` do frontend.

Mesma arquitetura do Bolão: Spring Boot 4 + Java 17, hexagonal (`api/rest` → `core` com ports e services → `infrastructure/supabase`), Postgres com Flyway, Docker e Render.

## Como funciona

- **Sem contas.** Quem se cadastra recebe um token pessoal (vai no link `#t=...`). O banco guarda só o hash SHA-256.
- **Organizador** entra com a chave `ADMIN_KEY` (link `#admin=...`, header `X-Admin-Key`).
- **O sorteio roda no servidor** (círculo único com Fisher–Yates e `SecureRandom`): ninguém tira a si mesmo e não há par fechado. Nenhuma rota devolve a lista de pares, nem para o organizador.
- Limite de tentativas: 10 chaves erradas por IP a cada 15 min e 30 cadastros por IP por hora.

## Rodar local

```bash
cp .env.example .env   # ajuste banco e ADMIN_KEY
export $(grep -v '^#' .env | xargs)
./mvnw spring-boot:run
```

- API: http://localhost:8080/api/config
- Swagger: http://localhost:8080/docs

No frontend, aponte `js/config.js` para a API:

```js
window.APP_CONFIG = { apiBaseUrl: "http://localhost:8080/api" };
```

## Testes

```bash
./mvnw test
```

## Publicar (Render + Supabase)

1. Crie um projeto no Supabase e copie a connection string do pooler (JDBC) para `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD`.
2. No Render, crie um Blueprint apontando para este repositório (`render.yaml`). O `ADMIN_KEY` é gerado automaticamente; copie o valor nas variáveis do serviço.
3. Em `CORS_ALLOWED_ORIGIN_PATTERNS`, coloque o endereço onde o frontend está publicado.
4. No frontend, `apiBaseUrl: "https://SEU-SERVICO.onrender.com/api"` e entre uma vez por `https://seu-site/#admin=SUA_ADMIN_KEY`.

O plano free do Render dorme sem uso: a primeira requisição depois de um tempo demora uns 30 s.

## Estrutura

```
api/rest/controller      ConfiguracaoController, ParticipanteController, AdminController (interface + Impl)
api/rest/handler         erros sempre como {"code", "message"}
api/rest/mapper          domínio -> DTO (nunca expõe token_hash nem pares)
core/domain/entity       Configuracao, Participante
core/port/in             ConfiguracaoPortIn, ParticipantePortIn, SorteioPortIn + DTOs
core/port/out            portas de repositório
core/application/service ConfiguracaoService, ParticipanteService, SorteioService
infrastructure/model     entidades JPA (tb_configuracao, tb_participante, tb_atribuicao, tb_sorteio_membro)
infrastructure/supabase  repositórios JPA + adaptadores (Persistence)
security                 AutenticacaoFilter (token pessoal / chave do organizador), SecurityConfig
shared                   SorteioCirculo, TokenUtil, LimitadorTentativas, mensagens
```

## Rotas

| Método | Caminho | Auth |
|---|---|---|
| GET | `/api/config` | — |
| GET / POST | `/api/participants` | — |
| GET / PUT / DELETE | `/api/me` | token |
| GET | `/api/me/assignment` | token |
| GET | `/api/admin/check` | chave |
| PUT | `/api/admin/config` | chave |
| GET / POST | `/api/admin/participants` | chave |
| PUT / DELETE | `/api/admin/participants/{id}` | chave |
| POST | `/api/admin/participants/{id}/link` | chave |
| POST | `/api/admin/draw` | chave |
| POST | `/api/admin/reopen` | chave |

`POST /api/me/ideas` (ideias com IA) não está implementada; `features.aiIdeas` volta `false` e o botão não aparece.
