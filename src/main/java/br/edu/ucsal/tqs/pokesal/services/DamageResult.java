package br.edu.ucsal.tqs.pokesal.services;

/**
 * Resultado do cálculo de dano de um golpe.
 *
 * @param damage              o dano final; zero caso o golpe tenha errado
 * @param hit                 true se o golpe acertou o alvo
 * @param critical            true se o golpe foi um acerto crítico
 * @param battlegroundApplied true se o efeito do terreno estava ativo e alterou o dano
 */
public record DamageResult(double damage, boolean hit, boolean critical,
    boolean battlegroundApplied) {

  /**
   * Cria o resultado de um golpe que errou o alvo.
   *
   * @return um resultado com dano zero, sem crítico e sem efeito de terreno
   */
  public static DamageResult miss() {
    return new DamageResult(0.0, false, false, false);
  }
}
