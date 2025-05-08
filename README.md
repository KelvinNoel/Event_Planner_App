# 📅 Event Planner App

An Android application to help users create, manage, and organize personal or professional events efficiently.

## 🚀 Features

- 🏠 **Home Screen**
  - List of all upcoming events with summary info using `RecyclerView`
  - Filter events by category (e.g., Wedding, Birthday, Office)
  - Navigation Drawer for quick access
  - Create New Event button

- 📝 **Create/Edit Event**
  - Add or update event name, date, time, venue, category, and description
  - Form validation and user-friendly input layout

- 📄 **Event Details**
  - View all event information
  - Edit or delete the event
  - Share event details
  - Add guests directly from the event page

- 👥 **Guest Management**
  - View guest list with status: Coming, Pending, Not Coming
  - Filter and search guests
  - Track attendance status easily

- 🗄️ **Local Storage**
  - Uses **Room** library for offline data persistence (built on SQLite)

## 🧪 Testing

- Implemented **JUnit** unit tests:
  - Event data validation (date, time, fields)
  - Category recognition logic
  - Special character support in descriptions
  - Event equality based on ID

## 🛠 Tech Stack

| Tool              | Usage                           |
|-------------------|----------------------------------|
| Java              | Core programming language        |
| XML               | UI layout design                 |
| Android Studio    | Development environment          |
| Room (Jetpack)    | Local storage abstraction over SQLite |
| JUnit             | Unit testing                     |
| Git & GitHub      | Version control & collaboration  |

## 🌟 Benefits to Users

- Simple and intuitive event and guest management
- Works offline using Room
- Easy guest status tracking and event filtering

## 🔮 Future Plans

- ☁️ Migrate to **Cloud Storage** (e.g., Firebase) for data syncing and scalability
- 📲 Add real-time notifications and reminders
- 📊 Integrate budget tracking and analytics features
- 🌐 Multi-device and multi-user support for collaborative planning

