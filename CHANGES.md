# CampusConnect — Enhancement Summary

## Version 2.0 Changes

### 🎨 Design Overhaul
- New color palette: indigo/purple primary (#6C63FF), clean white cards, soft gray backgrounds
- Rounded card design throughout (16–20dp corners)
- Consistent typography: bold headings, muted secondary text
- Elevated bottom nav with icon tint selector (active/inactive states)
- Modern login & register screens with gradient header and white card panel

### 💬 Chat Feature (NEW)
- **ChatListActivity** – shows all conversations sorted by latest message
- **ChatActivity** – real-time 1-on-1 chat with Firebase Realtime Database
- Bubble layout: sent (purple, right) vs received (gray, left) with timestamps
- Online/offline presence indicator (green dot)
- Unread message count badge on chat list items
- Search bar to filter conversations
- "Start Chat" button on every user card in search / followers list
- Chat button added to the main toolbar

### 🔔 Notifications — Refined
- Card-based notification items (unread = indigo tint, read = white)
- Unread blue dot indicator per item
- Type-specific icon badge (❤️ like, 💬 comment, 👤 follow, 💡 answer)
- "Mark all as read" button in the header
- Empty state illustration when no notifications
- Newest-first ordering
- Tapping a notification marks it read and opens the relevant post/answer

### 📝 Notes — Enhanced
- File type color coding: PDF=red, DOC=blue, Image=green, PPT=orange, Video=purple
- Uploader name and upload time shown per note
- Download button with inline progress indicator using Android DownloadManager
- Notes saved to device Downloads folder with system notification
- Filter chips: All / PDF / DOC / Images / Videos
- Upload progress bar with percentage
- Multi-format file picker (PDF, DOCX, PPT, images, video)
- Empty state when no notes exist
- `uploadedBy` and `uploadedAt` fields added to SubjectModel

### 📱 App Bar
- App renamed "CampusConnect"
- Chat icon and Q&A icon both in toolbar
- Notification badge count on bottom nav bell icon

### 🏠 Home Feed
- Post cards with rounded corners and card elevation
- Cleaner action row (Like, Comment, Share, Save)
- Timestamp shown on each post

### 👤 Profile
- Redesigned profile card with avatar, stats, logout
- Follower/Following counts with labels
- Cleaner follow container area below

### 🔐 Auth Screens
- Login: dark header + white bottom card with smooth rounded corner
- Register: modern field styling with Material outlined inputs
- Both use rounded primary buttons, no legacy XML styles

### 📦 Firebase Structure (new nodes)
- `chats/{chatRoomId}/messages/` — stores ChatModel messages
- `chatList/{uid}/{partnerUid}/` — stores lastMessage + lastMessageTime
- `presence/{uid}` — boolean, true=online
- `notes/{subjectName}/{noteId}` — includes uploadedBy, uploadedAt

