# Virtual Pet Adventure 🐾
**Your Digital Companion**  
CS2212 – Software Tools & Systems Programming  
Western University – Fall 2024  
Team 14

---

## 🎮 Project Overview

**Virtual Pet Adventure** is an interactive desktop simulation game where users care for a virtual pet by managing its health, sleep, hunger, and happiness. Inspired by Tamagotchi-style games, this project is built using Java Swing and follows object-oriented principles. The player interacts with their pet through feeding, playing, exercising, giving gifts, and visiting the vet — all while managing limited resources and time.

---

## 🛠️ Technologies Used

- Java 17  
- Java Swing (GUI)  
- Maven (build automation)  
- Git & GitHub (version control)

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR-GROUP-URL/group14.git

2. Navigate to the project folder:
cd group14

3. Compile and run using Maven:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="com.group14.virtualpet.Main"
   ```

⚠️ Please make sure Java 17 is installed and available on your system.

# 🐱 Pet Types
RoboFriend: Forgiving and easy to care for / Difficulty: Easy
MechaMate: Balanced stats and energy / Difficulty: Medium
Tech Titan: Demands high attention and strategy / Difficulty: Hard

Each pet has unique hunger rates, energy needs, and mood sensitivity!

-- 

# 📋 Key Features
✅ Customizable pet with user-defined name
✅ Pet states: Normal, Hungry, Angry, Sleeping, Dead
✅ Real-time stat decay and action cooldowns
✅ Randomly granted food/gift items
✅ Save/load game state (per pet name)
✅ Tutorial panel with instructions
✅ Parental controls with time limits
✅ Emergency food drop mechanic to avoid starvation (custom feature)

# 🔐 Parental Controls
Playtime Limit: Set and enforce session duration

Password Protection: Access requires parent password

Revive Option: Bring your pet back to life

(Planned): Track total & average playtime across sessions

# ✨ Extra Feature (Custom Requirement)
As our extra functional feature, we implemented:

Emergency Food Rescue System
If the pet enters a Hungry state and the player has no food in inventory, a special "Emergency Ration" is automatically granted to avoid unfair pet death.

# 🧑‍💻 Development Team
Pragalvha Sharma

Aalyan Muhammad

Athul Charanthara

Krish Naula

Manav Joshi

# 📁 File Structure
group14/
│
├── src/                     # Source code
│   └── com/group14/...
│
├── pom.xml                  # Maven build file
├── README.md                # Project documentation
└── .gitignore               # Ignored files

# 📦 Version
v1.0.0 – Final Build for Submission