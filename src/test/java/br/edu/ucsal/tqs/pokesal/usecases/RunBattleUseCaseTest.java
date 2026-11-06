package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.effects.SpeedBuffEffect;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Paralyzed;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RunBattleUseCaseTest {
  @Mock
  private BattleCli battleCli;

  @Mock
  private BattlegroundService battlegroundService;

  @Mock
  private ResultRepository resultRepository;

  @Mock
  private BattleLogger battleLogger;

  @Mock
  private TrainerNotifier trainerNotifier;

  private final Clock relogioParado =
      Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneOffset.UTC);

  private final List<Integer> valores = new ArrayList<>();

  private static final TurnAction NADA = (actor, opponent, battleground) -> { };

  private static final TurnAction NOCAUTE =
      (actor, opponent, battleground) -> opponent.takeDamage(1000.0);

  private RunBattleUseCase useCase() {
    return new RunBattleUseCase(battleCli, battlegroundService, resultRepository, battleLogger,
        trainerNotifier, relogioParado);
  }

  private Battle criarBatalha(Trainer trainerA, Trainer trainerB, Battleground terreno) {
    return new Battle(trainerA, trainerB, terreno);
  }

  @Test
  @DisplayName("Intimidate deve reduzir o ATK do oponente antes do primeiro turno")
  void intimidateAgeAoEntrarEmCampo() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCyndaSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn((actor, opponent, bg) -> {
      valores.add(opponent.getAtk());
      opponent.takeDamage(1000.0);
    });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    assertEquals(List.of(32), valores);
  }

  @Test
  @DisplayName("Rain Dish deve aumentar a DEF antes do primeiro turno")
  void rainDishAgeAoEntrarEmCampo() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createTotoSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createCharSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn((actor, opponent, bg) -> {
      valores.add(actor.getDef());
      opponent.takeDamage(1000.0);
    });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    assertEquals(List.of(76), valores);
  }

  @Test
  @DisplayName("Veneno deve causar dano no fim de cada turno")
  void venenoCausaDanoNoFimDoTurno() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    trainerB.getPokeSal().applyStatus(new Poison());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA,
        (actor, opponent, bg) -> {
          valores.add(opponent.getHp());
          opponent.takeDamage(1000.0);
        });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    assertEquals(List.of(38), valores);
  }

  @Test
  @DisplayName("Batalha deve acabar quando o veneno derruba o PokeSal no fim do turno")
  void venenoPodeDecidirABatalha() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    trainerB.getPokeSal().takeDamage(43.0);
    trainerB.getPokeSal().applyStatus(new Poison());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
    Battle battle = criarBatalha(trainerA, trainerB, new HotAsphalt());

    useCase().execute(battle);

    assertEquals(trainerA, battle.getWinner());
    verify(battleCli, times(1)).promptAction(eq(trainerA), anyInt());
    verify(battleCli).announceWinner(trainerA);
  }

  @Test
  @DisplayName("Central Flowerbed deve curar o PokeSal de planta no fim do turno")
  void centralFlowerbedCuraNoFimDoTurno() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createBulbaSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createCharSal());
    trainerA.getPokeSal().takeDamage(20.0);
    when(battlegroundService.isActive(any())).thenReturn(true);
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA,
        (actor, opponent, bg) -> {
          valores.add(actor.getHp());
          opponent.takeDamage(1000.0);
        });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new CentralFlowerbed()));

    assertEquals(List.of(27), valores);
  }

  @Test
  @DisplayName("Central Flowerbed nao deve curar um PokeSal derrubado pelo veneno")
  void centralFlowerbedNaoCuraPokeSalDerrubado() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createBulbaSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createCharSal());
    trainerA.getPokeSal().takeDamage(44.0);
    when(battlegroundService.isActive(any())).thenReturn(true);
    trainerA.getPokeSal().applyStatus(new Poison());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
    Battle battle = criarBatalha(trainerA, trainerB, new CentralFlowerbed());

    useCase().execute(battle);

    assertEquals(0, trainerA.getPokeSal().getHp());
    assertEquals(trainerB, battle.getWinner());
  }

  @Test
  @DisplayName("Blaze deve ativar quando o veneno deixa o HP abaixo de 1/3")
  void blazeAtivaComDanoDoVeneno() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    PokeSal charSal = trainerA.getPokeSal();
    charSal.takeDamage(22.0);
    charSal.applyStatus(new Poison());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA,
        (actor, opponent, bg) -> {
          valores.add(actor.getAtk());
          opponent.takeDamage(1000.0);
        });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    assertEquals(List.of(78), valores);
  }

  @Test
  @DisplayName("Speed buff deve durar ate o fim do turno seguinte ao uso")
  void speedBuffExpiraNoFimDoTurnoSeguinte() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createSquirtSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createCharSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(
        (actor, opponent, bg) -> actor.receiveEffect(new SpeedBuffEffect(), 1),
        (actor, opponent, bg) -> valores.add(actor.getSpd()),
        (actor, opponent, bg) -> {
          valores.add(actor.getSpd());
          opponent.takeDamage(1000.0);
        });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    assertEquals(List.of(55, 43), valores);
  }

  @Test
  void deveTerminarNoPrimeiroTurnoQuandoAlguemEhNocauteado() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
    Battle battle = criarBatalha(trainerA, trainerB, new HotAsphalt());

    useCase().execute(battle);

    assertEquals(1, battle.getTurns().size());
    assertEquals(trainerA, battle.getWinner());
  }

  @Test
  @DisplayName("Inicio da batalha deve ser registrado com os dois treinadores")
  void inicioDaBatalhaEhRegistrado() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    verify(battleLogger).logBattleStart(trainerA, trainerB);
  }

  @Test
  @DisplayName("Dano do veneno deve ser registrado com o HP perdido no fim do turno")
  void danoDoVenenoEhRegistrado() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    PokeSal squirtSal = trainerB.getPokeSal();
    Poison veneno = new Poison();
    squirtSal.applyStatus(veneno);
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA, NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    verify(battleLogger, times(1)).logStatusDamage(squirtSal, veneno, squirtSal.getMaxHp() - 38);
    verify(battleLogger, never()).logStatusEffect(any(), any());
  }

  @Test
  @DisplayName("Status que nao causa dano nao deve ser registrado no fim do turno")
  void statusSemDanoNaoEhRegistradoNoFimDoTurno() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    trainerB.getPokeSal().applyStatus(new Paralyzed());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA, NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase().execute(criarBatalha(trainerA, trainerB, new HotAsphalt()));

    verify(battleLogger, never()).logStatusDamage(any(), any(), anyInt());
    verify(battleLogger, never()).logStatusEffect(any(), any());
  }

  @Test
  @DisplayName("Resultado salvo ao fim da batalha deve trazer vencedor, perdedor e turnos")
  void testResultadoSalvoAoFimDaBatalha() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA, NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
    Battle battle = criarBatalha(trainerA, trainerB, new HotAsphalt());
    ArgumentCaptor<BattleResult> resultado = ArgumentCaptor.forClass(BattleResult.class);

    useCase().execute(battle);

    verify(resultRepository, times(1)).save(resultado.capture());
    assertSame(trainerA, resultado.getValue().winner());
    assertSame(trainerB, resultado.getValue().loser());
    assertEquals(2, resultado.getValue().turnCount());
    assertEquals("CharSal", resultado.getValue().winnerPokeSal());
    assertEquals("SquirtSal", resultado.getValue().loserPokeSal());
    verify(battleCli, atLeastOnce()).renderBattleState(battle);
  }

  @Test
  @DisplayName("Falha no repositorio deve ser registrada sem invalidar o vencedor da batalha")
  void testFalhaNoRepositorioNaoInvalidaBatalha() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
    IllegalStateException falha = new IllegalStateException("banco indisponível");
    doThrow(falha).when(resultRepository).save(any(BattleResult.class));
    Battle battle = criarBatalha(trainerA, trainerB, new HotAsphalt());

    useCase().execute(battle);

    assertTrue(battle.isOver());
    assertSame(trainerA, battle.getWinner());
    verify(battleLogger).logError("Não foi possível salvar o resultado da batalha", falha);
    verify(battleCli).announceWinner(trainerA);
  }

  @Test
  @DisplayName("Repositorio nao deve ser chamado se a batalha for interrompida antes do fim")
  void testRepositorioNaoChamadoSeBatalhaNaoTerminou() {
    Trainer trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    Trainer trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    // No segundo turno a entrada do jogador acaba e a CLI lança exceção, interrompendo a batalha.
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA)
        .thenThrow(new NoSuchElementException("entrada encerrada"));
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
    Battle battle = criarBatalha(trainerA, trainerB, new HotAsphalt());

    assertThrows(NoSuchElementException.class, () -> useCase().execute(battle));

    assertFalse(battle.isOver());
    verify(resultRepository, never()).save(any());
    verify(battleCli, never()).announceWinner(any());
  }
}
