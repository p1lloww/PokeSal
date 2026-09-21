package br.edu.ucsal.tqs.pokesal.entities.catalogs;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.effects.BurnHeal;
import br.edu.ucsal.tqs.pokesal.entities.effects.HealEffect;
import br.edu.ucsal.tqs.pokesal.entities.effects.ParalyzeHeal;
import br.edu.ucsal.tqs.pokesal.entities.effects.SuperAntidote;
import java.util.List;

/**
 * Catálogo fixo dos itens de mochila disponíveis para escolha antes de uma batalha. Classe
 * utilitária, não instanciável.
 */
public final class ItemCatalog {

  private static final double POTION_HEAL_PERCENTAGE = 0.20;
  private static final double SUPER_POTION_HEAL_PERCENTAGE = 0.50;

  private ItemCatalog() {
  }

  /**
   * Retorna uma nova instância de Potion, que restaura 20% do HP máximo.
   *
   * @return uma nova instância de Potion
   */
  public static Item createPotion() {
    return new Item("Potion", "Restaura 20% do HP máximo.",
        List.of(new HealEffect(POTION_HEAL_PERCENTAGE)));
  }

  /**
   * Retorna uma nova instância de Super Potion, que restaura 50% do HP máximo.
   *
   * @return uma nova instância de Super Potion
   */
  public static Item createSuperPotion() {
    return new Item("Super Potion", "Restaura 50% do HP máximo.",
        List.of(new HealEffect(SUPER_POTION_HEAL_PERCENTAGE)));
  }

  /**
   * Retorna uma nova instância de Super Antidote.
   *
   * @return uma nova instância de Super Antidote
   */
  public static Item createSuperAntidote() {
    return new Item("Super Antidote", "Cura pouco HP; cura mais e remove o veneno "
        + "se o PokeSal estiver envenenado.",
        List.of(new SuperAntidote()));
  }

  /**
   * Retorna uma nova instância de Burn Heal.
   *
   * @return uma nova instância de Burn Heal
   */
  public static Item createBurnHeal() {
    return new Item("Burn Heal", "Cura pouco HP; cura mais e remove a queimadura "
        + "se o PokeSal estiver queimado.",
        List.of(new BurnHeal()));
  }

  /**
   * Retorna uma nova instância de Paralyze Heal.
   *
   * @return uma nova instância de Paralyze Heal
   */
  public static Item createParalyzeHeal() {
    return new Item("Paralyze Heal", "Cura pouco HP; cura mais e remove a paralisia "
        + "se o PokeSal estiver paralisado.",
        List.of(new ParalyzeHeal()));
  }

  /**
   * Retorna todos os itens disponíveis para escolha, na ordem em que devem ser exibidos ao
   * jogador.
   *
   * @return a lista de itens disponíveis
   */
  public static List<Item> allAvailableItems() {
    return List.of(
        createPotion(),
        createSuperPotion(),
        createSuperAntidote(),
        createBurnHeal(),
        createParalyzeHeal());
  }
}