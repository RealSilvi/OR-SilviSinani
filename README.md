# 📊 Branch & Bound Solver for Integer Linear Programming (with IBM CPLEX)

This is a university project developed during the Operational Research course at the University of Bologna. It implements the Branch and Bound algorithm to solve Pure Linear Integer (PLI) problems using the IBM ILOG CPLEX library.

## ⚙️ Features

- Solves PLI problems via recursive Branch & Bound
- Interactive UI with 6 sample problems
- Binary tree-based problem storage
- Problem selection via best-bound-first strategy
- Smart variable selection via fractional closeness to 0.5

## 🔁 How It Works

1. Solve the current node using CPLEX.
2. If the solution is integer and better than current best → save it.
3. If fractional → generate new subproblems by branching on the most fractional variable.
4. Repeat recursively.

## 🔧 Installation & Run

> Requires IBM CPLEX (installed & linked)

```bash
git clone https://github.com/RealSilvi/OR-SilviSinani.git
# Link CPLEX libraries manually
# Run the project via your IDE (e.g., IntelliJ or Eclipse)
```

## 👨‍💻 Developer

Silvi Sinani – silvi.sinani@studio.unibo.it

