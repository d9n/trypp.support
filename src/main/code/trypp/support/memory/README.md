# Memory

Classes to help manage memory.

In games, a major optimization is to make sure you aren't allocating / deallocating any objects
while things are moving in action. Allocating even small objects excessively can trigger the garbage
collector which stutters random frames. To that end, we introduce various memory pool classes.