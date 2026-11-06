package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testes do RA-05: alerta de HP crítico ao treinador. O SquirtSal do treinador B tem 44 de HP
 * máximo, então o HP é crítico a partir de 10 (abaixo de 11, que é exatamente 25%).
 */
@ExtendWith(MockitoExtension.class)
public class CriticalHpAlertTest {
  private static final TurnAction NADA = (actor, opponent, battleground) -> { };

  private static final TurnAction NOCAUTE =
      (actor, opponent, battleground) -> opponent.takeDamage(1000.0);

  private static final TurnAction CURA_OPONENTE =
      (actor, opponent, battleground) -> opponent.heal(1000.0);

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
  private PokeSal squirtSal;
  private Battle battle;

  @BeforeEach
  void setUp() {
    trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    squirtSal = trainerB.getPokeSal();
    battle = new Battle(trainerA, trainerB, new HotAsphalt());
    when(clock.instant()).thenReturn(Instant.parse("2026-01-01T12:00:00Z"));
    when(battleCli.promptAction(eq(trainerB), anyInt())).thenReturn(NADA);
  }

  private static TurnAction deixaOponenteCom(int hp) {
    return (actor, opponent, battleground) -> opponent.takeDamage(opponent.getHp() - hp);
  }

  @Test
  @DisplayName("Alerta deve ser enviado ao treinador quando o HP fica abaixo de 25%")
  void alertaEnviadoQuandoHpFicaCritico() {
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(deixaOponenteCom(10), NOCAUTE);
    ArgumentCaptor<Trainer> treinador = ArgumentCaptor.forClass(Trainer.class);
    ArgumentCaptor<PokeSal> pokeSal = ArgumentCaptor.forClass(PokeSal.class);

    useCase.execute(battle);

    verify(trainerNotifier, times(1)).notifyCriticalHp(treinador.capture(), pokeSal.capture());
    assertSame(trainerB, treinador.getValue());
    assertSame(squirtSal, pokeSal.getValue());
  }

  @Test
  @DisplayName("Alerta nao deve se repetir enquanto o HP continua baixo")
  void alertaNaoSeRepete() {
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(deixaOponenteCom(10), deixaOponenteCom(5), NOCAUTE);

    useCase.execute(battle);

    verify(trainerNotifier, times(1)).notifyCriticalHp(trainerB, squirtSal);
  }

  @Test
  @DisplayName("Alerta nao deve ser enviado de novo depois que o PokeSal e curado e volta a cair")
  void alertaNaoSeRepeteAposCura() {
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(deixaOponenteCom(10), CURA_OPONENTE, deixaOponenteCom(5), NOCAUTE);

    useCase.execute(battle);

    assertEquals(4, battle.getTurns().size());
    verify(trainerNotifier, times(1)).notifyCriticalHp(trainerB, squirtSal);
  }

  @Test
  @DisplayName("PokeSal nocauteado direto nao deve gerar alerta")
  void nocauteDiretoNaoGeraAlerta() {
    when(battleCli.promptAction(eq(trainerA), anyInt())).thenReturn(NOCAUTE);

    useCase.execute(battle);

    verify(trainerNotifier, never()).notifyCriticalHp(any(), any());
  }

  @Test
  @DisplayName("HP em exatamente 25% do maximo ainda nao e critico")
  void exatamente25PorCentoNaoEhCritico() {
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(deixaOponenteCom(11), NOCAUTE);

    useCase.execute(battle);

    verify(trainerNotifier, never()).notifyCriticalHp(any(), any());
  }

  @Test
  @DisplayName("Dano do veneno no fim do turno deve disparar o alerta depois do registro do dano")
  void venenoNoFimDoTurnoDisparaAlerta() {
    Poison veneno = new Poison();
    squirtSal.applyStatus(veneno);
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(deixaOponenteCom(14), NOCAUTE);

    useCase.execute(battle);

    InOrder ordem = inOrder(battleLogger, trainerNotifier);
    ordem.verify(battleLogger).logStatusDamage(squirtSal, veneno, 6);
    ordem.verify(trainerNotifier).notifyCriticalHp(trainerB, squirtSal);
  }

  @Test
  @DisplayName("Falha ao enviar o alerta deve ser registrada sem interromper a batalha")
  void falhaNoNotificadorNaoInterrompeBatalha() {
    IllegalStateException falha = new IllegalStateException("canal indisponível");
    doThrow(falha).when(trainerNotifier).notifyCriticalHp(any(), any());
    when(battleCli.promptAction(eq(trainerA), anyInt()))
        .thenReturn(deixaOponenteCom(10), NOCAUTE);

    useCase.execute(battle);

    assertEquals(trainerA, battle.getWinner());
    verify(battleLogger).logError("Não foi possível enviar o alerta de HP crítico", falha);
    verify(resultRepository).save(any());
  }
}
