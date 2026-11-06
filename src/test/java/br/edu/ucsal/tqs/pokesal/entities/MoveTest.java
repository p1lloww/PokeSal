package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MoveTest {

  @Test
  @DisplayName("Golpe sem status deve ter precisao maxima e chance de status zero")
  void golpeSemStatusUsaValoresPadrao() {
    Move tackle = new Move("Tackle", "Ataque básico.", ElementType.WATER, 40);

    assertEquals(Move.MAX_ACCURACY, tackle.accuracy());
    assertNull(tackle.statusEffect());
    assertEquals(0.0, tackle.statusChance());
  }

  @Test
  @DisplayName("Golpe com status sem precisao informada deve ter precisao maxima")
  void golpeComStatusUsaPrecisaoMaxima() {
    Move golpe = new Move("Veneno", "Golpe de teste.", ElementType.PLANT, 40, new Poison(), 0.3);

    assertEquals(Move.MAX_ACCURACY, golpe.accuracy());
  }

  @Test
  @DisplayName("Golpe deve rejeitar nome, descricao e tipo nulos")
  void golpeRejeitaCamposNulos() {
    assertThrows(NullPointerException.class,
        () -> new Move(null, "Descrição", ElementType.FIRE, 40));
    assertThrows(NullPointerException.class,
        () -> new Move("Golpe", null, ElementType.FIRE, 40));
    assertThrows(NullPointerException.class, () -> new Move("Golpe", "Descrição", null, 40));
  }

  @ParameterizedTest(name = "poder {0}")
  @ValueSource(ints = {0, -1})
  @DisplayName("Golpe deve rejeitar poder menor ou igual a zero")
  void golpeRejeitaPoderInvalido(int poder) {
    assertThrows(IllegalArgumentException.class,
        () -> new Move("Golpe", "Descrição", ElementType.FIRE, poder));
  }

  @ParameterizedTest(name = "chance {0}")
  @ValueSource(doubles = {-0.01, 1.01})
  @DisplayName("Golpe deve rejeitar chance de status fora de 0 a 1")
  void golpeRejeitaChanceInvalida(double chance) {
    assertThrows(IllegalArgumentException.class,
        () -> new Move("Golpe", "Descrição", ElementType.FIRE, 40, new Poison(), chance));
  }

  @ParameterizedTest(name = "precisao {0}")
  @ValueSource(ints = {-1, 101})
  @DisplayName("Golpe deve rejeitar precisao fora de 0 a 100")
  void golpeRejeitaPrecisaoInvalida(int precisao) {
    assertThrows(IllegalArgumentException.class,
        () -> new Move("Golpe", "Descrição", ElementType.FIRE, 40, null, 0.0, precisao));
  }

  @ParameterizedTest(name = "precisao {0}")
  @ValueSource(ints = {0, 100})
  @DisplayName("Golpe deve aceitar os limites de precisao 0 e 100")
  void golpeAceitaLimitesDePrecisao(int precisao) {
    Move golpe = new Move("Golpe", "Descrição", ElementType.FIRE, 40, null, 0.0, precisao);

    assertEquals(precisao, golpe.accuracy());
  }
}
