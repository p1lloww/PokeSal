# PokeSal Battle System

Sistema de batalhas por turnos em Java, desenvolvido para a disciplina de Teste e
Qualidade de Software — UCSal.

## Requisitos para rodar

- JDK 17 ou superior
- Maven 3.9 ou superior

Verifique antes de rodar:

```
java -version
mvn -version
```

## Como rodar

```
mvn compile exec:java
```

Isso inicia a batalha via linha de comando: cada treinador escolhe seu nome e um dos
seis PokeSal iniciais, e a batalha é conduzida turno a turno, com escolha de ataque
ou uso de item, até que um dos dois seja derrotado.

## Como rodar os testes

```
mvn test
```

## Como validar o Checkstyle (Google Java Style)

```
mvn checkstyle:check
```

Para um relatório HTML legível:

```
mvn checkstyle:checkstyle
```

O relatório fica em `target/site/checkstyle.html`.

## Como gerar o JAR executável

```
mvn package
java -jar target/pokesal-battle-system.jar
```

## Estrutura de pacotes

```
br.edu.ucsal.tqs.pokesal
├── entities
│   ├── passives          — interface Passive e as sete implementações
│   ├── statusconditions   — interface StatusCondition e Burn, Poison, Paralyzed
│   ├── effects            — interface Effect e suas implementações
│   ├── battlegrounds      — interface Battleground e HotAsphalt, WaterPuddle, CentralFlowerbed
│   ├── turnactions        — interface TurnAction e MoveAction, ItemAction
│   ├── PokeSal, Trainer, Item, Move, Battle, Turn — entidades centrais
│   ├── ElementType, TypeChart, TypeMatchup — sistema de tipos elementais
│   └── PokeSalCatalog     — catálogo fixo dos seis PokeSal iniciais
├── services
│   └── DamageCalculatorService — cálculo de dano de um golpe
├── usecases
│   ├── ChooseInitialPokeSalUseCase — captura a escolha de PokeSal de um treinador
│   └── RunBattleUseCase            — conduz a batalha do início ao fim
└── io
    └── BattleCli          — entrada e saída via terminal (Scanner e console)
```

## Fluxo geral

1. `BattleRunner` (classe com `main`) monta as dependências e inicia a aplicação.
2. `ChooseInitialPokeSalUseCase` é executado duas vezes, uma por treinador, capturando
   nome e PokeSal inicial via `BattleCli`.
3. Um `Battleground` é sorteado para a batalha.
4. `RunBattleUseCase` dispara a entrada em campo dos dois PokeSal (passivas como
   Intimidate e Rain Dish) e conduz o loop de turnos: renderiza o estado atual, captura a
   ação de cada treinador (atacar ou usar item), executa o `Turn` correspondente, aplica
   os efeitos de fim de turno (status, passiva, terreno e expiração de efeitos
   temporários) e verifica se há um vencedor, repetindo até que a batalha termine.

## Documentação do projeto

- `docs/guia-dev.md` — decisões de design e como os módulos menos óbvios funcionam
  (por que certas classes são interface e outras não, o padrão de reversão de buffs,
  como os efeitos temporários expiram)
- `docs/requisitos-autorais.md` — especificação detalhada dos três requisitos autorais
- `docs/analise-estatica-requisitos.md` — relatório de inspeção dos requisitos,
  identificando ambiguidades e as interpretações adotadas pela equipe
- `docs/relatorio-contribuicao.md` — relatório individual de contribuição de cada
  membro da equipe

## Sobre o domínio

O sistema simula batalhas 1x1 entre PokeSal (Pokémon fictícios ambientados na UCSal),
cobrindo tipos elementais (Fogo, Água, Planta) com vantagens e desvantagens, efeitos
de terreno baseados no estacionamento da universidade, condições de status aplicadas
durante o combate, e um sistema de itens de batalha limitado a dois usos por
treinador por partida.