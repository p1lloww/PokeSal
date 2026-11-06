package br.edu.ucsal.tqs.pokesal.entities;

/**
 * Resultado de uma batalha encerrada, salvo no repositório de resultados.
 *
 * @param winner        o treinador vencedor
 * @param loser         o treinador perdedor
 * @param turnCount     o número de turnos disputados
 * @param winnerPokeSal o nome do PokeSal usado pelo vencedor
 * @param loserPokeSal  o nome do PokeSal usado pelo perdedor
 */
public record BattleResult(Trainer winner, Trainer loser, int turnCount, String winnerPokeSal,
    String loserPokeSal) {

  /**
   * Cria o resultado a partir de uma batalha já encerrada.
   *
   * @param battle a batalha encerrada
   * @return o resultado da batalha
   * @throws IllegalStateException se a batalha ainda não tiver um vencedor
   */
  public static BattleResult from(Battle battle) {
    if (!battle.isOver()) {
      throw new IllegalStateException("A batalha ainda não terminou");
    }

    Trainer winner = battle.getWinner();
    Trainer loser = winner == battle.getTrainerA() ? battle.getTrainerB() : battle.getTrainerA();
    return new BattleResult(winner, loser, battle.getTurns().size(),
        winner.getPokeSal().getName(), loser.getPokeSal().getName());
  }
}
