# critter-vm

![GitHub](https://img.shields.io/github/license/SamChou19815/critter-vm.svg)

A lightweight critter world virtual machine.

It is a virtual machine where you can run a critter program in isolation with almost unbounded resources.

## Specification

This is an simulated operating system where you can run a critter program with unlimited round of loops and up to 4GB
of simulated memory.

It tries to be compatible with the 
[original critter world specification](http://www.cs.cornell.edu/courses/cs2112/2018fa/project/project.pdf), but the 
following items are changed in order for it to be more useful:

1. The 1000 round limit has been removed. You can loop forever. The change is necessary to make it Turing complete.
2. All expression related to the surrounding world (e.g. `nearby[3], smell`) will always return 0.
3. Performing an action has no effect, except ending the program and print the action performed.
4. The memory location 0-7 cannot be accessed.

## Technology

The parser is generated by ANTLR v4 and the VM is implemented in Kotlin. They are all forbidden technology for the 
CS 2112 final project so there should no concern of leaking code.

## Usage

To build the project, run

```bash
./gradlew build
```

To run the VM, run

```bash
# interpreting from stdin
java -jar [built jar file] < critter-program-0.critter
# interpreting multiple programs
java -jar [built jar file] critter-program-1.critter critter-program-2.critter
```
