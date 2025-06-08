# Stories

A mini social media app, built with Kotlin. This project was created as the submission for Dicoding Indonesia's "Learn Intermediate Android App Development" class.

## Screenshots:

<img src="https://github.com/user-attachments/assets/247e0835-1531-498a-84d1-dc4da1683b40" height="512px">
<img src="https://github.com/user-attachments/assets/6a4ae7bd-2c69-48c2-84e9-caea1ecce4cb" height="512px">
<img src="https://github.com/user-attachments/assets/fc678c09-73b1-4216-9270-2e4f600b7263" height="512px">
<img src="https://github.com/user-attachments/assets/0ef7ecaa-db3a-4f40-b37b-da527a712b6e" height="512px">
<img src="https://github.com/user-attachments/assets/19be89f6-0d6a-40f5-8440-5b17ea661758" height="512px">
<img src="https://github.com/user-attachments/assets/482eb3cd-45f7-4e07-9f31-c6711c5030fd" height="512px">

## Features:

* **Story Feed:** View stories from users with continuous scrolling.
* **New Story Upload:** Create and upload new stories, with an option to include location.
* **Map View:** See stories displayed on Google Maps based on their location.

## Technologies Used:

### Languages:
* **Kotlin**

### UI:
* **XML Layouts**

### Libraries & APIs:
* **ViewModel**
* **LiveData**
* **Retrofit:** Used to interact with the Dicoding Story API.
* **Room & RemoteMediator:** Used to enable online-offline support via local caching.
* **DataStore:** Used to store login session data.
* **Paging 3:** Used to load the story data continuously.
* **Google Maps API:** Used to display stories with location.

## How to Run:

**Prerequisites:**

* Android Studio
* An Android Emulator or Physical Device

**Steps:**

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/maruffirdaus/stories.git
    ```
2.  **Open in Android Studio:**
    * Launch Android Studio.
    * If you see the Welcome Screen, click on `Open`.
    * If a project is already open, select `File` > `Open...`.
    * Navigate to the cloned `stories` folder and select it.
3.  **Sync Gradle:**
    * Wait for Android Studio to index files and sync the project with its Gradle files.
4.  **Run the app:**
    * Select an available emulator or connect a physical Android device.
    * Click the `Run 'app'` button (green play icon).
