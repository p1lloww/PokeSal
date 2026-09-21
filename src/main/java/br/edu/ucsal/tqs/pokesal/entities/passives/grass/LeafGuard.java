package br.edu.ucsal.tqs.pokesal.entities.passives.grass;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;

/**
 * Passiva de tipo Planta que impede a aplicação de qualquer condição de status
 * negativa no PokeSal dono, enquanto esta passiva estiver ativa.
 */
public class LeafGuard implements Passive {

  @Override
  public String getName() {
    return "Leaf Guard";
  }

  @Override
  public String getDescription() {
    return "Impede a aplicação de condições de status negativas.";
  }

  @Override
  public boolean preventsStatusApplication(PokeSal self) {
    return true;
  }
}