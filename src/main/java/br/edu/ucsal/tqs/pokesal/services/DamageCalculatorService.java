package br.edu.ucsal.tqs.pokesal.services;

import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.TypeChart;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Serviço responsável por calcular o dano de um golpe, combinando o dano base (derivado de ATK, DEF
 * e o poder do golpe), o multiplicador de vantagem de tipo, o efeito do terreno ativo e a sinergia
 * da passiva do atacante com esse terreno. Classe utilitária, não instanciável.
 */
public final class DamageCalculatorService {

  private DamageCalculatorService() {
  }

  /**
   * Calcula o dano final de um golpe de attacker contra defender, considerando o poder do golpe, a
   * vantagem de tipo, o terreno ativo na batalha e a sinergia da passiva do atacante com esse
   * terreno.
   *
   * @param attacker     o PokeSal que executa o golpe
   * @param defender     o PokeSal que recebe o golpe
   * @param move         o golpe utilizado
   * @param battleground o terreno ativo na batalha
   * @return o dano final a ser aplicado ao defensor
   * @throws NullPointerException se attacker, defender, move ou battleground forem nulos
   */
  public static double calculateDamage(PokeSal attacker, PokeSal defender, Move move,
      Battleground battleground) {
    if (attacker == null) {
      throw new NullPointerException("O atacante não pode ser nulo");
    }
    if (defender == null) {
      throw new NullPointerException("O defensor não pode ser nulo");
    }
    if (move == null) {
      throw new NullPointerException("O golpe não pode ser nulo");
    }
    if (battleground == null) {
      throw new NullPointerException("O terreno não pode ser nulo");
    }

    double baseDamage = calculateBaseDamage(attacker, defender, move);
    double typeMultiplier = TypeChart.getMultiplier(move.type(), defender.getElementType());
    double damage = baseDamage * typeMultiplier;

    damage = battleground.modifyDamage(damage, move.type());
    damage = damage * attacker.getPassive().onAttack(attacker, battleground);

    return damage;
  }

  /**
   * Calcula o dano base de um golpe, a partir do ATK do atacante, do DEF do defensor e do poder do
   * golpe.
   *
   * @param attacker o PokeSal que executa o golpe
   * @param defender o PokeSal que recebe o golpe
   * @param move     o golpe utilizado
   * @return o dano base, antes da aplicação de multiplicadores de tipo, terreno e passiva
   */
  private static double calculateBaseDamage(PokeSal attacker, PokeSal defender, Move move) {
    return ((double) attacker.getAtk() / defender.getDef()) * move.power();
  }
}