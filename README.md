# ⚔️ PokeSal Battle System

Sistema de batalhas por turnos no terminal, inspirado em Pokémon e ambientado na **UCSal**.
Desenvolvido em **Java 25** para a disciplina de **Teste e Qualidade de Software**, com foco em
arquitetura limpa, testes automatizados e análise estática de código.

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apachemaven)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5)
![Mockito](https://img.shields.io/badge/Mockito-5-6DB33F)
![JaCoCo](https://img.shields.io/badge/JaCoCo-coverage-blue)
![SonarQube](https://img.shields.io/badge/SonarQube-analisado-4E9BCD?logo=sonarqube)
![Checkstyle](https://img.shields.io/badge/Checkstyle-Google%20Style-yellow)

🎥 **Demonstração em vídeo:** [youtube.com/watch?v=NmYZp7DZt-4](https://www.youtube.com/watch?v=NmYZp7DZt-4)

---

## 🎮 Sobre o jogo

Dois treinadores escolhem um nome e um dos seis PokeSal iniciais e se enfrentam em uma
batalha 1x1. A cada turno, cada um decide entre **atacar** ou **usar um item**, até que um
dos PokeSal seja derrotado.

### PokeSal disponíveis

| PokeSal   | Tipo     | Golpe especial (10% de chance de status) | Passiva        |
|-----------|----------|------------------------------------------|----------------|
| BulbaSal  | 🌿 Planta | Vine Whip → Envenenamento                | Overgrow       |
| ChikoSal  | 🌿 Planta | Razor Leaf → Envenenamento               | Leaf Guard     |
| CharSal   | 🔥 Fogo   | Ember → Queimadura                       | Blaze          |
| CyndaSal  | 🔥 Fogo   | Ember → Queimadura                       | Intimidate     |
| SquirtSal | 💧 Água   | Water Gun → Paralisia                    | Torrent        |
| TotoSal   | 💧 Água   | Water Gun → Paralisia                    | Rain Dish      |

### Mecânicas

- **Tipos elementais** — Fogo > Planta > Água > Fogo. Golpes super efetivos causam 2x de
  dano; pouco efetivos, 0.5x.
- **Passivas** — habilidades que reagem ao estado da batalha: bônus de ATK com HP baixo
  (Overgrow, Blaze, Torrent), imunidade a status (Leaf Guard), redução do ATK do oponente
  ao entrar em campo (Intimidate) e mais.
- **Condições de status** — Queimadura, Envenenamento e Paralisia, aplicadas durante o combate.
- **Terrenos inspirados no estacionamento da UCSal**, sorteados a cada partida:
  - ☀️ *Hot Asphalt* — sol forte, golpes de Fogo +15%
  - 🌧️ *Water Puddle* — poça de chuva, golpes de Água +10%
  - 🌱 *Central Flowerbed* — canteiro central, PokeSal de Planta recuperam 5% do HP por turno
- **Itens de batalha** — Potion, Super Potion, Super Antidote, Burn Heal e Paralyze Heal,
  limitados a **dois usos por treinador** por partida.

---

## 🧱 Arquitetura

O código é organizado em camadas, separando regras de domínio, casos de uso e entrada/saída:

```
br.edu.ucsal.tqs.pokesal
├── entities          — domínio: PokeSal, Trainer, Battle, Turn, Move, Item, TypeChart...
│   ├── passives          — interface Passive e implementações por tipo
│   ├── statusconditions  — Burn, Poison, Paralyzed
│   ├── effects           — efeitos de itens (cura, remoção de status, buffs)
│   ├── battlegrounds     — terrenos de batalha
│   ├── turnactions       — MoveAction e ItemAction
│   └── catalogs          — catálogos de PokeSal, itens e terrenos
├── services          — DamageCalculatorService (cálculo de dano)
├── usecases          — ChooseInitialPokeSal, ChooseItems, RunBattle
└── io                — BattleCli (interface de terminal)
```

Passivas, condições de status, efeitos, terrenos e ações de turno são modelados como
**interfaces com múltiplas implementações** (padrão Strategy), o que permite adicionar novas
mecânicas sem alterar o fluxo da batalha.

### Fluxo de uma batalha

1. `BattleRunner` monta as dependências e inicia a aplicação.
2. `ChooseInitialPokeSalUseCase` captura o nome e o PokeSal de cada treinador.
3. Um `Battleground` é sorteado.
4. `RunBattleUseCase` aplica as passivas de entrada em campo e conduz o loop de turnos:
   captura as ações, executa o `Turn`, aplica efeitos de fim de turno (status, passivas,
   terreno e expiração de efeitos temporários) e verifica se há vencedor.

---

## ✅ Qualidade e testes

- **192 casos de teste** com JUnit 5 (incluindo testes parametrizados) e Mockito
- **Cobertura medida com JaCoCo**
- **Análise estática com SonarQube** e **Checkstyle** (Google Java Style)
- Documentação de engenharia de software em [`docs/`](docs/):
  - Casos de uso e diagrama de classes
  - Requisitos autorais e análise estática dos requisitos
  - Checklist estático e registro de bugs
  - Documento de testes e relatório do SonarQube
  - Atas de reunião e relatórios de contribuição individual

---

## 🚀 Como rodar

**Pré-requisitos:** JDK 25+ e Maven 3.9+

```bash
java -version
mvn -version
```

**Iniciar a batalha:**

```bash
mvn compile exec:java -Dexec.mainClass=br.edu.ucsal.tqs.pokesal.BattleRunner
```

**Rodar os testes:**

```bash
mvn test
```

**Gerar o relatório de cobertura (JaCoCo):**

```bash
mvn verify
# relatório em target/site/jacoco/index.html
```

**Validar o estilo de código (Checkstyle):**

```bash
mvn checkstyle:check
# ou, para um relatório HTML em target/site/checkstyle.html:
mvn checkstyle:checkstyle
```
