# Coin Swap Search AI

This project implements a state-space search solution for a coin arrangement puzzle using heuristic-guided search.

The objective is to rearrange blue and red coins into a target configuration by applying valid moves while efficiently exploring the state space.

---

## 🧩 Problem Description

The board consists of **7 adjacent cells** containing:
- `B` — Blue coins
- `R` — Red coins
- `0` — One empty cell

Only one cell may be empty at any time.

### Example Initial State
B R B R 0 R B

### Goal State
B B B 0 R R R

---

## 🔄 Legal Moves

1. **Slide Move**  
   A coin may move left or right into an adjacent empty cell.

2. **Jump Move**  
   A coin may jump over one adjacent coin into an empty cell two positions away.

---

## 🧠 State Representation

Each state is represented as an array:
- Index `0` stores the position of the empty cell
- Index `1–7` store the contents of the cells (`B`, `R`, or `0`)

This representation allows efficient move generation, comparison, and tracking of visited states.

---

## 📐 Heuristic Function

The heuristic used is:

h(n) = number of coins not in their correct positions


### Why this heuristic?
- Admissible (never overestimates the distance to the goal)
- Simple to compute
- Guides the search toward the goal state

---

## 🌳 Search Strategy

The algorithm:
- Generates valid successor states using slide and jump moves
- Uses a priority-based frontier ordered by heuristic value
- Avoids duplicate and previously visited states

This results in an efficient exploration of the state space.
