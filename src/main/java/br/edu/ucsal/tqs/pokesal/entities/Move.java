package br.edu.ucsal.tqs.pokesal.entities;

/**
 * Representa um golpe que um PokeSal pode executar em batalha, com tipo elemental, poder base e,
 * opcionalmente, uma condição de status que pode ser aplicada ao defensor com uma determinada
 * chance. É um dado imutável: instâncias diferentes de Move representam golpes diferentes apenas
 * por seus valores, sem comportamento próprio distinto entre elas.
 *
 * @param name         o nome do golpe
 * @param description  a descrição do golpe
 * @param type         o tipo elemental do golpe
 * @param power        o poder base do golpe, usado no cálculo de dano
 * @param statusEffect a condição de status que este golpe pode causar; nula caso o golpe não cause
 *                     nenhum status
 * @param statusChance a chance, entre 0.0 e 1.0, de o golpe causar statusEffect ao acertar o
 *                     defensor; ignorada caso statusEffect seja nulo
 */
public record Move(
    String name,
    String description,
    ElementType type,
    int power,
    StatusCondition statusEffect,
    double statusChance) {

  /**
   * Valida os componentes deste Move no momento da criação.
   *
   * @throws NullPointerException     se name, description ou type forem nulos
   * @throws IllegalArgumentException se power não for maior que zero, ou se statusChance estiver
   *                                  fora do intervalo de 0.0 a 1.0
   */
  public Move {
    if (name == null) {
      throw new NullPointerException("O nome não pode ser nulo");
    }
    if (description == null) {
      throw new NullPointerException("A descrição não pode ser nula");
    }
    if (type == null) {
      throw new NullPointerException("O tipo não pode ser nulo");
    }
    if (power <= 0) {
      throw new IllegalArgumentException("O poder precisa ser maior que 0");
    }
    if (statusChance < 0.0 || statusChance > 1.0) {
      throw new IllegalArgumentException("statusChance deve estar entre 0.0 e 1.0");
    }
  }
}