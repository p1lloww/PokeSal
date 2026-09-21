package br.edu.ucsal.tqs.pokesal.entities;

import br.edu.ucsal.tqs.pokesal.entities.effects.Effect;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um item de batalha que um treinador pode usar em seu PokeSal, aplicando
 * um ou mais efeitos. É um dado imutável: os efeitos que compõem o item são fixos
 * desde a criação.
 */
public class Item {

  private final String name;
  private final String description;
  private final List<Effect> effects;

  /**
   * Cria um novo item com o nome, descrição e efeitos especificados.
   *
   * @param name o nome do item
   * @param description a descrição do item
   * @param effects a lista de efeitos que este item aplica ao ser usado
   * @throws NullPointerException se name, description ou effects forem nulos
   */
  public Item(String name, String description, List<Effect> effects) {
    if (name == null) {
      throw new NullPointerException("O nome não pode ser nulo");
    }
    if (description == null) {
      throw new NullPointerException("A descrição não pode ser nula");
    }
    if (effects == null) {
      throw new NullPointerException("A lista de efeitos não pode ser nula");
    }

    this.name = name;
    this.description = description;
    this.effects = new ArrayList<>(effects);
  }

  /**
   * Usa este item no PokeSal informado, aplicando cada um de seus efeitos e
   * agendando a reversão dos efeitos temporários para o turno correspondente.
   *
   * @param pokeSal o PokeSal que recebe os efeitos deste item
   * @param currentTurn o turno em que o item está sendo usado
   * @throws NullPointerException se pokeSal ou currentTurn forem nulos
   */
  public void useItem(PokeSal pokeSal, Turn currentTurn) {
    if (pokeSal == null) {
      throw new NullPointerException("O PokeSal não pode ser nulo");
    }
    if (currentTurn == null) {
      throw new NullPointerException("O turno não pode ser nulo");
    }

    for (Effect effect : effects) {
      pokeSal.receiveEffect(effect, currentTurn.getTurnNumber());
    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<Effect> getEffects() {
    return new ArrayList<>(effects);
  }
}