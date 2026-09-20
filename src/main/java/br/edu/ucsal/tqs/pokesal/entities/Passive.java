package br.edu.ucsal.tqs.pokesal.entities;

public interface Passive {
    public String getName();
    public String getDescription();
    public void onSwitchIn(PokeSal self, PokeSal opponent);
    public void onDamageTaken(PokeSal self, PokeSal opponent, double damageTaken);
    public void onTurnEnd(PokeSal self);
    public double onAttack(PokeSal self, Battleground battleground);
    public boolean preventsStatusApplication(PokeSal self);
}
