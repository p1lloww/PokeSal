package br.edu.ucsal.tqs.pokesal.services.random;

/**
 * Fonte de números aleatórios usada pelas regras de batalha, como precisão, acerto crítico, chance
 * de status e sorteio de terreno. Existe para que nenhuma classe de regra de negócio dependa
 * diretamente de java.util.Random, permitindo que os testes controlem os valores sorteados.
 */
public interface RandomGenerator {

  /**
   * Sorteia um número inteiro entre 0 (inclusivo) e bound (exclusivo).
   *
   * @param bound o limite superior, exclusivo; precisa ser maior que zero
   * @return um inteiro no intervalo [0, bound)
   */
  int nextInt(int bound);

  /**
   * Sorteia um número decimal entre 0.0 (inclusivo) e 1.0 (exclusivo).
   *
   * @return um double no intervalo [0.0, 1.0)
   */
  double nextDouble();
}
