package br.edu.ucsal.tqs.pokesal.services.random;

import java.util.Random;

/**
 * Implementação de RandomGenerator usada no jogo real, delegando o sorteio para uma instância de
 * java.util.Random.
 */
public class DefaultRandomGenerator implements RandomGenerator {

  private final Random random;

  /**
   * Cria um novo gerador a partir da instância de Random informada.
   *
   * @param random a instância de Random usada nos sorteios
   * @throws NullPointerException se random for nulo
   */
  public DefaultRandomGenerator(Random random) {
    if (random == null) {
      throw new NullPointerException("O Random não pode ser nulo");
    }

    this.random = random;
  }

  @Override
  public int nextInt(int bound) {
    return random.nextInt(bound);
  }

  @Override
  public double nextDouble() {
    return random.nextDouble();
  }
}
