package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultMatchRunnerTest {
  @Mock
  private RunBattleUseCase runBattleUseCase;

  @Mock
  private RandomGenerator randomGenerator;

  @InjectMocks
  private DefaultMatchRunner matchRunner;

  private Trainer trainerA;
  private Trainer trainerB;

  @BeforeEach
  void setUp() {
    trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
  }

  private void simularVitoriaDe(Trainer vencedor, List<Integer> hpNoInicio) {
    doAnswer(invocacao -> {
      Battle battle = invocacao.getArgument(0);
      hpNoInicio.add(battle.getTrainerA().getPokeSal().getHp());
      hpNoInicio.add(battle.getTrainerB().getPokeSal().getHp());
      Trainer perdedor = vencedor == trainerA ? trainerB : trainerA;
      perdedor.getPokeSal().takeDamage(1000.0);
      battle.checkForWinner();
      return null;
    }).when(runBattleUseCase).execute(any(Battle.class));
  }

  @Test
  @DisplayName("Construtor deve rejeitar colaboradores nulos")
  void construtorRejeitaNulos() {
    assertThrows(NullPointerException.class, () -> new DefaultMatchRunner(null, randomGenerator));
    assertThrows(NullPointerException.class,
        () -> new DefaultMatchRunner(runBattleUseCase, null));
  }

  @Test
  @DisplayName("Batalha deve usar os dois treinadores e o terreno sorteado pelo gerador")
  void batalhaUsaTreinadoresETerrenoSorteado() {
    when(randomGenerator.nextInt(3)).thenReturn(2);
    simularVitoriaDe(trainerA, new ArrayList<>());
    ArgumentCaptor<Battle> batalha = ArgumentCaptor.forClass(Battle.class);

    matchRunner.run(trainerA, trainerB);

    verify(runBattleUseCase).execute(batalha.capture());
    assertSame(trainerA, batalha.getValue().getTrainerA());
    assertSame(trainerB, batalha.getValue().getTrainerB());
    assertInstanceOf(CentralFlowerbed.class, batalha.getValue().getBattleground());
  }

  @Test
  @DisplayName("PokeSal dos dois treinadores devem comecar a batalha restaurados")
  void pokeSalComecamRestaurados() {
    trainerA.getPokeSal().takeDamage(20.0);
    trainerB.getPokeSal().applyStatus(new Poison());
    trainerB.getPokeSal().takeDamage(30.0);
    List<Integer> hpNoInicio = new ArrayList<>();
    when(randomGenerator.nextInt(3)).thenReturn(0);
    simularVitoriaDe(trainerB, hpNoInicio);

    matchRunner.run(trainerA, trainerB);

    assertEquals(List.of(39, 44), hpNoInicio);
  }

  @Test
  @DisplayName("Resultado deve trazer vencedor, perdedor e PokeSal da batalha disputada")
  void resultadoTrazVencedorEPerdedor() {
    when(randomGenerator.nextInt(3)).thenReturn(0);
    simularVitoriaDe(trainerB, new ArrayList<>());

    BattleResult resultado = matchRunner.run(trainerA, trainerB);

    assertSame(trainerB, resultado.winner());
    assertSame(trainerA, resultado.loser());
    assertEquals("SquirtSal", resultado.winnerPokeSal());
    assertEquals("CharSal", resultado.loserPokeSal());
  }
}
