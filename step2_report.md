#  Step 2 Report — Data Storage / UI & Navigation

## Project Title
**5 Papers**

---

## 👥 Team Members

| Name    | Role                        |
|---------|-----------------------------|
| Robyn ([Anyro0](https://github.com/Anyro0))  |  UI design, project setup, team lead  |
| Mirko ([mamikek](https://github.com/mamikek))  |   Database implementation, data layer integration  |
| Mattias ([mattiasant](https://github.com/mattiasant)) | UI design, Front-end linkage, button logic, testing  |

---

## 1. What Data Is Stored Locally

The app uses a **Room Database** (`GameDatabase`) to handle all local data.  
Three main entities are implemented:

- **`GameItem`** — represents a card or object in the game.  
  *(fields: itemName, itemType, powerLevel, description, rarity, createdAt)*
- **`MatchHistory`** — stores results of past matches.  
- **`MatchAction`** — logs player actions within matches.

All data is stored **persistently on the device** and accessed through **DAO interfaces** and a **`GameRepository`** layer for clean separation of concerns.

---

## 2. UI & Navigation

The app currently consists of the following screens:

- **HomePage** — main navigation hub with buttons: *Play*, *Stats*, *Store*, *Settings*  
- **StatisticsPage** — displays local data such as *cards placed*, *cards picked up*, and *winrate*  
- **StorePage**, **GameSettingsPage**, **Join_Game** — supporting screens for additional functionality  

Navigation between screens is implemented via **explicit intents** in Kotlin.  
Each subpage uses `finish()` for back navigation, ensuring smooth transitions.  
The app now **launches directly into the HomePage** as its main screen.

---

## 3. How Data Storage Works

- The **Room database** is initialized once in the `GameDatabase` singleton.  
- The **Repository** (`GameRepository`) manages access to all three DAOs.  
- Data can be inserted or observed through **Kotlin coroutines** and **Flow**, for example:

```kotlin
repository.allItems.collect { items ->
    // Update UI when new data is added
}
```

## 4. Challenges and Solutions

| Challenge | Solution |
|------------|-----------|
| **App crash at startup** | Occurred due to a theme mismatch. Solved by switching to `Theme.AppCompat.Light.NoActionBar`. |
| **Database initialization errors** | Fixed lazy initialization order in `MainActivity` and verified proper context usage. |
| **Navigation setup issues** | Implemented explicit intents for page transitions and `finish()` for backtracking. |
| **Late-stage UI and DB connection** | Added temporary placeholders for statistics values; database confirmed to be working. |
