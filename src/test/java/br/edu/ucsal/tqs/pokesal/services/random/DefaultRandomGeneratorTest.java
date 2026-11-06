package br.edu.ucsal.tqs.pokesal.services.random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DefaultRandomGeneratorTest {
  private static final long SEMENTE = 42L;

  @Test
  @DisplayName("Gerador deve repassar os sorteios do Random recebido")
  void repassaOsSorteiosDoRandom() {
    Random referencia = new Random(SEMENTE);
    DefaultRandomGenerator gerador = new DefaultRandomGenerator(new Random(SEMENTE));

    assertEquals(referencia.nextInt(100), gerador.nextInt(100));
    assertEquals(referencia.nextDouble(), gerador.nextDouble());
  }

  @Test
  @DisplayName("Construtor deve rejeitar Random nulo")
  void construtorRejeitaRandomNulo() {
    assertThrows(NullPointerException.class, () -> new DefaultRandomGenerator(null));
  }
}
