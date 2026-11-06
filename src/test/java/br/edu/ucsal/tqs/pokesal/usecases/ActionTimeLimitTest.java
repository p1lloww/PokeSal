package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.ItemCatalog;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.ItemAction;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testes do RA-04: tempo limite para escolher a ação do turno.
 */
@ExtendWith(MockitoExtension.class)
public class ActionTimeLimitTest {
  private static final Instant INICIO = Instant.parse("2026-01-01T12:00:00Z");

  private static final TurnAction NADA = (actor, opponent, battleground) -> { };

  private static final TurnAction NOCAUTE =
      (actor, opponent, battleground) -> opponent.takeDamage(1000.0);

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

  @Mock
  private Clock clock;

  @InjectMocks
  private RunBattleUseCase useCase;

  private Trainer trainerA;
  private Trainer trainerB;
  private Battle battle;

  @BeforeEach
  void setUp() {
    trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    battle = new Battle(trainerA, trainerB, new HotAsphalt());
  }

  @Test
  @DisplayName("Treinador que passa do tempo limite deve perder a vez no turno")
  void treinadorQuePassaDoLimitePerdeAVez() {
    List<Integer> hpDoOponente = new ArrayList<>();
    // A primeira escolha de A leva 31s; todas as outras chamadas devolvem o mesmo instante (0s).
    when(clock.instant()).thenReturn(INICIO, INICIO.plusSeconds(31));
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn((actor, opponent, bg) -> {
      hpDoOponente.add(opponent.getHp());
      opponent.takeDamage(1000.0);
    });
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase.execute(battle);

    assertEquals(List.of(44), hpDoOponente);
    assertEquals(2, battle.getTurns().size());
    verify(battleLogger, times(1)).logActionTimeout(trainerA);
    verify(battleLogger, never()).logActionTimeout(trainerB);
  }

  @ParameterizedTest(name = "escolha em {0}s")
  @ValueSource(longs = {0, 10, 30})
  @DisplayName("Escolha dentro do tempo limite, inclusive exatamente 30s, deve ser executada")
  void escolhaDentroDoLimiteEhExecutada(long segundos) {
    when(clock.instant()).thenReturn(INICIO, INICIO.plusSeconds(segundos));
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase.execute(battle);

    assertEquals(trainerA, battle.getWinner());
    assertEquals(1, battle.getTurns().size());
    verify(battleLogger, never()).logActionTimeout(any());
  }

  @Test
  @DisplayName("Item escolhido depois do tempo limite nao deve ser consumido")
  void itemEscolhidoForaDoLimiteNaoEhConsumido() {
    Item potion = ItemCatalog.createPotion();
    trainerA.addItem(potion);
    when(clock.instant()).thenReturn(INICIO, INICIO.plusSeconds(45));
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(new ItemAction(potion, trainerA, 1, battleLogger), NOCAUTE);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);

    useCase.execute(battle);

    assertTrue(trainerA.getBackpack().contains(potion));
    verify(battleLogger).logActionTimeout(trainerA);
    verify(battleLogger, never()).logItemUse(any(), any());
  }

  @Test
  @DisplayName("Tempo limite deve ser medido separadamente para cada treinador")
  void tempoLimiteEhMedidoPorTreinador() {
    // A escolhe em 0s e B em 45s no primeiro turno; depois, todos escolhem em 0s.
    when(clock.instant()).thenReturn(INICIO, INICIO, INICIO, INICIO.plusSeconds(45));
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NADA);
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NOCAUTE);

    useCase.execute(battle);

    assertEquals(trainerB, battle.getWinner());
    assertEquals(2, battle.getTurns().size());
    verify(battleLogger, times(1)).logActionTimeout(trainerB);
    verify(battleLogger, never()).logActionTimeout(trainerA);
  }
}
