package br.edu.ucsal.tqs.pokesal.entities.catalogs;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.passives.fire.Blaze;
import br.edu.ucsal.tqs.pokesal.entities.passives.grass.LeafGuard;
import br.edu.ucsal.tqs.pokesal.entities.passives.grass.Overgrow;
import br.edu.ucsal.tqs.pokesal.entities.passives.neutral.Intimidate;
import br.edu.ucsal.tqs.pokesal.entities.passives.water.RainDish;
import br.edu.ucsal.tqs.pokesal.entities.passives.water.Torrent;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Paralyzed;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import java.util.List;

/**
 * Catálogo fixo dos seis PokeSal iniciais disponíveis para escolha, cada um criado com seus
 * atributos-base, tipo elemental, movimentos e passiva. Classe utilitária, não instanciável.
 */
public final class PokeSalCatalog {

  private static final double STATUS_CHANCE = 0.10;

  private PokeSalCatalog() {
  }

  /**
   * Retorna uma nova instância de BulbaSal, com seus atributos e passiva Overgrow.
   *
   * @return uma nova instância de BulbaSal
   */
  public static PokeSal createBulbaSal() {
    List<Move> moves = List.of(
        new Move("Tackle", "Um ataque físico básico.", ElementType.PLANT, 40),
        new Move("Vine Whip", "Ataca com vinhas afiadas, podendo envenenar o alvo.",
            ElementType.PLANT, 45, new Poison(), STATUS_CHANCE));

    return new PokeSal("BulbaSal", "Um PokeSal do tipo Planta.", 45, 49, 49, 45,
        ElementType.PLANT, moves, new Overgrow());
  }

  /**
   * Retorna uma nova instância de CharSal, com seus atributos e passiva Blaze.
   *
   * @return uma nova instância de CharSal
   */
  public static PokeSal createCharSal() {
    List<Move> moves = List.of(
        new Move("Scratch", "Um ataque físico básico.", ElementType.FIRE, 40),
        new Move("Ember", "Lança uma pequena chama, podendo queimar o alvo.",
            ElementType.FIRE, 40, new Burn(), STATUS_CHANCE));

    return new PokeSal("CharSal", "Um PokeSal do tipo Fogo.", 39, 52, 43, 65,
        ElementType.FIRE, moves, new Blaze());
  }

  /**
   * Retorna uma nova instância de SquirtSal, com seus atributos e passiva Torrent.
   *
   * @return uma nova instância de SquirtSal
   */
  public static PokeSal createSquirtSal() {
    List<Move> moves = List.of(
        new Move("Tackle", "Um ataque físico básico.", ElementType.WATER, 40),
        new Move("Water Gun", "Dispara um jato de água, podendo paralisar o alvo.",
            ElementType.WATER, 40, new Paralyzed(), STATUS_CHANCE));

    return new PokeSal("SquirtSal", "Um PokeSal do tipo Água.", 44, 48, 65, 43,
        ElementType.WATER, moves, new Torrent());
  }

  /**
   * Retorna uma nova instância de ChikoSal, com seus atributos e passiva Leaf Guard.
   *
   * @return uma nova instância de ChikoSal
   */
  public static PokeSal createChikoSal() {
    List<Move> moves = List.of(
        new Move("Tackle", "Um ataque físico básico.", ElementType.PLANT, 40),
        new Move("Razor Leaf", "Corta o alvo com folhas afiadas, podendo envenená-lo.",
            ElementType.PLANT, 55, new Poison(), STATUS_CHANCE));

    return new PokeSal("ChikoSal", "Um PokeSal do tipo Planta.", 45, 49, 65, 45,
        ElementType.PLANT, moves, new LeafGuard());
  }

  /**
   * Retorna uma nova instância de CyndaSal, com seus atributos e passiva Intimidate.
   *
   * @return uma nova instância de CyndaSal
   */
  public static PokeSal createCyndaSal() {
    List<Move> moves = List.of(
        new Move("Tackle", "Um ataque físico básico.", ElementType.FIRE, 40),
        new Move("Ember", "Lança uma pequena chama, podendo queimar o alvo.",
            ElementType.FIRE, 40, new Burn(), STATUS_CHANCE));

    return new PokeSal("CyndaSal", "Um PokeSal do tipo Fogo.", 39, 52, 43, 65,
        ElementType.FIRE, moves, new Intimidate());
  }

  /**
   * Retorna uma nova instância de TotoSal, com seus atributos e passiva Rain Dish.
   *
   * @return uma nova instância de TotoSal
   */
  public static PokeSal createTotoSal() {
    List<Move> moves = List.of(
        new Move("Scratch", "Um ataque físico básico.", ElementType.WATER, 40),
        new Move("Water Gun", "Dispara um jato de água, podendo paralisar o alvo.",
            ElementType.WATER, 40, new Paralyzed(), STATUS_CHANCE));

    return new PokeSal("TotoSal", "Um PokeSal do tipo Água.", 50, 65, 64, 43,
        ElementType.WATER, moves, new RainDish());
  }
}
