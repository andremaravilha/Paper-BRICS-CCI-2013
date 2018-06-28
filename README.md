# A new algorithm based on Differential Evolution for Combinatorial Optimization

> **Contributors:** André L. Maravilha<sup>1,3</sup>, Jaime A. Ramírez<sup>2</sup>, Felipe Campelo<sup>2,3</sup>  
> <sup>1</sup> *Graduate Program in Electrical Engineering, Universidade Federal de Minas Gerais ([PPGEE](https://www.ppgee.ufmg.br/), [UFMG](https://www.ufmg.br/))*  
> <sup>2</sup> *Dept. Electrical Engineering, Universidade Federal de Minas Gerais ([DEE](http://www.dee.ufmg.br/), [UFMG](https://www.ufmg.br/))*  
> <sup>3</sup> *Operations Research and Complex Systems Lab., Universidade Federal de Minas Gerais ([ORCS Lab](http://orcslab.ppgee.ufmg.br/), [UFMG](https://www.ufmg.br/))*

This repository contains the source code of the algorithms implemented in the paper: "A new algorithm based on differential evolution for combinatorial optimization" published by André L. Maravilha, Jaime A. Ramírez and Felipe Campelo at the 2013 BRICS Congress on Computational Intelligence & 11th Brazilian Congress on Computational Intelligence ([Link](http://dx.doi.org/10.1109/BRICS-CCI-CBIC.2013.21)).

## 1. Abstract

Differential evolution (DE) was originally designed to solve continuous optimization problems, but recent works have been investigating this algorithm for tackling combinatorial optimization (CO), particularly in permutation-based combinatorial problems. However, most DE approaches for combinatorial optimization are not general approaches to CO, being exclusive for per mutational problems and often failing to retain the good features of the original continuous DE. In this work we introduce a new DE-based technique for combinatorial optimization to addresses these issues. The proposed method employs operations on sets instead of the classical arithmetic operations, with the DE generating smaller sub problems to be solved. This new approach can be applied to general CO problems, not only permutation-based ones. We present results on instances of the traveling salesman problem to illustrate the adequacy of the proposed algorithm, and compare it with existing approaches.

## 2. How to build and run the project

### 2.1. Building the project

This project was developed with Java and uses Guribi to solve sub-MIP problems. To compile this project you need the Java SE Development Kit (JDK, [Link](https://www.java.com)) and Gurobi solver ([Link](http://www.gurobi.com/)) installed in your computer. The current version of the code was tested on a Linux machine with JDK 8 and Gurobi 8.0.1. Inside the root directory of the project, run the following commands:
```
./gradlew clean
./gradlew shadow
```

After running the commands above, the file `code-all.jar` can be found in the directory `build/libs`. You do not need any additional library to run the project. The gradle is configured to include all dependencies in the jar file.

By default, the project uses the value of environment variable `GUROBI_HOME` to search for Gurobi path. If this environment variable is not set or if you want to use another value, you can set the property `gurobi.home`:
```
./gradlew clean
./gradlew shadow -Dgurobi.home=/opt/gurobi801/linux64/
```


### 2.2. Running the project

Usage:  
```
java -jar code-all [options]
```  

Example:  
```
java -jar code-all --algorithm code --instance ./instances/tsplib/kroA200.tsp --iterations-limit 30 --param population-size=30 --param submip-time-limit=5000
```

#### 2.2.1. Options

`--algorithm <VALUE>`  
(Required)  
Algorithm used to solve the problem instance. Possible valures are:
* `code`: CoDE (the DE-based algorithm for combinatorial optimization proposed in the paper)
* `list-movements`: List of Movements
* `permutation-matrix`: Permutation Matrix
* `relative-position-index`: Relative Position Index
* `gurobi`: Solve the problem instance using Gurobi solver.

`--instance <VALUE>`  
(Required)  
Path to the file containing the instance data.

`--seed <VALUE>`  
(Default: 0)  
Seed used to initialize the random number generator used by the algorithms.

`--time-limit <VALUE>`  
(Default: a very large number)  
Maximum runtime for the algorithm (in milliseconds). The runtime is usually checked at the beginin of each iteration. Then, if an algorithm reaches the maximum time in the middle of an iteration, it will terminate the iteration before terminating.

`--iterations-limit <VALUE>`  
(Default: a very large number)  
Maximum number of iterations performed by the algorithm.

`--print-solution`  
If used, the best solution is displayed on the screen at the end of the optimization process. Otherwise, only the value of objective function is displayed.


#### 2.2.2 Algorithms' parameters

Algorithm parameters can be set through the sintax `--param <NAME>=<VALUE>`. Below are the specific parameters of each algorithm.


##### CoDE

`--param population-size=<VALUE>`  
(Default: 30)  
The population size, i.e., the number of solutions evaluated at each iteration of the algorithm.

`--param target-value=<VALUE>`  
(Default: a negative value)  
The algorithm stops as soon as it finds a solution with value of objective function equal or better (in this case, smaller) the this target value.

`--param submip-time-limit=<VALUE>`  
(Default: a very large number)  
The time limit (in milliseconds) to optimize each sub-MIP.


##### List of Movements

`--param population-size=<VALUE>`  
(Default: 30)  
The population size, i.e., the number of solutions evaluated at each iteration of the algorithm.

`--param target-value=<VALUE>`  
(Default: a negative value)  
The algorithm stops as soon as it finds a solution with value of objective function equal or better (in this case, smaller) the this target value.

`--param mutation-type=<VALUE>`  
(Default: 2)  
Define the strategy used to perform the mutation. A list with `N` possible movements and a mutation factor `F` that is a number between 0 and 1 (inclusive). Possible values of mutation type are:
* `1`: The first `ceil(F*N)` movements in the list are performed.
* `2`: Each movement in the list has a `100*F`% change of being performed.
* `3`: A total of `ceil(F*N)` movements in the list are randomly selected to be performed

`--param mutation-factor=<VALUE>`  
(Default: 0.5)  
Set the mutation factor `F`, which defines how strong will be the mutation performed.


##### Permutation Matrix

This algorithm does not have any additional parameter.


##### Relative Position Index

`--param population-size=<VALUE>`  
(Default: 30)  
The population size, i.e., the number of solutions evaluated at each iteration of the algorithm.

`--param target-value=<VALUE>`  
(Default: a negative value)  
The algorithm stops as soon as it finds a solution with value of objective function equal or better (in this case, smaller) the this target value.

`--param mutation-factor=<VALUE>`  
(Default: 0.5)  
Set the mutation factor `F` used to perform the standard DE mutation before encoding the result to a valid permutation.

`--param crossover-factor=<VALUE>`  
(Default: 0.9)  
Set the mutation factor `CR` used to perform the standard DE crossover before encoding the result to a valid permutation.


##### Gurobi solver

`--param verbose=<VALUE>`  
(Default: 0)  
If set to `1`, the progress of the optimization process is displayed. The value `0` does not display the progress.


