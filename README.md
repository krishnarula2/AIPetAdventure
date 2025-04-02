# Virtual Pet Adventure 🐾
**Your Digital Companion**  
CS2212 – Software Tools & Systems Programming  
Western University – Fall 2024  
Team 14

---

## 🎮 Project Overview

**Virtual Pet Adventure** is an interactive desktop simulation game where users care for a virtual pet by managing its health, sleep, hunger, and happiness. Inspired by Tamagotchi-style games, this project is built using Java Swing and follows object-oriented principles. The player interacts with their pet through actions such as feeding, playing, exercising, and visiting the vet—all while managing limited resources and time.

---

## 🛠️ Technologies Used

- Java 17 (or Java 21 if applicable; update here as needed)
- Java Swing (GUI)
- Maven (build automation)
- Git (version control)

---

## 🚀 How to Build and Run

### Building the Project

1. **Clone the repository:**
   ```bash
   git clone https://github.com/YOUR-GROUP-URL/group14.git
Navigate to the project directory:

bash
Copy
cd group14
Build the project using Maven:

bash
Copy
mvn clean package
This command will compile the code and package it into an executable JAR file, which will be located in the target/ directory (typically named group14-1.0-SNAPSHOT.jar).
Note: Please include this JAR file in your submission under a folder named built/.

Running the Game
You can run the game either by executing the JAR file directly or by using Maven:

Using the JAR file:

bash
Copy
java -jar built/group14-1.0-SNAPSHOT.jar
Using Maven (exec:java):

bash
Copy
mvn exec:java -Dexec.mainClass="com.group14.virtualpet.Main"
Ensure that your system has the required Java version installed (Java 17 or as specified).

📚 Documentation
Javadoc Output
Generate the Javadoc by running:

bash
Copy
mvn javadoc:javadoc
The generated HTML documentation will be found in the target/site/apidocs/ directory.

Important: Please zip the contents of target/site/apidocs/ into a file (e.g., javadoc.zip) and include it in your submission under a folder named docs/.

Testing Documentation
Export the Testing Documentation from your team’s GitLab Wiki (navigate to your Testing section and choose “Export as PDF” or “Print to PDF”).

Save the resulting file as TestingDocumentation.pdf and include it in your submission.

🔐 Parental Controls
Parental Password: The default parental password is admin.

The parental controls screen allows you to set playtime limits and revive a pet. Follow the in-game instructions on the Parental Controls screen for further details.

🐱 Pet Types
RoboFriend: Forgiving and easy to care for (Easy)

MechaMate: Balanced stats and energy (Medium)

Tech Titan: Demands high attention and strategy (Hard)

Each pet has unique hunger rates, energy needs, and mood sensitivities!

📁 File Structure
bash
Copy
group14/
│
├── src/                     # Source code (Java packages)
│   └── com/group14/...
│
├── target/                  # Maven build output (includes executable jar and javadoc)
├── built/                   # (To be created) Contains the executable JAR file
├── docs/                    # (To be created) Contains the zipped Javadoc output
├── TestingDocumentation.pdf # Exported PDF from GitLab Wiki (to be added)
├── pom.xml                  # Maven build file
├── README.md                # This README file
└── .gitignore               # Git ignore rules
📦 What’s Included in the Submission
ZIP of Repository: Contains all source code, resources (images), pom.xml, .gitignore, and README.md.

Built Executable Version: The executable JAR file is included in the built/ folder.

Javadoc Output: A ZIP archive of the generated Javadoc is included in the docs/ folder.

Testing Documentation: The exported PDF from the GitLab Wiki (named TestingDocumentation.pdf) is included.

Additional Build Instructions: All required build and run instructions are provided above.

🧑‍💻 Development Team
Pragalvha Sharma

Aaliyan Muhammad

Athul Charanthara

Krish Narula

Manan Joshi

📦 Version
v1.0.0 – Final Build for Submission
 