package br.edu.ucsal.tqs.pokesal.entities.catalogs;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.water.WaterPuddle;
import java.util.List;
import java.util.Random;

/**
 * Catálogo dos terrenos disponíveis, com suporte a sorteio de um terreno aleatório para uma nova
 * batalha. Classe utilitária, não instanciável.
 */
public final class BattlegroundCatalog {

  private static final Random RANDOM = new Random();

  private BattlegroundCatalog() {
  }

  /**
   * Sorteia e retorna um terreno aleatório entre os disponíveis.
   *
   * @return um novo Battleground sorteado
   */
  public static Battleground randomBattleground() {
    List<Battleground> options = List.of(
        new HotAsphalt(), new WaterPuddle(), new CentralFlowerbed());

    return options.get(RANDOM.nextInt(options.size()));
  }
}