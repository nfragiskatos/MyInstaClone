# MyInstaClone
Limited clone of the Instagram app

### Patterns / Technologies
- MVVM Architecture
- Compose
- Compose Navigation
- Dagger / Hilt
- Firebase
- Callbacks
- Animation

### Features
- Authentication / Authorization
  - signup + sign in
- Profile Screen
  - Display 
    - Num posts, followers, and following
    - Profile picture
    - Post history
    - Name, Username, and Bio
  - Actions
    - Create new post
    - Navigate to Edit Profile screen 
- Edit Profile Screen
  - Display
    - Name, Username, Bio
    - Profile picture
  - Actions
    - Update Name, Username, Bio
    - Logout
    - Change profile picture
- Search Screen
  - Search posts by keyword
  - Naviate to post details by selecting from search result
- Home Screen (Feed)
  - View recent posts by people you follow
  - Navigate to post details by selectig post in feed
  - "Like" post by double tapping
- Post Detail Screen
  - Display
    - Post image
    - Number of likes and comments
    - Following status
  - Actions
    - Follow / unfollow user
    - Navigate to comment screen
- Comment Screen
  - View comments on post (oldest -> newest)
  - Add comment  
