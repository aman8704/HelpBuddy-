**App Logo**

<img width="500" height="500" alt="help_buddy_logo" src="https://github.com/user-attachments/assets/caf1d4d2-32aa-44b0-bfdc-b391f0c3f38b" />

## 🚀 HelpBuddy! – Student Community Assistance Platform

 Empowering students to help each other while earning rewards.

---------------------------

**Project Overview**

**HelpBuddy!** is a **real-time, location-based peer-to-peer assistance platform** built exclusively for students. The application enables students to post assistance requests such as **printouts, medicine delivery, stationary items, parcel pickup, document collection, and campus errands**.

Nearby students can browse these requests, **accept tasks**, complete them, and **earn money** for their services. The platform promotes collaboration, trust, and mutual support within the student community while providing flexible earning opportunities.

---------------------------

## 🏗️ Project Structure (Feature-Based Architecture)

```
HelpBuddy
│
├── MainActivity
│
├── log_reg (Authentication Module)
│   ├── HomeActivity
│   ├── LoginActivity
│   └── RegisterActivity
│
├── home (Home Module)
│   ├── HomeScreenActivity
│   │
│   ├── fragments
│   │   ├── HomeFragment
│   │   ├── NeedHelpFragment
│   │   └── SettingFragment
│   │
│   ├── needs
│   │   ├── Need (Model)
│   │   ├── NeedAdapter
│   │   └── NeedDetailActivity
│   │
│   ├── notification
│   │   └── Notification Components
│   │
│   └── navigation_drawer.components
│       ├── EditProfileActivity
│       └── AboutUsActivity
│
└── Firebase
    ├── Authentication
    ├── Cloud Firestore
    └── Firebase Cloud Messaging
```

------------------------

## ✨ **Key Features**

### 📍 **Live Request Feed**
- Students can post assistance requests that are instantly visible to nearby students.

### 🤝 **Task Acceptance**
- Students can browse available requests and accept tasks they wish to complete.

### 💰 **Earn While Helping**
- Students receive payments for successfully completing assistance requests posted by other students.

### 📊 **Earnings Dashboard**
- Track total earnings, completed tasks, and contribution history.

### ⏳ **Automatic Request Expiry**
- Requests automatically expire after **5 hours** if they are not accepted, ensuring an up-to-date request feed.

### 🔐 **Secure Authentication**
- User authentication powered by **Firebase Authentication**.

### ☁️ **Cloud Database**
- Stores user profiles, requests, task history, and earnings securely using **Firebase Firestore**.

------------------------

## 🎯 **Project Objective**

The goal of **HelpBuddy!** is to create a **trusted student-to-student assistance ecosystem** where students can:

- ✅ Get immediate help with daily tasks.
- ✅ Earn money by helping fellow students.
- ✅ Build a supportive and collaborative campus community.
- ✅ Save time through real-time, location-based assistance.

------------------------

## 💡 **Why HelpBuddy?**

✔️ **Student-Centric Platform**

✔️ **Real-Time Assistance**

✔️ **Secure Authentication**

✔️ **Earn While Helping**

✔️ **Simple & User-Friendly Interface**

------------------------

## 🛠️ **Tech Stack & Technology Decisions**

| **Technology** | **Purpose** | **Why I Chose It** | **Alternatives** |
|----------------|-------------|--------------------|------------------|
| **Kotlin** | Android Development | Official language for Android, concise syntax, null safety, and excellent Jetpack support. | Java, Flutter (Dart), React Native |
| **XML** | User Interface | Provides better control over Android UI and is widely used in Android applications. | Jetpack Compose |
| **Firebase Authentication** | User Login | Easy to integrate, secure, and supports Email/Password and Google Sign-In with minimal backend setup. | Auth0, Supabase Auth, AWS Cognito |
| **Cloud Firestore** | Database | Real-time synchronization, scalable NoSQL database, offline support, and seamless Firebase integration. | Firebase Realtime Database, MongoDB, PostgreSQL, Supabase |
| **Feature-Based Architecture** | Project Architecture | Organizes the codebase into feature-specific modules (e.g., Home, Login, Needs, Notifications), improving maintainability, readability, and scalability as the application grows. | Package-by-Layer, MVC, MVP, MVVM, Clean Architecture |

------------------------

## 🚀 **Future Enhancements**

- 💳 In-App Payment Gateway
- ⭐ Student Rating & Review System
- 💬 Real-Time Chat
- 📞 Voice & Video Calling
- 🤖 AI-Based Request Recommendation
- 📈 Analytics Dashboard
- 🌙 Dark Mode
- 📍 Location-Based Matching**

------------------------

## 🚀 **Resources**

- Icons : https://www.flaticon.com/

------------------------

## 💡 Inspiration

The idea for **Help** came from a real-life experience during my second year of college.

One afternoon, my friend and I were sitting in the college canteen when we suddenly felt like eating samosas. The only place to buy them was outside the campus, but neither of us wanted to leave the college just for a snack. At that moment, I thought:

> **"What if there was a platform where one student could post a small request, and another student who was already going outside could complete it and earn some money?"**

That simple thought became the foundation of **Help**.

As I explored the idea further, I realized it could solve many everyday problems faced by students—not just buying snacks, but also medicine delivery, grocery shopping, parcel pickup, document collection, and other campus errands.

The platform creates a win-win ecosystem:
- 🧑‍🎓 Students get quick help with daily tasks.
- 💰 Other students earn money by completing those tasks.
- 🤝 The campus community becomes more connected and supportive.

What started as a craving for a samosa eventually evolved into a practical solution with real-world applications.
  
## **Download** : https://drive.google.com/drive/folders/1oT0B2GKDXSL22IyVHXdwXYlR58cYPeAv?usp=drive_link

------------------------

## ❤️ **Built to make campus life easier—one helping hand at a time.**
