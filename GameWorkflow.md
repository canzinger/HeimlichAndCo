This file describes the workflow that the Class HeimlichAndCo takes with every action (not the instantiation):

## 1. Player calls getPossibleActions()
    - depending on which type of action is next, 
      the Game calls <ActionClass>.getPossibleActions()
## 2. Player calls doAction(action)
    - Game saves the Map<Agent, Int> with the agents positiosn and the safe position

The Map is saved at the start of a players turn (when he rolls the die);
