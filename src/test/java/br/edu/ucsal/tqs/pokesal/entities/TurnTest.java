package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.ItemCatalog;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.ItemAction;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.MoveAction;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TurnTest {
  @Mock
  private PokeSal pokeSal1;

  @Mock
  private PokeSal pokeSal2;

  @Mock
  private TurnAction action1;

  @Mock
  private TurnAction action2;

  @Mock
  private Battleground battleground;

  @Mock
  private RandomGenerator randomGenerator;

  @Mock
  private BattlegroundService battlegroundService;

  @Mock
  private BattleLogger battleLogger;

  @Test
  @DisplayName("Deve executar a ação do pokeSal1 primeiro quando seu speed for maior")
  void deveExecutarAAcaoDoPokeSal1QuandoMaisRapido() {
    when(pokeSal1.getSpd()).thenReturn(100);
    when(pokeSal2.getSpd()).thenReturn(50);

    when(pokeSal2.getHp()).thenReturn(100);

    Turn turn = new Turn(1);

    turn.execute(pokeSal1, pokeSal2, action1, action2, battleground);

    InOrder inOrder = Mockito.inOrder(action1, action2);
    inOrder.verify(action1).execute(pokeSal1, pokeSal2, battleground);
    inOrder.verify(action2).execute(pokeSal2, pokeSal1, battleground);
  }

  @Test
  @DisplayName("Deve executar a ação do pokeSal2 primeiro quando seu speed for maior")
  void deveExecutarAAcaoDoPokeSal2QuandoMaisRapido() {
    when(pokeSal1.getSpd()).thenReturn(50);
    when(pokeSal2.getSpd()).thenReturn(100);

    when(pokeSal1.getHp()).thenReturn(100);

    Turn turn = new Turn(1);

    turn.execute(pokeSal1, pokeSal2, action1, action2, battleground);

    InOrder inOrder = Mockito.inOrder(action1, action2);
    inOrder.verify(action2).execute(pokeSal2, pokeSal1, battleground);
    inOrder.verify(action1).execute(pokeSal1, pokeSal2, battleground);
  }

  @Test
  @DisplayName("Deve executar a ação do pokeSal1 primeiro quando as velocidades forem iguais")
  void deveExecutarAAcaoDoPokeSal1QuandoVelocidadesForemIguais() {
    when(pokeSal1.getSpd()).thenReturn(50);
    when(pokeSal2.getSpd()).thenReturn(50);

    when(pokeSal2.getHp()).thenReturn(100);

    Turn turn = new Turn(1);

    turn.execute(pokeSal1, pokeSal2, action1, action2, battleground);

    InOrder inOrder = Mockito.inOrder(action1, action2);
    inOrder.verify(action1).execute(pokeSal1, pokeSal2, battleground);
    inOrder.verify(action2).execute(pokeSal2, pokeSal1, battleground);
  }

  @Test
  @DisplayName("Não deve executar a ação do segundo quando ele desmaiar na primeira ação")
  void naoDeveExecutarAAcaoDoSegundoQuandoEleDesmaiar() {
    when(pokeSal1.getSpd()).thenReturn(100);
    when(pokeSal2.getSpd()).thenReturn(50);

    when(pokeSal2.getHp()).thenReturn(0);

    Turn turn = new Turn(1);

    turn.execute(pokeSal1, pokeSal2, action1, action2, battleground);

    verify(action1).execute(pokeSal1, pokeSal2, battleground);
    verify(action2, never()).execute(any(), any(), any());
  }

  @Test
  @DisplayName("Deve guardar quem agiu primeiro e quem agiu depois")
  void deveGuardarAOrdemDosAtoresEAcoes() {
    when(pokeSal1.getSpd()).thenReturn(50);
    when(pokeSal2.getSpd()).thenReturn(100);

    when(pokeSal1.getHp()).thenReturn(100);

    Turn turn = new Turn(3);

    turn.execute(pokeSal1, pokeSal2, action1, action2, battleground);

    assertEquals(3, turn.getTurnNumber());
    assertSame(pokeSal2, turn.getFirstActor());
    assertSame(action2, turn.getFirstAction());
    assertSame(pokeSal1, turn.getSecondActor());
    assertSame(action1, turn.getSecondAction());
  }

  @Test
  @DisplayName("Deve lançar exceção quando algum PokeSal for nulo")
  void deveLancarExcecaoQuandoPokeSalForNulo() {
    Turn turn = new Turn(1);

    assertThrows(NullPointerException.class,
        () -> turn.execute(null, pokeSal2, action1, action2, battleground));
    assertThrows(NullPointerException.class,
        () -> turn.execute(pokeSal1, null, action1, action2, battleground));
  }

  @Test
  @DisplayName("Deve lançar exceção quando alguma ação for nula")
  void deveLancarExcecaoQuandoAcaoForNula() {
    Turn turn = new Turn(1);

    assertThrows(NullPointerException.class,
        () -> turn.execute(pokeSal1, pokeSal2, null, action2, battleground));
    assertThrows(NullPointerException.class,
        () -> turn.execute(pokeSal1, pokeSal2, action1, null, battleground));
  }

  @Test
  @DisplayName("Eventos do turno devem chegar ao registro na ordem ataque, dano, status, terreno e "
      + "item")
  void testSequenciaDeEventosNoTurno() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    Trainer treinadorB = new Trainer("B", squirtSal);
    Item potion = ItemCatalog.createPotion();
    treinadorB.addItem(potion);
    Move ember = charSal.getMoves().get(1);
    HotAsphalt asfalto = new HotAsphalt();
    when(randomGenerator.nextInt(anyInt())).thenReturn(0);
    // 0.99 evita o critico e 0.05 fica abaixo da chance de queimadura (10%).
    when(randomGenerator.nextDouble()).thenReturn(0.99, 0.05);
    when(battlegroundService.isActive(asfalto)).thenReturn(true);
    DamageCalculatorService calculadora =
        new DamageCalculatorService(randomGenerator, battlegroundService);
    TurnAction ataque = new MoveAction(ember, calculadora, randomGenerator, battleLogger);
    TurnAction item = new ItemAction(potion, treinadorB, 1, battleLogger);

    new Turn(1).execute(charSal, squirtSal, ataque, item, asfalto);

    InOrder ordem = Mockito.inOrder(battleLogger);
    ordem.verify(battleLogger).logAttack(charSal, squirtSal, ember);
    ordem.verify(battleLogger).logDamage(eq(squirtSal), anyDouble(), eq(false));
    ordem.verify(battleLogger).logStatusEffect(eq(squirtSal), any(Burn.class));
    ordem.verify(battleLogger).logBattlegroundEffect(asfalto, squirtSal);
    ordem.verify(battleLogger).logItemUse(squirtSal, potion);
    verifyNoMoreInteractions(battleLogger);
  }
}
