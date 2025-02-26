# **Sprint Week Project: Plushie Game**


**Members**

- Adam Sparkes
- Kyle Hollet
- Bradley Ayers
- Brian Janes
- Michael O'Brien

## **Overview**

This project was built as part of the Semester 4 midterm to fulfill all reqs while expanding on the original **Airport Management System** idea by creating a game-based system. Instead of managing airports, flights, and passengers, we used the **same structure** in our game system, making it like way more fun while still following all the reqs from the project.

✅ A **Java Server** exposing a **REST API** (Spring Boot).  
✅ A **Java Client** application that connects to the server over HTTP.  
✅ **Relational Database** (MySQL) to store and manage game-related data.  
✅ **Testing API with Postman** to verify all endpoints.  
✅ **GitHub Repository with PR workflow** for code collaboration.      
✅ **Demo Video** showcasing the working project.  
✅ **Dynamic UI using JPanel for seamless transitions**.  
✅ **Custom mini-games designed by each team member**.  

In **short**, we took the typical airports-and-flights concept and **completely gamified** it, but **still** used every data relationship. This way, we followed **all** the course instructions while making our project **super fun** to explore.

---
## **1️⃣ How This Aligns with the Original Sprint Assignment**


| **Original Requirement** | **Our Implementation** |
|-------------------------|---------------------------|
| **Cities have many airports** | **Game locations have multiple mini-games** |
| **Passengers travel on aircraft** | **Players participate in mini-games** |
| **Aircraft take off from airports** | **Mini-games exist in specific locations** |
| **Airports track passenger movement** | **Game tracks player stats and collectibles** |

We maintained the **same data structure**, but instead of tracking flights and passengers, we tracked **heroes, mini-games, locations, and plushie collectibles** instead.

**Extra Note**: Our database still has the same kind of relationships (one-to-many, many-to-many, etc.). For example, each **Location** can hold multiple **Mini-Games** (like a City holds multiple Airports), and **Players** (similar to Passengers) can join different Mini-Games. This ensures that, at the database level, we have the exact same relational approach.

---
## **2️⃣ How the Project Works**

This project is split into **two main parts**: the **server** (backend) and the **client** (frontend). 

### **The Server (Backend)**
- Built using **Java + Spring Boot**.
- Handles all API requests from the client.
- Stores and grabs game data in **MySQL**.
- Manages **Heroes, Locations, Mini-Games, and Plushies**.
- Uses **REST API endpoints** to talk with the client.
- **Organized** in a typical Spring Boot style: **Controllers**, **Services**, and **Repositories**.
  - **Controllers**: define endpoints.
  - **Services**: hold the logic for how a hero progresses or how a mini-game is scored.
  - **Repositories**: handle actual database queries and store data in MySQL.

### **The Client (Frontend)**
- Built using **Java + Swing**.
- Each team member created **their own unique mini game**.
- The UI is handled using **JPanel**.
- Talks with the server through **HTTP requests**.
- Players can **go to different game locations, play mini-games, and collect plushies**.
- **Updates dynamically** to reflect data changes from the server in real time.

### **How the Client & Server Communicate**
When the player does something in the game, like **choosing a hero** or **starting a mini-game**, the client makes an **HTTP request** to the server. The server then updates the database if needed, and sends a response back.

#### **Example: How a Mini-Game Starts**
1. The player selects a mini-game in the UI.
2. The client makes an HTTP `POST` request to the `/minigames/start/{id}` API endpoint.
3. The server checks if the mini-game is available.
4. The server returns with the game details.
5. The client updates the UI and starts the mini-game.

---
## **3️⃣ How Each Component Connects to JPanel**

The **JPanel** structure is like the css. Every part of the game is built using JPanels that dynamically update when the player interacts with the game.

### **How Different Screens Are Managed**
Each screen in the game is its own **JPanel**. The main game controller manages this:
```java
CardLayout cardLayout = new CardLayout();
JPanel mainPanel = new JPanel(cardLayout);

JPanel menuScreen = new MenuScreen();
JPanel gameScreen = new GameScreen();
JPanel inventoryScreen = new InventoryScreen();

mainPanel.add(menuScreen, "Menu");
mainPanel.add(gameScreen, "Game");
mainPanel.add(inventoryScreen, "Inventory");

cardLayout.show(mainPanel, "Menu");
```
This allows the client to easily switch between **menu, gameplay, and inventory screens** without having to reload.

### **How API Data Updates the UI**
Whenever data is pulled from the server, it updates the the appropriate JPanel:
```java
public void updateHeroDetails(Hero hero) {
    heroNameLabel.setText(hero.getName());
    heroStatsLabel.setText("Power: " + hero.getPowerLevel());
    heroPanel.repaint();
}
```
This ensures that when a player **selects a hero**, the screen dynamically updates with the correct data.

### **Interaction Flow**
- **User clicks** on a button (e.g., "View Plushies").
- **Client** calls the server endpoint (`GET /plushies`).
- **Server** returns a list of plushies.
- **Client** populates a **JPanel** with plushie icons or names.
- **User** sees the updated plushie collection in real-time.

By splitting each section into its own **JPanel**, the code stays **organized** and **modular**, making it easy to add new features.

---
## **4️⃣ Team Contributions: Who Built What?**

Each team member created their **own game** within the client. Below is what each person worked on:

| **Name** | **Game Name** | **Description** |
|---------------|-------------|----------------|
| [ Adam ]    | [Game Title] | Description. |
| [ Kyle ]    | [Game Title] | Description. |
| [ Brian ]   | [Game Title] | Description. |
| [ Brad ]    | [Game Title] | Description. |
| [ Michael ] | [Game Title] | Description. |

---
## **5️⃣ How to Run the Project**

### **Running the Server**
```bash
git clone <server-repo-url>
cd sdat-dev-ops-mid-term-sprint-server
mvn clean install
mvn spring-boot:run
```

**Server Setup**:
1. Make sure **MySQL** is running locally or via Docker.
2. Update `application.properties` with your MySQL creds.
3. Check if `data.sql` (if provided) seeds the database with default heroes or mini-games.
4. The server starts on `localhost:8080` by default.

### **Running the Client**
```bash
git clone <client-repo-url>
cd keyin-client
mvn compile exec:java -Dexec.mainClass="com.keyin.client.ClientApplication"
```

**Client Setup**:
1. Verify the **URL** for the server in the client (`http://localhost:8080`).
2. Make sure the server is **running** we've made that mistake a few times.
3. Run the client, and you should see the game UI appear.

---
## **6️⃣ API Endpoints Overview**

| Endpoint       | Method | Description                          |
|----------------|--------|--------------------------------------|
| `/heroes`      | GET    | Get all heroes                       |
| `/heroes`      | POST   | Create a new hero                    |
| `/locations`   | GET    | List all locations                   |
| `/minigames`   | GET    | List all mini-games                  |
| `/plushies`    | GET    | View collected plushies              |
| `/game/start`  | POST   | Start a new game session             |


Use **Postman** to test these endpoints individually. or For example:
```bash
GET http://localhost:8080/heroes
```
Will return an array of all heroes in the database.

---

feel free to change or anything anything bys srry if it sucks - mike 

