# 📊 Branch & Bound Solver for Integer Linear Programming

A university project developed for the **Operational Research** course at the **University of Bologna**.

The project implements a recursive **Branch & Bound** algorithm to solve **Pure Integer Linear Programming** problems, using the **IBM ILOG CPLEX** library to solve the linear relaxation at each node.

---

## 🧩 Project Overview

The solver explores the solution space of integer linear programming problems by recursively branching on fractional variables and evaluating subproblems through CPLEX.

The goal is to find the best integer solution while pruning branches that cannot improve the current best solution.

---

## ⚙️ Features

- Recursive Branch & Bound implementation
- Solves pure integer linear programming problems
- Integration with IBM ILOG CPLEX for linear relaxation solving
- Binary tree-based storage of generated subproblems
- Best-bound-first strategy for node selection
- Variable branching based on fractional closeness to `0.5`
- Interactive interface with 6 sample optimization problems

---

## 🧠 Algorithm

At each step, the solver:

1. Solves the current relaxed problem using IBM CPLEX.
2. Checks whether the solution is integer-feasible.
3. Updates the incumbent solution if a better integer solution is found.
4. If the solution is fractional, selects a branching variable.
5. Generates two new subproblems by applying lower and upper bounds.
6. Stores the generated subproblems in a binary tree structure.
7. Selects the next problem to explore using a best-bound-first strategy.
8. Repeats the process recursively until no better branches remain.

---

## 🧩 Implementation Highlights

- Recursive algorithm design
- Binary tree representation of the search space
- Bound management for generated subproblems
- Fractional variable detection and branching logic
- Best-bound-first node exploration
- CPLEX integration for solving relaxed linear problems
- Sample problem selection through a simple interactive UI

---

## 🛠 Tech Stack

- **Language:** Java
- **Optimization Library:** IBM ILOG CPLEX
- **IDE:** IntelliJ IDEA / Eclipse
- **Concepts:** Branch & Bound, Integer Linear Programming, Linear Relaxation, Binary Trees, Operational Research

---

## 🔧 Requirements

This project requires:

- Java Development Kit
- IBM ILOG CPLEX installed locally
- CPLEX libraries correctly linked in the project configuration
- An IDE such as IntelliJ IDEA or Eclipse

> IBM CPLEX is a commercial optimization library and must be installed separately.

---

## ▶️ Installation & Run

Clone the repository:

```bash
git clone https://github.com/sinanisilvi/OR-SilviSinani.git
```

Open the project in your preferred Java IDE.

Then:

1. Configure the project SDK.
2. Link the IBM ILOG CPLEX libraries.
3. Build the project.
4. Run the main class from the IDE.

---

## 📌 Notes

This project was developed in an academic context to study and implement optimization techniques for integer programming problems.

The focus of the project is the implementation of the Branch & Bound logic, including branching strategy, node selection, recursive exploration and integration with CPLEX for solving relaxed problems.

---

## 👨‍💻 Author

**Silvi Sinani**  
GitHub: [github.com/sinanisilvi](https://github.com/sinanisilvi)
