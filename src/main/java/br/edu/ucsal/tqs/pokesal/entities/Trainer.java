package br.edu.ucsal.tqs.pokesal.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um treinador em batalha, com seu nome, o PokeSal que utiliza e a mochila de itens
 * disponíveis. Cada treinador pode carregar no máximo 2 itens por batalha.
 */
public class Trainer {

  public static final int MAX_ITEMS = 2;

  private final String name;
  private final PokeSal pokeSal;
  private final List<Item> backpack = new ArrayList<>();

  /**
   * Cria um novo treinador com o nome e o PokeSal especificados.
   *
   * @param name    o nome do treinador
   * @param pokeSal o PokeSal utilizado por este treinador
   * @throws NullPointerException se name ou pokeSal forem nulos
   */
  public Trainer(String name, PokeSal pokeSal) {
    if (name == null) {
      throw new NullPointerException("O nome não pode ser nulo");
    }
    if (pokeSal == null) {
      throw new NullPointerException("O PokeSal não pode ser nulo");
    }

    this.name = name;
    this.pokeSal = pokeSal;
  }

  /**
   * Adiciona um item à mochila deste treinador, caso o limite de itens por batalha ainda não tenha
   * sido atingido.
   *
   * @param item o item a ser adicionado
   * @throws NullPointerException  se item for nulo
   * @throws IllegalStateException se a mochila já tiver atingido o limite de itens
   */
  public void addItem(Item item) {
    if (item == null) {
      throw new NullPointerException("O item não pode ser nulo");
    }
    if (backpack.size() >= MAX_ITEMS) {
      throw new IllegalStateException("A mochila já atingiu o limite de "
          + MAX_ITEMS + " itens");
    }

    backpack.add(item);
  }

  public String getName() {
    return name;
  }

  public PokeSal getPokeSal() {
    return pokeSal;
  }

  public List<Item> getBackpack() {
    return new ArrayList<>(backpack);
  }
}