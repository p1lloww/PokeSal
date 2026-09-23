package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
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
}
