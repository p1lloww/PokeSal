package br.edu.ucsal.tqs.pokesal.entities;

/**
 * Representa o par formado por um tipo elemental atacante e um tipo elemental defensor, usado como
 * chave para consultar o multiplicador de dano correspondente em TypeChart.
 *
 * @param attacker o tipo elemental do golpe que ataca
 * @param defender o tipo elemental do PokeSal que defende
 */
public record TypeMatchup(ElementType attacker, ElementType defender) {

  /**
   * Valida os componentes deste TypeMatchup no momento da criação.
   *
   * @throws NullPointerException se attacker ou defender forem nulos
   */
  public TypeMatchup {
    if (attacker == null) {
      throw new NullPointerException("O tipo atacante não pode ser nulo");
    }
    if (defender == null) {
      throw new NullPointerException("O tipo defensor não pode ser nulo");
    }
  }
}