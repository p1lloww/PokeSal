package br.edu.ucsal.tqs.pokesal.entities.catalogs;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.water.WaterPuddle;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import java.util.List;

/**
 * Catálogo dos terrenos disponíveis, com suporte a sorteio de um terreno aleatório para uma nova
 * batalha. Classe utilitária, não instanciável.
 */
public final class BattlegroundCatalog {

  private BattlegroundCatalog() {
  }

  /**
   * Sorteia e retorna um terreno aleatório entre os disponíveis.
   *
   * @param randomGenerator o gerador usado no sorteio
   * @return um novo Battleground sorteado
   */
  public static Battleground randomBattleground(RandomGenerator randomGenerator) {
    List<Battleground> options = List.of(
        new HotAsphalt(), new WaterPuddle(), new CentralFlowerbed());

    return options.get(randomGenerator.nextInt(options.size()));
  }
}