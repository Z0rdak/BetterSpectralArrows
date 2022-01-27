# BetterSpectralArrows

BetterSpectralArrows is a small, light-weight, vanilla+ style, server-side mod, which enhances Spectral Arrows to be able to place light blocks in the world when hitting a block.

This mod has the aim to make exploring the new cases in Minecraft 1.18 more fun and easy.

Each light source placed by a spectral arrow has its own time tracker (depending on the time of their placement). 
Each light block will decay independently of the interval of the other light blocks.

To make Spectral Arrows more accessible early game, this mods adds two new recipes for crafting Spectral Arrows, using ingredients found in the new caves: **Glow Berries** and **Glow Ink Sacks**

## Configuration support 

Within the server side configuration you have the possibility to configure the properties of the placed light sources, the recipes and some other things.

### Light source configuration

 - Staring light level of placed light blocks
 - Decay interval in ticks (20 ticks -> ~1 second)
 - Decay amount: how many light levels to reduce for each interval
 - Decay chance: how many likely a light block will reduce its light level
 
You want the light blocks to vanish completely after 2 minutes?
Try this: 
```toml
["BetterSpectralArrows - light block config"]
#Start light level for generated light blocks.
#Range: 1 ~ 15
start_light_lvl = 15
#Time interval in ticks for decreasing light level of the placed light blocks.
# -1 indicates forever
#Range: > -1
decay_interval = 1200  # ~2 minutes
#Amount of levels to for decreasing the light level of placed light blocks.
#Range: 1 ~ 15
decay_amount = 15
#Chance for a light block to decay/reduce its light level.
# 1 indicates 100% decaying chance
# 0 indicates no decaying
#Range: 0.0 ~ 1.0
decay_chance = 1.0
```
The arrows will place a light source with light level 15 and decay with a 100% chance once after about 2 minutes - after that, they are gone.

Or maybe something more long-lasting and slowly decaying:
```toml
start_light_lvl = 15
decay_interval = 800 # ~ 40 seconds
decay_amount = 1
decay_chance = 0.5
```
Using this configuration the placed light sources will slowly decay, decreasing their light level every ~40 seconds, with a 50% chance, by one light level.

It is also possible to make the placed light blocks last forever. This is done by setting the decay interval to -1 or setting the decay chance to 0.

The light blocks will still be tracked (don't worry about performance), but not removed.
You can change to configuration again to make them start decaying again.

### Arrow configuration

You are able to enable/disable the recipes to craft spectral arrows with glow berries or glow ink as well as tweak the amount required for crafting. 
Additionally, you can configure if fired arrows are discarded instantly after a light block is created to tweak the balance.

```toml
["BetterSpectralArrows - Recipe config"]
#Enable or disable crafting spectral arrows with glow ink
craft_with_glow_ink = true
#Amount of glow ink required for recipe
#Range: 1 ~ 8
amount_glow_ink = 1
#Enable or disable crafting spectral arrows with glow berries
craft_with_glow_berries = true
#Amount of glow berries required for recipe
#Range: 1 ~ 8
amount_glow_berries = 2
```

## Issues

Feel free to report any errors, issues, suggestions to [GitHub](https://github.com/Z0rdak/BetterSpectralArrows/issues). Please make sure to include your forge and mod version in your issue.

## License & Permission

This mod is provided with the MIT License. Feel free to include this mod in any modpack.

## FAQ

 - Will the mod be ported to versions <1.18? - No, that would mean that I would introduce a separate light block, which would result in the client having to install the mod as well.
