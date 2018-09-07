# How to create new zombies #

### Spine ###
- Each bleedable body part needs a blood_pos point attachment
- Each physics body part(so pretty much all) needs a physics_pos point attachment
- Each movement animation needs a "move" event for zombies to move

### Code ###
- If you get an error "not size >= 0" or the gdx error, "Zombie Creation: Add Bleed Points..." then it means that you have a body part without
  "blood_pos" in spine. This is fine if the part doesn't bleed. This just means that you have to @Override the Zombie.createPart() method in
  your special zombie( go to SuicideZombie.createPart for more info )
