package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RunTournamentUseCaseTest {
  @Mock
  private MatchRunner matchRunner;

  @Mock
  private BattleCli battleCli;

  @InjectMocks
  private RunTournamentUseCase useCase;

  private static List<Trainer> inscrever(int quantidade) {
    List<Trainer> treinadores = new ArrayList<>();
    for (int i = 1; i <= quantidade; i++) {
      treinadores.add(new Trainer("T" + i, PokeSalCatalog.createCharSal()));
    }
    return treinadores;
  }

  private static BattleResult vitoria(Trainer vencedor, Trainer perdedor) {
    return new BattleResult(vencedor, perdedor, 3, vencedor.getPokeSal().getName(),
        perdedor.getPokeSal().getName());
  }

  @Test
  @DisplayName("Vencedores de cada confronto devem avancar ate a final")
  void testChaveamentoTorneioAvancaVencedores() {
    List<Trainer> t = inscrever(4);
    when(matchRunner.run(t.get(0), t.get(1))).thenReturn(vitoria(t.get(0), t.get(1)));
    when(matchRunner.run(t.get(2), t.get(3))).thenReturn(vitoria(t.get(3), t.get(2)));
    when(matchRunner.run(t.get(0), t.get(3))).thenReturn(vitoria(t.get(3), t.get(0)));

    Trainer campeao = useCase.execute(t);

    assertSame(t.get(3), campeao);
    InOrder ordem = inOrder(matchRunner, battleCli);
    ordem.verify(matchRunner).run(t.get(0), t.get(1));
    ordem.verify(matchRunner).run(t.get(2), t.get(3));
    ordem.verify(battleCli).announceMatch("Final", t.get(0), t.get(3));
    ordem.verify(matchRunner).run(t.get(0), t.get(3));
    ordem.verify(battleCli).announceChampion(t.get(3));
  }

  @Test
  @DisplayName("Torneio com 8 treinadores deve ter quartas de final, semifinal e final")
  void torneioComOitoTreinadoresTemTresFases() {
    List<Trainer> t = inscrever(8);
    when(matchRunner.run(any(), any())).thenAnswer(
        invocacao -> vitoria(invocacao.getArgument(0), invocacao.getArgument(1)));

    Trainer campeao = useCase.execute(t);

    assertSame(t.get(0), campeao);
    verify(matchRunner, times(7)).run(any(), any());
    verify(battleCli, times(4)).announceMatch(eq("Quartas de final"), any(), any());
    verify(battleCli, times(2)).announceMatch(eq("Semifinal"), any(), any());
    verify(battleCli, times(1)).announceMatch(eq("Final"), any(), any());
  }

  @ParameterizedTest(name = "{0} treinadores")
  @ValueSource(ints = {0, 2, 3, 5, 6, 16})
  @DisplayName("Torneio deve aceitar apenas 4 ou 8 treinadores")
  void torneioRejeitaQuantidadeInvalida(int quantidade) {
    List<Trainer> treinadores = inscrever(quantidade);

    assertThrows(IllegalArgumentException.class, () -> useCase.execute(treinadores));
    verify(matchRunner, never()).run(any(), any());
  }

  @Test
  @DisplayName("Torneio deve rejeitar lista de treinadores nula")
  void torneioRejeitaListaNula() {
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  @Test
  @DisplayName("Vencedor que nao participou do confronto deve ser rejeitado")
  void vencedorForaDoConfrontoEhRejeitado() {
    List<Trainer> t = inscrever(4);
    when(matchRunner.run(t.get(0), t.get(1))).thenReturn(vitoria(t.get(2), t.get(1)));

    assertThrows(IllegalStateException.class, () -> useCase.execute(t));
  }

  @Test
  @DisplayName("Falha ao salvar o resultado de uma batalha nao deve interromper o torneio")
  void falhaNoRepositorioNaoInterrompeTorneio() {
    ResultRepository resultRepository = mock(ResultRepository.class);
    BattleLogger battleLogger = mock(BattleLogger.class);
    Clock clock = mock(Clock.class);
    RandomGenerator randomGenerator = mock(RandomGenerator.class);
    RunBattleUseCase runBattleUseCase = new RunBattleUseCase(battleCli,
        mock(BattlegroundService.class), resultRepository, battleLogger,
        mock(TrainerNotifier.class), clock);
    RunTournamentUseCase torneio = new RunTournamentUseCase(
        new DefaultMatchRunner(runBattleUseCase, randomGenerator), battleCli);
    TurnAction nocaute = (actor, opponent, battleground) -> opponent.takeDamage(1000.0);
    when(clock.instant()).thenReturn(Instant.parse("2026-01-01T12:00:00Z"));
    when(battleCli.promptAction(any(), anyInt())).thenReturn(nocaute);
    doThrow(new IllegalStateException("banco fora do ar")).when(resultRepository).save(any());
    List<Trainer> t = inscrever(4);

    Trainer campeao = torneio.execute(t);

    assertSame(t.get(0), campeao);
    verify(resultRepository, times(3)).save(any());
    verify(battleLogger, times(3)).logError(eq("Não foi possível salvar o resultado da batalha"),
        any(IllegalStateException.class));
    verify(battleCli).announceChampion(t.get(0));
  }
}
