# 🏰 Empire Builder

> **A Turn-Based Strategy Game** — Advanced Programming Course, Sharif University of Technology

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-6DB33F?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white)

---

## 📖 Table of Contents

- [About the Game](#-about-the-game)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [How to Run](#-how-to-run)
- [Gameplay Guide](#-gameplay-guide)
- [Controls](#-controls)
- [Technologies Used](#-technologies-used)
- [Project Structure](#-project-structure)
- [Credits](#-credits)

---

## 🎮 About the Game

**Empire Builder** is a single-player, turn-based strategy game where you take control of a fledgling civilization. Your goal is to **explore the unknown**, **gather resources**, **construct buildings**, and **expand your territory** — all while managing your economy and keeping your people fed.

This project was developed as the first phase of the Advanced Programming course at **Sharif University of Technology**, focusing on:
- Object-Oriented Programming (OOP)
- Model-View-Controller (MVC) architecture
- SOLID principles
- Advanced Java (Swing, Streams, Generics)
- Procedural map generation

---

## ✨ Features

| Category | Features |
| :--- | :--- |
| 🗺️ **Map** | Procedurally generated hexagonal grid with forests, mountains, grasslands, and plains. Each tile has unique resources. |
| ⛏️ **Resources** | Manage **Food**, **Wood**, **Stone**, and **Iron**. Each resource has specific production buildings. |
| 🏛️ **Buildings** | Construct Lumber Mills, Stone Mines, Iron Mines, Farms, and Stables. Each requires specific terrain. |
| 👷 **Units** | Command **Explorers** (scout the map), **Builders** (construct buildings with limited charges), **Workers** (gather resources), and **Border Expanders** (claim new territory). |
| 🔄 **Turn System** | Each unit has **Action Points (AP)**. Plan your moves carefully — every action consumes AP. |
| 🌫️ **Fog of War** | The map starts dark. Only explored tiles become visible. Explorers have the largest vision radius. |
| 🧭 **Auto-Explore** | Explorers can be set to automatically explore unknown territories (bonus feature). |
| 🗺️ **Minimap** | A small overview map shows explored areas, units, and your town hall. Click to teleport the camera. |
| 🎨 **Visual Polish** | Smooth unit animations, custom icons, tooltips on hover, and a sleek dark-themed HUD. |

---

## 📸 Screenshots


| Main Menu | In-Game HUD | Minimap |
| :---: | :---: | :---: |
| ![Main Menu](screenshots/menu.png) | ![Gameplay](screenshots/gameplay.png) | ![Minimap](screenshots/minimap.png) |

---

## 🚀 How to Run

### Prerequisites
- **Java 17** or higher installed
- **Gradle** (wrapped, no manual installation needed)

### Build

```bash
# Build and run the game
./gradlew run
```

> 💡 **Note:** On Windows, use `gradlew.bat run` instead.

### Alternative: Build JAR and Run

```bash
./gradlew build
java -jar build/libs/empire-builder.jar
```

---

## 🎯 Gameplay Guide

### 🏁 Starting Out
- You begin with a **Town Hall** and **5 units**: 1 Builder, 2 Workers, and 2 Explorers.
- Your starting resources: **50 Food**, **60 Wood**, **20 Stone**, and **0 Iron**.
- Only the tiles immediately around your Town Hall are visible — the rest is hidden by the **Fog of War**.

### 🔄 Turn Structure
Each turn follows this cycle:

1. **Action Points Refreshed** — All units regain their full AP.
2. **Auto-Explore** — Explorers with auto-explore enabled move automatically.
3. **Resource Production** — Buildings with stationed workers produce resources.
4. **Upkeep Deduction** — Each active building consumes 1 Wood per turn. If upkeep is missed for 3 consecutive turns, the building collapses.
5. **Player Actions** — You can move units, build structures, station workers, expand borders, or start production at the Town Hall.
6. **End Turn** — Click the **"End Turn"** button. If any unit still has AP left, you'll receive a warning.

### 🏗️ Buildings & Production

| Building | Terrain Required | Produces | Worker Capacity | Production per Worker |
| :--- | :--- | :--- | :--- | :--- |
| 🪵 **Lumber Mill** | Forest | Wood | 3 | 3 Wood/turn |
| 🪨 **Stone Mine** | Mountain (with Stone) | Stone | 3 | 2 Stone/turn |
| ⛏️ **Iron Mine** | Mountain (with Iron) | Iron | 2 | 1 Iron/turn |
| 🌾 **Farm** | Grassland (Wheat/Rice) | Food | 3 | 3 Food/turn |
| 🐄 **Stable** | Plain (Cattle/Sheep) | Food | 2 | 2 Food/turn |

### 🧑‍🌾 Units Overview

| Unit | Role | AP | Vision | Special |
| :--- | :--- | :--- | :--- | :--- |
| 🧭 **Explorer** | Scouting | 4 | 3 tiles | Can auto-explore; reveals fog of war |
| 🔨 **Builder** | Construction | 4 | 1 tile | Has 3 charges; consumed when charges run out |
| 🪣 **Worker** | Resource gathering | 3 | 1 tile | Must be stationed in a building to produce |
| 🚩 **Border Expander** | Territory expansion | 4 | 1 tile | Expands borders and is consumed after use |

### 🏛️ Town Hall Upgrades

| Upgrade | Cost | Effect |
| :--- | :--- | :--- |
| 📦 **Storage Upgrade** (x2) | Wood: 20, Stone: 30 | +100 capacity for all resources |
| 🪨 **Stone Mining Tech** | Wood: 25, Stone: 15 | Unlocks Stone Mine construction |
| ⛏️ **Iron Mining Tech** | Wood: 30, Stone: 20 | Unlocks Iron Mine construction |
| 🔧 **Professional Tools** | Stone: 40, Iron: 20 | Stone & Iron production ×1.5 |
| 🏘️ **Settlement Tech** | Wood: 40, Stone: 30 | +5 Unit Cap, creates a settlement on an empty tile |

---

## ⌨️ Controls

| Action | Input |
| :--- | :--- |
| **Select Unit** | Left-click on a unit |
| **Cycle Units on Same Tile** | Left-click repeatedly |
| **Move Unit** | Left-click on a reachable tile |
| **Open Context Menu** | Right-click on a unit or tile |
| **Pan Camera** | Click and drag on the map |
| **Zoom In/Out** | Mouse scroll wheel |
| **Deselect Unit** | Press `Esc` |
| **Toggle Fullscreen** | Press `F11` |
| **End Turn** | Click the "End Turn" button |

---

## 🛠️ Technologies Used

- **Java 17** — Core language
- **Swing** — GUI framework
- **Gradle** — Build automation
- **Java Sound API** — Background music playback
- **Custom Hex Grid** — Procedural map generation with axial coordinates
- **MVC Architecture** — Clean separation of concerns

---

## 📁 Project Structure

```
empire-builder/
├── src/
│   └── main/
│       ├── java/civgame/
│       │   ├── controller/     # Game logic & event handling
│       │   ├── model/          # Data & business logic
│       │   └── view/           # UI components (Swing)
│       └── resources/
│           ├── images/         # Icons, sprites, backgrounds
│           └── audio/          # Background music
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 👨‍💻 Credits

**Developed by** — Pouria AsnaAshari
**Course** — Advanced Programming, Sharif University of Technology  
**Semester** — Spring 2026

---

## 📜 License

This project is for educational purposes only. All rights reserved.

---

## 🙏 Acknowledgments

- Hex grid algorithms adapted from [Red Blob Games](https://www.redblobgames.com/grids/hexagons/)
- Icons designed with ❤️ using open-source assets

---

⭐ **If you enjoyed this project, don't forget to star the repository!** ⭐