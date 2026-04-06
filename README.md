# Ant-Simulator
## What it does
A grid-based simulation of ant pathfinding. 
Ants will navigate to food and then collect it and bring it back to home, before heading out to find food again. Along the way they will leave pheremone trails telling other ants where they have been.

Red grid cells represent home cells.
Yellow grid cells represent cells with food on it.
Black and the edge of the world are avoided by ants (are not currently physical blockers).
Green pheremone trails lead to food and are left by ants that have food.
Pink pheremone trails lead to home and are left by ants that have come from home.

## How it works
Each ant can detect the state of the grid cells from three directions ahead of it. Based on the status of the ant, they will choose the best path forward at that moment. 

## To do
- Implement proper ant mesh or texture
- Add food holding representation display
- Optimise processing by paralleliing
- Add collision with surfaces
- Add collision with other ants
- Add more complex behaviour/prioroties
