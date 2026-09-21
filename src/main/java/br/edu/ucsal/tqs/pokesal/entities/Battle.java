package br.edu.ucsal.tqs.pokesal.entities;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma batalha entre dois treinadores, guardando os participantes, o
 * terreno ativo, o histórico de turnos disputados e o vencedor, uma vez definido.
 * Não conduz a batalha por si mesma — apenas mantém seu estado e verifica, a partir
 * dele, se a batalha já foi decidida.
 */
public class Battle {

  private final Trainer trainerA;
  private final Trainer trainerB;
  private final Battleground battleground;
  private final List<Turn> turns = new ArrayList<>();
  private Trainer winner;

  /**
   * Cria uma nova batalha entre os dois treinadores informados, no terreno
   * especificado.
   *
   * @param trainerA o primeiro treinador
   * @param trainerB o segundo treinador
   * @param battleground o terreno ativo nesta batalha
   * @throws NullPointerException se trainerA, trainerB ou battleground forem nulos
   */
  public Battle(Trainer trainerA, Trainer trainerB, Battleground battleground) {
    if (trainerA == null) {
      throw new NullPointerException("O treinador A não pode ser nulo");
    }
    if (trainerB == null) {
      throw new NullPointerException("O treinador B não pode ser nulo");
    }
    if (battleground == null) {
      throw new NullPointerException("O terreno não pode ser nulo");
    }

    this.trainerA = trainerA;
    this.trainerB = trainerB;
    this.battleground = battleground;
  }

  /**
   * Adiciona um turno já executado ao histórico desta batalha.
   *
   * @param turn o turno a ser adicionado
   * @throws NullPointerException se turn for nulo
   */
  public void addTurn(Turn turn) {
    if (turn == null) {
      throw new NullPointerException("O turno não pode ser nulo");
    }

    turns.add(turn);
  }

  /**
   * Verifica, a partir do HP atual dos PokeSal dos dois treinadores, se a batalha
   * já tem um vencedor, definindo-o caso um dos PokeSal tenha sido derrotado. Não
   * tem efeito caso nenhum dos dois tenha sido derrotado, ou caso um vencedor já
   * tenha sido definido anteriormente.
   */
  public void checkForWinner() {
    if (winner != null) {
      return;
    }

    if (trainerA.getPokeSal().getHp() <= 0) {
      this.winner = trainerB;
    } else if (trainerB.getPokeSal().getHp() <= 0) {
      this.winner = trainerA;
    }
  }

  /**
   * Indica se esta batalha já foi decidida.
   *
   * @return true se um vencedor já foi definido; false caso contrário
   */
  public boolean isOver() {
    return winner != null;
  }

  public Trainer getTrainerA() {
    return trainerA;
  }

  public Trainer getTrainerB() {
    return trainerB;
  }

  public Battleground getBattleground() {
    return battleground;
  }

  public List<Turn> getTurns() {
    return new ArrayList<>(turns);
  }

  public Trainer getWinner() {
    return winner;
  }
}